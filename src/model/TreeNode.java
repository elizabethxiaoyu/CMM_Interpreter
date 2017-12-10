package  model;

public class TreeNode {

	private TreeNodeType type;   //树结点的类型
	private TreeNode mLeft;   
	private TreeNode mMiddle;
	private TreeNode mRight;
	private boolean mBoolean;
	//表达式的符号 （+ - 默认是+）
	private TokenType mDataType;
	private int lineNo;
	//存储字符串形式的值（变量名）
	private String value;
	private double mData;
	private String mString;
	 // 如果是代码块中的代码,则mNext指向其后面的一条语句 普通的顶级代码都是存在linkedlist中,不需要使用这个参数
	private TreeNode mNext;
	// 是否是中断节点
	private boolean isInterrupt = false;
	
	public TreeNode(){}
	public TreeNode(TreeNodeType type) {
		super();
		this.type = type;
		switch (this.type) {
		case FACTOR:
		case LITREAL:
			this.mDataType = TokenType.PLUS;
			
			break;
		default:
			break;
		}
	}
	public TreeNode(TreeNodeType type,String value,int lineNo) {
		super();
		this.type = type;
		this.value = value;
		this.lineNo = lineNo;
		switch (this.type) {
		case FACTOR:
		case LITREAL:
			this.mDataType = TokenType.PLUS;
			break;
		default:
			break;
		}
	}
	
	
	public String getString() {
		return mString;
	}
	public void setString(String mString) {
		this.mString = mString;
	}
	public boolean getBoolean() {
		return mBoolean;
	}
	public void setBoolean(boolean mBoolean) {
		this.mBoolean = mBoolean;
	}
	public double getData() {
		return mData;
	}
	public void setData(double mData) {
		this.mData = mData;
	}
	public TreeNodeType getType() {
		return type;
	}

	public void setType(TreeNodeType type) {
		this.type = type;
	}

	public TreeNode getLeft() {
		return mLeft;
	}

	public void setLeft(TreeNode mLeft) {
		this.mLeft = mLeft;
	}

	public TreeNode getMiddle() {
		return mMiddle;
	}

	public void setMiddle(TreeNode mMiddle) {
		this.mMiddle = mMiddle;
	}

	public TreeNode getRight() {
		return mRight;
	}

	public void setRight(TreeNode mRight) {
		this.mRight = mRight;
	}

	public TreeNode getNext() {
		return mNext;
	}

	public void setNext(TreeNode mNext) {
		this.mNext = mNext;
	}

	public TokenType getDataType() {
		return mDataType;
	}

	public void setDataType(TokenType type) {
		this.mDataType = type;
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
	@Override
	public String toString() {
		if(this.mDataType == TokenType.MINUS)
			return this.type + "    -"+this.value;
		else 
			return this.type + "    "+ this.value;
		
		
	}
	public boolean getInterrupt() {
		return isInterrupt;
	}
	public void setInterrupt(boolean isInterrupt) {
		this.isInterrupt = isInterrupt;
	}

}
