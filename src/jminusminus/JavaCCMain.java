// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Driver class for j-- compiler using JavaCC front-end. This is the main entry point for the
 * compiler. The compiler proceeds as follows:
 * <ol>
 *   <li>It reads arguments that affects its behavior.</li>
 *
 *   <li>It builds a scanner.</li>
 *
 *   <li>It builds a parser (using the scanner) and parses the input for producing an abstact
 *   syntax tree (AST).</li>
 *
 *   <li>It sends the preAnalyze() message to that AST, which recursively descends the tree
 *   so far as the member headers for declaring types and members in the symbol table
 *   (represented as a string of contexts).</li>
 *
 *   <li>It sends the analyze() message to that AST for declaring local variables, and
 *   checking and assigning types to expressions. Analysis also sometimes rewrites some of the
 *   abstract syntax tree for clarifying the semantics. Analysis does all of this by recursively
 *   descending the AST down to its leaves.</li>
 *
 *   <li>Finally, it sends a codegen() message to the AST for generating code. Again,
 *   codegen() recursively descends the tree, down to its leaves, generating JVM code for
 *   producing a .class or .s (SPIM) file for each defined type (class).</li>
 * </ol>
 */
public class JavaCCMain {
    // Whether an error occurred during compilation.
    private static boolean errorHasOccurred;

    /**
     * Entry point.
     *
     * @param args the command-line arguments.
     */
    public static void main(String args[]) {
        String caller = "java jminusminus.JavaCCMain";
        String sourceFile = "";
        String debugOption = "";
        String outputDir = ".";
        boolean spimOutput = false;
        String registerAllocation = "";
        errorHasOccurred = false;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("javaccj--")) {
                caller = "javaccj--";
            } else if (args[i].endsWith(".java")) {
                sourceFile = args[i];
            } else if (args[i].equals("-t") || args[i].equals("-p")
                    || args[i].equals("-pa") || args[i].equals("-a")) {
                debugOption = args[i];
            } else if (args[i].endsWith("-d") && (i + 1) < args.length) {
                outputDir = args[++i];
            } else if (args[i].endsWith("-s") && (i + 1) < args.length) {
                spimOutput = true;
                registerAllocation = args[++i];
                if (!registerAllocation.equals("naive") &&
                        !registerAllocation.equals("linear") &&
                        !registerAllocation.equals("graph")
                        || registerAllocation.equals("")) {
                    printUsage(caller);
                    return;
                }
            } else if (args[i].endsWith("-r") && (i + 1) < args.length) {
                NPhysicalRegister.MAX_COUNT = Math.min(18, Integer
                        .parseInt(args[++i]));
                NPhysicalRegister.MAX_COUNT = Math.max(1,
                        NPhysicalRegister.MAX_COUNT);
            } else {
                printUsage(caller);
                return;
            }
        }
        if (sourceFile.equals("")) {
            printUsage(caller);
            return;
        }

        JavaCCParserTokenManager javaCCScanner = null;
        try {
            javaCCScanner = new JavaCCParserTokenManager(new
                    SimpleCharStream(new FileInputStream(sourceFile), 1, 1));
        } catch (FileNotFoundException e) {
            System.err.println("Error: file " + sourceFile + " not found.");
        }

        if (debugOption.equals("-t")) {
            // Just tokenize input and print the tokens to STDOUT.
            Token token;
            do {
                token = javaCCScanner.getNextToken();
                if (token.kind == JavaCCParserConstants.ERROR) {
                    System.err.printf("%s:%d: Unidentified input token: '%s'\n", sourceFile,
                            token.beginLine, token.image);
                    errorHasOccurred |= true;
                } else {
                    System.out.printf("%d\t : %s = %s\n", token.beginLine,
                            JavaCCParserConstants.tokenImage[token.kind], token.image);
                }
            } while (token.kind != JavaCCParserConstants.EOF);
            return;
        }

        // Parse input.
        JCompilationUnit ast = null;
        JavaCCParser javaCCParser = new JavaCCParser(javaCCScanner);
        javaCCParser.fileName(sourceFile);
        try {
            ast = javaCCParser.compilationUnit();
            errorHasOccurred |= javaCCParser.errorHasOccurred();
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }
        if (debugOption.equals("-p")) {
            JSONElement json = new JSONElement();
            ast.toJSON(json);
            System.out.println(json.toString());
            return;
        }
        if (errorHasOccurred) {
            return;
        }

        // Do pre-analysis.
        ast.preAnalyze();
        errorHasOccurred |= JAST.compilationUnit.errorHasOccurred();
        if (debugOption.equals("-pa")) {
            JSONElement json = new JSONElement();
            ast.toJSON(json);
            System.out.println(json.toString());
            return;
        }
        if (errorHasOccurred) {
            return;
        }

        // Do analysis.
        ast.analyze(null);
        errorHasOccurred |= JAST.compilationUnit.errorHasOccurred();
        if (debugOption.equals("-a")) {
            JSONElement json = new JSONElement();
            ast.toJSON(json);
            System.out.println(json.toString());
            return;
        }
        if (errorHasOccurred) {
            return;
        }

        // Generate JVM code.
        CLEmitter clEmitter = new CLEmitter(!spimOutput);
        clEmitter.destinationDir(outputDir);
        ast.codegen(clEmitter);
        errorHasOccurred |= clEmitter.errorHasOccurred();
        if (errorHasOccurred) {
            return;
        }

        // If SPIM output was asked for, convert the in-memory JVM instructions to SPIM using the
        // specified register allocation scheme.
        if (spimOutput) {
            NEmitter nEmitter = new NEmitter(sourceFile, ast.clFiles(), registerAllocation);
            nEmitter.destinationDir(outputDir);
            nEmitter.write();
            errorHasOccurred |= nEmitter.errorHasOccurred();
        }
    }

    // Prints command usage to STDOUT.
    private static void printUsage(String caller) {
        String usage = "Usage: " + caller
                + " <options> <source file>\n"
                + "Where possible options include:\n"
                + "  -t  Only tokenize input and print tokens to STDOUT\n"
                + "  -p  Only parse input and print AST to STDOUT\n"
                + "  -pa Only parse and pre-analyze input and print AST to STDOUT\n"
                + "  -a  Only parse, pre-analyze, and analyze input and print AST to STDOUT\n"
                + "  -s  <naive|linear|graph> Generate SPIM code\n"
                + "  -r  <num> Physical registers (1-18) available for allocation; default = 8\n"
                + "  -d  <dir> Specify where to place output files; default = .";
        System.out.println(usage);
    }
}
