package syntax_analyzer.nodes;

import lexical_analyzer.LexicalType;
import syntax_analyzer.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class EndNode extends Node {

    private static Set<LexicalType> first = new HashSet<>(Arrays.asList(
            LexicalType.END
    ));

    public static boolean isMatch(LexicalType type){
        return first.contains(type);
    }

    public static Node getHandler(LexicalType type, Environment env) throws Exception {
        if (isMatch(type)) return new EndNode(env);
        else return null;
    }

    private EndNode(Environment env){
        this.env = env;
        this.type = NodeType.END;
    }

    public void parse() throws Exception {
        if (env.getInput().get().getType()!=LexicalType.END){
            throw new InternalError("Is not END.");
        }
    }

    @Override
    public String toString() {
        return "END";
    }
}
