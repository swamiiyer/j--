// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;

import static jminusminus.TokenKind.*;

/**
 * A recursive descent parser that, given a lexical analyzer (a LookaheadScanner), parses a j--
 * compilation unit (program file), taking tokens from the LookaheadScanner, and produces an
 * abstract syntax tree (AST) for it.
 */
public class Parser {
    // The lexical analyzer with which tokens are scanned.
    private LookaheadScanner scanner;

    // Whether a parser error has been found.
    private boolean isInError;

    // Whether we have recovered from a parser error.
    private boolean isRecovered;

    /**
     * Constructs a parser from the given lexical analyzer.
     *
     * @param scanner the lexical analyzer with which tokens are scanned.
     */
    public Parser(LookaheadScanner scanner) {
        this.scanner = scanner;
        isInError = false;
        isRecovered = true;

        // Prime the pump.
        scanner.next();
    }

    /**
     * Returns true if a parser error has occurred up to now, and false otherwise.
     *
     * @return true if a parser error has occurred up to now, and false otherwise.
     */
    public boolean errorHasOccurred() {
        return isInError;
    }

    /**
     * Parses a compilation unit (a program file) and returns an AST for it.
     *
     * <pre>
     *     compilationUnit ::= [ PACKAGE qualifiedIdentifier SEMI ]
     *                         { IMPORT  qualifiedIdentifier SEMI }
     *                         { typeDeclaration }
     *                         EOF
     * </pre>
     *
     * @return an AST for a compilation unit.
     */
    public JCompilationUnit compilationUnit() {
        int line = scanner.token().line();
        String fileName = scanner.fileName();
        TypeName packageName = null;
        if (have(PACKAGE)) {
            packageName = qualifiedIdentifier();
            mustBe(SEMI);
        }
        ArrayList<TypeName> imports = new ArrayList<TypeName>();
        while (have(IMPORT)) {
            imports.add(qualifiedIdentifier());
            mustBe(SEMI);
        }
        ArrayList<JAST> typeDeclarations = new ArrayList<JAST>();
        while (!see(EOF)) {
            JAST typeDeclaration = typeDeclaration();
            if (typeDeclaration != null) {
                typeDeclarations.add(typeDeclaration);
            }
        }
        mustBe(EOF);
        return new JCompilationUnit(fileName, line, packageName, imports, typeDeclarations);
    }

    /**
     * Parses and returns a qualified identifier.
     *
     * <pre>
     *   qualifiedIdentifier ::= IDENTIFIER { DOT IDENTIFIER }
     * </pre>
     *
     * @return a qualified identifier.
     */
    private TypeName qualifiedIdentifier() {
        int line = scanner.token().line();
        mustBe(IDENTIFIER);
        String qualifiedIdentifier = scanner.previousToken().image();
        while (have(DOT)) {
            mustBe(IDENTIFIER);
            qualifiedIdentifier += "." + scanner.previousToken().image();
        }
        return new TypeName(line, qualifiedIdentifier);
    }

    /**
     * Parses a type declaration and returns an AST for it.
     *
     * <pre>
     *   typeDeclaration ::= modifiers classDeclaration
     * </pre>
     *
     * @return an AST for a type declaration.
     */
    private JAST typeDeclaration() {
        ArrayList<String> mods = modifiers();
        return classDeclaration(mods);
    }

    /**
     * Parses and returns a list of modifiers.
     *
     * <pre>
     *   modifiers ::= { ABSTRACT | PRIVATE | PROTECTED | PUBLIC | STATIC }
     * </pre>
     *
     * @return a list of modifiers.
     */
    private ArrayList<String> modifiers() {
        ArrayList<String> mods = new ArrayList<String>();
        boolean scannedPUBLIC = false;
        boolean scannedPROTECTED = false;
        boolean scannedPRIVATE = false;
        boolean scannedSTATIC = false;
        boolean scannedABSTRACT = false;
        boolean more = true;
        while (more) {
            if (have(ABSTRACT)) {
                mods.add("abstract");
                if (scannedABSTRACT) {
                    reportParserError("Repeated modifier: abstract");
                }
                scannedABSTRACT = true;
            } else if (have(PRIVATE)) {
                mods.add("private");
                if (scannedPRIVATE) {
                    reportParserError("Repeated modifier: private");
                }
                if (scannedPUBLIC || scannedPROTECTED) {
                    reportParserError("Access conflict in modifiers");
                }
                scannedPRIVATE = true;
            } else if (have(PROTECTED)) {
                mods.add("protected");
                if (scannedPROTECTED) {
                    reportParserError("Repeated modifier: protected");
                }
                if (scannedPUBLIC || scannedPRIVATE) {
                    reportParserError("Access conflict in modifiers");
                }
                scannedPROTECTED = true;
            } else if (have(PUBLIC)) {
                mods.add("public");
                if (scannedPUBLIC) {
                    reportParserError("Repeated modifier: public");
                }
                if (scannedPROTECTED || scannedPRIVATE) {
                    reportParserError("Access conflict in modifiers");
                }
                scannedPUBLIC = true;
            } else if (have(STATIC)) {
                mods.add("static");
                if (scannedSTATIC) {
                    reportParserError("Repeated modifier: static");
                }
                scannedSTATIC = true;
            } else if (have(ABSTRACT)) {
                mods.add("abstract");
                if (scannedABSTRACT) {
                    reportParserError("Repeated modifier: abstract");
                }
                scannedABSTRACT = true;
            } else {
                more = false;
            }
        }
        return mods;
    }

