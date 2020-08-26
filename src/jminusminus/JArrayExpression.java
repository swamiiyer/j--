// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST for an array indexing operation. It has an expression denoting an array object and an
 * expression denoting an integer index.
 */
class JArrayExpression extends JExpression implements JLhs {
    // The array.
    private JExpression theArray;

    // The array index expression.
    private JExpression indexExpr;

    /**
     * Constructs an AST node for an array indexing operation.
     *
     * @param line      line in which the operation occurs in the source file.
     * @param theArray  the array we're indexing.
     * @param indexExpr the index expression.
     */
    public JArrayExpression(int line, JExpression theArray, JExpression indexExpr) {
        super(line);
        this.theArray = theArray;
        this.indexExpr = indexExpr;
    }

    /**
     * {@inheritDoc}
     */
    public JExpression analyze(Context context) {
        theArray = (JExpression) theArray.analyze(context);
        indexExpr = (JExpression) indexExpr.analyze(context);
        if (!(theArray.type().isArray())) {
            JAST.compilationUnit.reportSemanticError(line(), "attempt to index a non-array object");
            this.type = Type.ANY;
        } else {
            this.type = theArray.type().componentType();
        }
        indexExpr.type().mustMatchExpected(line(), Type.INT);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public JExpression analyzeLhs(Context context) {
        analyze(context);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        theArray.codegen(output);
        indexExpr.codegen(output);
        if (type == Type.INT) {
            output.addNoArgInstruction(IALOAD);
        } else if (type == Type.BOOLEAN) {
            output.addNoArgInstruction(BALOAD);
        } else if (type == Type.CHAR) {
            output.addNoArgInstruction(CALOAD);
        } else if (!type.isPrimitive()) {
            output.addNoArgInstruction(AALOAD);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output, String targetLabel, boolean onTrue) {
        codegen(output);
        if (onTrue) {
            output.addBranchInstruction(IFNE, targetLabel);
        } else {
            output.addBranchInstruction(IFEQ, targetLabel);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void codegenLoadLhsLvalue(CLEmitter output) {
        theArray.codegen(output);
        indexExpr.codegen(output);
    }

    /**
     * {@inheritDoc}
     */
    public void codegenLoadLhsRvalue(CLEmitter output) {
        if (type == Type.STRING) {
            output.addNoArgInstruction(DUP2_X1);
        } else {
            output.addNoArgInstruction(DUP2);
        }
        if (type == Type.INT) {
            output.addNoArgInstruction(IALOAD);
        } else if (type == Type.BOOLEAN) {
            output.addNoArgInstruction(BALOAD);
        } else if (type == Type.CHAR) {
            output.addNoArgInstruction(CALOAD);
        } else if (!type.isPrimitive()) {
            output.addNoArgInstruction(AALOAD);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void codegenDuplicateRvalue(CLEmitter output) {
        output.addNoArgInstruction(DUP_X2);
    }

    /**
     * {@inheritDoc}
     */
    public void codegenStore(CLEmitter output) {
        if (type == Type.INT) {
            output.addNoArgInstruction(IASTORE);
        } else if (type == Type.BOOLEAN) {
            output.addNoArgInstruction(BASTORE);
        } else if (type == Type.CHAR) {
            output.addNoArgInstruction(CASTORE);
        } else if (!type.isPrimitive()) {
            output.addNoArgInstruction(AASTORE);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JArrayExpression:" + line, e);
        JSONElement e1 = new JSONElement();
        e.addChild("TheArray", e1);
        theArray.toJSON(e1);
        JSONElement e2 = new JSONElement();
        e.addChild("TheIndex", e2);
        indexExpr.toJSON(e2);
    }
}
