package model;

public class Symbol {
	private String name;
	private SymbolType type;
	private Value value;
	private int level;
	private Symbol  next;
	public Symbol(String name, SymbolType type, int level) {
		this.name = name;
		this.type = type;
		this.level = level;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public SymbolType getType() {
		return type;
	}
	public void setType(SymbolType type) {
		this.type = type;
	}
	public Value getValue() {
		return value;
	}
	public void setValue(Value value) {
		this.value = value;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public Symbol getNext() {
		return next;
	}
	public void setNext(Symbol next) {
		this.next = next;
	}
	@Override
	public String toString() {
		return "Symbol [name=" + name + ", type=" + type + ", value=" + value + ", level=" + level + ", next=" + next
				+ "]";
	}
	
	
}
