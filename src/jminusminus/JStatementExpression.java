// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

/**
 * The AST node for an expression that appears as a statement. Only the expressions that have a
 * side-effect are valid statement expressions.
 */
class JStatementExpression extends JStatement {
    /**
     * The expression.
     */
    protected JExpression expr;

    /**
     * Constructs an AST node for a statement expression.
     *
     * @param line line in which the expression occurs in the source file.
     * @param expr the expression.
     */
    public JStatementExpression(int line, JExpression expr) {
        super(line);
        this.expr = expr;
    }

    /**
     * {@inheritDoc}
     */
    public JStatement analyze(Context context) {
        if (expr.isStatementExpression) {
            expr = expr.analyze(context);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        expr.codegen(output);
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JStatementExpression:" + line, e);
        expr.toJSON(e);
    }
}
