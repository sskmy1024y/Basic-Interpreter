package libfunc;

import lexical_analyzer.Value;
import syntax_analyzer.nodes.ExprListNode;

public class PrintFunction extends Function {
    public Value eval(ExprListNode arg) throws Exception {
        System.out.println(arg.get(0).getSValue());
        return null;
    }
}
