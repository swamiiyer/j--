import java.util.ArrayList;
import jminusminus.CLEmitter;
import static jminusminus.CLConstants.*;

public class GenFactorial {
    public static void main(String[] args) {
        CLEmitter e = new CLEmitter(true);
        ArrayList<String> modifiers = new ArrayList<String>();

        // public class Factorial { ... }.
        modifiers.add("public");
        e.addClass(modifiers, "Factorial", "java/lang/Object", null, true);

        // public static void main(String[] args) {
        //     int n = Integer.parseInt(args[0]);
        //     System.out.println(computeRec(n));
        //     System.out.println(computeIter(n));
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
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        e.addNoArgInstruction(ILOAD_1);
        e.addMemberAccessInstruction(INVOKESTATIC, "Factorial", "computeRec", "(I)I");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V");
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        e.addNoArgInstruction(ILOAD_1);
        e.addMemberAccessInstruction(INVOKESTATIC, "Factorial", "computeIter", "(I)I");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V");
        e.addNoArgInstruction(RETURN);

        // private static int computeRec(int n) {
        //     if (n == 0) {
        //         return 1;
        //     } else {
        //         return n * computeRec(n - 1);
        //     }
        // }
        modifiers.clear();
        modifiers.add("private");
        modifiers.add("static");
        e.addMethod(modifiers, "computeRec", "(I)I", null, true);
        e.addNoArgInstruction(ILOAD_0);
        e.addBranchInstruction(IFNE, "recurse");
        e.addNoArgInstruction(ICONST_1);
        e.addNoArgInstruction(IRETURN);
        e.addLabel("recurse");
        e.addNoArgInstruction(ILOAD_0);
        e.addNoArgInstruction(ILOAD_0);
        e.addNoArgInstruction(ICONST_1);
        e.addNoArgInstruction(ISUB);
        e.addMemberAccessInstruction(INVOKESTATIC, "Factorial", "computeRec", "(I)I");
        e.addNoArgInstruction(IMUL);
        e.addNoArgInstruction(IRETURN);

        // private static int computeIter(int n) {
        //     int result = 1;
        //     while (n > 0) {
        //         result = result * n--;
        //     }
        //     return result;
        // }
        modifiers.clear();
        modifiers.add("private");
        modifiers.add("static");
        e.addMethod(modifiers, "computeIter", "(I)I", null, true);
        e.addNoArgInstruction(ICONST_1);
        e.addNoArgInstruction(ISTORE_1);
        e.addLabel("continue");
        e.addNoArgInstruction(ILOAD_0);
        e.addNoArgInstruction(ICONST_0);
        e.addBranchInstruction(IF_ICMPLE, "done");
        e.addNoArgInstruction(ILOAD_1);
        e.addNoArgInstruction(ILOAD_0);
        e.addNoArgInstruction(IMUL);
        e.addNoArgInstruction(ISTORE_1);
        e.addIINCInstruction(0, -1);
        e.addBranchInstruction(GOTO, "continue");
        e.addLabel("done");
        e.addNoArgInstruction(ILOAD_1);
        e.addNoArgInstruction(IRETURN);
        e.write();
    }
}
