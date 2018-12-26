package syntax_analyzer.nodes;

import lexical_analyzer.*;
import syntax_analyzer.*;

import java.util.*;

public class CallNode extends Node {

    String funcName;        // 関数名
    ExprListNode arguments; // 引数

    private final static Set<LexicalType> first = new HashSet<>(Arrays.asList(
            LexicalType.NAME
    ));

    public static boolean isMatch(LexicalType type){
        return first.contains(type);
    }

    private CallNode(Environment env) {
        this.env = env;
        this.type = NodeType.FUNCTION_CALL;
    }

    public static Node getHandler(LexicalType type, Environment env) throws Exception{
        if (!isMatch(type)) return null;
        else return new CallNode(env);
    }

    public void parse() throws Exception {
        boolean isBracket = false;  // カッコの有無

        // 呼び出し関数名
        if (env.getInput().peek().getType() == LexicalType.NAME) {
            funcName = env.getInput().get().getValue().getSValue();
        } else {
            throw new SyntaxException("Invalid substitute name.");
        }

        // LPの確認
        if (env.getInput().peek().getType() == LexicalType.LP) {
            env.getInput().get();
            isBracket = true;
        }

        // 引数リスト
        LexicalType type = env.getInput().peek().getType();
        if (ExprListNode.isMatch(type)) {
            arguments = ExprListNode.getHandler(type, env);
            arguments.parse();
        }

        if (isBracket) {
            if (env.getInput().get().getType() != LexicalType.RP) {
                // 閉じ括弧がない状態
                throw new SyntaxException("Missing closing parenthesis for function call.");
            }
        }
    }

    public String toString() {
        return "FUNCTION:name="+funcName+" argments=["+arguments+"]";
    }

    public String toString(int indent) {
        return toString();
    }
}

