package preprocessed.instructions.returns_value;

import preprocessed.dummy_declaration;
import preprocessed.function_declaration;
import preprocessed.instructions.executable;
import preprocessed.interpreting_objects.function_on_stack;
import processing.pascal_program;

public class abstract_function_call implements returns_value, executable {
	String name;

	returns_value[] arguments;

	public abstract_function_call(String name, returns_value[] arguments) {
		this.name = name;
	}

	public Object get_value(function_on_stack f) {
		Class[] arg_types = new Class[arguments.length];
		Object[] values = new Object[arguments.length];
		for (int i = 0; i < arguments.length; i++) {
			arg_types[i] = arguments[i].get_type(f.program, f.prototype);
			values[i] = arguments[i].get_value(f);
		}
		dummy_declaration header = new dummy_declaration(name, arg_types);

		return new function_on_stack(
				f.program,
				(function_declaration) f.program.callable_functions.get(header),
				values).execute();
	}

	public String toString() {
		return "call function [" + "ARGFIXTHISNOW" + "] with args ["
				+ arguments + ']';
	}

	public void execute(function_on_stack f) {
		get_value(f);
	}

	public Class get_type(pascal_program p, function_declaration f) {
		Class[] arg_types = new Class[arguments.length];
		for (int i = 0; i < arguments.length; i++) {
			arg_types[i] = arguments[i].get_type(p, f);
		}
		dummy_declaration header = new dummy_declaration(name, arg_types);
		return p.callable_functions.get(header).get_return_type();
	}
}