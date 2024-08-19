package jminusminus;

import static jminusminus.CLConstants.ALOAD;
import static jminusminus.CLConstants.ALOAD_0;
import static jminusminus.CLConstants.ALOAD_1;
import static jminusminus.CLConstants.ALOAD_2;
import static jminusminus.CLConstants.ALOAD_3;
import static jminusminus.CLConstants.ASTORE;
import static jminusminus.CLConstants.ASTORE_0;
import static jminusminus.CLConstants.ASTORE_1;
import static jminusminus.CLConstants.ASTORE_2;
import static jminusminus.CLConstants.ASTORE_3;
import static jminusminus.CLConstants.DUP;
import static jminusminus.CLConstants.IFEQ;
import static jminusminus.CLConstants.IFNE;
import static jminusminus.CLConstants.ILOAD;
import static jminusminus.CLConstants.ILOAD_0;
import static jminusminus.CLConstants.ILOAD_1;
import static jminusminus.CLConstants.ILOAD_2;
import static jminusminus.CLConstants.ILOAD_3;
import static jminusminus.CLConstants.ISTORE;
import static jminusminus.CLConstants.ISTORE_0;
import static jminusminus.CLConstants.ISTORE_1;
import static jminusminus.CLConstants.ISTORE_2;
import static jminusminus.CLConstants.ISTORE_3;

/**
 * The AST node for an identifier used as a primary expression.
 */
class JVariable extends JExpression implements JLhs {
    // The variable's name.
    private final String name;

    // The variable's definition.
    private Defn defn;

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
    public Defn iDefn() {
        return defn;
    }

    /**
     * {@inheritDoc}
     */
    public JExpression analyze(Context context) {
        defn = context.lookup(name);
        if (defn == null) {
            // Not a local, but is it a field?
            Type definingType = context.definingType();
            Field field = definingType.fieldFor(name);
            if (field == null) {
                type = Type.ANY;
                JAST.compilationUnit.reportSemanticError(line, "cannot find name: " + name);
            } else {
                // Rewrite a variable denoting a field as an explicit field selection.
                type = field.type();
                JExpression newTree = new JFieldSelection(line(),
                        field.isStatic() || (context.methodContext() != null && context.methodContext().isStatic()) ?
                                new JVariable(line(), definingType.toString()) : new JThis(line), name);
                return newTree.analyze(context);
            }
        } else {
            if (!analyzeLhs && defn instanceof LocalVariableDefn && !((LocalVariableDefn) defn).isInitialized()) {
                JAST.compilationUnit.reportSemanticError(line, "variable " + name + " might not have been initialized");
            }
            type = defn.type();
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
            if (defn != null && !(defn instanceof LocalVariableDefn)) {
                JAST.compilationUnit.reportSemanticError(line(), name + " is a bad LHS to a =");
            }
        }
        return newTree;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        if (defn instanceof LocalVariableDefn) {
            int offset = ((LocalVariableDefn) defn).offset();
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
        if (defn instanceof LocalVariableDefn) {
            codegen(output);
            output.addBranchInstruction(onTrue ? IFNE : IFEQ, targetLabel);
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
        if (defn instanceof LocalVariableDefn) {
            // It's copied atop the stack.
            output.addNoArgInstruction(DUP);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void codegenStore(CLEmitter output) {
        if (defn instanceof LocalVariableDefn) {
            int offset = ((LocalVariableDefn) defn).offset();
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
        e.addAttribute("type", type == null? "" : type.toString());
    }
}
