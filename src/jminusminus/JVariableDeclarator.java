// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

/**
 * The AST node for a variable declarator, which declares a name, its type and (possibly)
 * provides an initialization.
 */
class JVariableDeclarator extends JAST {
    // Variable name.
    private String name;

    // Variable type.
    private Type type;

    // Variable initializer.
    private JExpression initializer;

    /**
     * Constructs an AST node for a variable declarator.
     *
     * @param line        line in which the variable occurs in the source file.
     * @param name        variable name.
     * @param type        variable type.
     * @param initializer initializer.
     */
    public JVariableDeclarator(int line, String name, Type type, JExpression initializer) {
        super(line);
        this.name = name;
        this.type = type;
        this.initializer = initializer;
    }

    /**
     * Returns the variable name.
     *
     * @return the variable name.
     */
    public String name() {
        return name;
    }

    /**
     * Returns the variable type.
     *
     * @return the variable type.
     */
    public Type type() {
        return type;
    }

    /**
     * Sets the variable type.
     *
     * @param type the type
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Returns the variable initializer.
     *
     * @return the variable initializer.
     */
    public JExpression initializer() {
        return initializer;
    }

    /**
     * Sets the variable initializer.
     *
     * @param initializer the initializer.
     */
    public void setInitializer(JExpression initializer) {
        this.initializer = initializer;
    }

    /**
     * {@inheritDoc}
     */
    public JVariableDeclarator analyze(Context context) {
        // Nothing here.
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        // Nothing here.
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JVariableDeclarator:" + line, e);
        e.addAttribute("name", name());
        e.addAttribute("type", type == null ? "" : type.toString());
        if (initializer != null) {
            JSONElement e1 = new JSONElement();
            e.addChild("Initializer", e1);
            initializer.toJSON(e1);
        }
    }
}
