package syntax_analyzer.nodes;

import lexical_analyzer.*;
import syntax_analyzer.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StmtNode extends Node {
    private static Set<LexicalType> first = new HashSet<>(Arrays.asList(
            LexicalType.IF,
            LexicalType.WHILE,
            LexicalType.DO
    ));

    public static boolean isMatch(LexicalType type){
        return first.contains(type);
    }

    public static Node getHandler(LexicalType type){
        switch (type){
            case IF:
                return new IfBlockNode();
            case DO:
                return new DoBlockNode();
            default:
                return null;
        }
    }
}
