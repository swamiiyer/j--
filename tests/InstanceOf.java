// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas
//
// Tests the instanceof operator.

import java.lang.System;

public class InstanceOf {
    // Entry point.
    public static void main(String[] args) {
        Object a = new Object();
        String b = "42";
        System.out.println(a instanceof Object);
        System.out.println(b instanceof String);
        System.out.println(b instanceof Object);
        System.out.println(a instanceof String);
    }
}