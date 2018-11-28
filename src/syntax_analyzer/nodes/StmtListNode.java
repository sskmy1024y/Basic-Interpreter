package syntax_analyzer.nodes;

import lexical_analyzer.LexicalType;
import lexical_analyzer.LexicalUnit;
import syntax_analyzer.Environment;
import syntax_analyzer.Node;
import syntax_analyzer.NodeType;

import java.util.*;

public class StmtListNode extends Node {

    List<Node> child = new ArrayList<>();
    static Set<LexicalType> first = new HashSet<>(Arrays.asList(
       LexicalType.IF,
       LexicalType.WHILE,
       LexicalType.DO,

       LexicalType.NAME,
       LexicalType.FOR,
       LexicalType.END
    ));

    public static Node getHandler(LexicalType type, Environment env){
        if (!isMatch(type)) return null;
        return new StmtListNode(env);
    }

    public static boolean isMatch(LexicalType type){
        return first.contains(type);
    }

    private StmtListNode(Environment e){
        super(e);
        //type = NodeType.STMT_LIST;
    }

    /**
     * 字句を読み込み、
     *
     * @return
     * @throws Exception
     *
    @Override
    public boolean parse() throws Exception {
        LexicalUnit lu;
        lu = env.getInput().get();
        if (isMatch(lu.getType())){
            Node handler = StmtNode.getHandler(lu.getType(), env);
            if (handler.parse();
        }
    }
    */

}
