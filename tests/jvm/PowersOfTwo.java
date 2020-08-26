// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas
//
// Accepts n (int) as command-line argument; and writes to standard output a table of powers of 2
// that are less than or equal to 2^n.

import java.lang.Integer;
import java.lang.System;

public class PowersOfTwo {
    // Entry point.
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int i = -1, power = 1;
        while (++i <= n) {
            System.out.println(i + " " + power);
            power = power * 2;
        }
    }
}
