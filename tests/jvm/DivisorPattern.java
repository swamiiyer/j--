// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas
//
// Accepts n (int) as command-line argument; and writes to standard output a table where entry
// (i, j) is a "* " if j divides i or i divides j and a "  " otherwise.

import java.lang.Integer;
import java.lang.System;

public class DivisorPattern {
    // Entry point.
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int i = 1;
        while (i <= n) {
            int j = 1;
            while (j <= n) {
                if (!(mod(j, i) == 0) && !(mod(i, j) == 0)) {
                    System.out.print("  ");
                } else {
                    System.out.print("* ");
                }
                ++j;
            }
            System.out.println(i);
            ++i;
        }
    }

    // Returns the remainder of a (>= 0) and b (>= 0) computed recursively.
    private static int mod(int a, int b) {
        if (b == 0) {
            return a;
        } else if (a == b) {
            return 0;
        } else if (a > b) {
            return mod(a - b, b);
        }
        return a;
    }
}
