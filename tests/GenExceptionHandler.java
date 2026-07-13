import java.util.ArrayList;
import jminusminus.CLEmitter;
import static jminusminus.CLConstants.*;

public class GenExceptionHandler {
    public static void main(String[] args) {
        CLEmitter e = new CLEmitter(true);
        ArrayList<String> modifiers = new ArrayList<String>();

        // public class ExceptionHandler { ... }.
        modifiers.add("public");
        e.addClass(modifiers, "ExceptionHandler", "java/lang/Object", null, true);

        // public static void main(String[] args) {
        //     try {
        //         double x = Double.parseDouble(args[0]);
        //         double result = sqrt(x);
        //         System.out.println(result);
        //     } catch (ArrayIndexOutOfBoundsException e) {
        //         System.out.println("x not specified");
        //     } catch (NumberFormatException e) {
        //         System.out.println("x must be a double");
        //     } catch (IllegalArgumentException e) {
        //         System.out.println(e.getMessage());
        //     } finally {
        //         System.out.println("Done!");
        //     }
        // }
        modifiers.clear();
        modifiers.add("public");
        modifiers.add("static");
        e.addMethod(modifiers, "main", "([Ljava/lang/String;)V", null, true);

        // try { ... }.
        e.addLabel("startTry");
        e.addNoArgInstruction(ALOAD_0);
        e.addNoArgInstruction(ICONST_0);
        e.addNoArgInstruction(AALOAD);
        e.addMemberAccessInstruction(INVOKESTATIC, "java/lang/Double", "parseDouble", "(Ljava/lang/String;)D");
        e.addNoArgInstruction(DSTORE_1);
        e.addNoArgInstruction(DLOAD_1);
        e.addMemberAccessInstruction(INVOKESTATIC, "ExceptionHandler", "sqrt", "(D)D");
        e.addNoArgInstruction(DSTORE_3);
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        e.addNoArgInstruction(DLOAD_3);
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(D)V");
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        e.addLDCInstruction("Done!");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
        e.addBranchInstruction(GOTO, "endFinally");
        e.addLabel("endTry");

        // catch (ArrayIndexOutOfBoundsException e) { ... }.
        e.addLabel("catch1");
        e.addNoArgInstruction(ASTORE_1);
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        e.addLDCInstruction("x not specified");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        e.addLDCInstruction("Done!");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
        e.addBranchInstruction(GOTO, "endFinally");

        // catch (NumberFormatException e) { ... }.
        e.addLabel("catch2");
        e.addNoArgInstruction(ASTORE_1);
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        e.addLDCInstruction("x must be a double");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        e.addLDCInstruction("Done!");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
        e.addBranchInstruction(GOTO, "endFinally");

        // catch (IllegalArgumentException e) { ... }.
        e.addLabel("catch3");
        e.addNoArgInstruction(ASTORE_1);
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        e.addNoArgInstruction(ALOAD_1);
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/IllegalArgumentException", "getMessage",
                "()Ljava/lang/String;");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        e.addLDCInstruction("Done!");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
        e.addBranchInstruction(GOTO, "endFinally");

        // finally { ... }.
        e.addLabel("startFinally");
        e.addOneArgInstruction(ASTORE, 5);
        e.addLabel("startFinallyPlusOne");
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        e.addLDCInstruction("Done!");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
        e.addOneArgInstruction(ALOAD, 5);
        e.addNoArgInstruction(ATHROW);
        e.addLabel("endFinally");

        // Exception handlers.
        e.addExceptionHandler("startTry", "endTry", "catch1", "java/lang/ArrayIndexOutOfBoundsException");
        e.addExceptionHandler("startTry", "endTry", "catch2", "java/lang/NumberFormatException");
        e.addExceptionHandler("startTry", "endTry", "catch3", "java/lang/IllegalArgumentException");
        e.addExceptionHandler("startTry", "endTry", "startFinally", null);
        e.addExceptionHandler("catch1", "catch2", "startFinally", null);
        e.addExceptionHandler("catch2", "catch3", "startFinally", null);
        e.addExceptionHandler("catch3", "startFinally", "startFinally", null);
        e.addExceptionHandler("startFinally", "startFinallyPlusOne", "startFinally", null);

        e.addNoArgInstruction(RETURN);

        // private static double sqrt(double x) throws IllegalArgumentException {
        //     if (x < 0) {
        //         throw new IllegalArgumentException("x must be positve");
        //     }
        //     return Math.sqrt(x);
        // }
        modifiers.clear();
        modifiers.add("private");
        modifiers.add("static");
        ArrayList<String> exceptions = new ArrayList<String>();
        exceptions.add("java/lang/IllegalArgumentException");
        e.addMethod(modifiers, "sqrt", "(D)D", exceptions, true);
        e.addNoArgInstruction(DLOAD_0);
        e.addNoArgInstruction(DCONST_0);
        e.addNoArgInstruction(DCMPG);
        e.addBranchInstruction(IFGE, "endIf");
        e.addReferenceInstruction(NEW, "java/lang/IllegalArgumentException");
        e.addNoArgInstruction(DUP);
        e.addLDCInstruction("x must be positive");
        e.addMemberAccessInstruction(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>",
                "(Ljava/lang/String;)V");
        e.addNoArgInstruction(ATHROW);
        e.addLabel("endIf");
        e.addNoArgInstruction(DLOAD_0);
        e.addMemberAccessInstruction(INVOKESTATIC, "java/lang/Math", "sqrt", "(D)D");
        e.addNoArgInstruction(DRETURN);

        e.write();
    }
}
