package model;



import exception.InterpretException;


public class Value {

	/**
	 * 存储值对象的类型,常量存储在Symbol中
	 */
	private SymbolType mType;
	private int mInt;
	private double mDouble;
	private int[] mArrayInt;
	private double[] mArrayDouble;

	/**
	 * 创建一个type型值对象
	 * 
	 * @param type
	 * @param value
	 */
	public Value(SymbolType type) {
		this.mType = type;
	}

	/**
	 * 存储boolean值用的Value对象
	 * 
	 * @param bool
	 */
	public Value(boolean bool) {
		if (bool) {
			this.mType = SymbolType.TRUE;
		} else {
			this.mType = SymbolType.FALSE;
		}
	}

	public Value() {
	}

	public SymbolType getType() {
		return mType;
	}

	/**
	 * 尽量不要中途改变type,除非你明确知道可能发生什么
	 * 
	 * @param mType
	 */
	public void setType(SymbolType mType) {
		this.mType = mType;
	}

	public int getInt() {
		return mInt;
	}

	public void setInt(int mInt) {
		this.mInt = mInt;
	}

	public double getDouble() {
		return mDouble;
	}

	public void setDouble(double mDouble) {
		this.mDouble = mDouble;
	}

	public int[] getArrayInt() {
		return mArrayInt;
	}

	public void setArrayInt(int[] mArrayInt) {
		this.mArrayInt = mArrayInt;
	}

	public double[] getArrayDouble() {
		return mArrayDouble;
	}

	public void setArrayDouble(double[] mArrayReal) {
		this.mArrayDouble = mArrayReal;
	}

	/**
	 * 初始化数组
	 * 
	 * @param dim
	 *            长度
	 */
	public void initArray(int dim) {
		if (mType == SymbolType.ARRAY_INT) {
			mArrayInt = new int[dim];
		} else {
			mArrayDouble = new double[dim];
		}
	}

	/**
	 * 两值相加
	 * 
	 * @param value
	 * @return
	 * @throws InterpretException
	 */
	public Value PLUS(Value value) throws InterpretException {
		if (this.mType == SymbolType.SINGLE_DOUBLE) {
			Value rv = new Value(SymbolType.SINGLE_DOUBLE);
			if (value.mType == SymbolType.SINGLE_INT) {
				rv.setDouble(this.mDouble + value.mInt);
				return rv;
			} else if (value.mType == SymbolType.SINGLE_DOUBLE) {
				rv.setDouble(this.mDouble + value.mDouble);
				return rv;
			}
		} else if (this.mType == SymbolType.SINGLE_INT) {
			if (value.mType == SymbolType.SINGLE_INT) {
				Value rv = new Value(SymbolType.SINGLE_INT);
				rv.setInt(this.mInt + value.mInt);
				return rv;
			} else if (value.mType == SymbolType.SINGLE_DOUBLE) {
				Value rv = new Value(SymbolType.SINGLE_DOUBLE);
				rv.setDouble(this.mInt + value.mDouble);
				return rv;
			}
		}
		throw new InterpretException("算数运算非法");
	}

	/**
	 * 两值相减
	 * 
	 * @param value
	 * @return
	 * @throws InterpretException
	 */
	public Value MINUS(Value value) throws InterpretException {
		if (this.mType == SymbolType.SINGLE_DOUBLE) {
			Value rv = new Value(SymbolType.SINGLE_DOUBLE);
			if (value.mType == SymbolType.SINGLE_INT) {
				rv.setDouble(this.mDouble - value.mInt);
				return rv;
			} else if (value.mType == SymbolType.SINGLE_DOUBLE) {
				rv.setDouble(this.mDouble- value.mDouble);
				return rv;
			}
		} else if (this.mType == SymbolType.SINGLE_INT) {
			if (value.mType == SymbolType.SINGLE_INT) {
				Value rv = new Value(SymbolType.SINGLE_INT);
				rv.setInt(this.mInt - value.mInt);
				return rv;
			} else if (value.mType == SymbolType.SINGLE_DOUBLE) {
				Value rv = new Value(SymbolType.SINGLE_DOUBLE);
				rv.setDouble(this.mInt - value.mDouble);
				return rv;
			}
		}
		throw new InterpretException("算数运算非法");
	}

	/**
	 * 两值相乘
	 * @param value
	 * @return
	 * @throws InterpretException
	 */
	public Value MUL(Value value) throws InterpretException {
		if (this.mType == SymbolType.SINGLE_DOUBLE) {
			Value rv = new Value(SymbolType.SINGLE_DOUBLE);
			if (value.mType == SymbolType.SINGLE_INT) {
				rv.setDouble(this.mDouble * value.mInt);
				return rv;
			} else if (value.mType == SymbolType.SINGLE_DOUBLE) {
				rv.setDouble(this.mDouble * value.mDouble);
				return rv;
			}
		} else if (this.mType == SymbolType.SINGLE_INT) {
			if (value.mType == SymbolType.SINGLE_INT) {
				Value rv = new Value(SymbolType.SINGLE_INT);
				rv.setInt(this.mInt * value.mInt);
				return rv;
			} else if (value.mType == SymbolType.SINGLE_DOUBLE) {
				Value rv = new Value(SymbolType.SINGLE_DOUBLE);
				rv.setDouble(this.mInt * value.mDouble);
				return rv;
			}
		}
		throw new InterpretException("算数运算非法");
	}