    /**
     * Parses a class declaration and returns an AST for it.
     *
     * <pre>
     *   classDeclaration ::= CLASS IDENTIFIER [ EXTENDS qualifiedIdentifier ] classBody
     * </pre>
     *
     * @param mods the class modifiers.
     * @return an AST for a class declaration.
     */
    private JClassDeclaration classDeclaration(ArrayList<String> mods) {
        int line = scanner.token().line();
        mustBe(CLASS);
        mustBe(IDENTIFIER);
        String name = scanner.previousToken().image();
        Type superClass;
        if (have(EXTENDS)) {
            superClass = qualifiedIdentifier();
        } else {
            superClass = Type.OBJECT;
        }
        return new JClassDeclaration(line, mods, name, superClass, null, classBody());
    }

    /**
     * Parses a class body and returns a list of members in the body.
     *
     * <pre>
     *   classBody ::= LCURLY { modifiers memberDecl } RCURLY
     * </pre>
     *
     * @return a list of members in the class body.
     */
    private ArrayList<JMember> classBody() {
        ArrayList<JMember> members = new ArrayList<JMember>();
        mustBe(LCURLY);
        while (!see(RCURLY) && !see(EOF)) {
            ArrayList<String> mods = modifiers();
            members.add(memberDecl(mods));
        }
        mustBe(RCURLY);
        return members;
    }

    /**
     * Parses a member declaration and returns an AST for it.
     *
     * <pre>
     *   memberDecl ::= IDENTIFIER formalParameters block
     *                | ( VOID | type ) IDENTIFIER formalParameters ( block | SEMI )
     *                | type variableDeclarators SEMI
     * </pre>
     *
     * @param mods the class member modifiers.
     * @return an AST for a member declaration.
     */
    private JMember memberDecl(ArrayList<String> mods) {
        int line = scanner.token().line();
        JMember memberDecl = null;
        if (seeIdentLParen()) {
            // A constructor.
            mustBe(IDENTIFIER);
            String name = scanner.previousToken().image();
            ArrayList<JFormalParameter> params = formalParameters();
            JBlock body = block();
            memberDecl = new JConstructorDeclaration(line, mods, name, params, null, body);
        } else {
            Type type = null;
            if (have(VOID)) {
                // A void method.
                type = Type.VOID;
                mustBe(IDENTIFIER);
                String name = scanner.previousToken().image();
                ArrayList<JFormalParameter> params = formalParameters();
                JBlock body = have(SEMI) ? null : block();
                memberDecl = new JMethodDeclaration(line, mods, name, type, params, null, body);
            } else {
                type = type();
                if (seeIdentLParen()) {
                    // A non void method.
                    mustBe(IDENTIFIER);
                    String name = scanner.previousToken().image();
                    ArrayList<JFormalParameter> params = formalParameters();
                    JBlock body = have(SEMI) ? null : block();
                    memberDecl = new JMethodDeclaration(line, mods, name, type, params, null, body);
                } else {
                    // A field.
                    memberDecl = new JFieldDeclaration(line, mods, variableDeclarators(type));
                    mustBe(SEMI);
                }
            }
        }
        return memberDecl;
    }

    /**
     * Parses a block and returns an AST for it.
     *
     * <pre>
     *   block ::= LCURLY { blockStatement } RCURLY
     * </pre>
     *
     * @return an AST for a block.
     */
    private JBlock block() {
        int line = scanner.token().line();
        ArrayList<JStatement> statements = new ArrayList<JStatement>();
        mustBe(LCURLY);
        while (!see(RCURLY) && !see(EOF)) {
            statements.add(blockStatement());
        }
        mustBe(RCURLY);
        return new JBlock(line, statements);
    }

