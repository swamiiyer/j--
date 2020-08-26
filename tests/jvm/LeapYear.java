// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas
//
// Accepts a year (int) as command-line argument; and writes to standard output whether the year
// is a leap year or not.

import java.lang.Integer;
import java.lang.System;

public class LeapYear {
    // Entry point.
    public static void main(String[] args) {
        int year = Integer.parseInt(args[0]);
        boolean isLeapYear = mod(year, 4) == 0 && !(mod(year, 100) == 0);
        if (!isLeapYear) {
            isLeapYear = mod(year, 400) == 0;
        }
        System.out.println(isLeapYear);
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
