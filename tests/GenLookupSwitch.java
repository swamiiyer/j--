import java.util.ArrayList;
import java.util.TreeMap;
import jminusminus.CLEmitter;
import static jminusminus.CLConstants.*;

public class GenLookupSwitch {
    public static void main(String[] args) {
        CLEmitter e = new CLEmitter(true);
        ArrayList<String> modifiers = new ArrayList<String>();

        // public class LookupSwitch { ... }.
        modifiers.add("public");
        e.addClass(modifiers, "LookupSwitch", "java/lang/Object", null, true);

        // public static void main(String[] args) {
        //     int faces = Integer.parseInt(args[0]);
        //     String platonicSolid;
        //     switch (faces) {
        //         case 4:
        //             platonicSolid = "Tetrahedron";
        //             break;
        //         case 6:
        //             platonicSolid = "Cube";
        //             break;
        //         case 8:
        //             platonicSolid = "Octahedron";
        //             break;
        //         case 12:
        //             platonicSolid = "Dodecahedron";
        //             break;
        //         case 20:
        //             platonicSolid = "Icosahedron";
        //             break;
        //         default:
        //             platonicSolid = "Error!";
        //     }
        //     System.out.println(platonicSolid);
        // }
        modifiers.clear();
        modifiers.add("public");
        modifiers.add("static");
        e.addMethod(modifiers, "main", "([Ljava/lang/String;)V", null, true);
        e.addNoArgInstruction(ALOAD_0);
        e.addNoArgInstruction(ICONST_0);
        e.addNoArgInstruction(AALOAD);
        e.addMemberAccessInstruction(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I");
        e.addNoArgInstruction(ISTORE_1);

        // switch(faces) { ... }.
        e.addNoArgInstruction(ILOAD_1);
        TreeMap<Integer, String> matchLabelPairs = new TreeMap<Integer, String>();
        matchLabelPairs.put(4, "case4");
        matchLabelPairs.put(6, "case6");
        matchLabelPairs.put(8, "case8");
        matchLabelPairs.put(12, "case12");
        matchLabelPairs.put(20, "case20");
        e.addLOOKUPSWITCHInstruction("default", 5, matchLabelPairs);

        // case 4: { ... }.
        e.addLabel("case4");
        e.addLDCInstruction("Tetrahedron");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "endSwitch");

        // case 6: { ... }.
        e.addLabel("case6");
        e.addLDCInstruction("Cube");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "endSwitch");

        // case 8: { ... }.
        e.addLabel("case8");
        e.addLDCInstruction("Octahedron");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "endSwitch");

        // case 12: { ... }.
        e.addLabel("case12");
        e.addLDCInstruction("Dodecahedron");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "endSwitch");

        // case 20: { ... }.
        e.addLabel("case20");
        e.addLDCInstruction("Icosahedron");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "endSwitch");

        // default: { ... }.
        e.addLabel("default");
        e.addLDCInstruction("Error!");
        e.addNoArgInstruction(ASTORE_2);

        e.addLabel("endSwitch");
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        e.addNoArgInstruction(ALOAD_2);
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
        e.addNoArgInstruction(RETURN);

        e.write();
    }
}