    /**
     * Parses a block statement and returns an AST for it.
     *
     * <pre>
     *   blockStatement ::= localVariableDeclarationStatement
     *                    | statement
     * </pre>
     *
     * @return an AST for a block statement.
     */
    private JStatement blockStatement() {
        if (seeLocalVariableDeclaration()) {
            return localVariableDeclarationStatement();
        } else {
            return statement();
        }
    }

    /**
     * Parses a statement and returns an AST for it.
     *
     * <pre>
     *   statement ::= block
     *               | IF parExpression statement [ ELSE statement ]
     *               | RETURN [ expression ] SEMI
     *               | SEMI
     *               | WHILE parExpression statement
     *               | statementExpression SEMI
     * </pre>
     *
     * @return an AST for a statement.
     */
    private JStatement statement() {
        int line = scanner.token().line();
        if (see(LCURLY)) {
            return block();
        } else if (have(IF)) {
            JExpression test = parExpression();
            JStatement consequent = statement();
            JStatement alternate = have(ELSE) ? statement() : null;
            return new JIfStatement(line, test, consequent, alternate);
        } else if (have(RETURN)) {
            if (have(SEMI)) {
                return new JReturnStatement(line, null);
            } else {
                JExpression expr = expression();
                mustBe(SEMI);
                return new JReturnStatement(line, expr);
            }
        } else if (have(SEMI)) {
            return new JEmptyStatement(line);
        } else if (have(WHILE)) {
            JExpression test = parExpression();
            JStatement statement = statement();
            return new JWhileStatement(line, test, statement);
        } else {
            // Must be a statementExpression.
            JStatement statement = statementExpression();
            mustBe(SEMI);
            return statement;
        }
    }

    /**
     * Parses and returns a list of formal parameters.
     *
     * <pre>
     *   formalParameters ::= LPAREN [ formalParameter { COMMA formalParameter } ] RPAREN
     * </pre>
     *
     * @return a list of formal parameters.
     */
    private ArrayList<JFormalParameter> formalParameters() {
        ArrayList<JFormalParameter> parameters = new ArrayList<JFormalParameter>();
        mustBe(LPAREN);
        if (have(RPAREN)) {
            return parameters;
        }
        do {
            parameters.add(formalParameter());
        } while (have(COMMA));
        mustBe(RPAREN);
        return parameters;
    }

    /**
     * Parses a formal parameter and returns an AST for it.
     *
     * <pre>
     *   formalParameter ::= type IDENTIFIER
     * </pre>
     *
     * @return an AST for a formal parameter.
     */
    private JFormalParameter formalParameter() {
        int line = scanner.token().line();
        Type type = type();
        mustBe(IDENTIFIER);
        String name = scanner.previousToken().image();
        return new JFormalParameter(line, name, type);
    }

    /**
     * Parses a parenthesized expression and returns an AST for it.
     *
     * <pre>
     *   parExpression ::= LPAREN expression RPAREN
     * </pre>
     *
     * @return an AST for a parenthesized expression.
     */
    private JExpression parExpression() {
        mustBe(LPAREN);
        JExpression expr = expression();
        mustBe(RPAREN);
        return expr;
    }

    /**
     * Parses a local variable declaration statement and returns an AST for it.
     *
     * <pre>
     *   localVariableDeclarationStatement ::= type variableDeclarators SEMI
     * </pre>
     *
     * @return an AST for a local variable declaration statement.
     */
    private JVariableDeclaration localVariableDeclarationStatement() {
        int line = scanner.token().line();
        Type type = type();
        ArrayList<JVariableDeclarator> vdecls = variableDeclarators(type);
        mustBe(SEMI);
        return new JVariableDeclaration(line, vdecls);
    }

    /**
     * Parses and returns a list of variable declarators.
     *
     * <pre>
     *   variableDeclarators ::= variableDeclarator { COMMA variableDeclarator }
     * </pre>
     *
     * @param type type of the variables.
     * @return a list of variable declarators.
     */
    private ArrayList<JVariableDeclarator> variableDeclarators(Type type) {
        ArrayList<JVariableDeclarator> variableDeclarators = new ArrayList<JVariableDeclarator>();
        do {
            variableDeclarators.add(variableDeclarator(type));
        } while (have(COMMA));
        return variableDeclarators;
    }

