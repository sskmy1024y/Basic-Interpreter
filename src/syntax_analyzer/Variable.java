package syntax_analyzer;

import lexical_analyzer.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Variable extends Node {
	String var_name;
	Value value;

	private final static Set<LexicalType> first = new HashSet<>(Arrays.asList(
			LexicalType.NAME
	));

	public Variable(String name) {
	        var_name = name;
	    }
	public Variable(String name, Value value) {
		this.var_name = name;
		this.value = value;
	}
	    
	public void setValue(Value value) {
		this.value = value;
	}

	public void parse() {
	}

	public Value eval() {
		return value;
	}

	public String toString() {
		return "Variable:"+var_name;
	}
	    
}
