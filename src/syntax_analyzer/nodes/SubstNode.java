package syntax_analyzer.nodes;

import java.util.*;
import lexical_analyzer.*;
import syntax_analyzer.*;

public class SubstNode extends Node {
    private String leftVar = "";
    private Node expr;

    private final static Set<LexicalType> FIRST = new HashSet<>(Arrays.asList(
            LexicalType.NAME
    ));

    public static boolean isMatch(LexicalType type){
        return FIRST.contains(type);
    }

    public static Node getHandler(LexicalType type, Environment env) {
        if (isMatch(type)) return new SubstNode(env);
        else return null;
    }

    public SubstNode(Environment env){
        this.env = env;
        this.type = NodeType.SUBST_STMT;
    }

    public void parse() throws Exception {
        LexicalUnit lu = env.getInput().peek();
        if (lu.getType() == LexicalType.NAME) {
            leftVar = env.getInput().get().getValue().getSValue();
        } else {
            throw new InternalError("Invalid token of variable name");
        }

        if (env.getInput().get().getType() != LexicalType.EQ) throw new SyntaxException("Not found EQ in Assignment statement");

        LexicalType type = env.getInput().peek().getType();
        if (ExprNode.isMatch(type)){
            expr = ExprNode.getHandler(type, env);
            expr.parse();
        } else {
            // 式の後半が式として評価ない
            throw new SyntaxException("Can't evaluate the second half of the expression.");
        }
    }

    public Value getValue() throws Exception {
        env.getVariable(leftVar).setValue(expr.getValue());
        return null;
    }

    public String toString() {
        return "SUBST:" + expr + " -> " + leftVar;
    }

    public String toString(int indent) {
        return toString();
    }
}
