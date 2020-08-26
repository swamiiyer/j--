// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;

import static jminusminus.CLConstants.*;

/**
 * The AST node for an array initializer.
 */
class JArrayInitializer extends JExpression {
    // The initializations.
    private ArrayList<JExpression> initials;

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
            JAST.compilationUnit.reportSemanticError(line, "Cannot initialize a " + type.toString()
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

        // Code to push array length.
        (new JLiteralInt(line, String.valueOf(initials.size()))).codegen(output);

        // Code to create the (empty) array.
        output.addArrayInstruction(componentType.isReference() ? ANEWARRAY : NEWARRAY,
                componentType.jvmName());

        // Code to load initial values and store them as elements in the newly created array.
        for (int i = 0; i < initials.size(); i++) {
            JExpression initial = initials.get(i);

            // Duplicate the array for each element store.
            output.addNoArgInstruction(DUP);

            // Code to push index for store.
            (new JLiteralInt(line, String.valueOf(i))).codegen(output);

            // Code to compute the initial value.
            initial.codegen(output);

            // Code to store the initial value in the array.
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
