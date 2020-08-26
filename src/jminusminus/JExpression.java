// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

/**
 * The AST node for an expression. The syntax says all expressions are statements, but a semantic
 * check throws some (those without a side-effect) out. Every expression has a type and a flag
 * indicating whether or not it's a statement-expression.
 */
abstract class JExpression extends JStatement {
    /**
     * Expression type.
     */
    protected Type type;

    /**
     * Whether or not this expression is a statement.
     */
    protected boolean isStatementExpression;

    /**
     * Constructs an AST node for an expression.
     *
     * @param line line in which the expression occurs in the source file.
     */
    protected JExpression(int line) {
        super(line);
        isStatementExpression = false; // by default
    }

    /**
     * Returns the expression type.
     *
     * @return the expression type.
     */
    public Type type() {
        return type;
    }

    /**
     * Returns true if this expression is being used as a statement, and false otherwise.
     *
     * @return true if this expression is being used as a statement, and false otherwise.
     */
    public boolean isStatementExpression() {
        return isStatementExpression;
    }

    /**
     * Analyzes and returns a JExpression.
     *
     * @param context context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */
    public abstract JExpression analyze(Context context);

    /**
     * Performs short-circuit code generation for a boolean expression, given the code emitter,
     * a target label, and whether we branch to that label on true or on false.
     *
     * @param output      the code emitter.
     * @param targetLabel the label to which we should branch.
     * @param onTrue      do we branch on true?
     */
    public void codegen(CLEmitter output, String targetLabel, boolean onTrue) {
        // We should never reach here, since all boolean (including identifier) expressions must
        // override this method.
        System.err.println("Error in short-circuit code generation");
    }
}
