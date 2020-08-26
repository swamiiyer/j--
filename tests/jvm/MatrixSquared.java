// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas
//
// Accepts a (int), b (int), c (int), and d (int) as command-line arguments representing the
// elements of a 2x2 matrix A in row-major order; and writes to standard output the matrix A * A.

import java.lang.Integer;
import java.lang.System;

public class MatrixSquared {
    // Entry point.
    public static void main(String[] args) {
        int a = Integer.parseInt(args[0]);
        int b = Integer.parseInt(args[1]);
        int c = Integer.parseInt(args[2]);
        int d = Integer.parseInt(args[3]);
        int[][] A = {{a, b}, {c, d}};
        int[][] B = {{a * a + b * c, a * b + b * d}, {a * c + c * c, b * c + d * d}};
        write(B);
    }

    // Writes the specified (possibly ragged) array to standard output.
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
