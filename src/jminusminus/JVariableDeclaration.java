// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;

/**
 * The AST node for a local variable declaration. Local variables are declared by its analyze()
 * method, which also re-writes any initializations as assignment statements, in turn generated
 * by its codegen() method.
 */
class JVariableDeclaration extends JStatement {
    // Variable declarators.
    private ArrayList<JVariableDeclarator> decls;

    // Variable initializers.
    private ArrayList<JStatement> initializations;

    /**
     * Constructs an AST node for a variable declaration.
     *
     * @param line  line in which the variable declaration occurs in the source file.
     * @param decls variable declarators.
     */
    public JVariableDeclaration(int line, ArrayList<JVariableDeclarator> decls) {
        super(line);
        this.decls = decls;
        initializations = new ArrayList<JStatement>();
    }

    /**
     * {@inheritDoc}
     */
    public JStatement analyze(Context context) {
        for (JVariableDeclarator decl : decls) {
            // Local variables are declared here (fields are declared in preAnalyze()).
            int offset = ((LocalContext) context).nextOffset();
            LocalVariableDefn defn = new LocalVariableDefn(decl.type().resolve(context), offset);

            // First, check for shadowing.
            IDefn previousDefn = context.lookup(decl.name());
            if (previousDefn != null && previousDefn instanceof LocalVariableDefn) {
                JAST.compilationUnit.reportSemanticError(decl.line(),
                        "The name " + decl.name() + " overshadows another local variable");
            }

            // Then declare it in the local context.
            context.addEntry(decl.line(), decl.name(), defn);

            // All initializations must be turned into assignment statements and analyzed.
            if (decl.initializer() != null) {
                defn.initialize();
                JAssignOp assignOp = new JAssignOp(decl.line(), new JVariable(decl.line(),
                        decl.name()), decl.initializer());
                assignOp.isStatementExpression = true;
                initializations.add(new JStatementExpression(decl.line(),
                        assignOp).analyze(context));
            }
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        for (JStatement initialization : initializations) {
            initialization.codegen(output);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JVariableDeclaration:" + line, e);
        if (decls != null) {
            for (JVariableDeclarator decl : decls) {
                decl.toJSON(e);
            }
        }
    }
}