    /**
     * Parses a variable declarator and returns an AST for it.
     *
     * <pre>
     *   variableDeclarator ::= IDENTIFIER [ ASSIGN variableInitializer ]
     * </pre>
     *
     * @param type type of the variable.
     * @return an AST for a variable declarator.
     */
    private JVariableDeclarator variableDeclarator(Type type) {
        int line = scanner.token().line();
        mustBe(IDENTIFIER);
        String name = scanner.previousToken().image();
        JExpression initial = have(ASSIGN) ? variableInitializer(type) : null;
        return new JVariableDeclarator(line, name, type, initial);
    }

    /**
     * Parses a variable initializer and returns an AST for it.
     *
     * <pre>
     *   variableInitializer ::= arrayInitializer | expression
     * </pre>
     *
     * @param type type of the variable.
     * @return an AST for a variable initializer.
     */
    private JExpression variableInitializer(Type type) {
        if (see(LCURLY)) {
            return arrayInitializer(type);
        }
        return expression();
    }

    /**
     * Parses an array initializer and returns an AST for it.
     *
     * <pre>
     *   arrayInitializer ::= LCURLY [ variableInitializer { COMMA variableInitializer }
     *                                 [ COMMA ] ] RCURLY
     * </pre>
     *
     * @param type type of the array.
     * @return an AST for an array initializer.
     */
    private JArrayInitializer arrayInitializer(Type type) {
        int line = scanner.token().line();
        ArrayList<JExpression> initials = new ArrayList<JExpression>();
        mustBe(LCURLY);
        if (have(RCURLY)) {
            return new JArrayInitializer(line, type, initials);
        }
        initials.add(variableInitializer(type.componentType()));
        while (have(COMMA)) {
            initials.add(see(RCURLY) ? null : variableInitializer(type.componentType()));
        }
        mustBe(RCURLY);
        return new JArrayInitializer(line, type, initials);
    }

    /**
     * Parses and returns a list of arguments.
     *
     * <pre>
     *   arguments ::= LPAREN [ expression { COMMA expression } ] RPAREN
     * </pre>
     *
     * @return a list of arguments.
     */
    private ArrayList<JExpression> arguments() {
        ArrayList<JExpression> args = new ArrayList<JExpression>();
        mustBe(LPAREN);
        if (have(RPAREN)) {
            return args;
        }
        do {
            args.add(expression());
        } while (have(COMMA));
        mustBe(RPAREN);
        return args;
    }

    /**
     * Parses and returns a type.
     *
     * <pre>
     *   type ::= referenceType | basicType
     * </pre>
     *
     * @return a type.
     */
    private Type type() {
        if (seeReferenceType()) {
            return referenceType();
        }
        return basicType();
    }

    /**
     * Parses and returns a basic type.
     *
     * <pre>
     *   basicType ::= BOOLEAN | CHAR | INT
     * </pre>
     *
     * @return a basic type.
     */
    private Type basicType() {
        if (have(BOOLEAN)) {
            return Type.BOOLEAN;
        } else if (have(CHAR)) {
            return Type.CHAR;
        } else if (have(INT)) {
            return Type.INT;
        } else {
            reportParserError("Type sought where %s found", scanner.token().image());
            return Type.ANY;
        }
    }

    /**
     * Parses and returns a reference type.
     *
     * <pre>
     *   referenceType ::= basicType LBRACK RBRACK { LBRACK RBRACK }
     *                   | qualifiedIdentifier { LBRACK RBRACK }
     * </pre>
     *
     * @return a reference type.
     */
    private Type referenceType() {
        Type type = null;
        if (!see(IDENTIFIER)) {
            type = basicType();
            mustBe(LBRACK);
            mustBe(RBRACK);
            type = new ArrayTypeName(type);
        } else {
            type = qualifiedIdentifier();
        }
        while (seeDims()) {
            mustBe(LBRACK);
            mustBe(RBRACK);
            type = new ArrayTypeName(type);
        }
        return type;
    }

    /**
     * Parses a statement expression and returns an AST for it.
     *
     * <pre>
     *   statementExpression ::= expression
     * </pre>
     *
     * @return an AST for a statement expression.
     */
    private JStatement statementExpression() {
        int line = scanner.token().line();
        JExpression expr = expression();
        if (expr instanceof JAssignment
                || expr instanceof JPreIncrementOp
                || expr instanceof JPreDecrementOp
                || expr instanceof JPostIncrementOp
                || expr instanceof JPostDecrementOp
                || expr instanceof JMessageExpression
                || expr instanceof JSuperConstruction
                || expr instanceof JThisConstruction
                || expr instanceof JNewOp
                || expr instanceof JNewArrayOp) {
            // So as not to save on stack.
            expr.isStatementExpression = true;
        } else {
            reportParserError("Invalid statement expression; it does not have a side-effect");
        }
        return new JStatementExpression(line, expr);
    }

