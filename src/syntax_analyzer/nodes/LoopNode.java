package syntax_analyzer.nodes;

import lexical_analyzer.*;
import syntax_analyzer.*;
import java.util.*;

public class LoopNode extends Node {

    Node cond;
    Node process;
    boolean isDo = false;
    boolean isUntil = false;

    static Set<LexicalType> FIRST = new HashSet<>(Arrays.asList(
            LexicalType.DO,
            LexicalType.WHILE
    ));

    public static boolean isMatch(LexicalType type){
        return FIRST.contains(type);
    }

    public static Node getHandler(LexicalType type, Environment env){
        if (!isMatch(type)) return null;
        return new LoopNode(env);
    }

    private LoopNode(Environment env) {
        this.env = env;
        this.type = NodeType.LOOP_BLOCK;
    }

    public void parse() throws Exception {
        LexicalType type = env.getInput().peek().getType();
        if (type == LexicalType.WHILE) {
            // WHILEを破棄
            env.getInput().get();

            LexicalType type2 = env.getInput().peek().getType();
            if (CondNode.isMatch(type2)) {
                cond = CondNode.getHandler(type2, env);
                cond.parse();
            } else {
                // WHILE文の構成が不正。
                throw new SyntaxException("Invalid constitution of 'WHILE'. Cond not found. ");
            }

            // 条件式の直後にNLがない場合
            if (env.getInput().get().getType() != LexicalType.NL) throw new SyntaxException("Invalid constitution of 'WHILE'. Not found NewLine after cond.");

            type2 = env.getInput().peek().getType();
            if (StmtListNode.isMatch(type2)) {
                process = StmtListNode.getHandler(type2, env);
                process.parse();
            } else {
                // 処理内容を検出できない場合
                throw new SyntaxException("Invalid constitution of 'WHILE'. Can't detect processing.");
            }

            // 処理内容の直後にNLがない場合
            if (env.getInput().get().getType() != LexicalType.NL) throw new SyntaxException("Invalid constitution of 'WHILE'. Not found NewLine after processing.");

            // WENDがない場合
            if (env.getInput().get().getType() != LexicalType.WEND) throw new SyntaxException("Invalid constitution of 'WHILE'. Not found WEND.");

        } else if (type == LexicalType.DO) {
            // DOを破棄
            env.getInput().get();

            isDo = true;
            // 条件式を取得
            getCond();

            // 処理内容の直前にNLがない場合
            if (env.getInput().get().getType() != LexicalType.NL) throw new SyntaxException("Invalid constitution of 'DO'. Not found NewLine before processing.");

            LexicalType type2 = env.getInput().peek().getType();
            if (StmtListNode.isMatch(type2)) {
                process = StmtListNode.getHandler(type2, env);
                process.parse();
            } else {
                throw new SyntaxException("Invalid constitution of 'DO'. Can't detect processing.");
            }

            // 処理内容の直後にNLがない場合
            if (env.getInput().get().getType() != LexicalType.NL) throw new SyntaxException("Invalid constitution of 'DO'. Not found NewLine after processing.");

            // 処理内容の後にLOOPがない場合
            if (env.getInput().get().getType() != LexicalType.LOOP) throw new SyntaxException("Invalid constitution of 'DO'. Not found LOOP after processing.");

            // WHILEまたはUNTILで始まる条件文が見つからない場合
            if (cond == null && !getCond()) throw new SyntaxException("Not found cond with WHILE or UNTIL");
        } else {
            throw new SyntaxException("Invalid constitution of 'LOOPBLOCK'.");
        }

        if (env.getInput().get().getType() != LexicalType.NL) {
            throw new SyntaxException("Invalid constitution of 'LOOPBLOCK'. Not found last NL.");
        }
    }

    private boolean getCond() throws Exception {
        switch (env.getInput().peek().getType()){
            case UNTIL:
                isUntil = true;
            case WHILE:
                // WHILEを破棄
                env.getInput().get();

                LexicalType type = env.getInput().peek().getType();
                if (CondNode.isMatch(type)) {
                    cond = CondNode.getHandler(type, env);
                    cond.parse();
                } else {
                    // WHILEもしくはUNTILの後に条件式がない場合
                    throw new SyntaxException("Invalid constitution of 'DO'. Not found cond after WHILE or UNTIL.");
                }
                break;
            default:
                return false;
        }
        return true;
    }

    public Value getValue() throws Exception {
        if (isDo) { // exec FIRST
            process.getValue();
        }

        while (true) {
            if (!judge()) {
                return null;
            }
            process.getValue();
        }
    }

    private boolean judge() throws Exception {
        if ((cond.getValue().getBValue() && !isUntil) ||
                (!cond.getValue().getBValue() && isUntil)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString(int indent) {
        String ret = "";
        ret += "LOOPBLOCK:cond=";
        if (isUntil) ret += "!(";
        ret += cond;
        if (isUntil) ret += ")";
        ret += " ";
        if (isDo) ret += "FIRST PROCESS...";
        ret += "[\n" + process.toString(indent+1);
        for(int i=0;i<indent;i++){
            ret += "\t";
        }
        ret += "]";
        return ret;
    }
}
