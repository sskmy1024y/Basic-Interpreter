package syntax_analyzer.nodes;

import lexical_analyzer.*;
import syntax_analyzer.*;

import java.util.*;

public class ConstNode extends Node {

    Value value;

    private final static Set<LexicalType> first = new HashSet<>(Arrays.asList(
            LexicalType.SUB,
            LexicalType.INTVAL,
            LexicalType.DOUBLEVAL,
            LexicalType.LITERAL
    ));

    public static boolean isMatch(LexicalType type){
        return first.contains(type);
    }

    private ConstNode(Environment env, Value value) {
        this.env = env;
        switch (value.getType()){
            case INTEGER:
                this.type = NodeType.INT_CONSTANT;
                break;
            case DOUBLE:
                this.type = NodeType.DOUBLE_CONSTANT;
                break;
            case STRING:
                this.type = NodeType.STRING_CONSTANT;
                break;
            default:
                // 未対応の型のValue
                throw new InternalError("Invalid value type.");
        }
        this.value = value;
    }

    public static Node getHandler(LexicalType type, Environment env, Value value) {
        if (isMatch(type)) return new ConstNode(env, value);
        else return null;
    }

    public void parse() {
        throw new InternalError("Can't exec parse in Const class.");
    }

    public String toString() {
        return "Constant:" + value;
    }

}
