import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import lexical_analyzer.*;
import syntax_analyzer.*;
import syntax_analyzer.nodes.ProgramNode;

public class Main {
//    private static String filepath = "./src/test1.bas";
    private static String filepath = "~/sho/Develop/Github/temp/interpreter-testPrograms/test05.bas";

    public static void main(String[] args) throws Exception {

        if (args.length > 0) {
            filepath = args[0];
        }
//
//        try (FileInputStream fs = new FileInputStream(filepath)) {
//            LexicalUnit lexicalUnit;
//            LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzerImpl(fs);
//            while (true) {
//                lexicalUnit = lexicalAnalyzer.get();
//                System.out.println(lexicalUnit);
//                if (lexicalUnit.getType() == LexicalType.EOF) {
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            System.out.println(e);
//        }

        FileInputStream fin = null;
        LexicalAnalyzerImpl lex;
        LexicalUnit first;
        Environment env;
        Node program;

        System.out.println("basic parser");
        fin = new FileInputStream(filepath);
        lex = new LexicalAnalyzerImpl(fin);
        env = new Environment(lex);
        first = lex.get();
        lex.unget(first);

        program = ProgramNode.getHandler(first.getType(), env);
//        if (program != null && program.parse()) {
//            System.out.println(program);
//            System.out.println("value=" + program.getValue());
//        }

    }

}
