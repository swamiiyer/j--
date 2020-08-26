// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;

/**
 * The AST node for a block, which delimits a nested level of scope.
 */
class JBlock extends JStatement {
    // List of statements forming the block body.
    private ArrayList<JStatement> statements;

    // The new context (built in analyze()) represented by this block.
    private LocalContext context;

    /**
     * Constructs an AST node for a block.
     *
     * @param line       line in which the block occurs in the source file.
     * @param statements list of statements forming the block body.
     */
    public JBlock(int line, ArrayList<JStatement> statements) {
        super(line);
        this.statements = statements;
    }

    /**
     * Returns the list of statements comprising this block.
     *
     * @return the list of statements comprising this block.
     */
    public ArrayList<JStatement> statements() {
        return statements;
    }

    /**
     * {@inheritDoc}
     */
    public JBlock analyze(Context context) {
        // { ... } defines a new level of scope.
        this.context = new LocalContext(context);

        for (int i = 0; i < statements.size(); i++) {
            statements.set(i, (JStatement) statements.get(i).analyze(this.context));
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        for (JStatement statement : statements) {
            statement.codegen(output);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JBlock:" + line, e);
        if (context != null) {
            context.toJSON(e);
        }
        for (JStatement statement : statements) {
            statement.toJSON(e);
        }
    }
}
