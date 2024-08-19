package jminusminus;

import static jminusminus.CLConstants.IADD;

/**
 * This abstract base class is the AST node for an assignment operation.
 */
abstract class JAssignment extends JBinaryExpression {
    /**
     * Constructs an AST node for an assignment operation.
     *
     * @param line     line in which the assignment operation occurs in the source file.
     * @param operator the assignment operator.
     * @param lhs      the lhs operand.
     * @param rhs      the rhs operand.
     */
    public JAssignment(int line, String operator, JExpression lhs, JExpression rhs) {
        super(line, operator, lhs, rhs);
    }
}

/**
 * The AST node for an assignment (=) operation.
 */
class JAssignOp extends JAssignment {
    /**
     * Constructs the AST node for an assignment (=) operation.
     *
     * @param line line in which the assignment operation occurs in the source file.
     * @param lhs  lhs operand.
     * @param rhs  rhs operand.
     */
    public JAssignOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "=", lhs, rhs);
    }

    /**
     * {@inheritDoc}
     */
    public JExpression analyze(Context context) {
        if (!(lhs instanceof JLhs)) {
            JAST.compilationUnit.reportSemanticError(line(), "illegal lhs for assignment");
        } else {
            lhs = ((JLhs) lhs).analyzeLhs(context);
            rhs = rhs.analyze(context);
            rhs.type().mustMatchExpected(line(), lhs.type());
            type = rhs.type();
            if (lhs instanceof JVariable) {
                Defn defn = ((JVariable) lhs).iDefn();
                if (defn != null) {
                    // Local variable; consider it to be initialized now.
                    ((LocalVariableDefn) defn).initialize();
                }
            }
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        ((JLhs) lhs).codegenLoadLhsLvalue(output);
        rhs.codegen(output);
        if (!isStatementExpression) {
            ((JLhs) lhs).codegenDuplicateRvalue(output);
        }
        ((JLhs) lhs).codegenStore(output);
    }
}

/**
 * The AST node for a plus-assign (+=) operation.
 */
class JPlusAssignOp extends JAssignment {
    /**
     * Constructs the AST node for a plus-assign (+=) operation.
     *
     * @param line line in which the assignment operation occurs in the source file.
     * @param lhs  the lhs operand.
     * @param rhs  the rhs operand.
     */
    public JPlusAssignOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "+=", lhs, rhs);
    }

    /**
     * {@inheritDoc}
     */
    public JExpression analyze(Context context) {
        if (!(lhs instanceof JLhs)) {
            JAST.compilationUnit.reportSemanticError(line(), "illegal lhs for assignment");
            return this;
        } else {
            lhs = ((JLhs) lhs).analyzeLhs(context);
        }
        rhs = rhs.analyze(context);
        if (lhs.type().equals(Type.STRING)) {
            rhs = (new JStringConcatenationOp(line, lhs, rhs)).analyze(context);
            type = Type.STRING;
        } else {
            lhs.type().mustMatchExpected(line(), Type.INT);
            rhs.type().mustMatchExpected(line(), Type.INT);
            type = Type.INT;
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        ((JLhs) lhs).codegenLoadLhsLvalue(output);
        if (lhs.type().equals(Type.STRING)) {
            rhs.codegen(output);
        } else {
            ((JLhs) lhs).codegenLoadLhsRvalue(output);
            rhs.codegen(output);
            output.addNoArgInstruction(IADD);
        }
        if (!isStatementExpression) {
            ((JLhs) lhs).codegenDuplicateRvalue(output);
        }
        ((JLhs) lhs).codegenStore(output);
    }
}

/**
 * The AST node for a minus-assign (-=) operation.
 */
class JMinusAssignOp extends JAssignment {
    /**
     * Constructs the AST node for a minus-assign operation.
     *
     * @param line line in which the assignment operation occurs in the source file.
     * @param lhs  the lhs operand.
     * @param rhs  the rhs operand.
     */
    public JMinusAssignOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "-=", lhs, rhs);
    }

    /**
     * {@inheritDoc}
     */
    public JExpression analyze(Context context) {
        // TODO
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        // TODO
    }
}

/**
 * The AST node for a star-assign (*=) operation.
 */
class JStarAssignOp extends JAssignment {
    /**
     * Constructs the AST node for a star-assign operation.
     *
     * @param line line in which the assignment operation occurs in the source file.
     * @param lhs  the lhs operand.
     * @param rhs  the rhs operand.
     */
    public JStarAssignOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "*=", lhs, rhs);
    }

    /**
     * {@inheritDoc}
     */
    public JExpression analyze(Context context) {
        // TODO
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        // TODO
    }
}

/**
 * The AST node for a div-assign (/=) operation.
 */
class JDivAssignOp extends JAssignment {
    /**
     * Constructs the AST node for a div-assign operation.
     *
     * @param line line in which the assignment operation occurs in the source file.
     * @param lhs  the lhs operand.
     * @param rhs  the rhs operand.
     */
    public JDivAssignOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "/=", lhs, rhs);
    }

    /**
     * {@inheritDoc}
     */
    public JExpression analyze(Context context) {
        // TODO
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        // TODO
    }
}

/**
 * The AST node for a rem-assign (%=) operation.
 */
class JRemAssignOp extends JAssignment {
    /**
     * Constructs the AST node for a rem-assign operation.
     *
     * @param line line in which the assignment operation occurs in the source file.
     * @param lhs  the lhs operand.
     * @param rhs  the rhs operand.
     */
    public JRemAssignOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "%=", lhs, rhs);
    }

    /**
     * {@inheritDoc}
     */
    public JExpression analyze(Context context) {
        // TODO
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        // TODO
    }
}
