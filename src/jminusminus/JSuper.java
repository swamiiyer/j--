// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a "super" expression. It serves as a target of some field selection or message.
 */
class JSuper extends JExpression {
    /**
     * Constructs an AST node for a "super" expression.
     *
     * @param line line in which the expression occurs in the source file.
     */
    public JSuper(int line) {
        super(line);
    }

    /**
     * {@inheritDoc}
     */
    public JExpression analyze(Context context) {
        type = ((JClassDeclaration) context.classContext.definition()).thisType();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        output.addNoArgInstruction(ALOAD_0);
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JSuper:" + line, e);
    }
}
