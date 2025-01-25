package jminusminus;

import java.util.Stack;

/**
 * An interface supported by all class (or later, interface) members.
 */
interface JMember {
    /**
     * Used to identify the enclosing control-flow statement of a break/continue statement.
     */
    static Stack<JStatement> enclosingStatement = new Stack<JStatement>();


    /**
     * Declares the member names in the specified (class) context and generates the member headers in the partial class.
     *
     * @param context class context in which names are resolved.
     * @param partial the code emitter.
     */
    void preAnalyze(Context context, CLEmitter partial);
}
