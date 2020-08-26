// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas
//
// Tests various cast operations.

import java.lang.Boolean;
import java.lang.System;

public class Casts {
    // Entry point.
    public static void main(String[] args) {
        // Identity, narrowing, and widening.
        System.out.println((int) 42);
        System.out.println((char) 42);
        System.out.println((int) '*');

        // Boxing and un-boxing.
        boolean x = true;
        Boolean y = (Boolean) x;
        System.out.println((boolean) y);

        // Widening and narrowing refernce.
        Object s = (Object) new String("Hello, World");
        System.out.println((String) s);
    }
}