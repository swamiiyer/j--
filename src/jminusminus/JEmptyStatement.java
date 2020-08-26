// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

/**
 * The AST node for representing the empty statement.
 */
class JEmptyStatement extends JStatement {
    /**
     * Constructs an AST node for an empty statement.
     *
     * @param line line in which the empty statement occurs in the source file.
     */
    protected JEmptyStatement(int line) {
        super(line);
    }

    /**
     * {@inheritDoc}
     */
    public JAST analyze(Context context) {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        // Nothing here.
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JEmptyStatement:" + line, e);
    }
}
