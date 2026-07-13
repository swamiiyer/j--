import java.util.ArrayList;
import jminusminus.CLEmitter;
import static jminusminus.CLConstants.*;

public class GenNHellos {
    public static void main(String[] args) {
        CLEmitter e = new CLEmitter(true);
        ArrayList<String> modifiers = new ArrayList<String>();

        // public class NHellos { ... }
        modifiers.add("public");
        e.addClass(modifiers, "NHellos", "java/lang/Object", null, true);

        // public static void main(String[] args) {
        //     int n = Integer.parseInt(args[0]);
        //     int i = 1;
        //     while (i <= n) {
        //         System.out.println("Hello # " + i);
        //         i += 1;
        //     }
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
        e.addNoArgInstruction(ICONST_1);
        e.addNoArgInstruction(ISTORE_2);
        e.addLabel("continue");
        e.addNoArgInstruction(ILOAD_2);
        e.addNoArgInstruction(ILOAD_1);
        e.addBranchInstruction(IF_ICMPGT, "done");
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        e.addReferenceInstruction(NEW, "java/lang/StringBuffer");
        e.addNoArgInstruction(DUP);
        e.addMemberAccessInstruction(INVOKESPECIAL, "java/lang/StringBuffer", "<init>", "()V");
        e.addLDCInstruction("Hello # ");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer", "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuffer;");
        e.addNoArgInstruction(ILOAD_2);
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer", "append", "(I)Ljava/lang/StringBuffer;");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer", "toString", "()Ljava/lang/String;");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
        e.addIINCInstruction(2, 1);
        e.addBranchInstruction(GOTO, "continue");
        e.addLabel("done");
        e.addNoArgInstruction(RETURN);

        e.write();
    }
}
