// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

/**
 * The AST node for a string literal.
 */
class JLiteralString extends JExpression {
    // String representation of the literal.
    private String text;

    /**
     * Constructs an AST node for a string literal given its line number and text representation.
     *
     * @param line line in which the literal occurs in the source file.
     * @param text string representation of the literal.
     */
    public JLiteralString(int line, String text) {
        super(line);
        this.text = text;
    }

    /**
     * {@inheritDoc}
     */
    public JExpression analyze(Context context) {
        type = Type.STRING;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        // Unescape the escaped escapes.
        String s = JAST.unescape(text);

        // The string representation is padded (by hand-written and JavaCC scanner) with double
        // quotes, so we substring.
        String literal = s.substring(1, s.length() - 1);
        output.addLDCInstruction(literal);
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JLiteralString:" + line, e);
        e.addAttribute("type", type == null ? "" : type.toString());
        e.addAttribute("value", text.substring(1, text.length() - 1));
    }
}
