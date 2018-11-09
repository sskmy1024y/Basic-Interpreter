import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import lexical_analyzer.*;

public class Main {
    private static String filepath = "./src/test1.bas";

    public static void main(String[] args) {

        if (args.length > 0) {
            filepath = args[0];
        }

        FileInputStream fs;
        try {
            fs = new FileInputStream(filepath);
            try {
                LexicalUnit lexicalUnit;
                LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzerImpl(fs);
                while (true) {
                    lexicalUnit = lexicalAnalyzer.get();
                    System.out.println(lexicalUnit);
                    if (lexicalUnit.getType() == LexicalType.EOF){
                        break;
                    }
                }
            } catch(IOException e) {
                System.out.println("io error.");
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                fs.close();
            }
        } catch (Exception e) {
            System.out.println("file input error!");
        }

    }

}