    /**
     * Parses an expression and returns an AST for it.
     *
     * <pre>
     *   expression ::= assignmentExpression
     * </pre>
     *
     * @return an AST for an expression.
     */
    private JExpression expression() {
        return assignmentExpression();
    }

    /**
     * Parses an assignment expression and returns an AST for it.
     *
     * <pre>
     *   assignmentExpression ::= conditionalAndExpression
     *                                [ ( ASSIGN | PLUS_ASSIGN ) assignmentExpression ]
     * </pre>
     *
     * @return an AST for an assignment expression.
     */
    private JExpression assignmentExpression() {
        int line = scanner.token().line();
        JExpression lhs = conditionalAndExpression();
        if (have(ASSIGN)) {
            return new JAssignOp(line, lhs, assignmentExpression());
        } else if (have(PLUS_ASSIGN)) {
            return new JPlusAssignOp(line, lhs, assignmentExpression());
        } else {
            return lhs;
        }
    }

    /**
     * Parses a conditional-and expression and returns an AST for it.
     *
     * <pre>
     *   conditionalAndExpression ::= equalityExpression { LAND equalityExpression }
     * </pre>
     *
     * @return an AST for a conditional-and expression.
     */
    private JExpression conditionalAndExpression() {
        int line = scanner.token().line();
        boolean more = true;
        JExpression lhs = equalityExpression();
        while (more) {
            if (have(LAND)) {
                lhs = new JLogicalAndOp(line, lhs, equalityExpression());
            } else {
                more = false;
            }
        }
        return lhs;
    }

    /**
     * Parses an equality expression and returns an AST for it.
     *
     * <pre>
     *   equalityExpression ::= relationalExpression { EQUAL relationalExpression }
     * </pre>
     *
     * @return an AST for an equality expression.
     */
    private JExpression equalityExpression() {
        int line = scanner.token().line();
        boolean more = true;
        JExpression lhs = relationalExpression();
        while (more) {
            if (have(EQUAL)) {
                lhs = new JEqualOp(line, lhs, relationalExpression());
            } else {
                more = false;
            }
        }
        return lhs;
    }

    /**
     * Parses a relational expression and returns an AST for it.
     *
     * <pre>
     *   relationalExpression ::= additiveExpression [ ( GT | LE ) additiveExpression
     *                                               | INSTANCEOF referenceType ]
     * </pre>
     *
     * @return an AST for a relational expression.
     */
    private JExpression relationalExpression() {
        int line = scanner.token().line();
        JExpression lhs = additiveExpression();
        if (have(GT)) {
            return new JGreaterThanOp(line, lhs, additiveExpression());
        } else if (have(LE)) {
            return new JLessEqualOp(line, lhs, additiveExpression());
        } else if (have(INSTANCEOF)) {
            return new JInstanceOfOp(line, lhs, referenceType());
        } else {
            return lhs;
        }
    }

    /**
     * Parses an additive expression and returns an AST for it.
     *
     * <pre>
     *   additiveExpression ::= multiplicativeExpression { MINUS multiplicativeExpression }
     * </pre>
     *
     * @return an AST for an additive expression.
     */
    private JExpression additiveExpression() {
        int line = scanner.token().line();
        boolean more = true;
        JExpression lhs = multiplicativeExpression();
        while (more) {
            if (have(MINUS)) {
                lhs = new JSubtractOp(line, lhs, multiplicativeExpression());
            } else if (have(PLUS)) {
                lhs = new JPlusOp(line, lhs, multiplicativeExpression());
            } else {
                more = false;
            }
        }
        return lhs;
    }

    /**
     * Parses a multiplicative expression and returns an AST for it.
     *
     * <pre>
     *   multiplicativeExpression ::= unaryExpression { STAR unaryExpression }
     * </pre>
     *
     * @return an AST for a multiplicative expression.
     */
    private JExpression multiplicativeExpression() {
        int line = scanner.token().line();
        boolean more = true;
        JExpression lhs = unaryExpression();
        while (more) {
            if (have(STAR)) {
                lhs = new JMultiplyOp(line, lhs, unaryExpression());
            } else {
                more = false;
            }
        }
        return lhs;
    }

