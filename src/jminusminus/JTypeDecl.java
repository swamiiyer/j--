// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

/**
 * An interface supported by class (or later, interface) declarations.
 */
interface JTypeDecl {
    /**
     * Declares this type in the parent context. Called before pre-analysis so that it is
     * available in the preAnalyze() method of other types.
     *
     * @param context the parent (compilation unit) context.
     */
    public void declareThisType(Context context);

    /**
     * Pre-analyzes the members of this declaration in the parent context.
     *
     * @param context the parent (compilation unit) context.
     */
    public void preAnalyze(Context context);

    /**
     * Returns the name of this type declaration.
     *
     * @return the name of this type declaration.
     */
    public String name();

    /**
     * Returns the type that this type declaration defines.
     *
     * @return the type defined by this type declaration.
     */
    public Type thisType();

    /**
     * Returns the super class' type.
     *
     * @return the super class' type.
     */
    public Type superType();
}
