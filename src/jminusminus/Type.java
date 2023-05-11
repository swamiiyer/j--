// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * A class for representing j-- types. All types are represented underneath (in the classRep
 * field) by Java objects of type Class. These objects represent types in Java, so this should
 * ease our interfacing with existing Java classes.
 * <p>
 * Class types (reference types that are represented by the identifiers introduced in class
 * declarations) are represented using TypeName. So for now, every TypeName represents a class.
 * In the future, TypeName could be extended to represent interfaces or enumerations.
 * <p>
 * IdentifierTypes must be "resolved" at some point, so that all Types having the same name refer
 * to the same Type object. The resolve() method does this.
 */
class Type {
    // The Type's internal (Java) representation.
    private Class<?> classRep;

    // Maps type names to their Type representations.
    private static Hashtable<String, Type> types = new Hashtable<String, Type>();

    /**
     * The int type.
     */
    public final static Type INT = typeFor(int.class);

    /**
     * The char type.
     */
    public final static Type CHAR = typeFor(char.class);

    /**
     * The boolean type.
     */
    public final static Type BOOLEAN = typeFor(boolean.class);

    /**
     * The long type.
     */
    public final static Type LONG = typeFor(long.class);

    /**
     * The double type.
     */
    public final static Type DOUBLE = typeFor(double.class);

    /**
     * The java.lang.Integer type.
     */
    public final static Type BOXED_INT = typeFor(java.lang.Integer.class);

    /**
     * The java.lang.Character type.
     */
    public final static Type BOXED_CHAR = typeFor(java.lang.Character.class);

    /**
     * The java.lang.Boolean type.
     */
    public final static Type BOXED_BOOLEAN = typeFor(java.lang.Boolean.class);

    /**
     * The java.lang.Long type.
     */
    public final static Type BOXED_LONG = typeFor(java.lang.Long.class);

    /**
     * The java.lang.Double type.
     */
    public final static Type BOXED_DOUBLE = typeFor(java.lang.Double.class);

    /**
     * The java.lang.String type.
     */
    public final static Type STRING = typeFor(java.lang.String.class);

    /**
     * The java.lang.Object type.
     */
    public final static Type OBJECT = typeFor(java.lang.Object.class);

    /**
     * The void type.
     */
    public final static Type VOID = typeFor(void.class);

    /**
     * The null type.
     */
    public final static Type NULLTYPE = new Type(java.lang.Object.class);

    /**
     * The "any" type (denotes wild expressions).
     */
    public final static Type ANY = new Type(null);

    /**
     * A type marker indicating a constructor (having no return type).
     */
    public final static Type CONSTRUCTOR = new Type(null);

    /**
     * This constructor is to keep the compiler happy.
     */
    protected Type() {
        super();
    }

    /**
     * Constructs and returns a representation for a type from its (Java) class representation,
     * making sure there is a unique representation for each unique type.
     *
     * @param classRep the Java class representation.
     * @return a type representation of classRep.
     */
    public static Type typeFor(Class<?> classRep) {
        if (types.get(descriptorFor(classRep)) == null) {
            types.put(descriptorFor(classRep), new Type(classRep));
        }
        return types.get(descriptorFor(classRep));
    }

    /**
     * Returns the class representation for this type.
     *
     * @return the class representation for this type.
     */
    public Class<?> classRep() {
        return classRep;
    }

    /**
     * Sets the class representation of this type to the specified partial class.
     *
     * @param classRep the partial class.
     */
    public void setClassRep(Class<?> classRep) {
        this.classRep = classRep;
    }

    /**
     * Returns true if this type has the same descriptor as other, and false otherwise.
     *
     * @param other the other type.
     * @return true if this type has the same descriptor as other, and false otherwise.
     */
    public boolean equals(Type other) {
        return this.toDescriptor().equals(other.toDescriptor());
    }

    /**
     * Returns true if this is an array type, and false otherwise.
     *
     * @return true if this is an array type, and false otherwise.
     */
    public boolean isArray() {
        return classRep.isArray();
    }

    /**
     * Returns an array type's component type.
     *
     * @return an array type's component type.
     */
    public Type componentType() {
        return typeFor(classRep.getComponentType());
    }

