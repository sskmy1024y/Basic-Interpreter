package syntax_analyzer.nodes;

import java.util.*;
import lexical_analyzer.*;
import syntax_analyzer.*;

public class ProgramNode extends Node {

    List<Node> child = new ArrayList<>();
    private static Set<LexicalType> first = new HashSet<>(Arrays.asList(
            LexicalType.IF,
            LexicalType.WHILE,
            LexicalType.DO,
            LexicalType.NAME,
            LexicalType.FOR,
            LexicalType.END
    ));

    public static boolean isMatch(LexicalType type){
        return first.contains(type);
    }

    public static Node getHandler(LexicalType type, Environment env) {
        return StmtListNode.getHandler(type, env);
    }

    private ProgramNode(Environment env) {
        this.env = env;
        this.type = NodeType.PROGRAM;
    }

    public void parse() throws Exception {
        throw new InternalError("Can't exec parse in Program Node.");
    }

    public String toString() {
        if (type == NodeType.PROGRAM) return "PROGRAM";
        else return "Node";
    }

}
