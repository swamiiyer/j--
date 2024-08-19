package jminusminus;

import java.util.ArrayList;

/**
 * The AST node for a field declaration.
 */
class JFieldDeclaration extends JAST implements JMember {
    // Field modifiers.
    private final ArrayList<String> mods;

    // Variable declarators.
    private final ArrayList<JVariableDeclarator> decls;

    // Variable initializations.
    private final ArrayList<JStatement> initializations;

    /**
     * Constructs an AST node for a field declaration.
     *
     * @param line  line in which the variable declaration occurs in the source file.
     * @param mods  field modifiers.
     * @param decls variable declarators.
     */
    public JFieldDeclaration(int line, ArrayList<String> mods, ArrayList<JVariableDeclarator> decls) {
        super(line);
        this.mods = mods;
        this.decls = decls;
        initializations = new ArrayList<>();
    }

    /**
     * Returns the list of modifiers.
     *
     * @return the list of modifiers.
     */
    public ArrayList<String> mods() {
        return mods;
    }

    /**
     * {@inheritDoc}
     */
    public void preAnalyze(Context context, CLEmitter partial) {
        if (mods.contains("abstract")) {
            JAST.compilationUnit.reportSemanticError(line(), "field cannot be declared abstract");
        }
        for (JVariableDeclarator decl : decls) {
            decl.setType(decl.type().resolve(context));
            if (partial.containsFieldName(decl.name())) {
                JAST.compilationUnit.reportSemanticError(line(), "redefining field " + decl.name());
            } else {
                partial.addField(mods, decl.name(), decl.type().toDescriptor(), false);
                partial.addFieldName(decl.name());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public JFieldDeclaration analyze(Context context) {
        for (JVariableDeclarator decl : decls) {
            if (decl.initializer() != null) {
                JAssignOp assignOp = new JAssignOp(decl.line(), new JVariable(decl.line(), decl.name()),
                        decl.initializer());
                assignOp.isStatementExpression = true;
                initializations.add(new JStatementExpression(decl.line(), assignOp).analyze(context));
            }
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        for (JVariableDeclarator decl : decls) {
            output.addField(mods, decl.name(), decl.type().toDescriptor(), false);
        }
    }

    /**
     * Generates code for the field initializations.
     *
     * @param output the code emitter.
     */
    public void codegenInitializations(CLEmitter output) {
        for (JStatement initialization : initializations) {
            initialization.codegen(output);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JFieldDeclaration", e);
        e.addAttribute("line", String.valueOf(line()));
        if (mods != null) {
            ArrayList<String> value = new ArrayList<>();
            for (String mod : mods) {
                value.add(String.format("\"%s\"", mod));
            }
            e.addAttribute("modifiers", value);
        }
        if (decls != null) {
            JSONElement e1 = new JSONElement();
            e.addChild("VariableDeclarators", e1);
            for (JVariableDeclarator decl : decls) {
                decl.toJSON(e1);
            }
        }
    }
}
