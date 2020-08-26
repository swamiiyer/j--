// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for the null literal.
 */
class JLiteralNull extends JExpression {
    /**
     * Constructs an AST node for the null literal given its line number.
     *
     * @param line line in which the literal occurs in the source file.
     */
    public JLiteralNull(int line) {
        super(line);
    }

    /**
     * {@inheritDoc}
     */
    public JExpression analyze(Context context) {
        type = Type.NULLTYPE;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        output.addNoArgInstruction(ACONST_NULL);
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JLiteralNull:" + line, e);
        e.addAttribute("type", type == null ? "" : type.toString());
        e.addAttribute("value", "null");
    }
}
