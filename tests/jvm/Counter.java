// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas
//
// A data type to represent a counter.

import java.lang.Integer;
import java.lang.System;
import java.util.Random;

public class Counter {
    private String id;
    private int count;

    // Constructs a counter.
    public Counter() {
        this("");
    }

    // Constructs a counter given its id.
    public Counter(String id) {
        this.id = id;
        this.count = 0;
    }

    // Increments this counter by 1.
    public void increment() {
        count += 1;
    }

    // Returns the current value of this counter.
    public int tally() {
        return count;
    }

    // Resets this counter to zero.
    public void reset() {
        count = 0;
    }

    // Returns true of this counter and other have the same tally, and false otherwise.
    public boolean equals(Counter other) {
        return this.tally() == other.tally();
    }

    // Returns a string representation of this counter.
    public String toString() {
        return tally() + " " + id;
    }

    // Unit tests the data type.
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        Counter heads = new Counter("heads");
        Counter tails = new Counter("tails");
        Random rng = new Random();
        int i = 1;
        while (i <= n) {
            if (rng.nextBoolean()) {
                heads.increment();
            } else {
                tails.increment();
            }
            ++i;
        }
        System.out.println((Object) heads);
        System.out.println((Object) tails);
        System.out.println("heads == tails? " + heads.equals(tails));
    }
}
