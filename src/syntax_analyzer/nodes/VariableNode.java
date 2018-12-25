package syntax_analyzer.nodes;

import lexical_analyzer.*;
import syntax_analyzer.*;

import java.util.*;

public class VariableNode extends Node {
    String name;

    private final static Set<LexicalType> first = new HashSet<>(Arrays.asList(
            LexicalType.NAME
    ));

    public static boolean isMatch(LexicalType type){
        return first.contains(type);
    }

    public static Node getHandler(LexicalType type, Environment env, Value value) {
        if (isMatch(type)) return new VariableNode(env, value);
        else return null;
    }

    private VariableNode(Environment env, Value value) {
        this.env = env;
        this.type = NodeType.VARIABLE;
        name = value.getSValue();
    }

    public void parse() throws Exception {
        LexicalUnit lu = env.getInput().get();
        env.getInput().unget(lu);
        if (lu.getType() == LexicalType.NAME) {
            name = env.getInput().get().getValue().getSValue();
        } else {
            // 変数の文字列が不適切な場合
            throw new InternalError("Inappropriate character string as variable name.");
        }
    }

    public String toString() {
        return "Variable:" + name;
    }
}
