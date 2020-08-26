// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas
//
// Writes 10! to standard output, computed both recursively and iteratively.

import jminusminus.SPIM;

public class Factorial {
    // Entry point.
    public static void main(String[] args) {
        SPIM.printString("10! = ");
        SPIM.printInt(Factorial.computeRec(10));
        SPIM.printString(" (computed recursively)\n");
        SPIM.printString("10! = ");
        SPIM.printInt(Factorial.computeIter(10));
        SPIM.printString(" (computed iteratively)\n");
    }

    // Returns n!, computed recursively.
    private static int computeRec(int n) {
        if (n == 0) {
            return 1;
        } else {
            return n * computeRec(n - 1);
        }
    }

    // Returns n!, computed iteratively.
    private static int computeIter(int n) {
        int result = 1;
        while (n > 0) {
            result = result * n--;
        }
        return result;
    }
}
