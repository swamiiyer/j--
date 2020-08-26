// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas
//
// Writes to standard output all Fibonacci numbers below 1000.

import jminusminus.SPIM;

public class Fibonacci {
    // Entry point.
    public static void main(String[] args) {
        int a = 0;
        int b = 1;
        while (1000 > b) {
            SPIM.printInt(b);
            SPIM.printChar('\n');
            int t = a;
            a = b;
            b += t;
        }
    }
}
