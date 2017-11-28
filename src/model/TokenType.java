package model;

public enum TokenType {
	ERROR,NULL,IF,ELSE,WHILE,READ,WRITE,BREAK,
	INT,DOUBLE,PLUS,MINUS,MUL,DIV, PERCENT,NOT,
	ASSIGN, //=
	LT,//  <
	EQ,//==
	NEQ,
	LPARENT,  // (
	RPARENT,  //
	COMMA,  // ,
	SEMI,      // ;
	LBRACE, // {
	RBRACE,//  }
	LBRACKET, // [
	RBRACKET, // ]
	LET,  // <=
	GT,   // >
	GET,  // >=
	ID,
	LITERAL_INT,
	LITERAL_DOUBLE,
	LOGIC_EXP,       // 逻辑表达式
	ADDTIVE_EXP,  //多项式
	TERM_EXP,         //项
	LITERAL_HEXI, //十六进制数
	SINGLE_COMMENT,   // 单行注释符 //
	LCOMMENT,					//多行注释符  /*
	RCOMMENT		,			//多行注释符 */
	TRUE,							//true
	FALSE							//false
	
}
