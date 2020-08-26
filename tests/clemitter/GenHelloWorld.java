// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas.

import java.util.ArrayList;

import jminusminus.CLEmitter;

import static jminusminus.CLConstants.*;

/**
 * This class programatically generates the class file for the following Java application using
 * CLEmitter:
 *
 * <pre>
 * public class HelloWorld {
 *     public static void main(String[] args) {
 *         System.out.println(HelloWorld.message());
 *     }
 *
 *     private static String message() {
 *         return "Hello, World";
 *     }
 * }
 * </pre>
 */
public class GenHelloWorld {
    public static void main(String[] args) {
        // CLEmitter instance
        CLEmitter e = new CLEmitter(true);

        // ArrayList instance to store modifiers
        ArrayList<String> modifiers = new ArrayList<String>();

        // public class HelloWorld {
        modifiers.add("public");
        e.addClass(modifiers, "HelloWorld", "java/lang/Object", null, true);

        // public static void main(String[] args) {
        modifiers.clear();
        modifiers.add("public");
        modifiers.add("static");
        e.addMethod(modifiers, "main", "([Ljava/lang/String;)V", null, true);

        // System.out.println(HelloWorld.message());
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        e.addMemberAccessInstruction(INVOKESTATIC, "HelloWorld", "message", "()Ljava/lang/String;");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V");

        // return;
        e.addNoArgInstruction(RETURN);

        // private static String message() {
        modifiers.clear();
        modifiers.add("private");
        modifiers.add("static");
        e.addMethod(modifiers, "message", "()Ljava/lang/String;", null, true);

        // return "Hello, World";
        e.addLDCInstruction("Hello, World");
        e.addNoArgInstruction(ARETURN);

        // Write HelloWorld.class to file system
        e.write();
    }
}
