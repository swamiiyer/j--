package jminusminus;

import static jminusminus.CLConstants.BIPUSH;
import static jminusminus.CLConstants.ICONST_0;
import static jminusminus.CLConstants.ICONST_1;
import static jminusminus.CLConstants.ICONST_2;
import static jminusminus.CLConstants.ICONST_3;
import static jminusminus.CLConstants.ICONST_4;
import static jminusminus.CLConstants.ICONST_5;
import static jminusminus.CLConstants.SIPUSH;

/**
 * The AST node for a char literal.
 */
class JLiteralChar extends JExpression {
    // String representation of the literal.
    private final String text;

    /**
     * Constructs an AST node for a char literal given its line number and string representation.
     *
     * @param line line in which the literal occurs in the source file.
     * @param text string representation of the literal.
     */
    public JLiteralChar(int line, String text) {
        super(line);
        this.text = text;
    }

    /**
     * Returns the literal as an int.
     *
     * @return the literal as an int.
     */
    public int toInt() {
        return JAST.unescape(text).charAt(1);
    }

    /**
     * {@inheritDoc}
     */
    public JExpression analyze(Context context) {
        type = Type.CHAR;
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
                } else {
                    output.addOneArgInstruction(SIPUSH, i);
                }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JLiteralChar:" + line, e);
        e.addAttribute("type", type == null ? "" : type.toString());
        e.addAttribute("value", text.substring(1, text.length() - 1));
    }
}
