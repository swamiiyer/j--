package jminusminus;

import java.util.ArrayList;

import static jminusminus.CLConstants.ALOAD_0;
import static jminusminus.CLConstants.INVOKESPECIAL;
import static jminusminus.CLConstants.RETURN;

/**
 * The AST node for a constructor declaration.
 */
class JConstructorDeclaration extends JMethodDeclaration {
    // Does this constructor invoke this(...) or super(...)?
    private boolean invokesConstructor;

    // Defining class
    private JClassDeclaration definingClass;

    /**
     * Constructs an AST node for a constructor declaration.
     *
     * @param line       line in which the constructor declaration occurs in the source file.
     * @param mods       modifiers.
     * @param name       constructor name.
     * @param params     the formal parameters.
     * @param exceptions exceptions thrown.
     * @param body       constructor body.
     */
    public JConstructorDeclaration(int line, ArrayList<String> mods, String name, ArrayList<JFormalParameter> params,
                                   ArrayList<TypeName> exceptions, JBlock body) {
        super(line, mods, name, Type.CONSTRUCTOR, params, exceptions, body);
    }

    /**
     * {@inheritDoc}
     */
    public void preAnalyze(Context context, CLEmitter partial) {
        super.preAnalyze(context, partial);
        if (isStatic) {
            JAST.compilationUnit.reportSemanticError(line(), "constructor cannot be static");
        } else if (isAbstract) {
            JAST.compilationUnit.reportSemanticError(line(), "constructor cannot be abstract");
        }
        if (!body.statements().isEmpty() && body.statements().get(0) instanceof JStatementExpression) {
            JStatementExpression first = (JStatementExpression) body.statements().get(0);
            if (first.expr instanceof JSuperConstruction) {
                ((JSuperConstruction) first.expr).markProperUseOfConstructor();
                invokesConstructor = true;
            } else if (first.expr instanceof JThisConstruction) {
                ((JThisConstruction) first.expr).markProperUseOfConstructor();
                invokesConstructor = true;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public JAST analyze(Context context) {
        definingClass = (JClassDeclaration) (context.classContext().definition());
        this.context = new MethodContext(context, isStatic, returnType);

        // Offset 0 is used to address "this".
        this.context.nextOffset();

        // Declare the parameters. We consider a formal parameter to be always initialized, via a method call.
        for (JFormalParameter param : params) {
            LocalVariableDefn defn = new LocalVariableDefn(param.type(), this.context.nextOffset());
            defn.initialize();
            this.context.addEntry(param.line(), param.name(), defn);
        }

        if (body != null) {
            body = body.analyze(this.context);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void partialCodegen(Context context, CLEmitter partial) {
        if (partial.containsMethodSignature(signature)) {
            JAST.compilationUnit.reportSemanticError(line(), "redefining constructor " + signature);
            return;
        }
        partial.addMethod(mods, "<init>", descriptor, null, false);
        partial.addMethodSignature(signature);
        if (!invokesConstructor) {
            partial.addNoArgInstruction(ALOAD_0);
            partial.addMemberAccessInstruction(INVOKESPECIAL,
                    ((JClassDeclaration) context.classContext().definition()).superType().jvmName(), "<init>", "()V");
        }
        partial.addNoArgInstruction(RETURN);
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        output.addMethod(mods, "<init>", descriptor, null, false);
        if (!invokesConstructor) {
            output.addNoArgInstruction(ALOAD_0);
            output.addMemberAccessInstruction(INVOKESPECIAL, definingClass.superType().jvmName(), "<init>", "()V");
        }
        for (JFieldDeclaration field : definingClass.instanceFieldInitializations()) {
            field.codegenInitializations(output);
        }
        body.codegen(output);
        output.addNoArgInstruction(RETURN);
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JConstructorDeclaration:" + line, e);
        if (mods != null) {
            ArrayList<String> value = new ArrayList<>();
            for (String mod : mods) {
                value.add(String.format("\"%s\"", mod));
            }
            e.addAttribute("modifiers", value);
        }
        e.addAttribute("name", name);
        if (params != null) {
            ArrayList<String> value = new ArrayList<>();
            for (JFormalParameter param : params) {
                value.add(String.format("[\"%s\", \"%s\"]", param.name(),
                        param.type() == null ? "" : param.type().toString()));
            }
            e.addAttribute("parameters", value);
        }
        if (exceptions != null) {
            ArrayList<String> value = new ArrayList<>();
            for (TypeName exception : exceptions) {
                value.add(String.format("\"%s\"", exception.toString()));
            }
            e.addAttribute("throws", value);
        }
        if (context != null) {
            context.toJSON(e);
        }
        if (body != null) {
            body.toJSON(e);
        }
    }
}
