package jminusminus;

import java.util.ArrayList;

import static jminusminus.CLConstants.ALOAD_0;
import static jminusminus.CLConstants.INVOKESPECIAL;

/**
 * The AST node for a this(...) constructor.
 */
class JThisConstruction extends JExpression {
    // Arguments to the constructor.
    private final ArrayList<JExpression> arguments;

    // Constructor representation of the constructor.
    private Constructor constructor;

    // Whether this constructor is used properly, ie, as the first statement within a constructor.
    private boolean properUseOfConstructor;

    /**
     * Constructs the AST node for a this(...) constructor.
     *
     * @param line      line in which the constructor occurs in the source file.
     * @param arguments the constructor's arguments.
     */
    protected JThisConstruction(int line, ArrayList<JExpression> arguments) {
        super(line);
        this.arguments = arguments;
        properUseOfConstructor = false;
    }

    /**
     * Marks this(...) as being properly placed, ie, as the first statement in its body.
     */
    public void markProperUseOfConstructor() {
        properUseOfConstructor = true;
    }

    /**
     * {@inheritDoc}
     */
    public JExpression analyze(Context context) {
        type = Type.VOID;

        // Analyze the arguments, collecting their types (in Class form) as argTypes.
        Type[] argTypes = new Type[arguments.size()];
        for (int i = 0; i < arguments.size(); i++) {
            arguments.set(i, arguments.get(i).analyze(context));
            argTypes[i] = arguments.get(i).type();
        }

        if (!properUseOfConstructor) {
            JAST.compilationUnit.reportSemanticError(line(), "this" + Type.argTypesAsString(argTypes)
                    + " must be first statement in the constructor's body");
            return this;
        }

        // Get the constructor this(...) refers to..
        constructor = ((JTypeDecl) context.classContext.definition()).thisType().constructorFor(argTypes);

        if (constructor == null) {
            JAST.compilationUnit.reportSemanticError(line(),
                    "no such constructor: this" + Type.argTypesAsString(argTypes));

        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        output.addNoArgInstruction(ALOAD_0); // this
        for (JExpression argument : arguments) {
            argument.codegen(output);
        }
        output.addMemberAccessInstruction(INVOKESPECIAL, constructor.declaringType().jvmName(), "<init>",
                constructor.toDescriptor());
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JThisConstruction:" + line, e);
        if (arguments != null) {
            for (JExpression argument : arguments) {
                JSONElement e1 = new JSONElement();
                e.addChild("Argument", e1);
                argument.toJSON(e1);
            }
        }
    }
}
