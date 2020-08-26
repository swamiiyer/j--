// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas.

import java.util.ArrayList;

import jminusminus.CLEmitter;

import static jminusminus.CLConstants.*;

/**
 * This class programatically generates the class file for the following Java application:
 *
 * <pre>
 * public class Factorial {
 *     public static void main(String[] args) {
 *         int n = Integer.parseInt(args[0]);
 *         int result = factorial(n);
 *         System.out.println(n + "! = " + result);
 *     }
 *
 *     private static int factorial(int n) {
 *         if (n <= 1) {
 *             return 1;
 *         }
 *         return n * factorial(n - 1);
 *     }
 * }
 * </pre>
 */
public class GenFactorial {
    public static void main(String[] args) {
        // Create a CLEmitter instance
        CLEmitter e = new CLEmitter(true);

        // Create an ArrayList instance to store modifiers
        ArrayList<String> modifiers = new ArrayList<String>();

        // public class Factorial {
        modifiers.add("public");
        e.addClass(modifiers, "Factorial", "java/lang/Object", null, true);

        // public static void main(String[] args) {
        modifiers.clear();
        modifiers.add("public");
        modifiers.add("static");
        e.addMethod(modifiers, "main", "([Ljava/lang/String;)V", null, true);

        // int n = Integer.parseInt(args[0]);
        e.addNoArgInstruction(ALOAD_0);
        e.addNoArgInstruction(ICONST_0);
        e.addNoArgInstruction(AALOAD);
        e.addMemberAccessInstruction(INVOKESTATIC, "java/lang/Integer", "parseInt",
                "(Ljava/lang/String;)I");
        e.addNoArgInstruction(ISTORE_1);

        // int result = factorial(n);
        e.addNoArgInstruction(ILOAD_1);
        e.addMemberAccessInstruction(INVOKESTATIC, "Factorial", "factorial", "(I)I");
        e.addNoArgInstruction(ISTORE_2);

        // System.out.println(n + "! = " + result);

        // Get System.out on stack
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

        // Create an intance (say sb) of StringBuffer on stack for string concatenations
        //    sb = new StringBuffer();
        e.addReferenceInstruction(NEW, "java/lang/StringBuffer");
        e.addNoArgInstruction(DUP);
        e.addMemberAccessInstruction(INVOKESPECIAL, "java/lang/StringBuffer", "<init>", "()V");

        // sb.append(n);
        e.addNoArgInstruction(ILOAD_1);
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer", "append",
                "(I)Ljava/lang/StringBuffer;");

        // sb.append("!=");
        e.addLDCInstruction("! = ");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer", "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuffer;");

        // sb.append(result);
        e.addNoArgInstruction(ILOAD_2);
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer", "append",
                "(I)Ljava/lang/StringBuffer;");

        // System.out.println(sb.toString());
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer",
                "toString", "()Ljava/lang/String;");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V");

        // return;
        e.addNoArgInstruction(RETURN);

        // private static int factorial(int n) {
        modifiers.clear();
        modifiers.add("private");
        modifiers.add("static");
        e.addMethod(modifiers, "factorial", "(I)I", null, true);

        // if (n > 1) branch to "Recurse"
        e.addNoArgInstruction(ILOAD_0);
        e.addNoArgInstruction(ICONST_1);
        e.addBranchInstruction(IF_ICMPGT, "Recurse");

        // Base case: return 1;
        e.addNoArgInstruction(ICONST_1);
        e.addNoArgInstruction(IRETURN);

        // Recursive case: return n * factorial(n - 1);
        e.addLabel("Recurse");
        e.addNoArgInstruction(ILOAD_0);
        e.addNoArgInstruction(ILOAD_0);
        e.addNoArgInstruction(ICONST_1);
        e.addNoArgInstruction(ISUB);
        e.addMemberAccessInstruction(INVOKESTATIC, "Factorial", "factorial", "(I)I");
        e.addNoArgInstruction(IMUL);
        e.addNoArgInstruction(IRETURN);

        // Write Factorial.class to file system
        e.write();
    }
}
