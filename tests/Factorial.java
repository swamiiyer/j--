// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas
//
// Accepts n (int) as command-line argument; and writes n! to standard output.

import java.lang.Integer;
import java.lang.System;

public class Factorial {
    // Entry point.
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        System.out.println(n + "! = " + Factorial.computeRec(n) + " (computed recursively)");
        System.out.println(n + "! = " + Factorial.computeIter(n) + " (computed iteratively)");
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
