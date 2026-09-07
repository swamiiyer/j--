import java.lang.System;

// Abstract representation of a person at a university.
abstract class Person {
    private static String organization = "University of Milkyway";
    protected int id;
    protected String name;
    protected String title;

    // Constructs a Person object from the given arguments.
    protected Person(int id, String name, String title) {
        this.id = id;
        this.name = name;
        this.title = title;
    }

    // Returns this person's organization name.
    public String organization() {
        return organization;
    }

    // Returns this person's id.
    public int id() {
        return id;
    }

    // Returns this person's name.
    public String name() {
        return name;
    }

    // Returns this person's title.
    public String title() {
        return title;
    }

    // Returns a string representation of this person.
    public String toString() {
        return organization() + ": " + name() + " (#" + id() + ", " + title() + ")";
    }
}

// Representation of a student.
class Student extends Person {
    private int classOf;

    // Constructs a Student object from the given arguments.
    public Student(int id, String name, int classOf) {
        super(id, name, "Student");
        this.classOf = classOf;
    }

    // Returns the graduation year for this student.
    public int classOf() {
        return classOf;
    }

    // Returns a string representation of this student.
    public String toString() {
        return organization() + ": " + name + " (#" + id + ", " + title + ", Class of " + classOf() + ")";
    }
}

// Representation of a faculty member.
class Faculty extends Person {
    // Constructs a Faculty object from the given arguments.
    public Faculty(int id, String name) {
        super(id, name, "Faculty");
    }
}

// Representation of a staff member.
class Staff extends Person {
    // Constructs a Staff object from the given arguments.
    public Staff(int id, String name) {
        super(id, name, "Staff");
    }
}

public class Inheritance {
    // Entry point.
    public static void main(String[] args) {
        Student a = new Student(1729, "Alice", 2048);
        Faculty b = new Faculty(42, "Bob");
        Staff c = new Staff(28, "Carol");
        System.out.println((Object) a);
        System.out.println((Object) b);
        System.out.println((Object) c);
    }
}