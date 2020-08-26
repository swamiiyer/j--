// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas
//
// Tests calls to functions with more than 4 arguments.

import jminusminus.SPIM;

public class Formals {
    // Entry point.
    public static void main(String[] args) {
        SPIM.printInt(Formals.sum(1, 2, 3, 4, 5, 6));
        SPIM.printChar('\n');
        SPIM.printInt(Formals.product(1, 2, 3, 4, 5, 6));
        SPIM.printChar('\n');
    }

    // Returns the sum of the arguments and a local variable.
    public static int sum(int a, int b, int c, int d, int e, int f) {
        int g = 7;
        return a + b + c + d + e + f + g;
    }

    // Returns the product of the arguments and a local variable.
    public static int product(int a, int b, int c, int d, int e, int f) {
        int g = 7;
        return a * b * c * d * e * f * g;
    }
}
