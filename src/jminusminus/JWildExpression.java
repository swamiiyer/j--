package jminusminus;

/**
 * The AST node for a "wild" expression. A wild expression is a placeholder expression, used when there is a syntax
 * error.
 */
class JWildExpression extends JExpression {
    /**
     * Constructs an AST node for a "wild" expression.
     *
     * @param line line in which the "wild" expression occurs in the source file.
     */

    public JWildExpression(int line) {
        super(line);
    }

    /**
     * {@inheritDoc}
     */
    public JExpression analyze(Context context) {
        type = Type.ANY;
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
        json.addChild("JWildExpression:" + line, e);
        e.addAttribute("type", type == null ? "" : type.toString());
        e.addAttribute("value", "");
    }
}
