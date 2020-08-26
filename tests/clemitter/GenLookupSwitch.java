// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas.

import java.util.ArrayList;
import java.util.TreeMap;

import jminusminus.CLEmitter;

import static jminusminus.CLConstants.*;

/**
 * This class programatically generates the class file for the following Java application:
 *
 * <pre>
 * public class LookupSwitch {
 *     public static void main(String[] args) {
 *         int faces = Integer.parseInt(args[0]);
 *         String platonicSolid;
 *         switch (faces) {
 *             case 4:
 *                 platonicSolid = "Tetrahedron";
 *                 break;
 *             case 6:
 *                 platonicSolid = "Cube";
 *                 break;
 *             case 8:
 *                 platonicSolid = "Octahedron";
 *                 break;
 *             case 12:
 *                 platonicSolid = "Dodecahedron";
 *                 break;
 *             case 20:
 *                 platonicSolid = "Icosahedron";
 *                 break;
 *             default:
 *                 platonicSolid = "Error!";
 *         }
 *         System.out.println(platonicSolid);
 *     }
 * }
 * </pre>
 */
public class GenLookupSwitch {
    public static void main(String[] args) {
        // Create a CLEmitter instance
        CLEmitter e = new CLEmitter(true);

        // Create an ArrayList instance to store modifiers
        ArrayList<String> modifiers = new ArrayList<String>();

        // public class LookupSwitch {
        modifiers.add("public");
        e.addClass(modifiers, "LookupSwitch", "java/lang/Object", null, true);

        // public static void main(String[] args) {
        modifiers.clear();
        modifiers.add("public");
        modifiers.add("static");
        e.addMethod(modifiers, "main", "([Ljava/lang/String;)V", null, true);

        // int faces = Integer.parseInt(args[0]);
        e.addNoArgInstruction(ALOAD_0);
        e.addNoArgInstruction(ICONST_0);
        e.addNoArgInstruction(AALOAD);
        e.addMemberAccessInstruction(INVOKESTATIC, "java/lang/Integer", "parseInt",
                "(Ljava/lang/String;)I");
        e.addNoArgInstruction(ISTORE_1);

        // switch(faces) {
        e.addNoArgInstruction(ILOAD_1);
        TreeMap<Integer, String> matchLabelPairs = new TreeMap<Integer, String>();
        matchLabelPairs.put(4, "Case4");
        matchLabelPairs.put(6, "Case6");
        matchLabelPairs.put(8, "Case8");
        matchLabelPairs.put(12, "Case12");
        matchLabelPairs.put(20, "Case20");
        e.addLOOKUPSWITCHInstruction("Default", 5, matchLabelPairs);

        // case 4:
        //     platonicSolid = "Tetrahedron";
        //     break;
        e.addLabel("Case4");
        e.addLDCInstruction("Tetrahedron");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "EndSwitch");

        // case 6:
        //     platonicSolid = "Cube";
        //     break;
        e.addLabel("Case6");
        e.addLDCInstruction("Cube");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "EndSwitch");

        // case 8:
        //     platonicSolid = "Octahedron";
        //     break;
        e.addLabel("Case8");
        e.addLDCInstruction("Octahedron");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "EndSwitch");

        // case 12:
        //     platonicSolid = "Dodecahedron";
        //     break;
        e.addLabel("Case12");
        e.addLDCInstruction("Dodecahedron");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "EndSwitch");

        // case 20:
        //     platonicSolid = "Icosahedron";
        //     break;
        e.addLabel("Case20");
        e.addLDCInstruction("Icosahedron");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "EndSwitch");

        // default:
        //     platonicSolid = "Error!".
        //     break;
        e.addLabel("Default");
        e.addLDCInstruction("Error!");
        e.addNoArgInstruction(ASTORE_2);

        // End switch(faces)
        e.addLabel("EndSwitch");

        // Sysem.out.println(platonicSolid);
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        e.addNoArgInstruction(ALOAD_2);
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V");

        // return;
        e.addNoArgInstruction(RETURN);

        // Write LookupSwitch.class to file system
        e.write();
    }
}