    /**
     * Parses an unary expression and returns an AST for it.
     *
     * <pre>
     *   unaryExpression ::= INC unaryExpression
     *                     | MINUS unaryExpression
     *                     | simpleUnaryExpression
     * </pre>
     *
     * @return an AST for an unary expression.
     */
    private JExpression unaryExpression() {
        int line = scanner.token().line();
        if (have(INC)) {
            return new JPreIncrementOp(line, unaryExpression());
        } else if (have(MINUS)) {
            return new JNegateOp(line, unaryExpression());
        } else {
            return simpleUnaryExpression();
        }
    }

    /**
     * Parses a simple unary expression and returns an AST for it.
     *
     * <pre>
     *   simpleUnaryExpression ::= LNOT unaryExpression
     *                           | LPAREN basicType RPAREN unaryExpression
     *                           | LPAREN referenceType RPAREN simpleUnaryExpression
     *                           | postfixExpression
     * </pre>
     *
     * @return an AST for a simple unary expression.
     */
    private JExpression simpleUnaryExpression() {
        int line = scanner.token().line();
        if (have(LNOT)) {
            return new JLogicalNotOp(line, unaryExpression());
        } else if (seeCast()) {
            mustBe(LPAREN);
            boolean isBasicType = seeBasicType();
            Type type = type();
            mustBe(RPAREN);
            JExpression expr = isBasicType ? unaryExpression() : simpleUnaryExpression();
            return new JCastOp(line, type, expr);
        } else {
            return postfixExpression();
        }
    }

    /**
     * Parses a postfix expression and returns an AST for it.
     *
     * <pre>
     *   postfixExpression ::= primary { selector } { DEC }
     * </pre>
     *
     * @return an AST for a postfix expression.
     */
    private JExpression postfixExpression() {
        int line = scanner.token().line();
        JExpression primaryExpr = primary();
        while (see(DOT) || see(LBRACK)) {
            primaryExpr = selector(primaryExpr);
        }
        while (have(DEC)) {
            primaryExpr = new JPostDecrementOp(line, primaryExpr);
        }
        return primaryExpr;
    }

    /**
     * Parses a selector and returns an AST for it.
     *
     * <pre>
     *   selector ::= DOT qualifiedIdentifier [ arguments ]
     *              | LBRACK expression RBRACK
     * </pre>
     *
     * @param target the target expression for this selector.
     * @return an AST for a selector.
     */
    private JExpression selector(JExpression target) {
        int line = scanner.token().line();
        if (have(DOT)) {
            // target.selector.
            mustBe(IDENTIFIER);
            String name = scanner.previousToken().image();
            if (see(LPAREN)) {
                ArrayList<JExpression> args = arguments();
                return new JMessageExpression(line, target, name, args);
            } else {
                return new JFieldSelection(line, target, name);
            }
        } else {
            mustBe(LBRACK);
            JExpression index = expression();
            mustBe(RBRACK);
            return new JArrayExpression(line, target, index);
        }
    }

    /**
     * Parses a primary expression and returns an AST for it.
     *
     * <pre>
     *   primary ::= parExpression
     *             | NEW creator
     *             | THIS [ arguments ]
     *             | SUPER ( arguments | DOT IDENTIFIER [ arguments ] )
     *             | qualifiedIdentifier [ arguments ]
     *             | literal
     * </pre>
     *
     * @return an AST for a primary expression.
     */
    private JExpression primary() {
        int line = scanner.token().line();
        if (see(LPAREN)) {
            return parExpression();
        } else if (have(NEW)) {
            return creator();
        } else if (have(THIS)) {
            if (see(LPAREN)) {
                ArrayList<JExpression> args = arguments();
                return new JThisConstruction(line, args);
            } else {
                return new JThis(line);
            }
        } else if (have(SUPER)) {
            if (!have(DOT)) {
                ArrayList<JExpression> args = arguments();
                return new JSuperConstruction(line, args);
            } else {
                mustBe(IDENTIFIER);
                String name = scanner.previousToken().image();
                JExpression newTarget = new JSuper(line);
                if (see(LPAREN)) {
                    ArrayList<JExpression> args = arguments();
                    return new JMessageExpression(line, newTarget, null, name, args);
                } else {
                    return new JFieldSelection(line, newTarget, name);
                }
            }
        } else if (see(IDENTIFIER)) {
            TypeName id = qualifiedIdentifier();
            if (see(LPAREN)) {
                // ambiguousPart.messageName(...).
                ArrayList<JExpression> args = arguments();
                return new JMessageExpression(line, null, ambiguousPart(id), id.simpleName(), args);
            } else if (ambiguousPart(id) == null) {
                // A simple name.
                return new JVariable(line, id.simpleName());
            } else {
                // ambiguousPart.fieldName.
                return new JFieldSelection(line, ambiguousPart(id), null, id.simpleName());
            }
        } else {
            return literal();
        }
    }

