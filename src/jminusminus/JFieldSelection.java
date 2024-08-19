package jminusminus;

import static jminusminus.CLConstants.ARRAYLENGTH;
import static jminusminus.CLConstants.DUP;
import static jminusminus.CLConstants.DUP_X1;
import static jminusminus.CLConstants.GETFIELD;
import static jminusminus.CLConstants.GETSTATIC;
import static jminusminus.CLConstants.IFEQ;
import static jminusminus.CLConstants.IFNE;
import static jminusminus.CLConstants.PUTFIELD;
import static jminusminus.CLConstants.PUTSTATIC;

/**
 * The AST node for a field selection operation. It has a target object, a field name, and the field it defines.
 */
class JFieldSelection extends JExpression implements JLhs {
    /**
     * The target expression.
     */
    protected JExpression target;

    // The ambiguous part that is reclassified in analyze().
    private final AmbiguousName ambiguousPart;

    // The field name.
    private final String fieldName;

    // The Field representing this field.
    private Field field;

    /**
     * Constructs an AST node for a field selection without an ambiguous part.
     *
     * @param line      the line number of the selection.
     * @param target    the target of the selection.
     * @param fieldName the field name.
     */
    public JFieldSelection(int line, JExpression target, String fieldName) {
        this(line, null, target, fieldName);
    }

    /**
     * Construct an AST node for a field selection having an ambiguous part.
     *
     * @param line          line in which the field selection occurs in the source file.
     * @param ambiguousPart the ambiguous part.
     * @param target        the target of the selection.
     * @param fieldName     the field name.
     */
    public JFieldSelection(int line, AmbiguousName ambiguousPart, JExpression target, String fieldName) {
        super(line);
        this.ambiguousPart = ambiguousPart;
        this.target = target;
        this.fieldName = fieldName;
    }

    /**
     * {@inheritDoc}
     */
    public JExpression analyze(Context context) {
        // Reclassify the ambiguous part.
        if (ambiguousPart != null) {
            JExpression expr = ambiguousPart.reclassify(context);
            if (expr != null) {
                if (target == null) {
                    target = expr;
                } else {
                    // Can't even happen syntactically.
                    JAST.compilationUnit.reportSemanticError(line(), "badly formed suffix");
                }
            }
        }

        target = target.analyze(context);
        Type targetType = target.type();

        // We use a workaround for the "length" field of arrays.
        if ((targetType.isArray()) && fieldName.equals("length")) {
            type = Type.INT;
        } else {
            // Other than that, targetType has to be a reference type.
            if (targetType.isPrimitive()) {
                JAST.compilationUnit.reportSemanticError(line(),
                        "target of a field selection must be a reference type");
                type = Type.ANY;
                return this;
            }
            field = targetType.fieldFor(fieldName);
            if (field == null) {
                JAST.compilationUnit.reportSemanticError(line(), "cannot find a field: " + fieldName);
                type = Type.ANY;
            } else {
                context.definingType().checkAccess(line, field);
                type = field.type();

                // Non-static field cannot be referenced from a static context.
                if (!field.isStatic()) {
                    if (target instanceof JVariable &&
                            ((JVariable) target).iDefn() instanceof TypeNameDefn) {
                        JAST.compilationUnit.reportSemanticError(line(), "non-static field " +
                                fieldName + " cannot be referenced from a static context");
                    }
                }
            }
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public JExpression analyzeLhs(Context context) {
        JExpression result = analyze(context);
        if (field.isFinal()) {
            JAST.compilationUnit.reportSemanticError(line, "the field " + fieldName + " in type " +
                    target.type.toString() + " is final");
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        target.codegen(output);

        // We use a workaround for the "length" field of arrays.
        if ((target.type().isArray()) && fieldName.equals("length")) {
            output.addNoArgInstruction(ARRAYLENGTH);
        } else {
            int mnemonic = field.isStatic() ? GETSTATIC : GETFIELD;
            output.addMemberAccessInstruction(mnemonic, target.type().jvmName(), fieldName, type.toDescriptor());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output, String targetLabel, boolean onTrue) {
        codegen(output);
        output.addBranchInstruction(onTrue ? IFNE : IFEQ, targetLabel);
    }

    /**
     * {@inheritDoc}
     */
    public void codegenLoadLhsLvalue(CLEmitter output) {
        // Nothing to do for static fields.
        if (!field.isStatic()) {
            target.codegen(output);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void codegenLoadLhsRvalue(CLEmitter output) {
        if (field.isStatic()) {
            output.addMemberAccessInstruction(GETSTATIC, target.type().jvmName(), fieldName,
                    field.type().toDescriptor());
        } else {
            output.addNoArgInstruction(type == Type.STRING ? DUP_X1 : DUP);
            output.addMemberAccessInstruction(GETFIELD, target.type().jvmName(), fieldName,
                    field.type().toDescriptor());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void codegenDuplicateRvalue(CLEmitter output) {
        output.addNoArgInstruction(field.isStatic() ? DUP : DUP_X1);
    }

    /**
     * {@inheritDoc}
     */
    public void codegenStore(CLEmitter output) {
        String descriptor = field.type().toDescriptor();
        output.addMemberAccessInstruction(field.isStatic() ? PUTSTATIC : PUTFIELD, target.type().jvmName(), fieldName,
                descriptor);
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JFieldSelection:" + line, e);
        e.addAttribute("ambiguousPart", ambiguousPart == null ? "null" : ambiguousPart.toString());
        e.addAttribute("name", fieldName);
        if (target != null) {
            JSONElement e1 = new JSONElement();
            e.addChild("Target", e1);
            target.toJSON(e1);
        }
    }
}
