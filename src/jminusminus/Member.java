// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

/**
 * This abstract base class provides a wrapper for class members (ie, fields, methods, and
 * constructors).
 */
abstract class Member {
    /**
     * Returns this member's (simple) name.
     *
     * @return this member's (simple) name.
     */
    public String name() {
        return member().getName();
    }

    /**
     * Returns the type in which this member was declared.
     *
     * @return the type in which this member was declared.
     */
    public Type declaringType() {
        return Type.typeFor(member().getDeclaringClass());
    }

    /**
     * Returns true if this member is static, and false otherwise.
     *
     * @return true if this member is static, and false otherwise.
     */
    public boolean isStatic() {
        return java.lang.reflect.Modifier.isStatic(member().getModifiers());
    }

    /**
     * Returns true if this member is public, and false otherwise.
     *
     * @return true if this member is public, and false otherwise.
     */
    public boolean isPublic() {
        return java.lang.reflect.Modifier.isPublic(member().getModifiers());
    }

    /**
     * Returns true if this member is protected, and false otherwise.
     *
     * @return true if this member is protected, and false otherwise.
     */
    public boolean isProtected() {
        return java.lang.reflect.Modifier.isProtected(member().getModifiers());
    }

    /**
     * Returns true if this member is private, and false otherwise.
     *
     * @return true if this member is private, and false otherwise.
     */
    public boolean isPrivate() {
        return java.lang.reflect.Modifier.isPrivate(member().getModifiers());
    }

    /**
     * Returns true if this member is abstract, and false otherwise.
     *
     * @return true if this member is abstract, and false otherwise.
     */
    public boolean isAbstract() {
        return java.lang.reflect.Modifier.isAbstract(member().getModifiers());
    }

    /**
     * Returns true if this member is final, and false otherwise.
     *
     * @return true if this member is final, and false otherwise.
     */
    public boolean isFinal() {
        return java.lang.reflect.Modifier.isFinal(member().getModifiers());
    }

    /**
     * Returns the JVM descriptor for this member.
     *
     * @return the JVM descriptor for this member.
     */
    public abstract String toDescriptor();

    /**
     * Returns this member's internal representation.
     *
     * @return this member's internal representation.
     */
    protected abstract java.lang.reflect.Member member();
}

/**
 * This class provides a wrapper for constructors.
 */
class Constructor extends Member {
    // Internal representation of this constructor.
    private java.lang.reflect.Constructor constructor;

    /**
     * Constructs a constructor given its internal representation.
     *
     * @param constructor internal representation.
     */
    public Constructor(java.lang.reflect.Constructor constructor) {
        this.constructor = constructor;
    }

    /**
     * {@inheritDoc}
     */
    public String toDescriptor() {
        String descriptor = "(";
        for (Class paramType : constructor.getParameterTypes()) {
            descriptor += Type.typeFor(paramType).toDescriptor();
        }
        descriptor += ")V";
        return descriptor;
    }

    /**
     * {@inheritDoc}
     */
    protected java.lang.reflect.Member member() {
        return constructor;
    }
}

/**
 * This class provides a wrapper for fields.
 */
class Field extends Member {
    // Internal representation of this field.
    private java.lang.reflect.Field field;

    /**
     * Constructs a field given its internal representation.
     *
     * @param field internal representation.
     */
    public Field(java.lang.reflect.Field field) {
        this.field = field;
    }

    /**
     * Returns this field's type.
     *
     * @return this field's type.
     */
    public Type type() {
        return Type.typeFor(field.getType());
    }

    /**
     * {@inheritDoc}
     */
    public String toDescriptor() {
        return type().toDescriptor();
    }

    /**
     * {@inheritDoc}
     */
    protected java.lang.reflect.Member member() {
        return field;
    }
}

/**
 * This class provides a wrapper for methods.
 */
class Method extends Member {
    // Internal representation of this method.
    private java.lang.reflect.Method method;

    /**
     * Constructs a method given its internal representation.
     *
     * @param method the internal representation.
     */
    public Method(java.lang.reflect.Method method) {
        this.method = method;
    }

    /**
     * Returns this method's return type.
     *
     * @return this method's return type.
     */
    public Type returnType() {
        return Type.typeFor(method.getReturnType());
    }

    /**
     * {@inheritDoc}
     */
    public String toDescriptor() {
        String descriptor = "(";
        for (Class paramType : method.getParameterTypes()) {
            descriptor += Type.typeFor(paramType).toDescriptor();
        }
        descriptor += ")" + Type.typeFor(method.getReturnType()).toDescriptor();
        return descriptor;
    }

    /**
     * {@inheritDoc}
     */
    protected java.lang.reflect.Member member() {
        return method;
    }
}