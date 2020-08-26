// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas
//
// Accepts n (int) as command-line argument; and writes to standard output all Fibonacci numbers
// below n.

import java.lang.Integer;
import java.lang.System;

public class Fibonacci {
    // Entry point.
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int a = 0;
        int b = 1;
        while (n > b) {
            System.out.println(b);
            int t = a;
            a = b;
            b += t;
        }
    }
}
