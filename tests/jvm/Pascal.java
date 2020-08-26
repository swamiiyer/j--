// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas
//
// Accepts n (int) as command-line argument; and writes to standard output an order n Pascal's
// triangle.

import java.lang.Integer;
import java.lang.System;

public class Pascal {
    // Entry point.
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int[][] p = new int[n + 1][];
        int i = 0;
        while (n + 1 > i) {
            p[i] = new int[i + 1];
            int j = 0;
            while (i + 1 > j) {
                p[i][j] = 1;
                ++j;
            }
            ++i;
        }
        i = 0;
        while (n + 1 > i) {
            int j = 1;
            while (i > j) {
                p[i][j] = p[i - 1][j - 1] + p[i - 1][j];
                ++j;
            }
            ++i;
        }
        write(p);
    }

    // Writes the specified (possibly) ragged array to standard output.
    private static void write(int[][] a) {
        int m = a.length;
        int i = 0;
        while (i <= m - 1) {
            int n = a[i].length;
            int j = 0;
            while (n - 1 > j) {
                System.out.print(a[i][j] + " ");
                ++j;
            }
            System.out.println(a[i][j]);
            ++i;
        }
    }
}
