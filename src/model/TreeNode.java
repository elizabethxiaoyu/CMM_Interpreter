package  model;

public class TreeNode {

	private TreeNodeType type;   //树结点的类型
	private TreeNode mLeft;   
	private TreeNode mMiddle;
	private TreeNode mRight;
	private boolean mBoolean;
	//表达式的符号 （+ - 默认是+）
	private TokenType mDataType;
	
	//存储字符串形式的值（变量名）
	private String value;
	private double mData;
	
	//TODO 代码块
	 // 如果是代码块中的代码,则mNext指向其后面的一条语句 普通的顶级代码都是存在linkedlist中,不需要使用这个参数
	private TreeNode mNext;
	
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
	public TreeNode(TreeNodeType type,String value) {
		super();
		this.type = type;
		this.value = value;
		switch (this.type) {
		case FACTOR:
		case LITREAL:
			this.mDataType = TokenType.PLUS;
			break;
		default:
			break;
		}
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

	@Override
	public String toString() {
		if(this.mDataType == TokenType.MINUS)
			return this.type + "    -"+this.value;
		else 
			return this.type + "    "+ this.value;
		
		
	}

}
