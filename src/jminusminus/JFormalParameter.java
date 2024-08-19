package jminusminus;

/**
 * The AST node for a formal parameter declaration.
 */
class JFormalParameter extends JAST {
    // Parameter name.
    private final String name;

    // Parameter type.
    private Type type;

    /**
     * Constructs an AST node for a formal parameter declaration.
     *
     * @param line line in which the parameter occurs in the source file.
     * @param name parameter name.
     * @param type parameter type.
     */
    public JFormalParameter(int line, String name, Type type) {
        super(line);
        this.name = name;
        this.type = type;
    }

    /**
     * Returns the parameter's name.
     *
     * @return the parameter's name.
     */
    public String name() {
        return name;
    }

    /**
     * Returns the parameter's type.
     *
     * @return the parameter's type.
     */
    public Type type() {
        return type;
    }

    /**
     * Sets the parameter's type to the specified type.
     *
     * @param type the new type.
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    public JAST analyze(Context context) {
        // Nothing here.
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        // Nothing here.
    }
}
