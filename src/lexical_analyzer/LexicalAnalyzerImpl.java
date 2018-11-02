package lexical_analyzer;

import java.io.*;
import java.util.HashMap;

public class LexicalAnalyzerImpl implements LexicalAnalyzer {

    private PushbackReader reader;
    private static HashMap<String, LexicalType> RESERVED_WORD_MAP = new HashMap<>();
    private static HashMap<String, LexicalType> RESERVED_OPERATOR_MAP = new HashMap<>();

    static {
        RESERVED_WORD_MAP.put("IF", LexicalType.IF);
        RESERVED_WORD_MAP.put("THEN", LexicalType.THEN);
        RESERVED_WORD_MAP.put("ELSE", LexicalType.ELSE);
        RESERVED_WORD_MAP.put("ELSEIF", LexicalType.ELSEIF);
        RESERVED_WORD_MAP.put("FOR", LexicalType.FOR);
        RESERVED_WORD_MAP.put("FORALL", LexicalType.FORALL);
        RESERVED_WORD_MAP.put("NEXT", LexicalType.NEXT);
        RESERVED_WORD_MAP.put("SUB", LexicalType.FUNC);
        RESERVED_WORD_MAP.put("DIM", LexicalType.DIM);
        RESERVED_WORD_MAP.put("AS", LexicalType.AS);
        RESERVED_WORD_MAP.put("END", LexicalType.END);
        RESERVED_WORD_MAP.put("WHILE", LexicalType.WHILE);
        RESERVED_WORD_MAP.put("DO", LexicalType.DO);
        RESERVED_WORD_MAP.put("UNTIL", LexicalType.UNTIL);
        RESERVED_WORD_MAP.put("LOOP", LexicalType.LOOP);
        RESERVED_WORD_MAP.put("TO", LexicalType.TO);
        RESERVED_WORD_MAP.put("WEND", LexicalType.WEND);
        RESERVED_OPERATOR_MAP.put("=", LexicalType.EQ);
        RESERVED_OPERATOR_MAP.put("<", LexicalType.LT);
        RESERVED_OPERATOR_MAP.put(">", LexicalType.GT);
        RESERVED_OPERATOR_MAP.put(">=", LexicalType.LE);
        RESERVED_OPERATOR_MAP.put("=>", LexicalType.LE);
        RESERVED_OPERATOR_MAP.put("<=", LexicalType.GE);
        RESERVED_OPERATOR_MAP.put("=<", LexicalType.GE);
        RESERVED_OPERATOR_MAP.put("<>", LexicalType.NE);
        RESERVED_OPERATOR_MAP.put("+", LexicalType.ADD);
        RESERVED_OPERATOR_MAP.put("-", LexicalType.SUB);
        RESERVED_OPERATOR_MAP.put("*", LexicalType.MUL);
        RESERVED_OPERATOR_MAP.put("/", LexicalType.DIV);
        RESERVED_OPERATOR_MAP.put("(", LexicalType.RP);
        RESERVED_OPERATOR_MAP.put(")", LexicalType.LP);
        RESERVED_OPERATOR_MAP.put(",", LexicalType.COMMA);
        RESERVED_OPERATOR_MAP.put(".", LexicalType.DOT);

    }

    public LexicalAnalyzerImpl(String filepath) throws Exception{
        Reader ir = new InputStreamReader(new FileInputStream(filepath));
        this.reader = new PushbackReader(ir);
    }

    @Override
    public LexicalUnit get() throws Exception {

        while(true) {
            //まずは一文字読む
            //EOF対策のためにreadはintで返す。
            int ci = reader.read();

            //End Of File(-1)であれば、EOFを返す。
            if (ci < 0) {
                return new LexicalUnit(LexicalType.EOF);
            }

            char ch = (char) ci;

            if (String.valueOf(ch).matches("^[a-zA-Z]\\w*")) {
                reader.unread(ci);
                return getString();
            }

            if (String.valueOf(ch).matches("[0-9.]+")){
                reader.unread(ci);
                return getNumber();
            }

            if (String.valueOf(ch).matches("^\"[^\"]*\"?")){
                return getLiteral();
            }

            if (String.valueOf(ch).matches("[\\.\\+\\-\\*\\/\\)\\(,]")){
                reader.unread(ci);
                return getSingleOperator();
            }

            if (String.valueOf(ch).matches("[><=]|=[><]|[><]=|<>")){
                reader.unread(ci);
                return getMultiOperator();
            }
        }
    }

    private LexicalUnit getString() throws Exception {
        String target = "";
        while(true) {
            int ci = reader.read();
            char ch = (char) ci;
            if (String.valueOf(ch).matches("^[a-zA-Z]\\w*")) {
                target += ch;
                continue;
            } else if (!target.equals("") && String.valueOf(ch).matches("^[a-zA-Z0-9]\\w*")){
                target += ch;
                continue;
            }
            reader.unread(ci);
            break;
        }
        if (RESERVED_WORD_MAP.containsKey(target)){
            return new LexicalUnit(RESERVED_WORD_MAP.get(target));
        } else {
            return new LexicalUnit(LexicalType.NAME, new ValueImpl(target));
        }
    }

    private LexicalUnit getNumber() throws Exception {
        String target = "";
        while(true) {
            int ci = reader.read();
            char ch = (char) ci;
            if (String.valueOf(ch).matches("[0-9.]+")) {
                target += ch;
                continue;
            }
            reader.unread(ci);
            break;
        }
        return new LexicalUnit(LexicalType.INTVAL, new ValueImpl(Integer.parseInt(target)));
    }

    private LexicalUnit getLiteral() throws Exception {
        String target = "";
        while(true) {
            int ci = reader.read();
            char ch = (char) ci;
            if (!String.valueOf(ch).matches("^\"[^\"]*\"?")) {
                target += ch;
                continue;
            }
            break;
        }
        return new LexicalUnit(LexicalType.LITERAL, new ValueImpl(target));
    }

    private LexicalUnit getSingleOperator() throws Exception {
        String target = "";
        while(true) {
            int ci = reader.read();
            char ch = (char) ci;
            if (String.valueOf(ch).matches("[\\.\\+\\-\\*\\/\\)\\(,]")) {
                target += ch;
                continue;
            }
            reader.unread(ci);
            break;
        }
        if (RESERVED_OPERATOR_MAP.containsKey(target)) return new LexicalUnit(RESERVED_OPERATOR_MAP.get(target));
        else throw new Exception();
    }

    private LexicalUnit getMultiOperator() throws Exception {
        String target = "";
        while(true) {
            int ci = reader.read();
            char ch = (char) ci;
            if (String.valueOf(ch).matches("[><=]|=[><]|[><]=|<>")) {
                target += ch;
                continue;
            }
            reader.unread(ci);
            break;
        }
        if (RESERVED_OPERATOR_MAP.containsKey(target)) return new LexicalUnit(RESERVED_OPERATOR_MAP.get(target));
        else throw new Exception();
    }


    @Override
    public boolean expect(LexicalType type) throws Exception {
        return false;
    }

    @Override
    public void unget(LexicalUnit token) throws Exception {

    }
}
