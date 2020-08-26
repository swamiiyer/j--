// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas.

import java.util.ArrayList;

import jminusminus.CLEmitter;

import static jminusminus.CLConstants.*;

/**
 * This class programatically generates the class file for the following Java application using
 * CLEmitter:
 *
 * <pre>
 * public class ExceptionHandler {
 *     public static void main(String[] args) {
 *         try {
 *             double x = Double.parseDouble(args[0]);
 *             double result = sqrt(x);
 *             System.out.println(result);
 *         } catch (ArrayIndexOutOfBoundsException e) {
 *             System.out.println("x not specified");
 *         } catch (NumberFormatException e) {
 *             System.out.println("x must be a double");
 *         } catch (IllegalArgumentException e) {
 *             System.out.println(e.getMessage());
 *         } finally {
 *             System.out.println("Done!");
 *         }
 *     }
 *
 *     private static double sqrt(double x) throws IllegalArgumentException {
 *         if (x < 0) {
 *             throw new IllegalArgumentException("x must be positve");
 *         }
 *         return Math.sqrt(x);
 *     }
 * }
 * </pre>
 */
public class GenExceptionHandler {
    public static void main(String[] args) {
        // Create a CLEmitter instance
        CLEmitter e = new CLEmitter(true);

        // Create an ArrayList instance to store modifiers
        ArrayList<String> modifiers = new ArrayList<String>();

        // public class ExceptionHandler {
        modifiers.add("public");
        e.addClass(modifiers, "ExceptionHandler", "java/lang/Object", null, true);

        // public static void main(String[] args) {
        modifiers.clear();
        modifiers.add("public");
        modifiers.add("static");
        e.addMethod(modifiers, "main", "([Ljava/lang/String;)V", null, true);

        // try {
        e.addLabel("StartTry");

        // double x = Double.parseDouble(args[0]);
        e.addNoArgInstruction(ALOAD_0);
        e.addNoArgInstruction(ICONST_0);
        e.addNoArgInstruction(AALOAD);
        e.addMemberAccessInstruction(INVOKESTATIC, "java/lang/Double", "parseDouble",
                "(Ljava/lang/String;)D");
        e.addNoArgInstruction(DSTORE_1);

        // double result = sqrt(x);
        e.addNoArgInstruction(DLOAD_1);
        e.addMemberAccessInstruction(INVOKESTATIC, "ExceptionHandler", "sqrt", "(D)D");
        e.addNoArgInstruction(DSTORE_3);

        // System.out.println(result);
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        e.addNoArgInstruction(DLOAD_3);
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(D)V");

        // Execute finally block and branch to "EndFinally"
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        e.addLDCInstruction("Done!");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V");
        e.addBranchInstruction(GOTO, "EndFinally");

        // End of try
        e.addLabel("EndTry");

        // catch (ArrayIndexOutOfBoundsException e) {
        //     System.out.println("x not specified");
        // }
        e.addLabel("Catch1");
        e.addNoArgInstruction(ASTORE_1);
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        e.addLDCInstruction("x not specified");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V");

        // Execute finally block and branch to "EndFinally"
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        e.addLDCInstruction("Done!");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V");
        e.addBranchInstruction(GOTO, "EndFinally");

        // catch (NumberFormatException e) {
        //     System.out.println("x must be a double");
        // }
        e.addLabel("Catch2");
        e.addNoArgInstruction(ASTORE_1);
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        e.addLDCInstruction("x must be a double");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V");

        // Execute finally block and branch to "EndFinally"
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        e.addLDCInstruction("Done!");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V");
        e.addBranchInstruction(GOTO, "EndFinally");

        // catch (IllegalArgumentException e) {
        //     System.out.println(e.getMessage());
        // }
        e.addLabel("Catch3");
        e.addNoArgInstruction(ASTORE_1);
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        e.addNoArgInstruction(ALOAD_1);
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/IllegalArgumentException",
                "getMessage", "()Ljava/lang/String;");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V");

        // Execute finally block and branch to "EndFinally"
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        e.addLDCInstruction("Done!");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V");
        e.addBranchInstruction(GOTO, "EndFinally");

        // finally {
        //     System.out.println("Done!");
        // }
        e.addLabel("StartFinally");
        e.addOneArgInstruction(ASTORE, 5);
        e.addLabel("StartFinallyPlusOne");
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        e.addLDCInstruction("Done!");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V");
        e.addOneArgInstruction(ALOAD, 5);
        e.addNoArgInstruction(ATHROW);

        // End of finally
        e.addLabel("EndFinally");

        // Add exception handlers
        e.addExceptionHandler("StartTry", "EndTry", "Catch1",
                "java/lang/ArrayIndexOutOfBoundsException");
        e.addExceptionHandler("StartTry", "EndTry", "Catch2", "java/lang/NumberFormatException");
        e.addExceptionHandler("StartTry", "EndTry", "Catch3", "java/lang/IllegalArgumentException");
        e.addExceptionHandler("StartTry", "EndTry", "StartFinally", null);
        e.addExceptionHandler("Catch1", "Catch2", "StartFinally", null);
        e.addExceptionHandler("Catch2", "Catch3", "StartFinally", null);
        e.addExceptionHandler("Catch3", "StartFinally", "StartFinally", null);
        e.addExceptionHandler("StartFinally", "StartFinallyPlusOne", "StartFinally", null);

        // return;
        e.addNoArgInstruction(RETURN);

        // private static double sqrt(double x) throws IllegalArgumentException {
        modifiers.clear();
        modifiers.add("private");
        modifiers.add("static");
        ArrayList<String> exceptions = new ArrayList<String>();
        exceptions.add("java/lang/IllegalArgumentException");
        e.addMethod(modifiers, "sqrt", "(D)D", exceptions, true);

        // if (x >= 0) branch to "EndIf"
        e.addNoArgInstruction(DLOAD_0);
        e.addNoArgInstruction(DCONST_0);
        e.addNoArgInstruction(DCMPG);
        e.addBranchInstruction(IFGE, "EndIf");

        // throw new IllegalArgumentException("x must be positive");
        e.addReferenceInstruction(NEW, "java/lang/IllegalArgumentException");
        e.addNoArgInstruction(DUP);
        e.addLDCInstruction("x must be positive");
        e.addMemberAccessInstruction(INVOKESPECIAL, "java/lang/IllegalArgumentException",
                "<init>", "(Ljava/lang/String;)V");
        e.addNoArgInstruction(ATHROW);

        // return Math.sqrt(x);
        e.addLabel("EndIf");
        e.addNoArgInstruction(DLOAD_0);
        e.addMemberAccessInstruction(INVOKESTATIC, "java/lang/Math", "sqrt", "(D)D");
        e.addNoArgInstruction(DRETURN);

        // Write ExceptionHandler.class to file system
        e.write();
    }
}
