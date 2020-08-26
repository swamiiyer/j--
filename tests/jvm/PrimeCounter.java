// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas
//
// Accepts n (int) as command-line argument; and writes to standard output the number of primes
// that are less than or equal to n.

import java.lang.Integer;
import java.lang.System;

public class PrimeCounter {
    // Entry point.
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        System.out.println("pi(" + n + ") = " + primes(n));
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

    // Returns true if x is a prime; and false otherwise.
    private static boolean isPrime(int x) {
        int i = 2;
        while (i * i <= x) {
            if (mod(x, i) == 0) {
                return false;
            }
            ++i;
        }
        return true;
    }

    // Returns the number of primes that are less than or equal to n.
    private static int primes(int n) {
        int count = 0;
        int i = 2;
        while (i <= n) {
            if (isPrime(i)) {
                count += 1;
            }
            ++i;
        }
        return count;
    }
}
