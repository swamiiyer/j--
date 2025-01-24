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
        int[][] B = {{A[0][0] * A[0][0] + A[0][1] * A[1][0], A[0][0] * A[0][1] + A[0][1] * A[1][1]}, 
                     {A[1][0] * A[0][0] + A[1][1] * A[1][0], A[1][0] * A[0][1] + A[1][1] * A[1][1]}};
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
