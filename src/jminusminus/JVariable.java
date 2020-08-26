// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for an identifier used as a primary expression.
 */
class JVariable extends JExpression implements JLhs {
    // The variable's name.
    private String name;

    // The variable's definition.
    private IDefn iDefn;

    // Was analyzeLhs() done?
    private boolean analyzeLhs;

    /**
     * Constructs the AST node for a variable.
     *
     * @param line line in which the variable occurs in the source file.
     * @param name the name.
     */
    public JVariable(int line, String name) {
        super(line);
        this.name = name;
    }

    /**
     * Returns the identifier name.
     *
     * @return the identifier name.
     */
    public String name() {
        return name;
    }

    /**
     * Returns the identifier's definition.
     *
     * @return the identifier's definition.
     */
    public IDefn iDefn() {
        return iDefn;
    }

    /**
     * {@inheritDoc}
     */
    public JExpression analyze(Context context) {
        iDefn = context.lookup(name);
        if (iDefn == null) {
            // Not a local, but is it a field?
            Type definingType = context.definingType();
            Field field = definingType.fieldFor(name);
            if (field == null) {
                type = Type.ANY;
                JAST.compilationUnit.reportSemanticError(line, "Cannot find name: " + name);
            } else {
                // Rewrite a variable denoting a field as an explicit field selection.
                type = field.type();
                JExpression newTree = new JFieldSelection(line(),
                        field.isStatic() || (context.methodContext() != null &&
                                context.methodContext().isStatic()) ?
                                new JVariable(line(), definingType.toString()) : new JThis(line),
                        name);
                return (JExpression) newTree.analyze(context);
            }
        } else {
            if (!analyzeLhs && iDefn instanceof LocalVariableDefn &&
                    !((LocalVariableDefn) iDefn).isInitialized()) {
                JAST.compilationUnit.reportSemanticError(line, "Variable " + name +
                        " might not have been initialized");
            }
            type = iDefn.type();
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public JExpression analyzeLhs(Context context) {
        analyzeLhs = true;
        JExpression newTree = analyze(context);
        if (newTree instanceof JVariable) {
            // Could (now) be a JFieldSelection, but if it's (still) a JVariable...
            if (iDefn != null && !(iDefn instanceof LocalVariableDefn)) {
                JAST.compilationUnit.reportSemanticError(line(), name + " is a bad LHS to a =");
            }
        }
        return newTree;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        if (iDefn instanceof LocalVariableDefn) {
            int offset = ((LocalVariableDefn) iDefn).offset();
            if (type.isReference()) {
                switch (offset) {
                    case 0:
                        output.addNoArgInstruction(ALOAD_0);
                        break;
                    case 1:
                        output.addNoArgInstruction(ALOAD_1);
                        break;
                    case 2:
                        output.addNoArgInstruction(ALOAD_2);
                        break;
                    case 3:
                        output.addNoArgInstruction(ALOAD_3);
                        break;
                    default:
                        output.addOneArgInstruction(ALOAD, offset);
                        break;
                }
            } else {
                // Primitive types.
                if (type == Type.INT || type == Type.BOOLEAN || type == Type.CHAR) {
                    switch (offset) {
                        case 0:
                            output.addNoArgInstruction(ILOAD_0);
                            break;
                        case 1:
                            output.addNoArgInstruction(ILOAD_1);
                            break;
                        case 2:
                            output.addNoArgInstruction(ILOAD_2);
                            break;
                        case 3:
                            output.addNoArgInstruction(ILOAD_3);
                            break;
                        default:
                            output.addOneArgInstruction(ILOAD, offset);
                            break;
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output, String targetLabel, boolean onTrue) {
        if (iDefn instanceof LocalVariableDefn) {
            codegen(output);
            if (onTrue) {
                output.addBranchInstruction(IFNE, targetLabel);
            } else {
                output.addBranchInstruction(IFEQ, targetLabel);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void codegenLoadLhsLvalue(CLEmitter output) {
        // Nothing here.
    }

    /**
     * {@inheritDoc}
     */
    public void codegenLoadLhsRvalue(CLEmitter output) {
        codegen(output);
    }

    /**
     * {@inheritDoc}
     */
    public void codegenDuplicateRvalue(CLEmitter output) {
        if (iDefn instanceof LocalVariableDefn) {
            // It's copied atop the stack.
            output.addNoArgInstruction(DUP);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void codegenStore(CLEmitter output) {
        if (iDefn instanceof LocalVariableDefn) {
            int offset = ((LocalVariableDefn) iDefn).offset();
            if (type.isReference()) {
                switch (offset) {
                    case 0:
                        output.addNoArgInstruction(ASTORE_0);
                        break;
                    case 1:
                        output.addNoArgInstruction(ASTORE_1);
                        break;
                    case 2:
                        output.addNoArgInstruction(ASTORE_2);
                        break;
                    case 3:
                        output.addNoArgInstruction(ASTORE_3);
                        break;
                    default:
                        output.addOneArgInstruction(ASTORE, offset);
                        break;
                }
            } else {
                // Primitive types.
                if (type == Type.INT || type == Type.BOOLEAN || type == Type.CHAR) {
                    switch (offset) {
                        case 0:
                            output.addNoArgInstruction(ISTORE_0);
                            break;
                        case 1:
                            output.addNoArgInstruction(ISTORE_1);
                            break;
                        case 2:
                            output.addNoArgInstruction(ISTORE_2);
                            break;
                        case 3:
                            output.addNoArgInstruction(ISTORE_3);
                            break;
                        default:
                            output.addOneArgInstruction(ISTORE, offset);
                            break;
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JVariable:" + line, e);
        e.addAttribute("name", name());
    }
}
