// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a "new" array operation. It keeps track of its base type and a list of its
 * dimensions.
 */
class JNewArrayOp extends JExpression {
    // The base (component) type of the array.
    private Type typeSpec;

    // Dimensions of the array.
    private ArrayList<JExpression> dimExprs;

    /**
     * Constructs an AST node for a "new" array operation.
     *
     * @param line     the line in which the operation occurs in the source file.
     * @param typeSpec the type of the array being created.
     * @param dimExprs a list of dimension expressions.
     */
    public JNewArrayOp(int line, Type typeSpec, ArrayList<JExpression> dimExprs) {
        super(line);
        this.typeSpec = typeSpec;
        this.dimExprs = dimExprs;
    }

    /**
     * {@inheritDoc}
     */
    public JExpression analyze(Context context) {
        type = typeSpec.resolve(context);
        for (int i = 0; i < dimExprs.size(); i++) {
            dimExprs.set(i, dimExprs.get(i).analyze(context));
            dimExprs.get(i).type().mustMatchExpected(line, Type.INT);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        // Code to push diemension exprs on to the stack.
        for (JExpression dimExpr : dimExprs) {
            dimExpr.codegen(output);
        }

        // Generates the appropriate array creation instruction.
        if (dimExprs.size() == 1) {
            output.addArrayInstruction(type.componentType().isReference() ?
                    ANEWARRAY : NEWARRAY, type.componentType().jvmName());
        } else {
            output.addMULTIANEWARRAYInstruction(type.toDescriptor(), dimExprs.size());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JNewArrayOp:" + line, e);
        e.addAttribute("type", type == null ? "" : type.toString());
        if (dimExprs != null) {
            for (JExpression dimExpr : dimExprs) {
                JSONElement e1 = new JSONElement();
                e.addChild("Dimension", e1);
                dimExpr.toJSON(e1);
            }
        }
    }
}
