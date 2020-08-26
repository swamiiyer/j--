// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas
//
// Accepts a (int) and b (int) as command-line arguments; and writes to standard output the
// greatest common divisor (GCD) of a and b.

import java.lang.Integer;
import java.lang.System;

public class Euclid {
    // Entry point.
    public static void main(String[] args) {
        int a = Integer.parseInt(args[0]);
        int b = Integer.parseInt(args[1]);
        System.out.println("gcd(" + a + ", " + b + ") = " + gcd(a, b));
    }

    // Returns gcd(a, b) computed recursively using Euclid's algorithm.
    private static int gcd(int a, int b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, mod(a, b));
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
