package syntax_analyzer.nodes;

import lexical_analyzer.*;
import syntax_analyzer.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StmtNode extends Node {
    private final static Set<LexicalType> first = new HashSet<>(Arrays.asList(
            LexicalType.NAME,
            LexicalType.FOR,
            LexicalType.END
    ));

    public static boolean isMatch(LexicalType type){
        return first.contains(type);
    }

    public static Node getHandler(LexicalType type, Environment env) throws Exception{
        if (!isMatch(type)) return null;
        switch (type){
            case NAME:
                LexicalType type2 = env.getInput().peek(2).getType();
                if (type2 == LexicalType.EQ) {
                    return SubstNode.getHandler(type, env);
                } else if (ExprListNode.isMatch(type2)) {
                    return CallNode.getHandler(type, env);
                } else {
                    // 正しい文ではない時のエラー
                    throw new SyntaxException("It is not a correct sentence.");
                }
            case FOR:
                return ForNode.getHandler(type, env);
            case END:
                return EndNode.getHandler(type, env);
            default:
                // StmtNodeに適合しない型でgetHandlerがコールされた場合
                throw new InternalError("GetHandler was called with a type that does not fit StmtNode.");
        }
    }

    public void parse() throws InternalError {
        throw new InternalError("Can't exec parse in StmtNode class.");
    }


    public String toString() {
        return "Stmt";
    }

}
