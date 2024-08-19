package jminusminus;

import java.util.Hashtable;

import static jminusminus.CLConstants.CHECKCAST;
import static jminusminus.CLConstants.D2I;
import static jminusminus.CLConstants.D2L;
import static jminusminus.CLConstants.I2C;
import static jminusminus.CLConstants.I2D;
import static jminusminus.CLConstants.I2L;
import static jminusminus.CLConstants.INVOKESTATIC;
import static jminusminus.CLConstants.INVOKEVIRTUAL;
import static jminusminus.CLConstants.L2D;
import static jminusminus.CLConstants.L2I;

/**
 * The AST for a cast expression, which has both a cast (a type) and the expression to be cast.
 */
class JCastOp extends JExpression {
    // The cast.
    private Type cast;

    // The expression we're casting.
    private JExpression expr;

    // The conversions table.
    private static Conversions conversions;

    // The converter to use for this cast.
    private Converter converter;

    /**
     * Constructs an AST node for a cast expression.
     *
     * @param line the line in which the expression occurs in the source file.
     * @param cast the type we're casting our expression as.
     * @param expr the expression we're casting.
     */
    public JCastOp(int line, Type cast, JExpression expr) {
        super(line);
        this.cast = cast;
        this.expr = expr;
        conversions = new Conversions();
    }

    /**
     * {@inheritDoc}
     */
    public JExpression analyze(Context context) {
        expr = expr.analyze(context);
        type = cast = cast.resolve(context);
        if (cast.equals(expr.type())) {
            converter = Converter.Identity;
        } else if (cast.isJavaAssignableFrom(expr.type())) {
            converter = Converter.WidenReference;
        } else if (expr.type().isJavaAssignableFrom(cast)) {
            converter = new NarrowReference(cast);
        } else if (conversions.get(expr.type(), cast) != null) {
            converter = conversions.get(expr.type(), cast);
        } else {
            JAST.compilationUnit.reportSemanticError(line,
                    "cannot cast a " + expr.type().toString() + " to a " + cast.toString());
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        expr.codegen(output);
        converter.codegen(output);
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JCastOp:" + line, e);
        e.addAttribute("type", cast == null ? "" : cast.toString());
        JSONElement e1 = new JSONElement();
        e.addChild("Expression", e1);
        expr.toJSON(e1);
    }
}

/**
 * A 2D table of conversions, from one type to another.
 */
class Conversions {
    // Table of conversions; maps a source and target type pair to its converter.
    private final Hashtable<String, Converter> table;

    /**
     * Constructs a table of conversions and populates it.
     */
    public Conversions() {
        table = new Hashtable<>();

        // Primitive casts.
        put(Type.CHAR, Type.INT, Converter.Identity);
        put(Type.INT, Type.CHAR, new I2C());
        put(Type.INT, Type.DOUBLE, new I2D());
        put(Type.INT, Type.LONG, new I2L());
        put(Type.DOUBLE, Type.INT, new D2I());
        put(Type.DOUBLE, Type.LONG, new D2L());
        put(Type.LONG, Type.DOUBLE, new L2D());
        put(Type.LONG, Type.INT, new L2I());

        // Boxing.
        put(Type.BOOLEAN, Type.BOXED_BOOLEAN, new Boxing(Type.BOOLEAN, Type.BOXED_BOOLEAN));
        put(Type.CHAR, Type.BOXED_CHAR, new Boxing(Type.CHAR, Type.BOXED_CHAR));
        put(Type.DOUBLE, Type.BOXED_LONG, new Boxing(Type.DOUBLE, Type.BOXED_DOUBLE));
        put(Type.INT, Type.BOXED_INT, new Boxing(Type.INT, Type.BOXED_INT));
        put(Type.LONG, Type.BOXED_LONG, new Boxing(Type.LONG, Type.BOXED_LONG));

        // Un-boxing.
        put(Type.BOXED_BOOLEAN, Type.BOOLEAN, new UnBoxing(Type.BOXED_BOOLEAN, Type.BOOLEAN, "booleanValue"));
        put(Type.BOXED_CHAR, Type.CHAR, new UnBoxing(Type.BOXED_CHAR, Type.CHAR, "charValue"));
        put(Type.BOXED_DOUBLE, Type.DOUBLE, new UnBoxing(Type.BOXED_DOUBLE, Type.INT, "doubleValue"));
        put(Type.BOXED_INT, Type.INT, new UnBoxing(Type.BOXED_INT, Type.INT, "intValue"));
        put(Type.BOXED_LONG, Type.LONG, new UnBoxing(Type.BOXED_LONG, Type.LONG, "longValue"));
    }

