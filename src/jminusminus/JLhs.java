package jminusminus;

/**
 * The type of any expression that can appear on the lhs of an assignment statement: JVariable, JFieldSelection, and
 * JArrayExpression.
 */
interface JLhs {
    /**
     * Analyzes the lhs of an assignment. This is very much like the analyze() methods, but perhaps a little more
     * selective here and there.
     *
     * @param context context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */
    JExpression analyzeLhs(Context context);

    /**
     * Generates code to load onto the stack any part of the lhs that must be there, as in a[i] = x.
     *
     * @param output the code emitter.
     */
    void codegenLoadLhsLvalue(CLEmitter output);

    /**
     * Generates code to load a Rvalue of the lhs, as in a += x.
     *
     * @param output the code emitter.
     */
    void codegenLoadLhsRvalue(CLEmitter output);

    /**
     * Generates the code to duplicate the Rvalue that is on the stack because it is to be used in a surrounding
     * expression, as in a[i] = x--.
     *
     * @param output the code emitter.
     */
    void codegenDuplicateRvalue(CLEmitter output);

    /**
     * Generates the code to do the actual assignment.
     *
     * @param output the code emitter.
     */
    void codegenStore(CLEmitter output);
}
