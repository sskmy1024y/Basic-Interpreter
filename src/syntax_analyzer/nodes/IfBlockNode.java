package syntax_analyzer.nodes;

import lexical_analyzer.*;
import syntax_analyzer.*;

import java.util.*;

public class IfBlockNode extends Node {

    private Node cond;			// 条件
    private Node process;	// trueの時の処理
    private Node elseProcess;			// elseの時の処理

    private List<Node> followIfBlock = new ArrayList<>();     // elseifを格納する

    static Set<LexicalType> first = new HashSet<>(Arrays.asList(
            LexicalType.IF
    ));

    public static boolean isMatch(LexicalType type){
        return first.contains(type);
    }

    public static Node getHandler(LexicalType type, Environment env){
        if (!isMatch(type)) return null;
        return new IfBlockNode(env);
    }

    private IfBlockNode(Environment env) {
        this.env = env;
        this.type = NodeType.IF_BLOCK;
    }

    public void parse() throws Exception {
        boolean isELSEIF = false;     // elseif

        // ELSEIFならばtrueへ
        LexicalType type = env.getInput().peek().getType();
        if (type == LexicalType.ELSEIF){
            isELSEIF = true;
            env.getInput().get();
        } else if (type == LexicalType.IF) {
            env.getInput().get();
        } else {
            throw new InternalError("Invalid call without IF or ELSEIF.");
        }


        // 条件文の確認
        type = env.getInput().peek().getType();
        if (CondNode.isMatch(type)){
            cond = CondNode.getHandler(type, env);
            cond.parse();
        } else {
            throw new SyntaxException("Invalid constitution of 'IF'. Cond not found.");
        }

        // THENの確認
        if (env.getInput().get().getType() != LexicalType.THEN) throw new SyntaxException("Invalid constitution of 'IF'. Not found 'THEN'. ");


        type = env.getInput().peek().getType();
        if (type == LexicalType.NL) {       // NLで改行が入る場合
            // NLを破棄
            env.getInput().get();

            LexicalType type2 = env.getInput().peek().getType();
            if (StmtListNode.isMatch(type2)){
                process = StmtListNode.getHandler(type2, env);
                process.parse();
            } else {
                // 分岐内の処理内容がおかしい場合
                throw new SyntaxException("Invalid constitution of 'IF'.");
            }

            // 分岐内終端NLを確認
            if (env.getInput().get().getType() != LexicalType.NL) throw new SyntaxException("Invalid constitution of 'IF'. Not found NewLine after processing.");

            // ELSEIFもしくはELSEが続いている場合
            type2 = env.getInput().peek().getType();
            while (!isELSEIF && type2 == LexicalType.ELSEIF) {
                // 別のIFBlockNodeを生成
//                elseProcess = IfBlockNode.getHandler(LexicalType.IF, env);
//                elseProcess.parse();

                Node elseIfBlock = IfBlockNode.getHandler(LexicalType.IF, env);
                elseIfBlock.parse();
                followIfBlock.add(elseIfBlock);

                type2 = env.getInput().peek().getType();
            }

            if (!isELSEIF && type2 == LexicalType.ELSE) {
                // ELSEを破棄
                env.getInput().get();

                type2 = env.getInput().peek().getType();
                if (env.getInput().get().getType() == LexicalType.NL){

                    LexicalType type3 = env.getInput().peek().getType();
                    if (StmtListNode.isMatch(type3)){
                        elseProcess = StmtListNode.getHandler(type3, env);
                        elseProcess.parse();
                    } else {
                        // ELSE文の構成が不正なとき
                        throw new SyntaxException("Invalid constitution of 'ELSE'.");
                    }

                    if (env.getInput().get().getType() != LexicalType.NL){
                        throw new SyntaxException("Invalid constitution of 'IF'. Not found NewLine after 'ELSE' processing.");
                    }

                } else {
                    // <THEN><NL>の場合は改行が必要なので
                    throw new SyntaxException("Invalid constitution of 'ELSE'. Not found NewLine after 'ELSE'.");
                }
            }

            // ENDIFの確認
            if (!isELSEIF){
                if (env.getInput().get().getType() != LexicalType.ENDIF) throw new SyntaxException("Invalid constitution of 'IF'. Not found 'ENDIF'");
            }

        } else if (StmtNode.isMatch(type)) {    // そのまま横につなげる場合
            process = StmtNode.getHandler(type, env);
            process.parse();

            if (env.getInput().peek().getType() == LexicalType.ELSE) {
                env.getInput().get();

                LexicalType type2 = env.getInput().peek().getType();
                if (StmtNode.isMatch(type2)) {
                    elseProcess = StmtNode.getHandler(type2, env);
                    elseProcess.parse();
                } else {
                    throw new SyntaxException("Invalid constitution of 'ELSE'.");
                }
            }
        } else {
            throw new SyntaxException("Invalid constitution of 'IF'.");
        }

        if (!isELSEIF){
            // 終端（ENDIFなど）の後にNLが必要
            if (env.getInput().get().getType() != LexicalType.NL) throw new SyntaxException("Invalid constitution of 'IF'. Not found NewLine in termination.");
        }
    }

    public String toString(int indent) {
        String ret = "";
        String dent = "";
        for (int i=0; i < indent; i++) {
            dent += "\t";
        }

        ret += "IF: condition="+cond+" THEN:[\n";
        if (process.getType() != NodeType.STMT_LIST) ret += dent+"\t";

        ret += process.toString(indent+1);
        if (process.getType() != NodeType.STMT_LIST) ret += "\n";

        if (followIfBlock.size() > 0) {
            for (Node elseifblock : followIfBlock) {
                ret += dent;
                ret += "] ELSEIF [\n";
                ret += dent+"\t";
                ret += elseifblock.toString(indent+1) + "\n";
            }
        }

        if (elseProcess != null) {
            ret += dent;
            ret += "] else [\n";
            if (elseProcess.getType() != NodeType.STMT_LIST) ret += dent+"\t";
            ret += elseProcess.toString(indent+1);
            if (elseProcess.getType() != NodeType.STMT_LIST) ret += "\n";
        }
        ret += dent;
        ret += "]";
        return ret;
    }
}
