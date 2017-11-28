package model;

public class Token {
	private TokenType type;
	private String value;
	private int lineNo;

	public Token(TokenType mDataType) {
		super();
		this.type = mDataType;
	}

	public Token(TokenType type, int lineNo) {
		this(type, null, lineNo);
	}

	public Token(TokenType type, String value, int lineNo) {
		super();
		this.type = type;
		this.value = value;
		this.lineNo = lineNo;
	}

	public Token(int lineNo) {
		super();
		this.lineNo = lineNo;
	}

	public TokenType getType() {
		return type;
	}

	public void setType(TokenType type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getLineNo() {
		return lineNo;
	}

	public void setLineNo(int lineNo) {
		this.lineNo = lineNo;
	}

	/**
	 * 带着行号输出
	 * 
	 * @return
	 */
	public String toStringWithLine() {
		return "lineNo: " + this.lineNo + "    " + this.type + "     " + this.value;
	}

	@Override
	public String toString() {
		return "type:  " + this.type + "    value:   " + this.value;
	}
}
