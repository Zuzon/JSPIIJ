package com.js.interpreter.pascaltypes;

import java.util.HashMap;

import ncsa.tools.common.util.TypeUtils;
import serp.bytecode.Code;

import com.js.interpreter.ast.ExpressionContext;
import com.js.interpreter.ast.returnsvalue.ReturnsValue;
import com.js.interpreter.ast.returnsvalue.StringIndexAccess;
import com.js.interpreter.ast.returnsvalue.boxing.CharacterBoxer;
import com.js.interpreter.ast.returnsvalue.boxing.StringBoxer;
import com.js.interpreter.ast.returnsvalue.boxing.StringBuilderBoxer;
import com.js.interpreter.ast.returnsvalue.cloning.StringBuilderCloner;
import com.js.interpreter.exceptions.NonArrayIndexed;
import com.js.interpreter.exceptions.ParsingException;
import com.js.interpreter.pascaltypes.bytecode.RegisterAllocator;
import com.js.interpreter.pascaltypes.bytecode.TransformationInput;
import com.js.interpreter.pascaltypes.typeconversion.TypeConverter;

public class JavaClassBasedType extends DeclaredType {
	Class c;

	protected static final HashMap<DeclaredType, Object> default_values = new HashMap<DeclaredType, Object>();

	public static final DeclaredType Boolean = new JavaClassBasedType(
			Boolean.class);

	public static final DeclaredType Character = new JavaClassBasedType(
			Character.class);

	public static final DeclaredType StringBuilder = new JavaClassBasedType(
			StringBuilder.class);

	public static final DeclaredType Long = new JavaClassBasedType(Long.class);

	public static final DeclaredType Double = new JavaClassBasedType(
			Double.class);

	public static final DeclaredType Integer = new JavaClassBasedType(
			Integer.class);

	static {
		default_values.put(JavaClassBasedType.Integer, 0);
		default_values.put(JavaClassBasedType.Double, 0.0D);
		default_values.put(JavaClassBasedType.Long, 0L);
		default_values.put(JavaClassBasedType.Character, '\0');
		default_values.put(JavaClassBasedType.Boolean, false);
	}

	private JavaClassBasedType(Class name) {
		c = name;
	}

	@Override
	public boolean isarray() {
		return false;
	}

	@Override
	public boolean equals(DeclaredType obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof JavaClassBasedType) {
			Class other = ((JavaClassBasedType) obj).c;
			return c == other || c == Object.class || other == Object.class;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (TypeUtils.isPrimitiveWrapper(c) ? TypeUtils.getTypeForClass(c)
				: c).getCanonicalName().hashCode();

	}

	@Override
	public Object initialize() {
		Object result;
		if ((result = JavaClassBasedType.default_values.get(this)) != null) {
			return result;
		} else {
			try {
				return c.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

			return null;
		}
	}

	@Override
	public Class getTransferClass() {
		return c;
	}

	@Override
	public String toString() {
		if (this == Boolean) {
			return "Boolean";
		} else if (this == Character) {
			return "Character";
		} else if (this == Integer) {
			return "Integer";
		} else if (this == Double) {
			return "Double";
		} else if (this == Long) {
			return "Long";
		} else if (this == StringBuilder) {
			return "String";
		}
		return c.getCanonicalName();
	}

	public static DeclaredType anew(Class c) {
		if (c == Integer.class) {
			return JavaClassBasedType.Integer;
		}
		if (c == Double.class) {
			return JavaClassBasedType.Double;
		}
		if (c == StringBuilder.class) {
			return JavaClassBasedType.StringBuilder;
		}
		if (c == Long.class) {
			return JavaClassBasedType.Long;
		}
		if (c == Character.class) {
			return JavaClassBasedType.Character;
		}
		if (c == Boolean.class) {
			return JavaClassBasedType.Boolean;
		}

		return new JavaClassBasedType(c);
	}

	@Override
	public ReturnsValue convert(ReturnsValue value, ExpressionContext f)
			throws ParsingException {

		RuntimeType other_type = value.get_type(f);

		if (other_type.declType instanceof JavaClassBasedType) {

			if (this.equals(other_type.declType)) {
				return cloneValue(value);
			}
			if (this == StringBuilder
					&& other_type.declType == JavaClassBasedType.Character) {
				return new CharacterBoxer(value);
			}
			if (this == StringBuilder
					&& ((JavaClassBasedType) other_type.declType).c == String.class) {
				return new StringBoxer(value);
			}
			if (this.c == String.class
					&& other_type.declType == JavaClassBasedType.StringBuilder) {
				return new StringBuilderBoxer(value);
			}
			if (this.c == String.class
					&& other_type.declType == JavaClassBasedType.Character) {
				return new StringBuilderBoxer(new CharacterBoxer(value));
			}
			return TypeConverter.autoConvert(this, value,
					(JavaClassBasedType) other_type.declType);
		}
		return null;
	}

	@Override
	public void pushDefaultValue(Code constructor_code, RegisterAllocator ra) {
		if (this == StringBuilder) {
			constructor_code.anew().setType(StringBuilder.class);
			constructor_code.dup();
			try {
				constructor_code.invokespecial().setMethod(
						StringBuilder.class.getConstructor());
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Object value = default_values.get(this);
			if (value != null) {
				constructor_code.constant().setValue(value);
			}
		}
	}

	@Override
	public ReturnsValue cloneValue(final ReturnsValue r) {
		if (this == StringBuilder) {
			return new StringBuilderCloner(r);
		} else {
			return r;
		}
	}

	@Override
	public void cloneValueOnStack(TransformationInput t) {
		if (this == StringBuilder) {
			Code c = t.getCode();
			c.anew().setType(StringBuilder.class);
			t.pushInputOnStack();
			try {
				c.invokespecial().setMethod(
						StringBuilder.class.getConstructor(CharSequence.class));
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			t.pushInputOnStack();
		}
	}

	@Override
	public ReturnsValue generateArrayAccess(ReturnsValue array,
			ReturnsValue index) throws NonArrayIndexed {
		if (this == StringBuilder) {
			return new StringIndexAccess(array, index);
		}
		throw new NonArrayIndexed(array.getLineNumber(), this);
	}

	@Override
	public Class<?> getStorageClass() {
		Class c2 = TypeUtils.getTypeForClass(c);
		return c2 == null ? c : c2;
	}

	@Override
	public void arrayStoreOperation(Code c) {
		if (this == Boolean) {
			c.bastore();
		} else if (this == Character) {
			c.castore();
		} else if (this == Double) {
			c.dastore();
		} else if (this == Integer) {
			c.iastore();
		} else {
			c.aastore();
		}
	}

	@Override
	public void convertStackToStorageType(Code c) {
		if (this == Integer) {
			c.invokestatic().setMethod(Integer.class, "valueOf", Integer.class,
					new Class[] { int.class });
		} else if (this == Double) {
			c.invokestatic().setMethod(Double.class, "valueOf", Double.class,
					new Class[] { double.class });
		} else if (this == Character) {
			c.invokestatic().setMethod(Character.class, "valueOf",
					Character.class, new Class[] { char.class });
		} else if (this == Boolean) {
			c.invokestatic().setMethod(Boolean.class, "valueOf", Boolean.class,
					new Class[] { boolean.class });
		}
		// Otherwise, do nothing.
	}
}
