import java.util.ArrayList;
import jminusminus.CLEmitter;
import static jminusminus.CLConstants.*;

public class GenTableSwitch {
    public static void main(String[] args) {
        CLEmitter e = new CLEmitter(true);
        ArrayList<String> modifiers = new ArrayList<String>();

        // public class TableSwitch { ... }.
        modifiers.add("public");
        e.addClass(modifiers, "TableSwitch", "java/lang/Object", null, true);

        // public static void main(String[] args) {
        //     int month = Integer.parseInt(args[0]);
        //     String monthString;
        //     switch (month) {
        //         case 1:
        //             monthString = "January";
        //             break;
        //         case 2:
        //             monthString = "February";
        //             break;
        //         case 3:
        //             monthString = "March";
        //             break;
        //         case 4:
        //             monthString = "April";
        //             break;
        //         case 5:
        //             monthString = "May";
        //             break;
        //         case 6:
        //             monthString = "June";
        //             break;
        //         case 7:
        //             monthString = "July";
        //             break;
        //         case 8:
        //             monthString = "August";
        //             break;
        //         case 9:
        //             monthString = "September";
        //             break;
        //         case 10:
        //             monthString = "October";
        //             break;
        //         case 11:
        //             monthString = "November";
        //             break;
        //         case 12:
        //             monthString = "December";
        //             break;
        //         default:
        //             monthString = "Error!";
        //     }
        //     System.out.println(monthString);
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

        // switch(month) { ... }.
        e.addNoArgInstruction(ILOAD_1);
        ArrayList<String> labels = new ArrayList<String>();
        labels.add("case1");
        labels.add("case2");
        labels.add("case3");
        labels.add("case4");
        labels.add("case5");
        labels.add("case6");
        labels.add("case7");
        labels.add("case8");
        labels.add("case9");
        labels.add("case10");
        labels.add("case11");
        labels.add("case12");
        e.addTABLESWITCHInstruction("default", 1, 12, labels);

        // case 1: { ... }.
        e.addLabel("case1");
        e.addLDCInstruction("January");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "endSwitch");

        // case 2: { ... }.
        e.addLabel("case2");
        e.addLDCInstruction("February");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "endSwitch");

        // case 3: { ... }.
        e.addLabel("case3");
        e.addLDCInstruction("March");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "endSwitch");

        // case 4: { ... }.
        e.addLabel("case4");
        e.addLDCInstruction("April");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "endSwitch");

        // case 5: { ... }.
        e.addLabel("case5");
        e.addLDCInstruction("May");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "endSwitch");

        // case 6: { ... }.
        e.addLabel("case6");
        e.addLDCInstruction("June");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "endSwitch");

        // case 7: { ... }.
        e.addLabel("case7");
        e.addLDCInstruction("July");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "endSwitch");

        // case 8: { ... }.
        e.addLabel("case8");
        e.addLDCInstruction("August");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "endSwitch");

        // case 9: { ... }.
        e.addLabel("case9");
        e.addLDCInstruction("September");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "endSwitch");

        // case 10: { ... }.
        e.addLabel("case10");
        e.addLDCInstruction("October");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "endSwitch");

        // case 11: { ... }.
        e.addLabel("case11");
        e.addLDCInstruction("November");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "endSwitch");

        // case 12: { ... }.
        e.addLabel("case12");
        e.addLDCInstruction("December");
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