    /**
     * Returns this type's super type, or null.
     *
     * @return this type's super type, or null.
     */
    public Type superClass() {
        return classRep == null || classRep.getSuperclass() == null ? null :
                typeFor(classRep.getSuperclass());
    }

    /**
     * Returns true if this is a primitive type, and false otherwise.
     *
     * @return true if this is a primitive type, and false otherwise.
     */
    public boolean isPrimitive() {
        return classRep.isPrimitive();
    }

    /**
     * Returns true if this is an interface type, and false otherwise.
     *
     * @return true if this is an interface type, and false otherwise.
     */
    public boolean isInterface() {
        return classRep.isInterface();
    }

    /**
     * Returns true if this is a reference type, and false otherwise.
     *
     * @return true if this is a reference type, and false otherwise.
     */
    public boolean isReference() {
        return !isPrimitive();
    }

    /**
     * Returns true of this type is declared final, and false otherwise.
     *
     * @return true of this type is declared final, and false otherwise.
     */
    public boolean isFinal() {
        return Modifier.isFinal(classRep.getModifiers());
    }

    /**
     * Returns true of this type is declared abstract, and false otherwise.
     *
     * @return true of this type is declared abstract, and false otherwise.
     */
    public boolean isAbstract() {
        return Modifier.isAbstract(classRep.getModifiers());
    }

    /**
     * Returns true if this is a supertype of other, and false otherwise.
     *
     * @param that the candidate subtype.
     * @return true if this is a supertype of other, and false otherwise.
     */
    public boolean isJavaAssignableFrom(Type that) {
        return this.classRep.isAssignableFrom(that.classRep);
    }

    /**
     * Returns a list of this class' abstract methods.
     * <p>
     * It has abstract methods if:
     * <ol>
     *   <li>Any method declared in the class is abstract or
     *   <li>its superclass has an abstract method which is not overridden here.
     * </ol>
     *
     * @return a list of this class' abstract methods.
     */
    public ArrayList<Method> abstractMethods() {
        ArrayList<Method> inheritedAbstractMethods = superClass() == null ? new ArrayList<Method>()
                : superClass().abstractMethods();
        ArrayList<Method> abstractMethods = new ArrayList<Method>();
        ArrayList<Method> declaredConcreteMethods = declaredConcreteMethods();
        ArrayList<Method> declaredAbstractMethods = declaredAbstractMethods();
        abstractMethods.addAll(declaredAbstractMethods);
        for (Method method : inheritedAbstractMethods) {
            if (!declaredConcreteMethods.contains(method) &&
                    !declaredAbstractMethods.contains(method)) {
                abstractMethods.add(method);
            }
        }
        return abstractMethods;
    }

    /**
     * Returns a list of this class' declared abstract methods.
     *
     * @return a list of this class' declared abstract methods.
     */
    private ArrayList<Method> declaredAbstractMethods() {
        ArrayList<Method> declaredAbstractMethods = new ArrayList<Method>();
        for (java.lang.reflect.Method method : classRep.getDeclaredMethods()) {
            if (Modifier.isAbstract(method.getModifiers())) {
                declaredAbstractMethods.add(new Method(method));
            }
        }
        return declaredAbstractMethods;
    }

    /**
     * Returns a list of this class' declared concrete methods.
     *
     * @return a list of this class' declared concrete methods.
     */
    private ArrayList<Method> declaredConcreteMethods() {
        ArrayList<Method> declaredConcreteMethods = new ArrayList<Method>();
        for (java.lang.reflect.Method method : classRep.getDeclaredMethods()) {
            if (!Modifier.isAbstract(method.getModifiers())) {
                declaredConcreteMethods.add(new Method(method));
            }
        }
        return declaredConcreteMethods;
    }

    /**
     * An assertion that this type matches one of the specified types. If there is no match, an
     * error is reported.
     *
     * @param line          the line near which the mismatch occurs.
     * @param expectedTypes expected types.
     */
    public void mustMatchOneOf(int line, Type... expectedTypes) {
        if (this == Type.ANY) {
            return;
        }
        for (Type type : expectedTypes) {
            if (matchesExpected(type)) {
                return;
            }
        }
        JAST.compilationUnit.reportSemanticError(line,
                "Type %s doesn't match any of the expected types %s", this,
                Arrays.toString(expectedTypes));
    }