    /**
     * Parses a creator and returns an AST for it.
     *
     * <pre>
     *   creator ::= ( basicType | qualifiedIdentifier )
     *                   ( arguments
     *                   | LBRACK RBRACK { LBRACK RBRACK } [ arrayInitializer ]
     *                   | newArrayDeclarator
     *                   )
     * </pre>
     *
     * @return an AST for a creator.
     */
    private JExpression creator() {
        int line = scanner.token().line();
        Type type = seeBasicType() ? basicType() : qualifiedIdentifier();
        if (see(LPAREN)) {
            ArrayList<JExpression> args = arguments();
            return new JNewOp(line, type, args);
        } else if (see(LBRACK)) {
            if (seeDims()) {
                Type expected = type;
                while (have(LBRACK)) {
                    mustBe(RBRACK);
                    expected = new ArrayTypeName(expected);
                }
                return arrayInitializer(expected);
            } else {
                return newArrayDeclarator(line, type);
            }
        } else {
            reportParserError("( or [ sought where %s found", scanner.token().image());
            return new JWildExpression(line);
        }
    }

    /**
     * Parses a new array declarator and returns an AST for it.
     *
     * <pre>
     *   newArrayDeclarator ::= LBRACK expression RBRACK
     *                              { LBRACK expression RBRACK } { LBRACK RBRACK }
     * </pre>
     *
     * @param line line in which the declarator occurred.
     * @param type type of the array.
     * @return an AST for a new array declarator.
     */
    private JNewArrayOp newArrayDeclarator(int line, Type type) {
        ArrayList<JExpression> dimensions = new ArrayList<JExpression>();
        mustBe(LBRACK);
        dimensions.add(expression());
        mustBe(RBRACK);
        type = new ArrayTypeName(type);
        while (have(LBRACK)) {
            if (have(RBRACK)) {
                // We're done with dimension expressions.
                type = new ArrayTypeName(type);
                while (have(LBRACK)) {
                    mustBe(RBRACK);
                    type = new ArrayTypeName(type);
                }
                return new JNewArrayOp(line, type, dimensions);
            } else {
                dimensions.add(expression());
                type = new ArrayTypeName(type);
                mustBe(RBRACK);
            }
        }
        return new JNewArrayOp(line, type, dimensions);
    }

    /**
     * Parses a literal and returns an AST for it.
     *
     * <pre>
     *   literal ::= CHAR_LITERAL | FALSE | INT_LITERAL | NULL | STRING_LITERAL | TRUE
     * </pre>
     *
     * @return an AST for a literal.
     */
    private JExpression literal() {
        int line = scanner.token().line();
        if (have(CHAR_LITERAL)) {
            return new JLiteralChar(line, scanner.previousToken().image());
        } else if (have(FALSE)) {
            return new JLiteralBoolean(line, scanner.previousToken().image());
        } else if (have(INT_LITERAL)) {
            return new JLiteralInt(line, scanner.previousToken().image());
        } else if (have(NULL)) {
            return new JLiteralNull(line);
        } else if (have(STRING_LITERAL)) {
            return new JLiteralString(line, scanner.previousToken().image());
        } else if (have(TRUE)) {
            return new JLiteralBoolean(line, scanner.previousToken().image());
        } else {
            reportParserError("Literal sought where %s found", scanner.token().image());
            return new JWildExpression(line);
        }
    }

    //////////////////////////////////////////////////
    // Parsing Support
    // ////////////////////////////////////////////////

    // Returns true if the current token equals sought, and false otherwise.
    private boolean see(TokenKind sought) {
        return (sought == scanner.token().kind());
    }

    // If the current token equals sought, scans it and returns true. Otherwise, returns false
    // without scanning the token.
    private boolean have(TokenKind sought) {
        if (see(sought)) {
            scanner.next();
            return true;
        } else {
            return false;
        }
    }

