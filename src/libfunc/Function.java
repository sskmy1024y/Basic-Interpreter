package libfunc;

import lexical_analyzer.Value;
import syntax_analyzer.nodes.ExprListNode;

public abstract class Function {
    public abstract Value eval(ExprListNode arg) throws Exception;
}
