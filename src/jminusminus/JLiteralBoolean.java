// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a boolean literal.
 */
class JLiteralBoolean extends JExpression {
    // String representation of the literal.
    private String text;

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
        if (text.equals("true")) {
            output.addNoArgInstruction(ICONST_1);
        } else {
            output.addNoArgInstruction(ICONST_0);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output, String targetLabel, boolean onTrue) {
        if (text.equals("true")) {
            if (onTrue) {
                output.addBranchInstruction(GOTO, targetLabel);
            }
        } else {
            if (!onTrue) {
                output.addBranchInstruction(GOTO, targetLabel);
            }
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
