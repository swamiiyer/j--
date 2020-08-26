// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

/**
 * An interface supported by all class (or later, interface) members.
 */
interface JMember {
    /**
     * Declares the member names in the specified (class) context and generates the member headers
     * in the partial class.
     *
     * @param context class context in which names are resolved.
     * @param partial the code emitter.
     */
    public void preAnalyze(Context context, CLEmitter partial);
}
