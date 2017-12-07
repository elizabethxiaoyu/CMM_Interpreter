package model;

public enum TreeNodeType {
	NULL,
	
	 // if语句
	 
	IF_STMT, 
	
	// left存储EXP middle存储循环体
	 
	WHILE_STMT,
	
	PRINT_STMT,
	
	// left存储一个VAR
	READ_STMT,
	
	//left存储一个EXP
	WRITE_STMT,
	
	// 声明语句 left中存放VAR节点 如果有赋值EXP,则存放中middle中
	DECLARE_STMT,
  
	//赋值语句 left存放var节点 middle存放exp节点
	ASSIGN_STMT,
	
	// 复合表达式
	EXP,
	
	//变量
	VAR,
	
	//运算符
	OP,
	
	//因子
	FACTOR,
	
	//字面值
	LITREAL,
	
	//break
	BREAK, 
	
	//blockStmt
	BLOCK_STMT,
	
	//left brace
	LBRACE,
	
	//right brace
	RBRACE,
	//;
	SEMI,
	//,
	COMMA,
	
	//int
	INT,
	
	//double
	DOUBLE,
	
	// variable list
	VAL_LIST,
	
	//statement list
	STMT_LIST,
	//type
	TYPE,
	//id
	ID,
	//array
	ARRAY,
	//[
	LBRACKET,
	//]
	RBRACKET,
	//int number
	LITERAL_INT,
	//double number
	LITERAL_DOUBLE, 
	
	BREAK_STMT, 
	//while
	WHILE,
	//if
	IF, 
	//read
	READ,
	//(
	LPARENT,
	//)
	RPARENT, 
	//write
	WRITE,
	//=
	ASSIGN,
	TRUE,  //true
	FALSE,//false
	 ELSE_STMT,  //else 语句
	//初始化语句
	 INTIA, 
	 //多项式
	 ADDTIVE_EXP, 
	 //-
	 MINUS , 
	//+
	 PLUS, 
	 //*
	 MUL,
	 // / 
	 DIV,
	 
	 TERM_EXP, 
	 FOR_STMT,
	


}
