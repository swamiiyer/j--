package jminusminus;

import java.util.ArrayList;

import static jminusminus.CLConstants.IFEQ;
import static jminusminus.CLConstants.IFNE;
import static jminusminus.CLConstants.INVOKEINTERFACE;
import static jminusminus.CLConstants.INVOKESTATIC;
import static jminusminus.CLConstants.INVOKEVIRTUAL;
import static jminusminus.CLConstants.POP;

/**
 * The AST node for a message expression that has a target, optionally an ambiguous part, a message name, and zero or
 * more actual arguments.
 */
class JMessageExpression extends JExpression {
    // The target expression.
    private JExpression target;

    // The ambiguous part that is reclassified in analyze().
    private final AmbiguousName ambiguousPart;

    // The message name.
    private final String messageName;

    // Message arguments.
    private final ArrayList<JExpression> arguments;

    // The Method representing this message.
    private Method method;

    /**
     * Constructs an AST node for a message expression without an ambiguous part.
     *
     * @param line        line in which the expression occurs in the source file.
     * @param target      the target expression.
     * @param messageName the message name.
     * @param arguments   the arguments.
     */
    public JMessageExpression(int line, JExpression target, String messageName, ArrayList<JExpression> arguments) {
        this(line, target, null, messageName, arguments);
    }

    /**
     * Constructs an AST node for a message expression having an ambiguous part.
     *
     * @param line          line in which the expression occurs in the source file.
     * @param target        the target expression.
     * @param ambiguousPart the ambiguous part.
     * @param messageName   the message name.
     * @param arguments     the arguments.
     */
    public JMessageExpression(int line, JExpression target, AmbiguousName ambiguousPart, String messageName,
                              ArrayList<JExpression> arguments) {
        super(line);
        this.target = target;
        this.ambiguousPart = ambiguousPart;
        this.messageName = messageName;
        this.arguments = arguments;
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

        // Then analyze the arguments, collecting their types (in Class form) as argTypes.
        Type[] argTypes = new Type[arguments.size()];
        for (int i = 0; i < arguments.size(); i++) {
            arguments.set(i, arguments.get(i).analyze(context));
            argTypes[i] = arguments.get(i).type();
        }

        // Then analyze the target.
        if (target == null) {
            // Implied this (or, implied type for statics).
            if (!context.methodContext().isStatic()) {
                target = new JThis(line()).analyze(context);
            } else {
                target = new JVariable(line(), context.definingType().toString()).analyze(context);
            }
        } else {
            target = target.analyze(context);
            if (target.type().isPrimitive()) {
                JAST.compilationUnit.reportSemanticError(line(),
                        "cannot invoke a message on a primitive type: " + target.type());
            }
        }

        // Find appropriate Method for this message expression.
        method = target.type().methodFor(messageName, argTypes);
        if (method == null) {
            JAST.compilationUnit.reportSemanticError(line(),
                    "cannot find method for: " + Type.signatureFor(messageName, argTypes));
            type = Type.ANY;
        } else {
            context.definingType().checkAccess(line, method);
            type = method.returnType();

            // Non-static method cannot be referenced from a static context.
            if (!method.isStatic()) {
                if (target instanceof JVariable &&
                        ((JVariable) target).iDefn() instanceof TypeNameDefn) {
                    JAST.compilationUnit.reportSemanticError(line(),
                            "non-static method " + Type.signatureFor(messageName, argTypes) +
                                    " cannot be referenced from a static context");
                }
            }
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        if (!method.isStatic()) {
            target.codegen(output);
        }
        for (JExpression argument : arguments) {
            argument.codegen(output);
        }
        int mnemonic = method.isStatic() ? INVOKESTATIC : target.type().isInterface() ? INVOKEINTERFACE : INVOKEVIRTUAL;
        output.addMemberAccessInstruction(mnemonic, target.type().jvmName(), messageName, method.toDescriptor());
        if (isStatementExpression && type != Type.VOID) {
            // Pop any value left on the stack.
            output.addNoArgInstruction(POP);
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
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JMessageExpression:" + line, e);
        e.addAttribute("ambiguousPart", ambiguousPart == null ? "null" : ambiguousPart.toString());
        e.addAttribute("name", messageName);
        if (target != null) {
            JSONElement e1 = new JSONElement();
            e.addChild("Target", e1);
            target.toJSON(e1);
        }
        if (arguments != null) {
            for (JExpression argument : arguments) {
                JSONElement e1 = new JSONElement();
                e.addChild("Argument", e1);
                argument.toJSON(e1);
            }
        }
    }
}
