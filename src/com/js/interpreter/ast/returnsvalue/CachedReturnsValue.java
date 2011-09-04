package com.js.interpreter.ast.returnsvalue;

import com.js.interpreter.ast.CompileTimeContext;
import com.js.interpreter.ast.ExpressionContext;
import com.js.interpreter.ast.instructions.SetValueExecutable;
import com.js.interpreter.exceptions.ParsingException;
import com.js.interpreter.exceptions.UnassignableTypeException;
import com.js.interpreter.linenumber.LineInfo;
import com.js.interpreter.pascaltypes.RuntimeType;
import com.js.interpreter.runtime.VariableContext;
import com.js.interpreter.runtime.codeunit.RuntimeExecutable;
import com.js.interpreter.runtime.exception.RuntimePascalException;

public class CachedReturnsValue implements ReturnsValue {
	ReturnsValue other;
	Object cache = null;

	public CachedReturnsValue(ReturnsValue other) {
		this.other = other;
	}

	@Override
	public RuntimeType get_type(ExpressionContext f) throws ParsingException {
		return other.get_type(f);
	}

	@Override
	public LineInfo getLineNumber() {
		return other.getLineNumber();
	}

	@Override
	public Object compileTimeValue(CompileTimeContext context)
			throws ParsingException {
		if (cache != null) {
			return cache;
		}
		cache = other.compileTimeValue(context);
		return cache;
	}

	@Override
	public ReturnsValue compileTimeExpressionFold(CompileTimeContext context)
			throws ParsingException {
		if (cache != null) {
			return this;
		} else {
			return new CachedReturnsValue(other);
		}
	}

	@Override
	public SetValueExecutable createSetValueInstruction(ReturnsValue r)
			throws UnassignableTypeException {
		throw new UnassignableTypeException(this);
	}

	@Override
	public Object getValue(VariableContext f, RuntimeExecutable<?> main)
			throws RuntimePascalException {
		if (cache != null) {
			return cache;
		}
		cache = other.getValue(f, main);
		return cache;
	}

}
