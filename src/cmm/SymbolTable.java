package cmm;

import java.util.ArrayList;
import java.util.LinkedList;

import exception.InterpretException;
import model.Symbol;
import model.SymbolType;
import model.Value;

public class SymbolTable {

	private static final String TEMP_PREFIX = "temp";

	private static SymbolTable symbolTable = new SymbolTable();
	private static LinkedList<Symbol> tempNames = new LinkedList<Symbol>();

	private ArrayList<Symbol> symbolList = new ArrayList<Symbol>();

	private SymbolTable() {
	}

	public static SymbolTable getSymbolTable() {
		return symbolTable;
	}

	/**
	 * 新建符号表和临时表
	 */
	public void newTable() {
		symbolList = new ArrayList<Symbol>();
		tempNames = new LinkedList<Symbol>();
	}

	/**
	 * 删除符号表和临时符号表
	 */
	public void deleteTable() {
		if (symbolList != null) {
			symbolList.clear();
			symbolList = null;
		}
		if (tempNames != null) {
			tempNames.clear();
			tempNames = null;
		}
	}

	/**
	 * 将符号登记到符号表中
	 * 
	 * @param symbol
	 * @throws InterpretException
	 */
	public void register(Symbol symbol, int lineNo) throws InterpretException {
		int i = 0;
		while(i < symbolList.size()){
			if (symbolList.get(i).getName() == null) {
					continue;
					//throw new InterpretException("There occurs an error in lexcial analysis.");
			} 
				if (symbolList.get(i).getName().equals(symbol.getName())) {
					if (symbolList.get(i).getLevel() < symbol.getLevel()) {
						// 将新的高层次的symbol放到原来symbol的位置，原来的symbol串到后边
						symbol.setNext(symbolList.get(i));
						symbolList.set(i, symbol);
						return;
					} else {
						throw new InterpretException("line " + lineNo +"  变量 <" + symbol.getName() + "> 重复声明");
					}
				}
				i++;
			}
			symbolList.add(symbol);
			
		}
	

	/**
	 * 将某一层次的变量全部销毁
	 * 
	 * @param level
	 */
	public void deregister(int level) {
		for (int i = 0; i < symbolList.size(); i++) {
			if (symbolList.get(i).getLevel() == level) {
				symbolList.set(i, symbolList.get(i).getNext());
			}
		}
		for (int i = symbolList.size() - 1; i >= 0; i--) {
			if (symbolList.get(i) == null) {
				symbolList.remove(i);
			}
		}
	}

	/**
	 * 给符号表中的符号赋值--单值
	 * 
	 * @param name
	 * @param value
	 * @throws InterpretException
	 */
	public void setSymbolValue(String name, Value value) throws InterpretException {
		getSymbol(name).setValue(value);
	}

	/**
	 * 给符号表中的符号赋值--整型数组
	 * 
	 * @param name
	 * @param value
	 * @param index
	 * @throws InterpretException
	 */
	public void setSymbolValue(String name, int value, int index) throws InterpretException {
		if (getSymbol(name).getValue().getArrayInt().length > index) {
			getSymbol(name).getValue().getArrayInt()[index] = value;
		} else {
			throw new InterpretException("数组 <" + name + "> 下标 " + index + " 越界");
		}

	}

	/**
	 * 给符号表中的符号赋值--浮点数数组
	 * 
	 * @param name
	 * @param value
	 * @param index
	 * @throws InterpretException
	 */
	public void setSymbolValue(String name, double value, int index) throws InterpretException {
		getSymbol(name).getValue().getArrayDouble()[index] = value;
	}

	/**
	 * 返回Symbol中的类型
	 * 
	 * @param name
	 * @param currentLevel
	 * @return
	 * @throws InterpretException
	 */
	public SymbolType getSymbolType(String name) throws InterpretException {
		return getSymbol(name).getType();
	}

	/**
	 * 取单值用这个函数
	 * 
	 * @param name
	 * @param currentLevel
	 * @return
	 */
	public Value getSymbolValue(String name) throws InterpretException {
		return getSymbolValue(name, -1);
	}

	/**
	 * 取值用这个函数
	 * 
	 * @param name
	 * @param index
	 *            -1时表示单值,否则表示索引值
	 * @param currentLevel
	 * @return
	 * @throws InterpretException
	 */
	public Value getSymbolValue(String name, int index) throws InterpretException {
		Symbol s = getSymbol(name);
		if (index == -1) {// 单值
			return s.getValue();
		} else {
			if (s.getType() == SymbolType.ARRAY_INT && s.getValue().getArrayInt().length < index + 1) {
				throw new InterpretException("数组 <" + name + "> 下标 " + index + " 越界");
			} else if (s.getType() == SymbolType.ARRAY_DOUBLE && s.getValue().getArrayDouble().length < index + 1) {
				throw new InterpretException("数组 <" + name + "> 下标 " + index + " 越界");
			}
			if (s.getType() == SymbolType.ARRAY_INT) {
				Value rv = new Value(SymbolType.SINGLE_INT);
				rv.setInt(s.getValue().getArrayInt()[index]);
				return rv;
			} else {
				Value rv = new Value(SymbolType.SINGLE_DOUBLE);
				rv.setDouble(s.getValue().getArrayDouble()[index]);
				return rv;
			}
		}
	}

	private Symbol getSymbol(String name) throws InterpretException {
		if (tempNames.isEmpty())

			for (Symbol s : symbolList) {
				if (s.getName().equals(name)) {
					return s;
				}
			}
		for (Symbol s : tempNames) {
			if (s.getName().equals(name)) {
				return s;
			}
		}
		if (name.startsWith(TEMP_PREFIX)) {
			Symbol s = new Symbol(name, SymbolType.TEMP, -1);
			tempNames.add(s);
			return s;
		}
		throw new InterpretException("变量 <" + name + "> 不存在");
	}

	/**
	 * 获取一个没有使用的临时符号名
	 * 
	 * @return
	 */
	public Symbol getTempSymbol() {
		String temp = null;
		for (int i = 1;; i++) {
			temp = TEMP_PREFIX + i;
			boolean exist = false;
			for (Symbol s : tempNames) {
				if (s.getName().equals(temp)) {
					exist = true;
					break;
				}
			}
			for (Symbol s : symbolList) {
				if (s.getName().equals(temp)) {
					exist = true;
					break;
				}
			}
			if (exist) {
				continue;
			}
			Symbol s = new Symbol(temp, SymbolType.TEMP, -1);
			tempNames.add(s);
			return s;
		}
	}

	/**
	 * 清空临时符号名
	 */
	public void clearTempNames() {
		tempNames.clear();
	}
}
