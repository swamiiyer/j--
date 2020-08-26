// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A Context encapsulates the environment in which an AST is analyzed. It represents a scope; the
 * scope of a variable is captured by its context. It's the symbol table.
 * <p>
 * Because scopes are lexically nested in Java (and so in j--), the environment can be seen as a
 * stack of contexts, each of which is a mapping from names to their definitions (IDefns). A
 * Context keeps track of its (most closely) surrounding context, its surrounding class  context,
 * and its surrounding compilation unit context, and as well as a map from names to definitions
 * in the level of scope that the Context represents. Contexts are created for the compilation
 * unit (a CompilationUnitContext), a class (a ClassContext), each method (a MethodContext), and
 * each block (a LocalContext). If we were to add the for-statement to j--, we would necessarily
 * create a (local) context.
 * <p>
 * From the outside, the structure looks like a tree strung over the AST. But from any location
 * on the AST, that is from any point along a particular branch, it looks like a stack of context
 * objects leading back to the root of the AST, that is, back to the JCompilationUnit object at
 * the root.
 * <p>
 * Part of this structure is built during pre-analysis; pre-analysis reaches only into the type
 * (for example a class) declaration for typing the members; pre-analysis does not reach into the
 * method bodies. The rest of it is built during analysis.
 */
class Context {
    /**
     * The surrounding context (scope).
     */
    protected Context surroundingContext;

    /**
     * The surrounding class context.
     */
    protected ClassContext classContext;

    /**
     * The compilation unit context (for the whole source program or file).
     */
    protected CompilationUnitContext compilationUnitContext;

    /**
     * Map of (local variable, formal parameters, type) names to their definitions.
     */
    protected Map<String, IDefn> entries;

    /**
     * Constructs a Context.
     *
     * @param surrounding            the surrounding context (scope).
     * @param classContext           the surrounding class context.
     * @param compilationUnitContext the compilation unit context (for the whole source program or
     *                               file).
     */
    protected Context(Context surrounding, ClassContext classContext,
                      CompilationUnitContext compilationUnitContext) {
        this.surroundingContext = surrounding;
        this.classContext = classContext;
        this.compilationUnitContext = compilationUnitContext;
        this.entries = new HashMap<String, IDefn>();
    }

    /**
     * Adds an entry to the symbol table, binding a name to its definition in the current context.
     *
     * @param line       the line number of the entry.
     * @param name       the name being declared.
     * @param definition and its definition.
     */
    public void addEntry(int line, String name, IDefn definition) {
        if (entries.containsKey(name)) {
            JAST.compilationUnit.reportSemanticError(line, "redefining name: " + name);
        } else {
            entries.put(name, definition);
        }
    }

    /**
     * Returns the definition for a name in the current (or surrounding) context, or null.
     *
     * @param name the name whose definition we're looking for.
     * @return the definition for a name in the current (or surrounding) context, or null.
     */
    public IDefn lookup(String name) {
        IDefn iDefn = (IDefn) entries.get(name);
        return iDefn != null ?
                iDefn : surroundingContext != null ? surroundingContext.lookup(name) : null;
    }

    /**
     * Returns the definition for a type name in the compilation unit context, or null.
     *
     * @param name the name of the type whose definition we're looking for.
     * @return the definition for a type name in the compilation unit context, or null.
     */
    public Type lookupType(String name) {
        TypeNameDefn defn = (TypeNameDefn) compilationUnitContext.lookup(name);
        return defn == null ? null : defn.type();
    }

    /**
     * Adds the given type to the compilation unit context.
     *
     * @param line line number of type declaration.
     * @param type the type we are declaring.
     */
    public void addType(int line, Type type) {
        IDefn iDefn = new TypeNameDefn(type);
        compilationUnitContext.addEntry(line, type.simpleName(), iDefn);
        if (!type.toString().equals(type.simpleName())) {
            compilationUnitContext.addEntry(line, type.toString(), iDefn);
        }
    }

    /**
     * Returns the type that defines this context (used principally for checking accessibility).
     *
     * @return the type that defines this context.
     */
    public Type definingType() {
        return ((JTypeDecl) classContext.definition()).thisType();
    }

    /**
     * Returns the surrounding context (scope) in the stack of contexts.
     *
     * @return the surrounding context.
     */
    public Context surroundingContext() {
        return surroundingContext;
    }

    /**
     * Returns the surrounding class context.
     *
     * @return the surrounding class context.
     */
    public ClassContext classContext() {
        return classContext;
    }

    /**
     * Returns the surrounding compilation unit context. This is where imported types and other
     * types defined in the compilation unit are declared.
     *
     * @return the compilation unit context.
     */
    public CompilationUnitContext compilationUnitContext() {
        return compilationUnitContext;
    }

    /**
     * Returns the closest surrounding method context, or null (if we are not within a method).
     *
     * @return the closest surrounding method context, or null.
     */
    public MethodContext methodContext() {
        Context context = this;
        while (context != null && !(context instanceof MethodContext)) {
            context = context.surroundingContext();
        }
        return (MethodContext) context;
    }

    /**
     * Returns a set containing the names declared in this context.
     *
     * @return a set containing the names declared in this context.
     */
    public Set<String> names() {
        return entries.keySet();
    }

