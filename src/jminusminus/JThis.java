// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST for a "this" expression. It serves as a target of some field selection or message.
 */
class JThis extends JExpression {
    /**
     * Constructs an AST node for a "this" expression.
     *
     * @param line line in which the expression occurs in the source file.
     */
    public JThis(int line) {
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
        json.addChild("JThis:" + line, e);
    }
}
