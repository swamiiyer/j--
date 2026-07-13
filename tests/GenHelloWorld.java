import java.util.ArrayList;
import jminusminus.CLEmitter;
import static jminusminus.CLConstants.*;

public class GenHelloWorld {
    public static void main(String[] args) {
        CLEmitter e = new CLEmitter(true);
        ArrayList<String> modifiers = new ArrayList<String>();

        // public class HelloWorld { ... }.
        modifiers.add("public");
        e.addClass(modifiers, "HelloWorld", "java/lang/Object", null, true);

        // public static void main(String[] args) {
        //     System.out.println("Hello, World");
        // }
        modifiers.clear();
        modifiers.add("public");
        modifiers.add("static");
        e.addMethod(modifiers, "main", "([Ljava/lang/String;)V", null, true);
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        e.addLDCInstruction("Hello, World");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
        e.addNoArgInstruction(RETURN);

        e.write();
    }
}
