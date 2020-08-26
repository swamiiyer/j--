// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for an if-statement.
 */
class JIfStatement extends JStatement {
    // Test expression.
    private JExpression condition;

    // Then part.
    private JStatement thenPart;

    // Else part.
    private JStatement elsePart;

    /**
     * Constructs an AST node for an if-statement.
     *
     * @param line      line in which the if-statement occurs in the source file.
     * @param condition test expression.
     * @param thenPart  then part.
     * @param elsePart  else part.
     */
    public JIfStatement(int line, JExpression condition, JStatement thenPart, JStatement elsePart) {
        super(line);
        this.condition = condition;
        this.thenPart = thenPart;
        this.elsePart = elsePart;
    }

    /**
     * {@inheritDoc}
     */
    public JStatement analyze(Context context) {
        condition = (JExpression) condition.analyze(context);
        condition.type().mustMatchExpected(line(), Type.BOOLEAN);
        thenPart = (JStatement) thenPart.analyze(context);
        if (elsePart != null) {
            elsePart = (JStatement) elsePart.analyze(context);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void codegen(CLEmitter output) {
        String elseLabel = output.createLabel();
        String endLabel = output.createLabel();
        condition.codegen(output, elseLabel, false);
        thenPart.codegen(output);
        if (elsePart != null) {
            output.addBranchInstruction(GOTO, endLabel);
        }
        output.addLabel(elseLabel);
        if (elsePart != null) {
            elsePart.codegen(output);
            output.addLabel(endLabel);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void toJSON(JSONElement json) {
        JSONElement e = new JSONElement();
        json.addChild("JIfStatement:" + line, e);
        JSONElement e1 = new JSONElement();
        e.addChild("Condition", e1);
        condition.toJSON(e1);
        JSONElement e2 = new JSONElement();
        e.addChild("ThenPart", e2);
        thenPart.toJSON(e2);
        if (elsePart != null) {
            JSONElement e3 = new JSONElement();
            e.addChild("ElsePart", e3);
            elsePart.toJSON(e3);
        }
    }
}
