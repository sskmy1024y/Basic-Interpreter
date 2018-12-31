import java.io.FileInputStream;
import lexical_analyzer.*;
import syntax_analyzer.*;
import syntax_analyzer.nodes.*;

public class Main {
    	private static String filepath = "./src/test1.bas";
//    private static String filepath = "/Users/sho/sho/Develop/Github/temp/interpreter-testPrograms/test20.bas";

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        LexicalAnalyzerImpl	lex;
        LexicalUnit			lu;
        Environment 		env;
        Node 				program;

        System.out.println("basic parser");

        if (args.length > 0) filepath = args[0];

        try (FileInputStream fs = new FileInputStream(filepath)) {

            lex = new LexicalAnalyzerImpl(fs);
            env = new Environment(lex);

            program = ProgramNode.getHandler(lex.peek().getType(), env);
            program.parse();

//            System.out.println(program.toString(0));

            System.out.println(program.getValue());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
