package syntax_analyzer.nodes;

import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;
import lexical_analyzer.LexicalType;
import lexical_analyzer.LexicalUnit;
import syntax_analyzer.Environment;
import syntax_analyzer.Node;
import syntax_analyzer.NodeType;
import syntax_analyzer.SyntaxException;

import java.util.*;

public class StmtListNode extends Node {

    List<Node> child = new ArrayList<>();
    static Set<LexicalType> first = new HashSet<>(Arrays.asList(
        LexicalType.IF,
        LexicalType.WHILE,
        LexicalType.DO,

        LexicalType.NAME,
        LexicalType.FOR,
        LexicalType.END,

        LexicalType.NL
    ));

    public static Node getHandler(LexicalType type, Environment env){
        if (!isMatch(type)) return null;
        return new StmtListNode(env);
    }

    public static boolean isMatch(LexicalType type){
        return first.contains(type);
    }

    private StmtListNode(Environment env){
        this.env = env;
        this.type = NodeType.STMT_LIST;
    }

    /**
     * 字句を読み込み、
     *
     */
    @Override
    public void parse() throws Exception {
        while (true) {
            try {
                // リスト終端以外のNLを読み飛ばす
                while (env.getInput().peek().getType() == LexicalType.NL &&
                        isMatch(env.getInput().peek(2).getType())) {
                    env.getInput().get();
                }

                Node handler;
                LexicalType type = env.getInput().peek().getType();
                if (StmtNode.isMatch(type)) {
                    handler = StmtNode.getHandler(type, env);
                } else if (BlockNode.isMatch(type)) {
                    handler = BlockNode.getHandler(type, env);
                } else {
                    break;
                }
                handler.parse();
                child.add(handler);
            } catch (SyntaxException e) {
                System.out.println(e.fillInStackTrace());
                LexicalUnit lu = env.getInput().get();
                while (lu.getType() != LexicalType.NL &&
                        lu.getType() != LexicalType.EOF) {
                    lu = env.getInput().get();
                }
            }
        }
    }

    @Override
    public String toString() {
        return "StmtList";
    }

    @Override
    public String toString(int indent) {
        String ret = "";
        for (int i=0; i<indent; i++) {
            ret += "\t";
        }
        ret += "stmtList(" + child.size() + "):\n";
        for (int i=0; i<child.size(); i++) {
            for (int j=0; j<indent+1; j++) {
                ret += "\t";
            }
            ret += child.get(i).toString(indent+1) + "\n";
        }
        return ret;
    }
}
