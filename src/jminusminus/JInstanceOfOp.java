// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for an instanceof expression, having two arguments: an expression and a reference
 * type.
 */
class JInstanceOfOp extends JExpression {
    // The expression denoting the value to be tested.
    private JExpression expr;

    // The reference type we are testing for.
    private Type typeSpec;

    /**
     * Constructs an AST node for an instanceof expression.
     *
     * @param line     the line in which the instanceof expression occurs in the source file.
     * @param expr     the expression denoting the value to be tested.
     * @param typeSpec the reference type we are testing for.
     */
    public JInstanceOfOp(int line, JExpression expr, Type typeSpec) {
        super(line);
        this.expr = expr;
        this.typeSpec = typeSpec;
    }

    /**
     * {@inheritDoc}
     */
    public JInstanceOfOp analyze(Context context) {
        expr = (JExpression) expr.analyze(context);
        typeSpec = typeSpec.resolve(context);
        if (!typeSpec.isReference()) {
            JAST.compilationUnit.reportSemanticError(line(),
                    "RHS of instanceof must be a reference type");
        } else if (!(expr.type() == Type.NULLTYPE || expr.type() == Type.ANY ||
                expr.type().isReference())) {
            JAST.compilationUnit.reportSemanticError(line(),
                    "LHS of instanceof must be a reference type");
        } else if (expr.type().isReference() && !typeSpec.isJavaAssignableFrom(expr.type()) &&
                !expr.type().isJavaAssignableFrom(typeSpec)) {
            JAST.compilationUnit.reportSemanticError(line(),
                    "It is impossible for the expression to be an instance of " +
                            typeSpec.toString());
        }
        type = Type.BOOLEAN;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        expr.codegen(output);
        output.addReferenceInstruction(INSTANCEOF, typeSpec.jvmName());
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
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JInstanceOfOp:" + line, e);
        e.addAttribute("type", type == null ? "" : type.toString());
        e.addAttribute("referenceType", typeSpec == null ? "" : typeSpec.toString());
        JSONElement e1 = new JSONElement();
        e.addChild("Expression", e1);
        expr.toJSON(e1);
    }
}