    /**
     * Adds information pertaining to this context to the given JSON element.
     *
     * @param json JSON element.
     */
    public void toJSON(JSONElement json) {
        // Nothing here.
    }
}

/**
 * The compilation unit context is always the outermost context and is where imported types and
 * locally defined types (classes) are declared.
 */
class CompilationUnitContext extends Context {
    /**
     * Constructs a new compilation unit context.
     */
    public CompilationUnitContext() {
        super(null, null, null);
        compilationUnitContext = this;
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("CompilationUnitContext", e);
        if (entries != null) {
            ArrayList<String> value = new ArrayList<String>();
            for (String name : names()) {
                value.add(String.format("\"%s\"", name));
            }
            e.addAttribute("entries", value);
        }
    }
}

/**
 * Represents the context (scope, environment, symbol table) for a type, for example a class, in
 * j--. It also keeps track of its surrounding context(s) and the type whose context it represents.
 */
class ClassContext extends Context {
    /**
     * AST node of the type that this class represents.
     */
    private JAST definition;

    /**
     * Constructs a class context.
     *
     * @param definition  the AST node of the type that this class represents.
     * @param surrounding the surrounding context(s).
     */
    public ClassContext(JAST definition, Context surrounding) {
        super(surrounding, null, surrounding.compilationUnitContext());
        classContext = this;
        this.definition = definition;
    }

    /**
     * Returns the AST node of the type defined by this class.
     *
     * @return the AST of the type defined by this class.
     */
    public JAST definition() {
        return definition;
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("ClassContext", e);
    }
}

/**
 * A local context is a context (scope) in which local variables (including formal parameters)
 * can be declared. Local variables are allocated at fixed offsets from the base of the current
 * method's stack frame; this is done during analysis. The definitions for local variables record
 * these offsets. The offsets are used in code generation.
 */
class LocalContext extends Context {
    /**
     * Next offset for a local variable.
     */
    protected int offset;

    /**
     * Constructs a local context. A local context is constructed for each block.
     *
     * @param surrounding the surrounding context.
     */
    public LocalContext(Context surrounding) {
        super(surrounding, surrounding.classContext(), surrounding.compilationUnitContext());
        offset = (surrounding instanceof LocalContext) ? ((LocalContext) surrounding).offset() : 0;
    }

    /**
     * Returns the "next" offset. Not to be used for allocating new offsets (the nextOffset()
     * method is used for that).
     *
     * @return the next available offset.
     */
    public int offset() {
        return offset;
    }

    /**
     * Allocates and returns a new offset (eg, for a parameter or local variable).
     *
     * @return the next allocated offset.
     */
    public int nextOffset() {
        return offset++;
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("LocalContext", e);
        if (entries != null) {
            ArrayList<String> value = new ArrayList<String>();
            for (String name : names()) {
                IDefn defn = entries.get(name);
                if (defn instanceof LocalVariableDefn) {
                    int offset = ((LocalVariableDefn) defn).offset();
                    value.add(String.format("[\"%s\", \"%s\"]", name, offset));
                }
            }
            e.addAttribute("entries", value);
        }
    }
}

/**
 * A method context is where formal parameters are declared. Also, it's where we start computing
 * the offsets for local variables (formal parameters included), which are allocated in the
 * current stack frame (for a method invocation).
 */
class MethodContext extends LocalContext {
    /**
     * Is this method static?
     */
    private boolean isStatic;

    /**
     * Return type of this method.
     */
    private Type methodReturnType;

    /**
     * Does (non-void) method have at least one return?
     */
    private boolean hasReturnStatement;

    /**
     * Constructs a method context.
     *
     * @param surrounding      the surrounding (class) context.
     * @param isStatic         is this method static?
     * @param methodReturnType return type of this method.
     */
    public MethodContext(Context surrounding, boolean isStatic, Type methodReturnType) {
        super(surrounding);
        super.offset = 0;
        this.isStatic = isStatic;
        this.methodReturnType = methodReturnType;
        hasReturnStatement = false;
    }

    /**
     * Returns true if this is a static method, and false otherwise.
     *
     * @return true if this is a static method, and false otherwise.
     */
    public boolean isStatic() {
        return isStatic;
    }

    /**
     * Records fact that (non-void) method has at least one return.
     */
    public void confirmMethodHasReturn() {
        hasReturnStatement = true;
    }

    /**
     * Returns true if this (non-void) method has at least one return, and false otherwise.
     *
     * @return true if this (non-void) method has at least one return, and false otherwise.
     */
    public boolean methodHasReturn() {
        return hasReturnStatement;
    }

    /**
     * Returns the return type of this method.
     *
     * @return the return type of this method.
     */
    public Type methodReturnType() {
        return methodReturnType;
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("MethodContext", e);
        if (entries != null) {
            ArrayList<String> value = new ArrayList<String>();
            for (String name : names()) {
                IDefn defn = entries.get(name);
                if (defn instanceof LocalVariableDefn) {
                    int offset = ((LocalVariableDefn) defn).offset();
                    value.add(String.format("[\"%s\", \"%s\"]", name, offset));
                }
            }
            e.addAttribute("entries", value);
        }
    }
}
