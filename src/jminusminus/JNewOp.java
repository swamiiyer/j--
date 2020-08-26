// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a new expression. It keeps track of the constructor representing the
 * expression, its arguments and their types.
 */
class JNewOp extends JExpression {
    // The constructor representing this "new" expression.
    private Constructor constructor;

    // The arguments to the constructor.
    private ArrayList<JExpression> arguments;

    // Types of the arguments.
    private Type[] argTypes;

    /**
     * Constructs an AST node for a new expression.
     *
     * @param line      the line in which the "new" expression occurs in the source file.
     * @param type      the type being constructed.
     * @param arguments arguments to the constructor.
     */
    public JNewOp(int line, Type type, ArrayList<JExpression> arguments) {
        super(line);
        super.type = type;
        this.arguments = arguments;
    }

    /**
     * {@inheritDoc}
     */
    public JExpression analyze(Context context) {
        // First resolve the type.
        type = type.resolve(context);

        // Analyze the arguments, collecting their types (in Class form) as argTypes.
        argTypes = new Type[arguments.size()];
        for (int i = 0; i < arguments.size(); i++) {
            arguments.set(i, (JExpression) arguments.get(i).analyze(context));
            argTypes[i] = arguments.get(i).type();
        }

        // Can't instantiate an abstract type.
        if (type.isAbstract()) {
            JAST.compilationUnit.reportSemanticError(line(),
                    "Cannot instantiate an abstract type: " + type.toString());
        }

        // Then get the proper constructor, given the arguments.
        constructor = type.constructorFor(argTypes);

        if (constructor == null) {
            JAST.compilationUnit.reportSemanticError(line(),
                    "Cannot find constructor: " + Type.signatureFor(type.toString(), argTypes));
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        output.addReferenceInstruction(NEW, type.jvmName());
        output.addNoArgInstruction(DUP);
        for (JExpression argument : arguments) {
            argument.codegen(output);
        }
        output.addMemberAccessInstruction(INVOKESPECIAL,
                type.jvmName(),
                "<init>",
                constructor.toDescriptor());
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JNewOp:" + line, e);
        e.addAttribute("type", type == null ? "" : type.toString());
        if (arguments != null) {
            for (JExpression argument : arguments) {
                JSONElement e1 = new JSONElement();
                e.addChild("Argument", e1);
                argument.toJSON(e1);
            }
        }
    }
}
