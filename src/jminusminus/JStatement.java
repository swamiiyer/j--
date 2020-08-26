// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

/**
 * This abstract base class is the AST node for a statement (includes expressions).
 */
abstract class JStatement extends JAST {
    /**
     * Constructs an AST node for a statement.
     *
     * @param line line in which the statement occurs in the source file.
     */
    protected JStatement(int line) {
        super(line);
    }
}