    // Attempts to match a token we're looking for with the current input token. On success,
    // scans the token and goes into a "Recovered" state. On failure, what happens next depends
    // on whether or not the parser is currently in a "Recovered" state: if so, it reports the
    // error and goes into an "Unrecovered" state; if not, it repeatedly scans tokens until it
    // finds the one it is looking for (or EOF) and then returns to a "Recovered" state. This
    // gives us a kind of poor man's syntactic error recovery, a strategy due to David Turner and
    // Ron Morrison.
    private void mustBe(TokenKind sought) {
        if (scanner.token().kind() == sought) {
            scanner.next();
            isRecovered = true;
        } else if (isRecovered) {
            isRecovered = false;
            reportParserError("%s found where %s sought", scanner.token().image(), sought.image());
        } else {
            // Do not report the (possibly spurious) error, but rather attempt to recover by
            // forcing a match.
            while (!see(sought) && !see(EOF)) {
                scanner.next();
            }
            if (see(sought)) {
                scanner.next();
                isRecovered = true;
            }
        }
    }

    // Pulls out and returns the ambiguous part of a name.
    private AmbiguousName ambiguousPart(TypeName name) {
        String qualifiedName = name.toString();
        int i = qualifiedName.lastIndexOf('.');
        return i == -1 ? null : new AmbiguousName(name.line(), qualifiedName.substring(0, i));
    }

    // Reports a syntax error.
    private void reportParserError(String message, Object... args) {
        isInError = true;
        isRecovered = false;
        System.err.printf("%s:%d: error: ", scanner.fileName(), scanner.token().line());
        System.err.printf(message, args);
        System.err.println();
    }

    //////////////////////////////////////////////////
    // Lookahead Methods
    //////////////////////////////////////////////////

    // Returns true if we are looking at an IDENTIFIER followed by a LPAREN, and false otherwise.
    private boolean seeIdentLParen() {
        scanner.recordPosition();
        boolean result = have(IDENTIFIER) && see(LPAREN);
        scanner.returnToPosition();
        return result;
    }

    // Returns true if we are looking at a cast (basic or reference), and false otherwise.
    private boolean seeCast() {
        scanner.recordPosition();
        if (!have(LPAREN)) {
            scanner.returnToPosition();
            return false;
        }
        if (seeBasicType()) {
            scanner.returnToPosition();
            return true;
        }
        if (!see(IDENTIFIER)) {
            scanner.returnToPosition();
            return false;
        } else {
            scanner.next();
            // A qualified identifier is ok.
            while (have(DOT)) {
                if (!have(IDENTIFIER)) {
                    scanner.returnToPosition();
                    return false;
                }
            }
        }
        while (have(LBRACK)) {
            if (!have(RBRACK)) {
                scanner.returnToPosition();
                return false;
            }
        }
        if (!have(RPAREN)) {
            scanner.returnToPosition();
            return false;
        }
        scanner.returnToPosition();
        return true;
    }

    // Returns true if we are looking at a local variable declaration, and false otherwise.
    private boolean seeLocalVariableDeclaration() {
        scanner.recordPosition();
        if (have(IDENTIFIER)) {
            // A qualified identifier is ok.
            while (have(DOT)) {
                if (!have(IDENTIFIER)) {
                    scanner.returnToPosition();
                    return false;
                }
            }
        } else if (seeBasicType()) {
            scanner.next();
        } else {
            scanner.returnToPosition();
            return false;
        }
        while (have(LBRACK)) {
            if (!have(RBRACK)) {
                scanner.returnToPosition();
                return false;
            }
        }
        if (!have(IDENTIFIER)) {
            scanner.returnToPosition();
            return false;
        }
        while (have(LBRACK)) {
            if (!have(RBRACK)) {
                scanner.returnToPosition();
                return false;
            }
        }
        scanner.returnToPosition();
        return true;
    }

    // Returns true if we are looking at a basic type, and false otherwise.
    private boolean seeBasicType() {
        return (see(BOOLEAN) || see(CHAR) || see(INT));
    }

    // Returns true if we are looking at a reference type, and false otherwise.
    private boolean seeReferenceType() {
        if (see(IDENTIFIER)) {
            return true;
        } else {
            scanner.recordPosition();
            if (have(BOOLEAN) || have(CHAR) || have(INT)) {
                if (have(LBRACK) && see(RBRACK)) {
                    scanner.returnToPosition();
                    return true;
                }
            }
            scanner.returnToPosition();
        }
        return false;
    }

    // Returns true if we are looking at a [] pair, and false otherwise.
    private boolean seeDims() {
        scanner.recordPosition();
        boolean result = have(LBRACK) && see(RBRACK);
        scanner.returnToPosition();
        return result;
    }
}
