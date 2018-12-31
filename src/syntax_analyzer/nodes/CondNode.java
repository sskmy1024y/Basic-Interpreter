package syntax_analyzer.nodes;

import lexical_analyzer.*;
import libfunc.CalculateException;
import syntax_analyzer.*;
import java.util.*;

public class CondNode extends Node {

    Node left;              // 左側
    LexicalType operator;   // 演算子
    Node right;             // 右側

    private final static Set<LexicalType> FIRST = new HashSet<>(Arrays.asList(
            LexicalType.NAME,
            LexicalType.SUB,
            LexicalType.LP,
            LexicalType.INTVAL,
            LexicalType.DOUBLEVAL,
            LexicalType.LITERAL
    ));

    private final static Set<LexicalType> OPERATORS = new HashSet<>(Arrays.asList(
            LexicalType.EQ,
            LexicalType.LT,
            LexicalType.LE,
            LexicalType.GT,
            LexicalType.GE,
            LexicalType.NE
    ));

    public static boolean isMatch(LexicalType type){
        return FIRST.contains(type);
    }

    public static Node getHandler (LexicalType type, Environment env) {
        if (!isMatch(type)) return null;
        else return new CondNode(env);
    }

    private CondNode(Environment env) {
        this.env = env;
        this.type = NodeType.COND;
    }

    public void parse() throws Exception {
        LexicalType type = env.getInput().peek().getType();
        if (ExprNode.isMatch(type)) {
            left = ExprNode.getHandler(type, env);
            left.parse();
        } else {
            // 条件文の開始が不正
            throw new SyntaxException("Invalid start of condition statement.");
        }

        if (OPERATORS.contains(env.getInput().peek().getType())) {
            operator = env.getInput().get().getType();
        } else {
            // 条件文の中に不正な文字
            throw new SyntaxException("Invalid character in conditional statement.");
        }

        type = env.getInput().peek().getType();
        if (ExprNode.isMatch(type)) {
            right = ExprNode.getHandler(type, env);
            right.parse();
        } else {
            // 条件文の中に不正な文字
            throw new SyntaxException("Invalid character in conditional statement.");
        }
    }

    public Value getValue() throws Exception {
        Value leftValue = left.getValue();
        Value rightValue = right.getValue();

        boolean res = false;
        switch (operator) {
            case EQ:
                res = leftValue.getSValue().equals(rightValue.getSValue());
                break;
            case LT:
                res = leftValue.getDValue() < rightValue.getDValue();
                break;
            case GT:
                res = leftValue.getDValue() > rightValue.getDValue();
                break;
            case GE:
                res = leftValue.getDValue() >= rightValue.getDValue();
                break;
            case LE:
                res = leftValue.getDValue() <= rightValue.getDValue();
                break;
            default:
                throw new CalculateException("Invalid operator.");
        }

        return new ValueImpl(res);
    }

    public String toString() {
        return "COND:" + left + " " + operator + " " + right;
    }
}
