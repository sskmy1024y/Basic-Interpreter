package syntax_analyzer;

import lexical_analyzer.LexicalType;
import lexical_analyzer.LexicalUnit;
import lexical_analyzer.Value;

abstract public class Node {
    protected NodeType type;
    protected Environment env;

    /** Creates a new instance of Node */
    public Node() {
    }
    public Node(NodeType my_type) {
        type = my_type;
    }
    public Node(Environment my_env) {
        env = my_env;
    }
    
    public NodeType getType() {
        return type;
    }
    
    public void parse() throws Exception {
    }
    
    public Value getValue() throws Exception {
        return null;
    }
 
    public String toString() {
    	if (type == NodeType.END) return "END";
    	else return "Node";        
    }

    public String toString(int indent) {
        if (type == NodeType.END) return "END";
        else return "Node";
    }
}
