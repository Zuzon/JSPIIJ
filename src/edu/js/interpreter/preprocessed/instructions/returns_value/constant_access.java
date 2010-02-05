package edu.js.interpreter.preprocessed.instructions.returns_value;

import edu.js.interpreter.pascal_types.class_pascal_type;
import edu.js.interpreter.pascal_types.pascal_type;
import edu.js.interpreter.preprocessed.function_declaration;
import edu.js.interpreter.preprocessed.interpreting_objects.function_on_stack;

public class constant_access implements returns_value {
	final Object constant_value;

	public constant_access(Object o) {
		this.constant_value = o;
	}

	public Object get_value(function_on_stack f) {
		return constant_value;
	}

	@Override
	public String toString() {
		return "get_constant[" + constant_value + ']';
	}

	public pascal_type get_type(function_declaration f) {
		return class_pascal_type.anew(constant_value.getClass());
	}
}