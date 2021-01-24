// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for an int literal.
 */
class JLiteralInt extends JExpression {
    // String representation of the literal.
    private String text;

    /**
     * Constructs an AST node for an int literal given its line number and string representation.
     *
     * @param line line in which the literal occurs in the source file.
     * @param text string representation of the literal.
     */
    public JLiteralInt(int line, String text) {
        super(line);
        this.text = text;
    }

    /**
     * Returns the literal as an int.
     *
     * @return the literal as an int.
     */
    public int toInt() {
        return Integer.parseInt(text);
    }

    /**
     * {@inheritDoc}
     */
    public JExpression analyze(Context context) {
        type = Type.INT;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        int i = toInt();
        switch (i) {
            case 0:
                output.addNoArgInstruction(ICONST_0);
                break;
            case 1:
                output.addNoArgInstruction(ICONST_1);
                break;
            case 2:
                output.addNoArgInstruction(ICONST_2);
                break;
            case 3:
                output.addNoArgInstruction(ICONST_3);
                break;
            case 4:
                output.addNoArgInstruction(ICONST_4);
                break;
            case 5:
                output.addNoArgInstruction(ICONST_5);
                break;
            default:
                if (i >= 6 && i <= 127) {
                    output.addOneArgInstruction(BIPUSH, i);
                } else if (i >= 128 && i <= 32767) {
                    output.addOneArgInstruction(SIPUSH, i);
                } else {
                    output.addLDCInstruction(i);
                }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JLiteralInt:" + line, e);
        e.addAttribute("type", type == null ? "" : type.toString());
        e.addAttribute("value", text);
    }
}
