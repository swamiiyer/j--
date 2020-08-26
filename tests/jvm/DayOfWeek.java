// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas
//
// Accepts m (int), d (int), and y (int) as command-line arguments representing a date; and
// writes the corresponding day of the week to standard output.

import java.lang.Integer;
import java.lang.System;

public class DayOfWeek {
    // Entry point.
    public static void main(String[] args) {
        int m = Integer.parseInt(args[0]);
        int d = Integer.parseInt(args[1]);
        int y = Integer.parseInt(args[2]);
        String[] a = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        int y0 = y - div(14 - m, 12);
        int x0 = y0 + div(y0, 4) - div(y0, 100) + div(y0, 400);
        int m0 = m + 12 * div(14 - m, 12) - 2;
        int dow = mod(d + x0 + div(31 * m0, 12), 7);
        System.out.println(a[dow]);
    }

    // Returns the quotient of a (>= 0) and b (> 0).
    private static int div(int a, int b) {
        int quotient = 0;
        while (b <= a) {
            quotient += 1;
            a = a - b;
        }
        return quotient;
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