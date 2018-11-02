package lexical_analyzer;

public interface LexicalAnalyzer {
    /**
     * ファイルの先頭から字句を返す
     * @return
     * @throws Exception
     */
    public LexicalUnit get() throws Exception;
    public boolean expect(LexicalType type) throws Exception;
    public void unget(LexicalUnit token) throws Exception;
}
