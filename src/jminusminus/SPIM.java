// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

/**
 * This is a Java wrapper class for the SPIM runtime file SPIM.s. Any j-- program that's
 * compiled for the SPIM target must import this class for console IO operations. Note that the
 * functions have no implementations here which means that if the programs using this class are
 * compiled using j--, they will compile fine but won't function as desired when run against the
 * JVM. Such programs must be compiled using the j-- compiler for the SPIM target and must be run
 * against the SPIM simulator.
 */
public class SPIM {
    /**
     * Prints an integer to the console.
     *
     * @param value the integer.
     */
    public static void printInt(int value) {
    }

    /**
     * Prints a float to the console.
     *
     * @param value the float.
     */
    public static void printFloat(float value) {
    }

    /**
     * Prints a double to the console.
     *
     * @param value the double.
     */
    public static void printDouble(double value) {
    }

    /**
     * Prints a string to the console.
     *
     * @param value the string.
     */
    public static void printString(String value) {
    }

    /**
     * Prints a char to the console.
     *
     * @param value the char.
     */
    public static void printChar(char value) {
    }

    /**
     * Reads and returns an integer from the console.
     *
     * @return an integer from the console.
     */
    public static int readInt() {
        return 0;
    }

    /**
     * Reads and returns a float from the console.
     *
     * @return a float from the console.
     */
    public static float readFloat() {
        return 0;
    }

    /**
     * Reads and returns a double from the console.
     *
     * @return a double from the console.
     */
    public static double readDouble() {
        return 0;
    }

    /**
     * Reads and returns a string from the console.
     *
     * @return a string from the console.
     */
    public static String readString(int length) {
        return null;
    }

    /**
     * Reads and returns a char from the console.
     *
     * @return a char from the console.
     */
    public static char readChar() {
        return ' ';
    }

    /**
     * Exits SPIM.
     */
    public static void exit() {
    }

    /**
     * Exits SPIM with the given code.
     *
     * @param code the exit code.
     */
    public static void exit2(int code) {
    }
}
