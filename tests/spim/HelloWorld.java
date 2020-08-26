// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas
//
// Writes the message "Hello, World" to standard output.

import jminusminus.SPIM;

public class HelloWorld {
    // Entry point.
    public static void main(String[] args) {
        SPIM.printString("Hello, World");
        SPIM.printString("\n");
    }
}
