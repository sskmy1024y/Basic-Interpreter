package syntax_analyzer;

import java.util.HashMap;
import lexical_analyzer.*;

public class Environment {
	   LexicalAnalyzer input;
	   HashMap var_table;
	    
	    public Environment(LexicalAnalyzer my_input) {
	        input = my_input;
	        var_table = new HashMap();
	    }
	    
	    public LexicalAnalyzer getInput() {
	        return input;
	    }	    
}
