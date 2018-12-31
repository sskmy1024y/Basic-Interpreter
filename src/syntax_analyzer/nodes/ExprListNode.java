package syntax_analyzer.nodes;

import lexical_analyzer.*;
import syntax_analyzer.*;

import java.util.*;

public class ExprListNode extends Node {

    List<Node> list = new ArrayList<>();

    private final static Set<LexicalType> FIRST = new HashSet<>(Arrays.asList(
            LexicalType.NAME,
            LexicalType.SUB,
            LexicalType.LP,
            LexicalType.INTVAL,
            LexicalType.DOUBLEVAL,
            LexicalType.LITERAL
    ));

    public static boolean isMatch(LexicalType type){
        return FIRST.contains(type);
    }

    public static ExprListNode getHandler(LexicalType type, Environment env) {
        if (!isMatch(type)) return null;
        else return new ExprListNode(env);
    }

    private ExprListNode(Environment env) {
        this.env = env;
        this.type = NodeType.EXPR_LIST;
    }

    public void parse() throws Exception {
        LexicalType type = env.getInput().peek().getType();
        if (ExprNode.isMatch(type)) {
            Node handler = ExprNode.getHandler(type, env);
            handler.parse();
            list.add(handler);
        } else {
            // 関数呼び出しにおける引数として不適切
            throw new InternalError("Inappropriate as argument in function call.");
        }

        while (true) {
            if (env.getInput().peek().getType() == LexicalType.COMMA) {
                env.getInput().get();
            } else break;

            type = env.getInput().peek().getType();
            if (ExprNode.isMatch(type)) {
                Node handler = ExprNode.getHandler(type, env);
                handler.parse();
                list.add(handler);
            } else {
                // 引数リストの構成が不正
                throw new SyntaxException("Illegal configuration of argument list in function call.");
            }
        }
    }

    public Value get(int n) throws Exception {
        return list.get(n).getValue();
    }

    public String toString() {
        String result = "";
        for (int i=0; i < list.size(); i++){
            result += list.get(i);
            if (i != list.size() -1){
                result += ", ";
            }
        }
        return result;
    }
}
