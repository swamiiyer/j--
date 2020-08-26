// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas
//
// Tests inheritance.

import java.lang.System;

abstract class Person {
    private static String organization = "University of Milkyway";
    protected int id;
    protected String name;
    protected String title;

    protected Person(int id, String name, String title) {
        this.id = id;
        this.name = name;
        this.title = title;
    }

    public String organization() {
        return organization;
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String title() {
        return title;
    }

    public String toString() {
        return organization() + ": " + name() + " (#" + id() + ", " + title() + ")";
    }
}

class Student extends Person {
    private int classOf;

    public Student(int id, String name, int classOf) {
        super(id, name, "Student");
        this.classOf = classOf;
    }

    public int classOf() {
        return classOf;
    }

    public String toString() {
        return organization() + ": " + name + " (#" + id + ", " + title + ", Class of " +
                classOf() + ")";
    }
}

class Faculty extends Person {
    public Faculty(int id, String name) {
        super(id, name, "Faculty");
    }
}

class Staff extends Person {
    public Staff(int id, String name) {
        super(id, name, "Staff");
    }
}

public class Inheritance {
    public static void main(String[] args) {
        Student a = new Student(1729, "Alice", 2024);
        Faculty b = new Faculty(42, "Bob");
        Staff c = new Staff(28, "Carol");
        System.out.println((Object) a);
        System.out.println((Object) b);
        System.out.println((Object) c);
    }
}