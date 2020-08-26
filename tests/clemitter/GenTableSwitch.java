// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas.

import java.util.ArrayList;

import jminusminus.CLEmitter;

import static jminusminus.CLConstants.*;

/**
 * This class programatically generates the class file for the following Java application:
 *
 * <pre>
 * public class TableSwitch {
 *     public static void main(String[] args) {
 *         int month = Integer.parseInt(args[0]);
 *         String monthString;
 *         switch (month) {
 *             case 1:
 *                 monthString = "January";
 *                 break;
 *             case 2:
 *                 monthString = "February";
 *                 break;
 *             case 3:
 *                 monthString = "March";
 *                 break;
 *             case 4:
 *                 monthString = "April";
 *                 break;
 *             case 5:
 *                 monthString = "May";
 *                 break;
 *             case 6:
 *                 monthString = "June";
 *                 break;
 *             case 7:
 *                 monthString = "July";
 *                 break;
 *             case 8:
 *                 monthString = "August";
 *                 break;
 *             case 9:
 *                 monthString = "September";
 *                 break;
 *             case 10:
 *                 monthString = "October";
 *                 break;
 *             case 11:
 *                 monthString = "November";
 *                 break;
 *             case 12:
 *                 monthString = "December";
 *                 break;
 *             default:
 *                 monthString = "Error!";
 *         }
 *         System.out.println(monthString);
 *     }
 * }
 * </pre>
 */
public class GenTableSwitch {
    public static void main(String[] args) {
        // Create a CLEmitter instance
        CLEmitter e = new CLEmitter(true);

        // Create an ArrayList instance to store modifiers
        ArrayList<String> modifiers = new ArrayList<String>();

        // public class TableSwitch {
        modifiers.add("public");
        e.addClass(modifiers, "TableSwitch", "java/lang/Object", null, true);

        // public static void main(String[] args) {
        modifiers.clear();
        modifiers.add("public");
        modifiers.add("static");
        e.addMethod(modifiers, "main", "([Ljava/lang/String;)V", null, true);

        // int month = Integer.parseInt(args[0]);
        e.addNoArgInstruction(ALOAD_0);
        e.addNoArgInstruction(ICONST_0);
        e.addNoArgInstruction(AALOAD);
        e.addMemberAccessInstruction(INVOKESTATIC, "java/lang/Integer", "parseInt",
                "(Ljava/lang/String;)I");
        e.addNoArgInstruction(ISTORE_1);

        // switch(month) {
        e.addNoArgInstruction(ILOAD_1);
        ArrayList<String> labels = new ArrayList<String>();
        labels.add("Case1");
        labels.add("Case2");
        labels.add("Case3");
        labels.add("Case4");
        labels.add("Case5");
        labels.add("Case6");
        labels.add("Case7");
        labels.add("Case8");
        labels.add("Case9");
        labels.add("Case10");
        labels.add("Case11");
        labels.add("Case12");
        e.addTABLESWITCHInstruction("Default", 1, 12, labels);

        // case 1:
        //     monthString = "January";
        //     break;
        e.addLabel("Case1");
        e.addLDCInstruction("January");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "EndSwitch");

        // case 2:
        //     monthString = "February";
        //     break;
        e.addLabel("Case2");
        e.addLDCInstruction("February");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "EndSwitch");

        // case 3:
        //     monthString = "March";
        //     break;
        e.addLabel("Case3");
        e.addLDCInstruction("March");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "EndSwitch");

        // case 4:
        //     monthString = "April";
        //     break;
        e.addLabel("Case4");
        e.addLDCInstruction("April");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "EndSwitch");

        // case 5:
        //     monthString = "May";
        //     break;
        e.addLabel("Case5");
        e.addLDCInstruction("May");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "EndSwitch");

        // case 6:
        //     monthString = "June";
        //     break;
        e.addLabel("Case6");
        e.addLDCInstruction("June");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "EndSwitch");

        // case 7:
        //     monthString = "July";
        //     break;
        e.addLabel("Case7");
        e.addLDCInstruction("July");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "EndSwitch");

        // case 8:
        //     monthString = "August";
        //     break;
        e.addLabel("Case8");
        e.addLDCInstruction("August");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "EndSwitch");

        // case 9:
        //     monthString = "September";
        //     break;
        e.addLabel("Case9");
        e.addLDCInstruction("September");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "EndSwitch");

        // case 10:
        //     monthString = "October";
        //     break;
        e.addLabel("Case10");
        e.addLDCInstruction("October");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "EndSwitch");

        // case 11:
        //     monthString = "November";
        //     break;
        e.addLabel("Case11");
        e.addLDCInstruction("November");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "EndSwitch");

        // case 12:
        //     monthString = "December";
        //     break;
        e.addLabel("Case12");
        e.addLDCInstruction("December");
        e.addNoArgInstruction(ASTORE_2);
        e.addBranchInstruction(GOTO, "EndSwitch");

        // default:
        //     monthString = "Error!";
        e.addLabel("Default");
        e.addLDCInstruction("Error!");
        e.addNoArgInstruction(ASTORE_2);

        // End of switch(month)
        e.addLabel("EndSwitch");

        // System.out.println(monthString);
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        e.addNoArgInstruction(ALOAD_2);
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V");

        // return;
        e.addNoArgInstruction(RETURN);

        // Write TableSwitch.class to file system
        e.write();
    }
}
