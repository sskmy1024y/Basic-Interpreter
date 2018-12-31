package lexical_analyzer;

import java.io.*;
import java.util.*;

public class LexicalAnalyzerImpl implements LexicalAnalyzer {

    private PushbackReader reader;
    private static HashMap<String, LexicalType> RESERVED_WORD_MAP = new HashMap<>();
    private static HashMap<String, LexicalType> SYMBOL_MAP = new HashMap<>();
    private List<LexicalUnit> buffer = new ArrayList<>();

    static {
        RESERVED_WORD_MAP.put("IF", LexicalType.IF);
        RESERVED_WORD_MAP.put("THEN", LexicalType.THEN);
        RESERVED_WORD_MAP.put("ELSE", LexicalType.ELSE);
        RESERVED_WORD_MAP.put("ELSEIF", LexicalType.ELSEIF);
        RESERVED_WORD_MAP.put("ENDIF", LexicalType.ENDIF);
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

        SYMBOL_MAP.put("=", LexicalType.EQ);
        SYMBOL_MAP.put("<", LexicalType.LT);
        SYMBOL_MAP.put(">", LexicalType.GT);
        SYMBOL_MAP.put(">=", LexicalType.GE);
        SYMBOL_MAP.put("=>", LexicalType.GE);
        SYMBOL_MAP.put("<=", LexicalType.LE);
        SYMBOL_MAP.put("=<", LexicalType.LE);
        SYMBOL_MAP.put("<>", LexicalType.NE);
        SYMBOL_MAP.put("+", LexicalType.ADD);
        SYMBOL_MAP.put("-", LexicalType.SUB);
        SYMBOL_MAP.put("*", LexicalType.MUL);
        SYMBOL_MAP.put("/", LexicalType.DIV);
        SYMBOL_MAP.put("(", LexicalType.LP);
        SYMBOL_MAP.put(")", LexicalType.RP);
        SYMBOL_MAP.put(",", LexicalType.COMMA);
        SYMBOL_MAP.put(".", LexicalType.DOT);
        SYMBOL_MAP.put("\n", LexicalType.NL);
    }

    public LexicalAnalyzerImpl(FileInputStream fs) throws Exception {
        Reader ir = new InputStreamReader(fs);
        this.reader = new PushbackReader(ir);
    }

    @Override
    public LexicalUnit get() throws Exception {
        // ungetされたものがないか確認する
        if (!buffer.isEmpty()){
            int index = buffer.size()-1;
            LexicalUnit unit = buffer.get(index);
            buffer.remove(index);
            return unit;
        }

        while (true) {
            // まずは一文字読む
            // EOF対策のためにreadはintで返す。
            int ci = reader.read();

            // End Of File(-1)であれば、EOFを返す。
            if (ci < 0) {
                return new LexicalUnit(LexicalType.EOF);
            }

            char ch = (char) ci;

            if (String.valueOf(ch).matches("^[a-zA-Z]\\w*")) {
                reader.unread(ci);
                return getString();
            }

            if (String.valueOf(ch).matches("[0-9.]+")) {
                reader.unread(ci);
                return getNumber();
            }

            if (String.valueOf(ch).matches("^\"[^\"]*\"?")) {
                return getLiteral();
            }

            if (SYMBOL_MAP.containsKey(ch + "")) {
                reader.unread(ci);
                return getSymbol();
            }
        }
    }

    public LexicalUnit peek () throws Exception {
        return peek(1);
    }

    public LexicalUnit peek (int num) throws Exception {
        List<LexicalUnit> list = new ArrayList<>();
        for(int i=0; i<num; i++){
            list.add(get());
        }
        LexicalUnit result = list.get(list.size()-1);
        for (int i=list.size()-1; i >= 0; i--) {
            unget(list.get(i));
        }
        return result;
    }

    private LexicalUnit getString() throws Exception {
        String target = "";
        while (true) {
            int ci = reader.read();
            char ch = (char) ci;
            if (String.valueOf(ch).matches("^[a-zA-Z]\\w*")) {
                target += ch;
                continue;
            } else if (!target.equals("") && String.valueOf(ch).matches("^[a-zA-Z0-9]\\w*")) {
                target += ch;
                continue;
            }
            reader.unread(ci);
            break;
        }
        if (RESERVED_WORD_MAP.containsKey(target)) {
            return new LexicalUnit(RESERVED_WORD_MAP.get(target));
        } else {
            return new LexicalUnit(LexicalType.NAME, new ValueImpl(target));
        }
    }

    private LexicalUnit getNumber() throws Exception {
        String target = "";
        boolean doubleFlug = false;
        while (true) {
            int ci = reader.read();
            char ch = (char) ci;
            if (String.valueOf(ch).matches("[0-9.]+")) {
                if (ch == '.')
                    doubleFlug = true;
                target += ch;
                continue;
            }
            reader.unread(ci);
            break;
        }
        if (doubleFlug) {
            return new LexicalUnit(LexicalType.DOUBLEVAL, new ValueImpl(Double.parseDouble(target)));
        } else {
            return new LexicalUnit(LexicalType.INTVAL, new ValueImpl(Integer.parseInt(target)));
        }
    }

    private LexicalUnit getLiteral() throws Exception {
        String target = "";
        while (true) {
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

    private LexicalUnit getSymbol() throws Exception {
        String target = "";
        while (true) {
            int ci = reader.read();

            if (ci < 0) return new LexicalUnit(SYMBOL_MAP.get(target));
            char ch = (char) ci;

            if (SYMBOL_MAP.containsKey(target + ch)) {
                target += ch;
            } else {
                reader.unread(ci);
                return new LexicalUnit(SYMBOL_MAP.get(target));
            }
        }
    }


    @Override
    public boolean expect(LexicalType type) throws Exception {
        return false;
    }

    @Override
    public void unget(LexicalUnit token) throws Exception {
        buffer.add(token);
    }
}
