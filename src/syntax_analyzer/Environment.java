package syntax_analyzer;

import java.util.HashMap;
import lexical_analyzer.*;

public class Environment {
	LexicalAnalyzerImpl input;
	HashMap var_table;
	    
	public Environment(LexicalAnalyzerImpl my_input) {
		input = my_input;
		var_table = new HashMap();
	}
	    
	public LexicalAnalyzerImpl getInput() {
		return input;
	}
}
