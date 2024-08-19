package jminusminus;

import java.util.ArrayList;

import static jminusminus.CLConstants.AASTORE;
import static jminusminus.CLConstants.ANEWARRAY;
import static jminusminus.CLConstants.BASTORE;
import static jminusminus.CLConstants.CASTORE;
import static jminusminus.CLConstants.DUP;
import static jminusminus.CLConstants.IASTORE;
import static jminusminus.CLConstants.NEWARRAY;

/**
 * The AST node for an array initializer.
 */
class JArrayInitializer extends JExpression {
    // The initializations.
    private final ArrayList<JExpression> initials;

    /**
     * Constructs an AST node for an array initializer.
     *
     * @param line     line in which this array initializer occurs in the source file.
     * @param type     the type of the array we're initializing.
     * @param initials initializations.
     */
    public JArrayInitializer(int line, Type type, ArrayList<JExpression> initials) {
        super(line);
        this.type = type;
        this.initials = initials;
    }

    /**
     * {@inheritDoc}
     */
    public JExpression analyze(Context context) {
        type = type.resolve(context);
        if (!type.isArray()) {
            JAST.compilationUnit.reportSemanticError(line, "cannot initialize a " + type.toString()
                    + " with an array sequence {...}");
            return this;
        }
        Type componentType = type.componentType();
        for (int i = 0; i < initials.size(); i++) {
            JExpression initial = initials.get(i);
            initials.set(i, initial = initial.analyze(context));
            if (!(initial instanceof JArrayInitializer)) {
                initial.type().mustMatchExpected(line, componentType);
            }
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        Type componentType = type.componentType();

        // Push array length.
        (new JLiteralInt(line, String.valueOf(initials.size()))).codegen(output);

        // Create the (empty) array.
        output.addArrayInstruction(componentType.isReference() ? ANEWARRAY : NEWARRAY, componentType.jvmName());

        // Load initial values and store them as elements in the newly created array.
        for (int i = 0; i < initials.size(); i++) {
            JExpression initial = initials.get(i);

            // Duplicate the array for each element store.
            output.addNoArgInstruction(DUP);

            // Push index for store.
            (new JLiteralInt(line, String.valueOf(i))).codegen(output);

            // Compute the initial value.
            initial.codegen(output);

            // Store the initial value in the array.
            if (componentType == Type.INT) {
                output.addNoArgInstruction(IASTORE);
            } else if (componentType == Type.BOOLEAN) {
                output.addNoArgInstruction(BASTORE);
            } else if (componentType == Type.CHAR) {
                output.addNoArgInstruction(CASTORE);
            } else if (!componentType.isPrimitive()) {
                output.addNoArgInstruction(AASTORE);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JArrayInitializer:" + line, e);
        if (initials != null) {
            for (JExpression initial : initials) {
                initial.toJSON(e);
            }
        }
    }
}