    /**
     * An assertion that this type matches the specified type. If there is no match, an error is
     * reported.
     *
     * @param line         the line near which the mismatch occurs.
     * @param expectedType type with which to match.
     */
    public void mustMatchExpected(int line, Type expectedType) {
        if (!matchesExpected(expectedType)) {
            JAST.compilationUnit.reportSemanticError(line, "Type %s doesn't match type %s", this,
                    expectedType);
        }
    }

    /**
     * Returns true if this type matches expected, and false otherwise.
     *
     * @param expected the type that this might match.
     * @return true if this type matches expected, and false otherwise.
     */
    public boolean matchesExpected(Type expected) {
        return this == Type.ANY || expected == Type.ANY ||
                (this == Type.NULLTYPE && expected.isReference()) || this.equals(expected);
    }

    /**
     * Returns true if the argument types match, and false otherwise.
     *
     * @param argTypes1 arguments (classReps) of one method.
     * @param argTypes2 arguments (classReps) of another method.
     * @return true if the argument types match, and false otherwise.
     */
    public static boolean argTypesMatch(Class<?>[] argTypes1, Class<?>[] argTypes2) {
        if (argTypes1.length != argTypes2.length) {
            return false;
        }
        for (int i = 0; i < argTypes1.length; i++) {
            if (!Type.descriptorFor(argTypes1[i]).equals(Type.descriptorFor(argTypes2[i]))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the simple (unqualified) name of this type.
     *
     * @return the simple (unqualified) name of this type.
     */
    public String simpleName() {
        return classRep.getSimpleName();
    }

    /**
     * Returns a string representation of this type.
     *
     * @return a string representation of this type.
     */
    public String toString() {
        return toJava(this.classRep);
    }

    /**
     * Returns the JVM descriptor of this type.
     *
     * @return the JVM descriptor of this type.
     */
    public String toDescriptor() {
        return descriptorFor(classRep);
    }

    /**
     * Returns the JVM representation of this type's name.
     *
     * @return the JVM representation of this type's name.
     */
    public String jvmName() {
        return this.isArray() || this.isPrimitive() ?
                this.toDescriptor() : classRep.getName().replace('.', '/');
    }

    /**
     * Returns this type's package name.
     *
     * @return this type's package name.
     */
    public String packageName() {
        String name = toString();
        return name.lastIndexOf('.') == -1 ? "" : name.substring(0, name.lastIndexOf('.') - 1);
    }

    /**
     * Returns a string representation for a type being appended to a StringBuffer (for the + and
     * += operations over strings).
     *
     * @return a string representation for a type being appended to a StringBuffer.
     */
    public String argumentTypeForAppend() {
        return this == Type.STRING || this.isPrimitive() ? toDescriptor() : "Ljava/lang/Object;";
    }

    /**
     * Finds and returns a method in this type having the given name and argument types, or null.
     *
     * @param name     the method name.
     * @param argTypes the argument types.
     * @return a method in this type having the given name and argument types, or null.
     */
    public Method methodFor(String name, Type[] argTypes) {
        Class[] classes = new Class[argTypes.length];
        for (int i = 0; i < argTypes.length; i++) {
            classes[i] = argTypes[i].classRep;
        }
        Class cls = classRep;

        // Search this class and all superclasses.
        while (cls != null) {
            java.lang.reflect.Method[] methods = cls.getDeclaredMethods();
            for (java.lang.reflect.Method method : methods) {
                if (method.getName().equals(name) && Type.argTypesMatch(classes,
                        method.getParameterTypes())) {
                    return new Method(method);
                }
            }
            cls = cls.getSuperclass();
        }

        return null;
    }

    /**
     * Finds and returns a constructor in this type having the given argument types, or null.
     *
     * @param argTypes the argument types.
     * @return a constructor in this type having the given argument types, or null.
     */
    public Constructor constructorFor(Type[] argTypes) {
        Class[] classes = new Class[argTypes.length];
        for (int i = 0; i < argTypes.length; i++) {
            classes[i] = argTypes[i].classRep;
        }

        // Search only this class (we don't inherit constructors).
        java.lang.reflect.Constructor[] constructors = classRep.getDeclaredConstructors();
        for (java.lang.reflect.Constructor constructor : constructors) {
            if (argTypesMatch(classes, constructor.getParameterTypes())) {
                return new Constructor(constructor);
            }
        }

        return null;
    }

    /**
     * Finds and returns a field in this type having the given name, or null.
     *
     * @param name the name of the field we want.
     * @return a field in this type having the given name, or null.
     */
    public Field fieldFor(String name) {
        Class<?> cls = classRep;
        while (cls != null) {
            java.lang.reflect.Field[] fields = cls.getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                if (field.getName().equals(name)) {
                    return new Field(field);
                }
            }
            cls = cls.getSuperclass();
        }
        return null;
    }

    /**
     * Returns a string representation of an array of argument types.
     *
     * @param argTypes the array of argument types.
     * @return a string representation of an array of argument types.
     */
    public static String argTypesAsString(Type[] argTypes) {
        if (argTypes.length == 0) {
            return "()";
        } else {
            String str = "(" + argTypes[0].toString();
            for (int i = 1; i < argTypes.length; i++) {
                str += "," + argTypes[i];
            }
            str += ")";
            return str;
        }
    }

    /**
     * Returns true if the member is accessible from this type, and false otherwise.
     *
     * @param line   the line in which the access occurs.
     * @param member the member being accessed.
     * @return true if the member is accessible from this type, and false otherwise.
     */
    public boolean checkAccess(int line, Member member) {
        if (!checkAccess(line, classRep, member.declaringType().classRep)) {
            return false;
        }
        // The member must be either public, protected, or private.
        if (member.isPublic()) {
            return true;
        }
        java.lang.Package p1 = classRep.getPackage();
        java.lang.Package p2 = member.declaringType().classRep.getPackage();
        if ((p1 == null ? "" : p1.getName()).equals((p2 == null ? "" : p2.getName()))) {
            return true;
        }
        if (member.isProtected()) {
            if (classRep.getPackage().getName().equals(
                    member.declaringType().classRep.getPackage().getName())
                    || typeFor(member.getClass().getDeclaringClass())
                    .isJavaAssignableFrom(this)) {
                return true;
            } else {
                JAST.compilationUnit.reportSemanticError(line,
                        "The protected member, " + member.name() + ", is not accessible.");
                return false;
            }
        }
        if (member.isPrivate()) {
            if (descriptorFor(classRep).equals(
                    descriptorFor(member.member().getDeclaringClass()))) {
                return true;
            } else {
                JAST.compilationUnit.reportSemanticError(line,
                        "The private member, " + member.name() + ", is not accessible.");
                return false;
            }
        }

        // Otherwise, the member has default access.
        if (packageName().equals(member.declaringType().packageName())) {
            return true;
        } else {
            JAST.compilationUnit.reportSemanticError(line, "The member, " + member.name() +
                    ", is not accessible because it's in a different package.");
            return false;
        }
    }

    /**
     * Returns true if the target type is accessible from this type, and false otherwise.
     *
     * @param line       line in which the access occurs.
     * @param targetType the type being accessed.
     * @return true if the target type is accessible from this type, and false otherwise.
     */
    public boolean checkAccess(int line, Type targetType) {
        if (targetType.isPrimitive()) {
            return true;
        }
        if (targetType.isArray()) {
            return this.checkAccess(line, targetType.componentType());
        }
        return checkAccess(line, classRep, targetType.classRep);
    }

    /**
     * Returns true if the referenced type is accessible from the referencing type, and false
     * otherwise.
     *
     * @param line            the line in which the access occurs.
     * @param referencingType the type attempting the access.
     * @param type            the type that we want to access.
     * @return true if the referenced type is accessible from the referencing type, and false
     * otherwise.
     */
    public static boolean checkAccess(int line, Class referencingType, Class type) {
        java.lang.Package p1 = referencingType.getPackage();
        java.lang.Package p2 = type.getPackage();
        if (Modifier.isPublic(type.getModifiers()) ||
                (p1 == null ? "" : p1.getName()).equals((p2 == null ? "" : p2.getName()))) {
            return true;
        } else {
            JAST.compilationUnit.reportSemanticError(line, "The type, " + type.getCanonicalName() +
                    ", is not accessible from " + referencingType.getCanonicalName());
            return false;
        }
    }

    /**
     * Resolves this type in the given context and returns the resolved type.
     *
     * @param context context in which the names are resolved.
     * @return the resolved type.
     */
    public Type resolve(Context context) {
        return this;
    }

    /**
     * Returns a signature for reporting unfound methods and constructors.
     *
     * @param name     the message or type name.
     * @param argTypes the actual argument types.
     * @return a signature for reporting unfound methods and constructors.
     */
    public static String signatureFor(String name, Type[] argTypes) {
        String signature = name + "(";
        if (argTypes.length > 0) {
            signature += argTypes[0].toString();
            for (int i = 1; i < argTypes.length; i++) {
                signature += "," + argTypes[i].toString();
            }
        }
        signature += ")";
        return signature;
    }

    // Constructs a representation for a type from its Java (Class) representation. Use typeFor()
    // that maps types having like classReps to like Types.
    private Type(Class<?> classRep) {
        this.classRep = classRep;
    }

    // Returns the JVM descriptor of a type's class representation.
    private static String descriptorFor(Class<?> classRep) {
        return classRep == null ? "V" : classRep == void.class ? "V"
                : classRep.isArray() ? "[" + descriptorFor(classRep.getComponentType())
                : classRep.isPrimitive() ? (classRep == int.class ? "I"
                : classRep == char.class ? "C"
                : classRep == boolean.class ? "Z"
                : classRep == double.class ? "D"
                : classRep == long.class ? "J" : "?")
                : "L" + classRep.getName().replace('.', '/') + ";";
    }

    // Returns the Java (and so j--) denotation for the specified type.
    private static String toJava(Class classRep) {
        return classRep.isArray() ? toJava(classRep.getComponentType()) + "[]" : classRep.getName();
    }
}

/**
 * A representation of any reference type that can be denoted as a (possibly qualified) identifier.
 */
class TypeName extends Type {
    // The line in which the identifier occurs in the source file.
    private int line;

    // The identifier's name.
    private String name;

    /**
     * Constructs a TypeName.
     *
     * @param line the line in which the identifier occurs in the source file.
     * @param name fully qualified name for the identifier.
     */
    public TypeName(int line, String name) {
        this.line = line;
        this.name = name;
    }

    /**
     * Returns the line in which the identifier occurs in the source file.
     *
     * @return the line in which the identifier occurs in the source file.
     */
    public int line() {
        return line;
    }

    /**
     * {@inheritDoc}
     */
    public String jvmName() {
        return name.replace('.', '/');
    }

    /**
     * {@inheritDoc}
     */
    public String toDescriptor() {
        return "L" + jvmName() + ";";
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public String simpleName() {
        return name.substring(name.lastIndexOf('.') + 1);
    }

    /**
     * {@inheritDoc}
     */
    public Type resolve(Context context) {
        Type resolvedType = context.lookupType(name);
        if (resolvedType == null) {
            // Try loading a type with the given fullname.
            try {
                resolvedType = typeFor(Class.forName(name));
                context.addType(line, resolvedType);
            } catch (Exception e) {
                JAST.compilationUnit.reportSemanticError(line, "Unable to locate %s", name);
                resolvedType = Type.ANY;
            }
        }
        return resolvedType;
    }
}

/**
 * A representation of an array type. It is built by the Parser to stand in for a Type until the
 * analyze() phase, at which point it is resolved to an actual Type object (having a Class that
 * identifies it).
 */
class ArrayTypeName extends Type {
    // The array's base or component type.
    private Type componentType;

    /**
     * Constructs an ArrayTypeName given its component type.
     *
     * @param componentType the type of the array's elements.
     */
    public ArrayTypeName(Type componentType) {
        this.componentType = componentType;
    }

    /**
     * {@inheritDoc}
     */
    public Type componentType() {
        return componentType;
    }

    /**
     * {@inheritDoc}
     */
    public String toDescriptor() {
        return "[" + componentType.toDescriptor();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return componentType.toString() + "[]";
    }

    /**
     * {@inheritDoc}
     */
    public Type resolve(Context context) {
        componentType = componentType.resolve(context);
        Class classRep = Array.newInstance(componentType().classRep(), 0).getClass();
        return Type.typeFor(classRep);
    }
}