    /**
     * Retrieves and returns a converter for converting from some original type to a target type.
     *
     * @param source the source type.
     * @param target the target type.
     * @return the converter.
     */
    public Converter get(Type source, Type target) {
        return table.get(source.toDescriptor() + "2" + target.toDescriptor());
    }

    // Defines a conversion. This is used for populating the conversions table.
    private void put(Type source, Type target, Converter c) {
        table.put(source.toDescriptor() + "2" + target.toDescriptor(), c);
    }
}

/**
 * A Converter encapsulates any (possibly none) code necessary to perform a cast operation.
 */
interface Converter {
    /**
     * For identity conversion (no run-time code needed).
     */
    Converter Identity = new Identity();

    /**
     * For widening conversion (no run-time code needed).
     */
    Converter WidenReference = Identity;

    /**
     * Emits code necessary to convert (cast) a source type to a target type.
     *
     * @param output the code emitter.
     */
    void codegen(CLEmitter output);
}

/**
 * An identity converter.
 */
class Identity implements Converter {
    /**
     * Constructs an Identity converter.
     */
    public Identity() {
        // Noting here.
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        // Nothing here.
    }
}

/**
 * A narrowing reference converter.
 */
class NarrowReference implements Converter {
    // The target type.
    private final Type target;

    /**
     * Constructs a narrowing reference converter.
     *
     * @param target the target type.
     */
    public NarrowReference(Type target) {
        this.target = target;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        output.addReferenceInstruction(CHECKCAST, target.jvmName());
    }
}

/**
 * A boxing converter.
 */
class Boxing implements Converter {
    // The source type.
    private final Type source;

    // The target type.
    private final Type target;

    /**
     * Constructs a Boxing converter.
     *
     * @param source the source type.
     * @param target the target type.
     */
    public Boxing(Type source, Type target) {
        this.source = source;
        this.target = target;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        output.addMemberAccessInstruction(INVOKESTATIC, target.jvmName(), "valueOf",
                "(" + source.toDescriptor() + ")" + target.toDescriptor());
    }
}

/**
 * An un-boxing converter.
 */
class UnBoxing implements Converter {
    // The source type.
    private final Type source;

    // The target type.
    private final Type target;

    // The Java method to invoke for the conversion.
    private final String methodName;

    /**
     * Constructs an UnBoxing converter.
     *
     * @param source     the source type.
     * @param target     the target type.
     * @param methodName the Java method to invoke for the conversion.
     */
    public UnBoxing(Type source, Type target, String methodName) {
        this.source = source;
        this.target = target;
        this.methodName = methodName;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        output.addMemberAccessInstruction(INVOKEVIRTUAL, source.jvmName(), methodName, "()" + target.toDescriptor());
    }
}

/**
 * An int to char converter.
 */
class I2C implements Converter {
    /**
     * Constructs an I2C converter.
     */
    public I2C() {
        // Noting here.
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        output.addNoArgInstruction(I2C);
    }
}

/**
 * An int to long converter.
 */
class I2L implements Converter {
    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        output.addNoArgInstruction(I2L);
    }
}

/**
 * An int to double converter.
 */
class I2D implements Converter {
    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        output.addNoArgInstruction(I2D);
    }
}

/**
 * A long to int converter.
 */
class L2I implements Converter {
    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        output.addNoArgInstruction(L2I);
    }
}

/**
 * A long to double converter.
 */
class L2D implements Converter {
    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        output.addNoArgInstruction(L2D);
    }
}

/**
 * A double to int converter.
 */
class D2I implements Converter {
    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        output.addNoArgInstruction(D2I);
    }
}

/**
 * A double to long converter.
 */
class D2L implements Converter {
    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        output.addNoArgInstruction(D2L);
    }
}
