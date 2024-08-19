package jminusminus;

import static jminusminus.CLConstants.ARETURN;
import static jminusminus.CLConstants.IRETURN;
import static jminusminus.CLConstants.RETURN;

/**
 * The AST node for a return-statement. If the enclosing method is non-void, then there is a value to return, so we
 * keep track of the expression denoting that value and its type.
 */
class JReturnStatement extends JStatement {
    // The returned expression.
    private JExpression expr;

    /**
     * Constructs an AST node for a return-statement.
     *
     * @param line line in which the return-statement appears in the source file.
     * @param expr the returned expression.
     */
    public JReturnStatement(int line, JExpression expr) {
        super(line);
        this.expr = expr;
    }

    /**
     * {@inheritDoc}
     */
    public JStatement analyze(Context context) {
        MethodContext methodContext = context.methodContext();
        if (methodContext.methodReturnType() == Type.CONSTRUCTOR) {
            if (expr != null) {
                JAST.compilationUnit.reportSemanticError(line(), "cannot return a value from a constructor");
            }
        } else {
            // Must be a method.
            Type returnType = methodContext.methodReturnType();
            methodContext.confirmMethodHasReturn();
            if (expr != null) {
                if (returnType == Type.VOID) {
                    JAST.compilationUnit.reportSemanticError(line(), "cannot return a value from a void method");
                } else {
                    expr = expr.analyze(context);
                    expr.type().mustMatchExpected(line(), returnType);
                }
            } else {
                if (returnType != Type.VOID) {
                    JAST.compilationUnit.reportSemanticError(line(), "missing return value");
                }
            }
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        if (expr == null) {
            output.addNoArgInstruction(RETURN);
        } else {
            expr.codegen(output);
            if (expr.type() == Type.INT || expr.type() == Type.BOOLEAN || expr.type() == Type.CHAR) {
                output.addNoArgInstruction(IRETURN);
            } else {
                output.addNoArgInstruction(ARETURN);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JReturnStatement:" + line, e);
        if (expr != null) {
            JSONElement e1 = new JSONElement();
            e.addChild("Expression", e1);
            expr.toJSON(e1);
        }
    }
}
