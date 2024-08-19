package jminusminus;

import static jminusminus.CLConstants.GOTO;
import static jminusminus.CLConstants.ICONST_0;
import static jminusminus.CLConstants.ICONST_1;

/**
 * The AST node for a boolean literal.
 */
class JLiteralBoolean extends JExpression {
    // String representation of the literal.
    private final String text;

    /**
     * Constructs an AST node for a boolean literal given its line number and string representation.
     *
     * @param line line in which the literal occurs in the source file.
     * @param text string representation of the literal.
     */
    public JLiteralBoolean(int line, String text) {
        super(line);
        this.text = text;
    }

    /**
     * Returns the literal as a boolean.
     *
     * @return the literal as a boolean.
     */
    public boolean toBoolean() {
        return text.equals("true");
    }

    /**
     * {@inheritDoc}
     */
    public JExpression analyze(Context context) {
        type = Type.BOOLEAN;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        output.addNoArgInstruction(toBoolean() ? ICONST_1 : ICONST_0);
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output, String targetLabel, boolean onTrue) {
        boolean b = toBoolean();
        if (b && onTrue || !b && !onTrue) {
            output.addBranchInstruction(GOTO, targetLabel);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JLiteralBoolean:" + line, e);
        e.addAttribute("type", type == null ? "" : type.toString());
        e.addAttribute("value", text);
    }
}