	/**
	 * 两值相除，含有除数是否为0的检测
	 * @param value
	 * @return
	 * @throws InterpretException
	 */
	public Value DIV(Value value) throws InterpretException {
		if (this.mType == SymbolType.SINGLE_DOUBLE) {
			Value rv = new Value(SymbolType.SINGLE_DOUBLE);
			if (value.mType == SymbolType.SINGLE_INT) {
				if (value.getInt() == 0) {
					throw new InterpretException("不能除0");
				}
				rv.setDouble(this.mDouble / value.mInt);
				return rv;
			} else if (value.mType == SymbolType.SINGLE_DOUBLE) {
				if (value.getDouble() == 0) {
					throw new InterpretException("不能除0");
				}
				rv.setDouble(this.mDouble / value.mDouble);
				return rv;
			}
		} else if (this.mType == SymbolType.SINGLE_INT) {
			if (value.mType == SymbolType.SINGLE_INT) {
				if (value.getInt() == 0) {
					throw new InterpretException("不能除0");
				}
				Value rv = new Value(SymbolType.SINGLE_INT);
				rv.setInt(this.mInt / value.mInt);
				return rv;
			} else if (value.mType == SymbolType.SINGLE_DOUBLE) {
				if (value.getDouble() == 0) {
					throw new InterpretException("不能除0");
				}
				Value rv = new Value(SymbolType.SINGLE_DOUBLE);
				rv.setDouble(this.mInt / value.mDouble);
				return rv;
			}
		}
		throw new InterpretException("算数运算非法");
	}

	/**
	 * 该值是否大于参数列表里的值？
	 * @param value
	 * @return
	 * @throws InterpretException
	 */
	public Value GT(Value value) throws InterpretException {
		if (this.mType == SymbolType.SINGLE_INT) {
			if (value.mType == SymbolType.SINGLE_INT) {
				return new Value(this.mInt > value.mInt);
			} else if (value.mType == SymbolType.SINGLE_DOUBLE) {
				return new Value(this.mInt > value.mDouble);
			}
		} else if (this.mType == SymbolType.SINGLE_DOUBLE) {
			if (value.mType == SymbolType.SINGLE_INT) {
				return new Value(this.mDouble > value.mInt);
			} else if (value.mType == SymbolType.SINGLE_DOUBLE) {
				return new Value(this.mDouble > value.mDouble);
			}
		}
		throw new InterpretException("逻辑比较非法");
	}

	/**
	 * 该值是否等于参数列表里的值？
	 * @param value
	 * @return
	 * @throws InterpretException
	 */
	public Value EQ(Value value) throws InterpretException {
		if (this.mType == SymbolType.SINGLE_INT) {
			if (value.mType == SymbolType.SINGLE_INT) {
				return new Value(this.mInt == value.mInt);
			} else if (value.mType == SymbolType.SINGLE_DOUBLE) {
				return new Value(this.mInt == value.mDouble);
			}
		} else if (this.mType == SymbolType.SINGLE_DOUBLE) {
			if (value.mType == SymbolType.SINGLE_INT) {
				return new Value(this.mDouble == value.mInt);
			} else if (value.mType == SymbolType.SINGLE_DOUBLE) {
				return new Value(this.mDouble == value.mDouble);
			}
		}
		throw new InterpretException("逻辑比较非法");
	}

	public Value OR(Value value) {
		if (this.mType == SymbolType.TRUE || value.mType == SymbolType.TRUE) {
			return new Value(SymbolType.TRUE);
		} else {
			return new Value(SymbolType.FALSE);
		}
	}

	public Value GET(Value value) throws InterpretException {
		return this.GT(value).OR(this.EQ(value));
	}

	public Value LT(Value value) throws InterpretException {
		return NOT(this.GET(value));
	}

	public Value LET(Value value) throws InterpretException {
		return NOT(this.GT(value));
	}

	public Value NEQ(Value value) throws InterpretException {
		return NOT(this.EQ(value));
	}

	public static Value NOT(Value value) throws InterpretException {
		if (value.mType == SymbolType.TRUE) {
			return new Value(SymbolType.FALSE);
		} else if (value.mType == SymbolType.FALSE) {
			return new Value(SymbolType.TRUE);
		} else if (value.mType == SymbolType.SINGLE_INT) {
			Value rv = new Value(SymbolType.SINGLE_INT);
			rv.setInt(value.mInt * -1);
			return rv;
		} else if (value.mType == SymbolType.SINGLE_DOUBLE) {
			Value rv = new Value(SymbolType.SINGLE_DOUBLE);
			rv.setDouble(value.mDouble * -1);
			return rv;
		}
		throw new InterpretException("负号使用非法");
	}

	@Override
	public String toString() {
		switch (this.mType) {
		case SINGLE_INT:
			return mInt + "";
		case SINGLE_DOUBLE:
			return mDouble + "";
		case TRUE:
			return "true";
		case FALSE:
			return "false";
		default:
			return "array can't be write";
		}
	}

	/**
	 * 获取Value对应的double值
	 * 
	 * @return
	 */
	public Value toDouble() {
		if (mType == SymbolType.SINGLE_DOUBLE) {
			return this;
		} else {
			mType = SymbolType.SINGLE_DOUBLE;
			mDouble = (int) mInt;
			mInt = 0;
			return this;
		}
	}
}
