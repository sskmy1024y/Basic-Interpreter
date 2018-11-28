package syntax_analyzer.nodes;

import lexical_analyzer.*;
import syntax_analyzer.*;

public class ProgramNode extends Node {

    public static boolean isMatch(LexicalType first) {
        return false;
    }

    public static Node getMatch(LexicalUnit lex){
        return null;
    }

    public boolean parse() throws Exception {
        return true;
    }

//    public String toString() {
//        if (type == NodeType.PROGRAM) return "PROGRAM";
//        else return "Node";
//    }

}
