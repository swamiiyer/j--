// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.io.FileNotFoundException;
import java.util.Stack;
import java.util.Vector;

/**
 * A lexical analyzer for j-- that interfaces with the hand-written parser (Parser). It
 * provides a backtracking mechanism, and makes use of the underlying hand-written scanner
 * (Scanner).
 */
class LookaheadScanner {
    // The underlying hand-written scanner.
    private Scanner scanner;

    // Backtracking queue.
    private Vector<TokenInfo> backtrackingQueue;

    // Token queue.
    private Vector<TokenInfo> nextQueue;

    // Stack of token queues for nested lookahead.
    private Stack<Vector<TokenInfo>> queueStack;

    // Whether we are looking ahead.
    public boolean isLookingAhead;

    // Previous token.
    private TokenInfo previousToken;

    // Current token.
    private TokenInfo token;

    /**
     * Constructs a LookaheadScanner.
     *
     * @param fileName the name of the file containing the source.
     * @throws FileNotFoundException when the named file cannot be found.
     */
    public LookaheadScanner(String fileName) throws FileNotFoundException {
        scanner = new Scanner(fileName);
        backtrackingQueue = new Vector<TokenInfo>();
        nextQueue = new Vector<TokenInfo>();
        queueStack = new Stack<Vector<TokenInfo>>();
        isLookingAhead = false;
    }

    /**
     * Scans to the next token in the input.
     */
    public void next() {
        previousToken = token;
        if (backtrackingQueue.size() == 0) {
            token = scanner.getNextToken();
        } else {
            token = backtrackingQueue.remove(0);
        }
        if (isLookingAhead) {
            nextQueue.add(token);
        }
    }

    /**
     * Records the current position in the input, so that we can start looking ahead in the input
     * (and later return to this position) --- the current and subsequent tokens are queued until
     * returnToPosition() is invoked.
     */
    public void recordPosition() {
        isLookingAhead = true;
        queueStack.push(nextQueue);
        nextQueue = new Vector<TokenInfo>();
        nextQueue.add(previousToken);
        nextQueue.add(token);
    }

    /**
     * Returns to the previously recorded position in the input stream of tokens.
     */
    public void returnToPosition() {
        while (backtrackingQueue.size() > 0) {
            nextQueue.add(backtrackingQueue.remove(0));
        }
        backtrackingQueue = nextQueue;
        nextQueue = queueStack.pop();
        isLookingAhead = !(queueStack.empty());

        // Restore previous and current tokens
        previousToken = backtrackingQueue.remove(0);
        token = backtrackingQueue.remove(0);
    }

    /**
     * Returns the current token.
     *
     * @return the current token.
     */
    public TokenInfo token() {
        return token;
    }

    /**
     * Returns the previous token.
     *
     * @return the previous token.
     */
    public TokenInfo previousToken() {
        return previousToken;
    }

    /**
     * Returns true if an error has occurred, and false otherwise.
     *
     * @return true if an error has occurred, and false otherwise.
     */
    public boolean errorHasOccured() {
        return scanner.errorHasOccurred();
    }

    /**
     * Returns the name of the source file.
     *
     * @return the name of the source file.
     */
    public String fileName() {
        return scanner.fileName();
    }
}
