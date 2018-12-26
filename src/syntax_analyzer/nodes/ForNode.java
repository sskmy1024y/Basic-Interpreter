package syntax_analyzer.nodes;

import lexical_analyzer.*;
import syntax_analyzer.*;

import java.util.*;

public class ForNode extends Node {

    Node init;  // 初期化
    Node max;   // 継続終了条件
    Node process; // 処理内容
    String step;    // 更新対象

    private final static Set<LexicalType> first = new HashSet<>(Arrays.asList(
            LexicalType.FOR
    ));

    public static boolean isMatch(LexicalType type){
        return first.contains(type);
    }

    public static Node getHandler(LexicalType type, Environment env) {
        if (!isMatch(type)) return null;
        return new ForNode(env);
    }

    private ForNode(Environment env) {
        this.env = env;
        this.type = NodeType.FOR_STMT;
    }

    public void parse() throws Exception {
        // forなので破棄
        env.getInput().get();

        // 条件式(subst)
        LexicalType type = env.getInput().peek().getType();
        if (SubstNode.isMatch(type)) {
            init = SubstNode.getHandler(type, env);
            init.parse();
        } else {
            // 条件式が成り立たない場合
            throw new SyntaxException("Invalid constitution of 'FOR'.");
        }

        // TOの確認
        if (env.getInput().get().getType()!=LexicalType.TO) throw new SyntaxException("Invalid constitution of 'FOR', Not found 'TO'."); // TOが認識しない場合

        // 条件の上限値
        if (env.getInput().peek().getType()==LexicalType.INTVAL){
            LexicalUnit lu = env.getInput().get();
            max = ConstNode.getHandler(lu.getType(), env, lu.getValue());
        } else {
            // 継続条件の終了値が見つからない場合
            throw new SyntaxException("Invalid constitution of 'FOR', Continuation conditional expression not found.");
        }

        // NLの確認
        if (env.getInput().get().getType() != LexicalType.NL) throw new SyntaxException("Invalid constitution of 'For', NewLine not found after conditional expression.");

        // 処理部分の内容
        type = env.getInput().peek().getType();
        if (StmtListNode.isMatch(type)) {
            process = StmtListNode.getHandler(type, env);
            process.parse();
        } else {
            throw new SyntaxException("Invalid constitution of 'FOR', Processing content not found.");
        }

        // NLの確認
        if (env.getInput().get().getType() != LexicalType.NL) throw new SyntaxException("Invalid constitution of 'FOR', NewLine not found after processing content.");

        // NEXTの確認
        if (env.getInput().get().getType() != LexicalType.NEXT) throw new SyntaxException("Invalid constitution of 'FOR', Not found 'NEXT'.");

        // 更新対象の確認
        if (env.getInput().peek().getType() == LexicalType.NAME) {
            step = env.getInput().get().getValue().getSValue();
        } else {
            throw new SyntaxException("Invalid constitution of 'FOR', Not found update target.");
        }

    }

    public String toString(int indent) {
        String ret = "";
        ret += "FOR:init=" + init + " max=" + max + " process[\n";
        ret += process.toString(indent + 1) + "\n";
        for (int i = 0; i < indent; i++) {
            ret += "\t";
        }
        ret += "] update:" + step;
        return ret;
    }
}
