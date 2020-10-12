// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a message expression that has a target, optionally an ambiguous part, a
 * message name, and zero or more actual arguments.
 */
class JMessageExpression extends JExpression {
    // The target expression.
    private JExpression target;

    // The ambiguous part that is reclassfied in analyze().
    private AmbiguousName ambiguousPart;

    // The message name.
    private String messageName;

    // Message arguments.
    private ArrayList<JExpression> arguments;

    // Types of arguments.
    private Type[] argTypes;

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
    public JMessageExpression(int line, JExpression target, String messageName,
                              ArrayList<JExpression> arguments) {
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
    public JMessageExpression(int line, JExpression target, AmbiguousName ambiguousPart,
                              String messageName, ArrayList<JExpression> arguments) {
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
                    JAST.compilationUnit.reportSemanticError(line(), "Badly formed suffix");
                }
            }
        }

        // Then analyze the arguments, collecting their types (in Class form) as argTypes.
        argTypes = new Type[arguments.size()];
        for (int i = 0; i < arguments.size(); i++) {
            arguments.set(i, (JExpression) arguments.get(i).analyze(context));
            argTypes[i] = arguments.get(i).type();
        }

        // Where are we now? (For access)
        Type thisType = ((JTypeDecl) context.classContext.definition()).thisType();

        // Then analyze the target.
        if (target == null) {
            // Implied this (or, implied type for statics).
            if (!context.methodContext().isStatic()) {
                target = new JThis(line()).analyze(context);
            } else {
                target = new JVariable(line(), context.definingType().toString()).analyze(context);
            }
        } else {
            target = (JExpression) target.analyze(context);
            if (target.type().isPrimitive()) {
                JAST.compilationUnit.reportSemanticError(line(),
                        "Cannot invoke a message on a primitive type: " + target.type());
            }
        }

        // Find appropriate Method for this message expression.
        method = target.type().methodFor(messageName, argTypes);
        if (method == null) {
            JAST.compilationUnit.reportSemanticError(line(),
                    "Cannot find method for: " + Type.signatureFor(messageName, argTypes));
            type = Type.ANY;
        } else {
            context.definingType().checkAccess(line, (Member) method);
            type = method.returnType();

            // Non-static method cannot be referenced from a static context.
            if (!method.isStatic()) {
                if (target instanceof JVariable &&
                        ((JVariable) target).iDefn() instanceof TypeNameDefn) {
                    JAST.compilationUnit.reportSemanticError(line(),
                            "Non-static method " + Type.signatureFor(messageName, argTypes) +
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
        int mnemonic = method.isStatic() ? INVOKESTATIC : target.type().isInterface() ?
                INVOKEINTERFACE : INVOKEVIRTUAL;
        output.addMemberAccessInstruction(mnemonic, target.type().jvmName(), messageName,
                method.toDescriptor());
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
