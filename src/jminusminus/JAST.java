// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;

/**
 * JAST is the abstract superclass of all nodes in the abstract syntax tree (AST).
 */
abstract class JAST {
    /**
     * Current compilation unit (set in JCompilationUnit()).
     */
    public static JCompilationUnit compilationUnit;

    /**
     * Line in which the source for the AST was found.
     */
    protected int line;

    /**
     * Constructs an AST node the given its line number in the source file.
     *
     * @param line line in which the source for the AST was found.
     */
    protected JAST(int line) {
        this.line = line;
    }

    /**
     * Returns the line in which the source for the AST was found.
     *
     * @return the line in which the source for the AST was found.
     */
    public int line() {
        return line;
    }

    /**
     * Performs semantic analysis on this AST and returns the (possibly modified) AST.
     *
     * @param context the environment (scope) in which code is analyzed.
     * @return the (possibly modified) AST.
     */
    public abstract JAST analyze(Context context);

    /**
     * Generates a partial class for a type, reflecting only the member information required to
     * do analysis.
     *
     * @param context the parent (class) context.
     * @param partial the code emitter.
     */
    public void partialCodegen(Context context, CLEmitter partial) {
        // A dummy -- redefined where necessary.
    }

    /**
     * Performs code generation for this AST.
     *
     * @param output the code emitter.
     */
    public abstract void codegen(CLEmitter output);

    /**
     * Stores information about this AST in JSON format.
     *
     * @param json the JSON emitter.
     */
    public void toJSON(JSONElement json) {
        // Nothing here.
    }

    /**
     * Unescapes the escaped characters in the specified string and returns the unescaped string.
     *
     * @param s string to unescape.
     * @return the unescaped string.
     */
    public static String unescape(String s) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\') {
                i++;
                if (i >= s.length()) {
                    break;
                }
                c = s.charAt(i);
                switch (c) {
                    case 'b':
                        b.append('\b');
                        break;
                    case 't':
                        b.append('\t');
                        break;
                    case 'n':
                        b.append('\n');
                        break;
                    case 'f':
                        b.append('\f');
                        break;
                    case 'r':
                        b.append('\r');
                        break;
                    case '"':
                        b.append('"');
                        break;
                    case '\'':
                        b.append('\'');
                        break;
                    case '\\':
                        b.append('\\');
                        break;
                }
            } else {
                b.append(c);
            }
        }
        return b.toString();
    }
}

/**
 * Representation of an element with a JSON document.
 */
class JSONElement {
    // List of attribute names.
    private ArrayList<String> attrNames;

    // List of attribute values.
    private ArrayList<String> attrValues;

    // List of children names.
    private ArrayList<String> childrenNames;

    // List of children.
    private ArrayList<JSONElement> children;

    // Indentation level.
    private int indentation;

    /**
     * Constructs an empty JSON element.
     */
    public JSONElement() {
        this.attrNames = new ArrayList<String>();
        this.attrValues = new ArrayList<String>();
        this.childrenNames = new ArrayList<String>();
        this.children = new ArrayList<JSONElement>();
        indentation = 0;
    }

    /**
     * Adds an attribute to this JSON element with the given name and value.
     *
     * @param name  name of the attribute.
     * @param value value of the attribute.
     */
    public void addAttribute(String name, String value) {
        attrNames.add(name);
        attrValues.add(value);
    }

    /**
     * Adds an attribute to this JSON element with the given name and value as a list of strings.
     *
     * @param name  name of the attribute.
     * @param value value of the attribute as a list of strings.
     */
    public void addAttribute(String name, ArrayList<String> value) {
        attrNames.add(name);
        attrValues.add(value.toString());
    }

    /**
     * Adds a child to this JSON element with the given name.
     *
     * @param name  name of the child.
     * @param child the child.
     */
    public void addChild(String name, JSONElement child) {
        child.indentation = indentation + 4;
        childrenNames.add(name);
        children.add(child);
    }

    /**
     * Returns a string representation of this JSON element.
     *
     * @return a string representation of this JSON element.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (indentation > 0) {
            sb.append(String.format("%" + indentation + "s", " "));
        }
        sb.append("{\n");
        for (int i = 0; i < attrNames.size(); i++) {
            String name = attrNames.get(i);
            String value = attrValues.get(i);
            sb.append(String.format("%" + (indentation + 4) + "s", " "));
            if (value.startsWith("[") && value.endsWith("]")) {
                sb.append(String.format("\"%s\": %s", name, value));
            } else {
                sb.append(String.format("\"%s\": \"%s\"", name, value));
            }
            if (i < attrNames.size() - 1 || childrenNames.size() > 0) {
                sb.append(",\n");
            } else {
                sb.append("\n");
            }
        }
        for (int i = 0; i < childrenNames.size(); i++) {
            String name = childrenNames.get(i);
            JSONElement child = children.get(i);
            sb.append(String.format("%" + (indentation + 4) + "s", " "));
            sb.append(String.format("\"%s\":\n", name));
            sb.append(child.toString());
            if (i < childrenNames.size() - 1) {
                sb.append(",\n");
            } else {
                sb.append("\n");
            }
        }
        if (indentation > 0) {
            sb.append(String.format("%" + indentation + "s", " "));
        }
        sb.append("}");
        return sb.toString();
    }
}
