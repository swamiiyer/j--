// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas.

import java.util.ArrayList;

import jminusminus.CLEmitter;

import static jminusminus.CLConstants.*;

/**
 * This class programatically generates the class file for the following Java application using
 * CLEmitter:
 *
 * <pre>
 * public class NHellos {
 *     public static void main(String[] args) {
 *         int n = Integer.parseInt(args[0]);
 *         int i = 1;
 *         while (i <= n) {
 *             System.out.println("Hello # " + i);
 *             i += 1;
 *         }
 *     }
 * }
 * </pre>
 */
public class GenNHellos {
    public static void main(String[] args) {
        // CLEmitter instance
        CLEmitter e = new CLEmitter(true);

        // ArrayList instance to store modifiers
        ArrayList<String> modifiers = new ArrayList<String>();

        // public class NHellos {
        modifiers.add("public");
        e.addClass(modifiers, "NHellos", "java/lang/Object", null, true);

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

        // int i = 1;
        e.addNoArgInstruction(ICONST_1);
        e.addNoArgInstruction(ISTORE_2);

        // while (i <= n) {
        e.addLabel("StartWhile");

        // if i > n branch to "EndWhile"
        e.addNoArgInstruction(ILOAD_2);
        e.addNoArgInstruction(ILOAD_1);
        e.addBranchInstruction(IF_ICMPGT, "EndWhile");

        // System.out.println("Hello # " + i);

        // Get System.out on stack
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

        // Create an intance (say sb) of StringBuffer on stack for string concatenations
        //    sb = new StringBuffer();
        e.addReferenceInstruction(NEW, "java/lang/StringBuffer");
        e.addNoArgInstruction(DUP);
        e.addMemberAccessInstruction(INVOKESPECIAL, "java/lang/StringBuffer", "<init>", "()V");

        // sb.append("Hello # ");
        e.addLDCInstruction("Hello # ");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer", "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuffer;");

        // sb.append(i);
        e.addNoArgInstruction(ILOAD_2);
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer", "append",
                "(I)Ljava/lang/StringBuffer;");

        // System.out.println(sb.toString());
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer",
                "toString", "()Ljava/lang/String;");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V");

        // i += 1;
        e.addIINCInstruction(2, 1);

        // Branch to "StartWhile"
        e.addBranchInstruction(GOTO, "StartWhile");

        // End of while
        e.addLabel("EndWhile");

        // return;
        e.addNoArgInstruction(RETURN);

        // Write NHellos.class to file system
        e.write();
    }
}
