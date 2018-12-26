package syntax_analyzer;

import java.util.Hashtable;

import lexical_analyzer.*;
import libfunc.Function;
import libfunc.PrintFunction;
import sun.tools.tree.Vset;
import syntax_analyzer.nodes.VariableNode;

public class Environment {
	private LexicalAnalyzerImpl input;

	private Hashtable<String, VariableNode> var_table = new Hashtable<>() ;
	private Hashtable<String, Function> func_table = new Hashtable<>();

	private void initFunction() {
		func_table.put("PRINT", new PrintFunction());
	}

	public Environment(LexicalAnalyzerImpl my_input) {
		input = my_input;
		initFunction();
	}
	    
	public LexicalAnalyzerImpl getInput() {
		return input;
	}


	public VariableNode getVariable(String varName) {
		VariableNode vn;
		vn = var_table.get(varName);
		if (vn == null) {
			vn = new VariableNode(varName);
			var_table.put(varName, vn);
		}
		return vn;
	}

	public Function getFunction(String funcName) {
		Function res = func_table.get(funcName);
		if (res == null) throw new InternalError("Not found function: "+funcName);
		return res;
	}


}
