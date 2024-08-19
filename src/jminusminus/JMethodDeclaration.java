package jminusminus;

import java.util.ArrayList;

import static jminusminus.CLConstants.ACONST_NULL;
import static jminusminus.CLConstants.ARETURN;
import static jminusminus.CLConstants.ICONST_0;
import static jminusminus.CLConstants.IRETURN;
import static jminusminus.CLConstants.RETURN;

/**
 * The AST node for a method declaration.
 */
class JMethodDeclaration extends JAST implements JMember {
    /**
     * Method modifiers.
     */
    protected ArrayList<String> mods;

    /**
     * Method name.
     */
    protected String name;

    /**
     * Return type.
     */
    protected Type returnType;

    /**
     * The formal parameters.
     */
    protected ArrayList<JFormalParameter> params;

    /**
     * Exceptions thrown.
     */
    protected ArrayList<TypeName> exceptions;

    /**
     * Method body.
     */
    protected JBlock body;

    /**
     * Method context (built in analyze()).
     */
    protected MethodContext context;

    /**
     * Method descriptor (computed in preAnalyze()).
     */
    protected String descriptor;

    /**
     * Method signature (also computed in preAnalyze()).
     */
    protected String signature;

    /**
     * Is this method abstract?
     */
    protected boolean isAbstract;

    /**
     * Is this method static?
     */
    protected boolean isStatic;

    /**
     * Is this method private?
     */
    protected boolean isPrivate;

    /**
     * Constructs an AST node for a method declaration.
     *
     * @param line       line in which the method declaration occurs in the source file.
     * @param mods       modifiers.
     * @param name       method name.
     * @param returnType return type.
     * @param params     the formal parameters.
     * @param exceptions exceptions thrown.
     * @param body       method body.
     */
    public JMethodDeclaration(int line, ArrayList<String> mods, String name, Type returnType,
                              ArrayList<JFormalParameter> params, ArrayList<TypeName> exceptions, JBlock body) {
        super(line);
        this.mods = mods;
        this.name = name;
        this.returnType = returnType;
        this.params = params;
        this.exceptions = exceptions;
        this.body = body;
        isAbstract = mods.contains("abstract");
        isStatic = mods.contains("static");
        isPrivate = mods.contains("private");
    }

    /**
     * {@inheritDoc}
     */
    public void preAnalyze(Context context, CLEmitter partial) {
        // Resolve types of the formal parameters, the return type, and compute method descriptor/signature.
        descriptor = "(";
        Type[] argTypes = new Type[params.size()];
        for (int i = 0; i < params.size(); i++) {
            JFormalParameter param = params.get(i);
            param.setType(param.type().resolve(context));
            descriptor += param.type().toDescriptor();
            argTypes[i] = param.type();
        }
        returnType = returnType.resolve(context);
        descriptor += ")" + returnType.toDescriptor();
        signature = Type.signatureFor(name, argTypes);

        // Check proper local use of abstract.
        if (isAbstract && body != null) {
            JAST.compilationUnit.reportSemanticError(line(), "abstract method cannot have a body");
        } else if (body == null && !isAbstract) {
            JAST.compilationUnit.reportSemanticError(line(), "method without body must be abstract");
        } else if (isAbstract && isPrivate) {
            JAST.compilationUnit.reportSemanticError(line(), "private method cannot be abstract");
        } else if (isAbstract && isStatic) {
            JAST.compilationUnit.reportSemanticError(line(), "static method cannot be abstract");
        }

        // Generate the method with an empty body (for now).
        partialCodegen(context, partial);
    }

    /**
     * {@inheritDoc}
     */
    public JAST analyze(Context context) {
        MethodContext methodContext = new MethodContext(context, isStatic, returnType);
        this.context = methodContext;

        if (!isStatic) {
            // Offset 0 is used to address "this".
            this.context.nextOffset();
        }

        // Declare the parameters. We consider a formal parameter to be always initialized, via a method call.
        for (JFormalParameter param : params) {
            LocalVariableDefn defn = new LocalVariableDefn(param.type(), this.context.nextOffset());
            defn.initialize();
            this.context.addEntry(param.line(), param.name(), defn);
        }

        if (body != null) {
            body = body.analyze(this.context);
            if (returnType != Type.VOID && !methodContext.methodHasReturn()) {
                JAST.compilationUnit.reportSemanticError(line(), "non-void method must have a return statement");
            }
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void partialCodegen(Context context, CLEmitter partial) {
        if (partial.containsMethodSignature(signature)) {
            JAST.compilationUnit.reportSemanticError(line(), "redefining method " + signature);
            return;
        }
        partial.addMethod(mods, name, descriptor, null, false);
        partial.addMethodSignature(signature);
        if (returnType == Type.VOID) {
            partial.addNoArgInstruction(RETURN);
        } else if (returnType == Type.INT || returnType == Type.BOOLEAN || returnType == Type.CHAR) {
            partial.addNoArgInstruction(ICONST_0);
            partial.addNoArgInstruction(IRETURN);
        } else {
            partial.addNoArgInstruction(ACONST_NULL);
            partial.addNoArgInstruction(ARETURN);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        output.addMethod(mods, name, descriptor, null, false);
        if (body != null) {
            body.codegen(output);
        }
        if (returnType == Type.VOID) {
            output.addNoArgInstruction(RETURN);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JMethodDeclaration:" + line, e);
        if (mods != null) {
            ArrayList<String> value = new ArrayList<>();
            for (String mod : mods) {
                value.add(String.format("\"%s\"", mod));
            }
            e.addAttribute("modifiers", value);
        }
        e.addAttribute("returnType", returnType.toString());
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
