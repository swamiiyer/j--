## *j--* Compiler

*j--* is a compiler for a language (also called *j--*) that is a 
non-trivial subset of the Java programming language. Refer to 
[The *j--* Language Specification](langspec) for the formal 
details about the *j--* language.The compiler. The *j--* compiler 
generates code for the Java Virtual Machine (JVM).

The following command compiles the compiler:
```bash
$ ant
```

The following command runs the compiler and prints the usage string:
```bash
$ ./bin/j-- 
```

The following command compiles a test *j--* program `HelloWorld.java` 
using the *j--* compiler, which translates the program into a JVM 
bytecode program `HelloWorld.class`:
```bash
$ ./bin/j-- tests/HelloWorld.java
```

The following command runs the `HelloWorld.class` program using the 
JVM:
```bash
$ java HelloWorld
```

## Software Dependencies

* [OpenJDK](https://openjdk.org/)
* [Ant](https://ant.apache.org/)