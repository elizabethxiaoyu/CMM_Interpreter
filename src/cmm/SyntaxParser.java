package cmm;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.ListIterator;

import exception.LexerException;
import exception.ParserException;
import model.Token;
import model.TokenType;
import model.TreeNode;
import model.TreeNodeType;


public class SyntaxParser {
	private static LinkedList<TreeNode> treeNodeList;
	private static ListIterator<Token> iterator = null;
	private static Token currentToken = null;
	
	//获得词法生成器生成的token链表
	public static LinkedList<Token> getTokenList(String filestr) throws IOException, LexerException {
		FileReader fr;
		fr = new FileReader(filestr);
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filestr), "UTF-8"));
		LinkedList<Token> tokenList = Lexer.lexicalAnalyse(br);
		br.close();
		fr.close();
		return tokenList;
	}
	// 递归下降子程序法
	public static LinkedList<TreeNode> syntaxAnalyse(LinkedList<Token> tokenList) throws ParserException {
		treeNodeList = new LinkedList<TreeNode>();
		iterator = (ListIterator<Token>) tokenList.iterator();
		while (iterator.hasNext()) {
			treeNodeList.add(parseStmt());
		}
		return treeNodeList;
	}

	private static TreeNode parseStmt() throws ParserException {
		switch (getNextTokenType()) { 
		case SINGLE_COMMENT:
			consumeNextToken(TokenType.SINGLE_COMMENT);
			break;
		case LCOMMENT:
			consumeNextToken(TokenType.LCOMMENT);
			consumeNextToken(TokenType.RCOMMENT);
			break;
		case REFRENCE:
			consumeNextToken(TokenType.REFRENCE);
			return parseString();
		case INT:
		case DOUBLE:
		case STRING:
				return parseVarDecl();
		case IF:
			return parseIfStmt();
		case WHILE:
			return parseWhileStmt();
		case BREAK:
			return parseBreakStmt();
		case ID:
			return parseAssignStmt();
		case READ:
			return parseReadStmt();
		case WRITE:
			return parseWriteStmt();
		case PRINT:
			return parsePrintStmt();
		case FOR:
			return parseForStmt();
		case LBRACE:
			return parseBlockStmt();
		default:
//			try {
//				throw new ParserException("line: " + getNextTokenLineNo() + "   unexpected token-> " + currentToken);
//			} catch (ParserException e) {
//				e.printStackTrace();
//			}
			currentToken = iterator.next();

		}
		return null;

	}
	/**
	 * 解析字面常量值（字符串）
	 * @return
	 */
	private static TreeNode parseString() {
		consumeNextToken(TokenType.LITERAL_STRING);
		TreeNode treeNode = new TreeNode(TreeNodeType.LITERAL_STRING);
		treeNode.setValue(currentToken.getValue());
		treeNode.setLineNo(currentToken.getLineNo());
		
		consumeNextToken(TokenType.REFRENCE);
		return  treeNode;
	}
	/**
	 * TODO 完成for循环
	 * @return
	 */
	private static TreeNode parseForStmt() throws ParserException{
		consumeNextToken(TokenType.FOR);
		TreeNode treeNode =  new TreeNode(TreeNodeType.FOR_STMT, "for", currentToken.getLineNo());
		consumeNextToken(TokenType.LPARENT);
		treeNode.setLeft(parseForJudgeList());
		consumeNextToken(TokenType.RPARENT);
		consumeNextToken(TokenType.LBRACE);
		treeNode.setMiddle(parseStmtList());
		consumeNextToken(TokenType.RBRACE);
		return treeNode;
	}
	
	private static TreeNode parseForJudgeList() throws ParserException{
		TreeNode treeNode = new TreeNode(TreeNodeType.FOR_JUDGE_LIST, "for_judge_list", currentToken.getLineNo());
		if (getNextTokenType() == TokenType.ID) {
			treeNode.setLeft(parseAssignStmt());
		} else {
			treeNode.setLeft(parseVarDecl());
		}
		treeNode.setMiddle(parseExpr());
		consumeNextToken(TokenType.SEMI);
		treeNode.setRight(parseStmt());
		return treeNode;
	}
	
	private static TreeNode parseBreakStmt() throws ParserException {
		consumeNextToken(TokenType.BREAK);
		TreeNode treeNode = new TreeNode(TreeNodeType.BREAK_STMT);
		treeNode.setLineNo(currentToken.getLineNo());
		treeNode.setLeft(new TreeNode(TreeNodeType.BREAK, "break",currentToken.getLineNo()));
		consumeNextToken(TokenType.SEMI);
		treeNode.setMiddle(new TreeNode(TreeNodeType.SEMI, ";",currentToken.getLineNo())); 
		return treeNode;
	}

	private static TreeNode parseBlockStmt() throws ParserException {
		TreeNode treeNode = new TreeNode(TreeNodeType.BLOCK_STMT);
		consumeNextToken(TokenType.LBRACE);
		treeNode.setLeft(new TreeNode(TreeNodeType.LBRACE, "{",currentToken.getLineNo()));
		treeNode.setMiddle(parseStmtList());
		consumeNextToken(TokenType.RBRACE);
		treeNode.setRight(new TreeNode(TreeNodeType.RBRACE, "}",currentToken.getLineNo()));
		treeNode.setLineNo(currentToken.getLineNo());
		return treeNode;
	}


	private static TreeNode parseStmtList() throws ParserException {
		TreeNode treeNode = new TreeNode(TreeNodeType.STMT_LIST);
		TreeNode head = treeNode;
		while (getNextTokenType() != TokenType.RBRACE) {
			head.setLeft(parseStmt());
			TreeNode node = new TreeNode(TreeNodeType.NULL);
			head.setMiddle(node);
			head = head.getMiddle();
		}
		
		return treeNode;
	}
	

	// 分号没有加到树中去，但是进行了语法检查
	private static TreeNode parseWriteStmt() throws ParserException {
		//System.out.println(currentToken);
		TreeNode treeNode = new TreeNode(TreeNodeType.WRITE_STMT);
		consumeNextToken(TokenType.WRITE);
		treeNode.setValue("write");
		treeNode.setLineNo(currentToken.getLineNo());
		consumeNextToken(TokenType.LPARENT);
		treeNode.setLeft(new TreeNode(TreeNodeType.LPARENT, "(",currentToken.getLineNo()));
		if(getNextTokenType() == TokenType.REFRENCE){
			consumeNextToken(TokenType.REFRENCE);
			treeNode.setMiddle(parseString());
		}
		else
			treeNode.setMiddle(parseExpr());
		consumeNextToken(TokenType.RPARENT);
		treeNode.setRight(new TreeNode(TreeNodeType.RPARENT, ")",currentToken.getLineNo()));
		consumeNextToken(TokenType.SEMI);
		return treeNode;
	}
	
	// 分号没有加到树中去，但是进行了语法检查
		private static TreeNode parsePrintStmt() throws ParserException {
			//System.out.println(currentToken);
			TreeNode treeNode = new TreeNode(TreeNodeType.PRINT_STMT);
			consumeNextToken(TokenType.PRINT);
			treeNode.setValue("print");
			treeNode.setLineNo(currentToken.getLineNo());
			consumeNextToken(TokenType.LPARENT);
			treeNode.setLeft(new TreeNode(TreeNodeType.LPARENT, "(",currentToken.getLineNo()));
			if(getNextTokenType() == TokenType.REFRENCE){
				consumeNextToken(TokenType.REFRENCE);
				treeNode.setMiddle(parseString());
			}
			else
				treeNode.setMiddle(parseExpr());
			consumeNextToken(TokenType.RPARENT);
			treeNode.setRight(new TreeNode(TreeNodeType.RPARENT, ")",currentToken.getLineNo()));
			consumeNextToken(TokenType.SEMI);
			return treeNode;
		}
	/**
	 * 语法分析变量
	 * @return
	 * @throws ParserException
	 */
	private static TreeNode parseVariable() throws ParserException {
		consumeNextToken(TokenType.ID);
		TreeNode treeNode = new TreeNode(TreeNodeType.VAR);
		if (getNextTokenType() == TokenType.LBRACKET) { //数组类型
			TreeNode node = new TreeNode(TreeNodeType.ARRAY, currentToken.getValue(),currentToken.getLineNo());
			treeNode.setLeft(node);
			consumeNextToken(TokenType.LBRACKET);
			node.setLeft(new TreeNode(TreeNodeType.LBRACKET, "[",currentToken.getLineNo()));
			node.setMiddle( parseAddtiveExp());
			consumeNextToken(TokenType.RBRACKET);
			node.setRight(new TreeNode(TreeNodeType.RBRACKET, "]",currentToken.getLineNo()));
		} else {
			TreeNode node = new TreeNode(TreeNodeType.ID, currentToken.getValue(),currentToken.getLineNo());
			treeNode.setLeft(node);
			treeNode.setString(node.getString());
		}
		treeNode.setLineNo(treeNode.getLeft().getLineNo());
		return treeNode;
	}

	// read 没有给；加结点
	private static TreeNode parseReadStmt() throws ParserException {
		TreeNode treeNode = new TreeNode(TreeNodeType.READ_STMT, "read",currentToken.getLineNo());
		consumeNextToken(TokenType.READ);
		consumeNextToken(TokenType.LPARENT);
		treeNode.setLeft(new TreeNode(TreeNodeType.LPARENT, "(",currentToken.getLineNo()));
		treeNode.setMiddle(parseVariable());
		consumeNextToken(TokenType.RPARENT);
		treeNode.setRight(new TreeNode(TreeNodeType.RPARENT, ")",currentToken.getLineNo()));
		consumeNextToken(TokenType.SEMI);
		return treeNode;

	}

	// 赋值没有给；加结点
	private static TreeNode parseAssignStmt() throws ParserException {
		TreeNode treeNode = new TreeNode(TreeNodeType.ASSIGN_STMT);
		treeNode.setLeft(parseVariable());
		consumeNextToken(TokenType.ASSIGN);
		treeNode.setMiddle(new TreeNode(TreeNodeType.ASSIGN, "=",currentToken.getLineNo()));	
		treeNode.setRight(parseExpr());
		if(getNextTokenType() != TokenType.RPARENT)
			consumeNextToken(TokenType.SEMI);
		return treeNode;
	}

	// while 没有给括号和分号加结点
	private static TreeNode parseWhileStmt() throws ParserException {
		consumeNextToken(TokenType.WHILE);
		TreeNode treeNode = new TreeNode(TreeNodeType.WHILE_STMT, "while",currentToken.getLineNo());
		consumeNextToken(TokenType.LPARENT);
		treeNode.setLeft(parseExpr());
		consumeNextToken(TokenType.RPARENT);
		consumeNextToken(TokenType.LBRACE);
		treeNode.setMiddle(parseStmtList());
		consumeNextToken(TokenType.RBRACE);
		return treeNode;
	}

	// if 没有给括号和分号加结点
	private static TreeNode parseIfStmt() throws ParserException {
		consumeNextToken(TokenType.IF);
		TreeNode treeNode = new TreeNode(TreeNodeType.IF_STMT, "if",currentToken.getLineNo());
		consumeNextToken(TokenType.LPARENT);
		treeNode.setLeft(parseExpr());
		consumeNextToken(TokenType.RPARENT);
		consumeNextToken(TokenType.LBRACE);
		treeNode.setMiddle(parseStmtList());
		consumeNextToken(TokenType.RBRACE);
		if (getNextTokenType() == TokenType.ELSE) {
			consumeNextToken(TokenType.ELSE);
			treeNode.setRight(parseElseStmt());
		}
		return treeNode;
	}

	// else 没有给括号加结点
	private static TreeNode parseElseStmt() throws ParserException {
		TreeNode treeNode = new TreeNode(TreeNodeType.ELSE_STMT);
		consumeNextToken(TokenType.LBRACE);
		treeNode.setLeft(parseStmtList());
		treeNode.setLineNo(treeNode.getLeft().getLineNo());
		consumeNextToken(TokenType.RBRACE);
		return treeNode;
	}

	private static TreeNode parseExpr() throws ParserException {
		TreeNode node = new TreeNode(TreeNodeType.EXP);
		node.setDataType(TokenType.LOGIC_EXP); // 表达式结点的数据类型为boolean
		TreeNode leftNode = parseAddtiveExp();
		if (checkNextTokenType(TokenType.EQ, TokenType.NEQ, TokenType.GT, TokenType.GET, TokenType.LT, TokenType.LET)) {
			node.setLeft(leftNode);
			node.setMiddle(logicalOp());
			node.setRight(parseAddtiveExp());
		} else{
			node.setLeft(leftNode);
		}
		node.setLineNo(node.getLeft().getLineNo());
		node.setString(node.getLeft().getString());
		return node;
	}

	/**
	 * 多项式
	 * 
	 * @throws ParserException
	 */
	private static TreeNode parseAddtiveExp() throws ParserException {
		TreeNode node = new TreeNode(TreeNodeType.ADDTIVE_EXP);
		node.setDataType(TokenType.ADDTIVE_EXP);
		TreeNode leftNode = term();
		node.setString(leftNode.getString());
		if (checkNextTokenType(TokenType.PLUS)) {
			node.setLeft(leftNode);
			node.setMiddle(addtiveOp());
			node.setRight(parseAddtiveExp());
			//consumeNextToken(TokenType.PLUS);
		} else if (checkNextTokenType(TokenType.MINUS)) {
			node.setLeft(leftNode);
			TreeNode opnode = new TreeNode(TreeNodeType.OP);
			opnode.setDataType(TokenType.MINUS);
			node.setMiddle(opnode);
			node.setRight(parseAddtiveExp());
			//consumeNextToken(TokenType.MINUS);
		} else{
			node.setLeft(leftNode);
		}
		node.setLineNo(node.getLeft().getLineNo());
		return node;
	}

	/**
	 * 项
	 * 
	 * @throws ParserException
	 */
	private static TreeNode term() throws ParserException {
		TreeNode node = new TreeNode(TreeNodeType.TERM_EXP);
		node.setDataType(TokenType.TERM_EXP);
		TreeNode leftNode = factor();
		node.setString(leftNode.getString());
		if (checkNextTokenType(TokenType.MUL, TokenType.DIV)) {
			node.setLeft(leftNode);
			node.setMiddle(multiplyOp());
			node.setRight(term());
		} else{
			node.setLeft(leftNode);
		}
		node.setLineNo(node.getLeft().getLineNo());
		return node;
	}

	/**
	 * 因子
	 * 
	 * @throws ParserException
	 */
	private static TreeNode factor() throws ParserException  {
		if (iterator.hasNext()) {
			TreeNode expNode = new TreeNode(TreeNodeType.FACTOR);
			switch (getNextTokenType()) {
			case LITERAL_INT:
			case LITERAL_DOUBLE:
				expNode.setLeft(parseConstant());
				expNode.setLineNo(expNode.getLeft().getLineNo());
				expNode.setDataType(expNode.getLeft().getDataType());
				break;
			case REFRENCE:
				consumeNextToken(TokenType.REFRENCE);
				expNode.setLeft(parseString());
				expNode.setLineNo(expNode.getLeft().getLineNo());
				break;
			case LPARENT:
				consumeNextToken(TokenType.LPARENT);
				expNode = parseExpr();
				consumeNextToken(TokenType.RPARENT);
				break;
			case MINUS:
			
				expNode.setDataType(TokenType.MINUS);
				currentToken = iterator.next();
				expNode.setLeft(term());
				expNode.setLineNo(expNode.getLeft().getLineNo());
				break;
			case PLUS:
				currentToken = iterator.next();
				expNode.setLeft(term());
				expNode.setLineNo(expNode.getLeft().getLineNo());
				break;
			default:
				// 返回的不是expNode
				return parseVariable();
			}
			return expNode;
		}
		try {
			throw new ParserException("line " + getNextTokenLineNo() + " : next token should be factor");
		} catch (ParserException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 逻辑运算符
	 * 
	 * @throws ParserException
	 */
	private static TreeNode logicalOp()  {
		if (iterator.hasNext()) {
			currentToken = iterator.next();
			TokenType type = currentToken.getType();
			if (type == TokenType.EQ || type == TokenType.GET || type == TokenType.GT || type == TokenType.LET
					|| type == TokenType.LT || type == TokenType.NEQ) {
				TreeNode node = new TreeNode(TreeNodeType.OP, currentToken.getValue(),currentToken.getLineNo());
				node.setDataType(type);
				return node;
			}
		}
		try {
			throw new ParserException("line " + getNextTokenLineNo() + " : next token should be logical operator");
		} catch (ParserException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 加减运算符
	 * 
	 * @throws ParserException
	 */
	private static TreeNode addtiveOp()   {
		if (iterator.hasNext()) {
			currentToken = iterator.next();
			TokenType type = currentToken.getType();
			if (type == TokenType.PLUS || type == TokenType.MINUS) {
				TreeNode node = new TreeNode(TreeNodeType.OP, currentToken.getValue(),currentToken.getLineNo());
				node.setDataType(type);
				node.setLineNo(currentToken.getLineNo());
				return node;
			}
			return null;
		}
		
			try {
				throw new ParserException("line " + getNextTokenLineNo() + " : next token should be addtive operator");
			} catch (ParserException e) {
				e.printStackTrace();
			}
			return null;
	}

	/**
	 * 乘除运算符
	 * 
	 * @throws ParserException
	 */
	private static TreeNode multiplyOp()  {
		if (iterator.hasNext()) {
			currentToken = iterator.next();
			TokenType type = currentToken.getType();
			if (type == TokenType.MUL || type == TokenType.DIV) {
				TreeNode node = new TreeNode(TreeNodeType.OP, currentToken.getValue(),currentToken.getLineNo()); // 新建一个操作符结点
				node.setDataType(type); // 设置到底是乘还是除
				node.setLineNo(currentToken.getLineNo());
				return node;
			}
		}
		
			try {
				throw new ParserException("line " + getNextTokenLineNo() + " : next token should be multiple operator");
			} catch (ParserException e) {
				e.printStackTrace();
			}
			return null;
	}

	private static TreeNode parseConstant() throws ParserException  {
		TreeNode treeNode = new TreeNode();
		TokenType next = getNextTokenType();
		switch (next) {
		case LITERAL_INT:
			consumeNextToken(TokenType.LITERAL_INT);
			treeNode.setType(TreeNodeType.LITERAL_INT);
			treeNode.setDataType(TokenType.LITERAL_INT);
			treeNode.setValue(currentToken.getValue());
			break;
		case LITERAL_DOUBLE:
			consumeNextToken(TokenType.LITERAL_DOUBLE);
			treeNode.setType(TreeNodeType.LITERAL_DOUBLE);
			treeNode.setDataType(TokenType.LITERAL_DOUBLE);
			treeNode.setValue(currentToken.getValue());
			break;
		case TRUE:
			consumeNextToken(TokenType.TRUE);
			treeNode.setType(TreeNodeType.TRUE);
			treeNode.setValue(currentToken.getValue());
			break;
		case FALSE:
			consumeNextToken(TokenType.FALSE);
			treeNode.setType(TreeNodeType.FALSE);
			treeNode.setValue(currentToken.getValue());
			break;
		default:
			try {
				throw new ParserException("line " + getNextTokenLineNo() + " : next token should be  a constant ");
			} catch (ParserException e) {	
				e.printStackTrace();
			}
		}
		treeNode.setLineNo(currentToken.getLineNo());
		return treeNode;
	}

	private static TreeNode parseVarDecl() throws ParserException {
		TreeNode treeNode = new TreeNode(TreeNodeType.DECLARE_STMT);
		treeNode.setLeft(parseType());
		treeNode.setMiddle(parseValList());
		treeNode.setLineNo(currentToken.getLineNo());
		consumeNextToken(TokenType.SEMI);
		return treeNode;
	}

	private static TreeNode parseIntialStmt() throws ParserException {
		TreeNode treeNode = new TreeNode(TreeNodeType.INTIA);
		consumeNextToken(TokenType.ASSIGN);
		treeNode.setLeft(new TreeNode(TreeNodeType.ASSIGN,"=",currentToken.getLineNo()));
		if(getNextTokenType() == TokenType.LITERAL_STRING)
			treeNode.setMiddle(parseString());
		else
			treeNode.setMiddle(parseExpr());
		treeNode.setLineNo(currentToken.getLineNo());
		return treeNode;
	}

	private static TreeNode parseType() throws ParserException {
		TreeNode treeNode = new TreeNode(TreeNodeType.TYPE);
		if (getNextTokenType() == TokenType.INT) {
			consumeNextToken(TokenType.INT);
			treeNode.setLeft(new TreeNode(TreeNodeType.INT, "int",currentToken.getLineNo()));

		} else if (getNextTokenType() == TokenType.DOUBLE) {
			consumeNextToken(TokenType.DOUBLE);
			treeNode.setLeft(new TreeNode(TreeNodeType.DOUBLE, "double",currentToken.getLineNo()));
		}else if(getNextTokenType() == TokenType.STRING){
			consumeNextToken(TokenType.STRING);
			treeNode.setLeft(new TreeNode(TreeNodeType.STRING, "string",currentToken.getLineNo()));
		}

		if (getNextTokenType() == TokenType.LBRACKET) {
			TreeNode node = new TreeNode(TreeNodeType.ARRAY);
			treeNode.setMiddle(node);

			consumeNextToken(TokenType.LBRACKET);
			node.setLeft(new TreeNode(TreeNodeType.LBRACKET, "[",currentToken.getLineNo()));

			consumeNextToken(TokenType.LITERAL_INT);
			node.setMiddle(new TreeNode(TreeNodeType.LITERAL_INT, currentToken.getValue(),currentToken.getLineNo()));

			consumeNextToken(TokenType.RBRACKET);
			node.setRight(new TreeNode(TreeNodeType.RBRACKET, "]",currentToken.getLineNo()));
		}
		treeNode.setLineNo(currentToken.getLineNo());
		return treeNode;

	}

	private static TreeNode parseValList() throws ParserException {
		TreeNode treeNode = new TreeNode(TreeNodeType.VAL_LIST);
		consumeNextToken(TokenType.ID);
		TreeNode node = new TreeNode(TreeNodeType.ID, currentToken.getValue(),currentToken.getLineNo());
		treeNode.setLeft(node);
		if (getNextTokenType() == TokenType.ASSIGN) {
			node.setLeft(parseIntialStmt());
		}
		
		TreeNode head = treeNode;
		while (getNextTokenType() == TokenType.COMMA) {
			consumeNextToken(TokenType.COMMA);
			head.setMiddle(new TreeNode(TreeNodeType.COMMA, ",",currentToken.getLineNo()));
			consumeNextToken(TokenType.ID);
			head.setRight(new TreeNode(TreeNodeType.ID, currentToken.getValue(),currentToken.getLineNo()));
			
			if (getNextTokenType() == TokenType.ASSIGN) {
				head.getRight().setLeft(parseIntialStmt());
			}
			
			head = head.getRight();
		}
		treeNode.setLineNo(currentToken.getLineNo());
		return treeNode;
	}

	/**
	 * 获取下一个token类型
	 * 
	 * @return
	 */
	private static TokenType getNextTokenType() {
		if (iterator.hasNext()) {
			TokenType type = iterator.next().getType();
			iterator.previous();
			return type;
		}
		return TokenType.NULL;
	}

	/**
	 * 检查下一个token类型
	 * 
	 * @param type
	 * @return
	 */
	private static boolean checkNextTokenType(TokenType... type) {
		if (iterator.hasNext()) {
			TokenType nextType = iterator.next().getType();
			iterator.previous();
			for (TokenType each : type) {
				if (nextType == each) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 获取目标token
	 * 
	 * @param type
	 * @throws ParserException
	 */
	private static void consumeNextToken(TokenType type)  {
		if (iterator.hasNext()) {
			currentToken = iterator.next();
			if (currentToken.getType() == type) {
				return;
			}
		}
		
			try {
				throw new ParserException("line " + getNextTokenLineNo() + " : The type of next token should be -> " +type);
			} catch (ParserException e) {
				e.printStackTrace();
			}
	}

	/**
	 * 获取下一个token的行号
	 * 
	 * @return
	 */
	private static int getNextTokenLineNo() {
		if (iterator.hasNext()) {
			int lineNo = iterator.next().getLineNo();
			iterator.previous();
			return lineNo;
		}
		return -1;
	}
	
	/**
	 * 前序遍历森林中的每一棵代表语句或代码块的树
	 * @param root
	 */
	public static void preOrderTraverse(TreeNode root) {
		if (root == null) 
			return ;
			System.out.println(root.toString());
			preOrderTraverse(root.getLeft());
			preOrderTraverse(root.getMiddle());
			preOrderTraverse(root.getRight());
	
	}
	

}
