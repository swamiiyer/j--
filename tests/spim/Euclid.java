// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas
//
// Writes gcd(408, 1440) to standard output.

import jminusminus.SPIM;

public class Euclid {
    // Entry point.
    public static void main(String[] args) {
        SPIM.printString("gcd(408, 1440) = ");
        SPIM.printInt(Euclid.gcd(408, 1440));
        SPIM.printChar('\n');
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
