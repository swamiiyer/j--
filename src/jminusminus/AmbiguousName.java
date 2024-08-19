package jminusminus;

import java.util.StringTokenizer;

/**
 * This class is used to encapsulate ambiguous names that the parser can't distinguish and disambiguate them during
 * the analysis phase. Ambiguous names are meant to deal with snippets like x.y.z and x.y.z().
 */
class AmbiguousName {
    // Line in which the ambiguous name occurs in the source file.
    private final int line;

    // The ambiguous part, for example x.y in x.y.z.
    private final String name;

    /**
     * Constructs an encapsulation of the ambiguous portion of a snippet like x.y.z.
     *
     * @param line line in which the ambiguous name occurs in the source file.
     * @param name the ambiguous part, for example x.y in x.y.z.
     */
    public AmbiguousName(int line, String name) {
        this.line = line;
        this.name = name;
    }

    /**
     * Reclassifies the name according to the rules in the Java Language Specification, and returns an AST for it.
     *
     * @param context context in which we look up the component names.
     * @return the AST for the reclassified name.
     */
    public JExpression reclassify(Context context) {
        JExpression result;
        StringTokenizer st = new StringTokenizer(name, ".");

        // Firstly, find a variable or type.
        String newName = st.nextToken();
        Defn defn;
        do {
            defn = context.lookup(newName);
            if (defn != null) {
                result = new JVariable(line, newName);
                break;
            } else if (!st.hasMoreTokens()) {
                // Nothing found. :(
                JAST.compilationUnit.reportSemanticError(line, "cannot find name " + newName);
                return null;
            } else {
                newName += "." + st.nextToken();
            }
        } while (true);

        // For now, we can assume everything else is a field.
        while (st.hasMoreTokens()) {
            result = new JFieldSelection(line, result, st.nextToken());
        }

        return result;
    }

    /**
     * Returns the ambiguous part.
     *
     * @return the ambiguous part.
     */
    public String toString() {
        return name;
    }
}
