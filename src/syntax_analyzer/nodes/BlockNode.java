package syntax_analyzer.nodes;

import lexical_analyzer.*;
import syntax_analyzer.*;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

public class BlockNode extends Node {
    static Set<LexicalType> first = new HashSet<>(Arrays.asList(
            LexicalType.IF,
            LexicalType.WHILE,
            LexicalType.DO
    ));

    public static boolean isMatch(LexicalType type){
        return first.contains(type);
    }

    public static Node getHandler(LexicalType type){
        switch (type){
            case IF: return new IfBlockNode();
            case DO: return new DoBlockNode();
            //case WHILE: return new WhileBlockNode();
            default:
                return null;
        }
    }
}
