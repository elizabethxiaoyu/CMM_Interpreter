package cmm;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;

import exception.LexerException;
import model.*;
/**
 * 词法分析器，从流中读取字符，分析，并产生一个含有词法单元的链表
 * 
 * @author 刘大美女
 *
 */
public class Lexer {
	private static BufferedReader mBufferedReader; // 带缓冲的输入流
	private static int currentInt; // 行读取判断标准
	private static char currentChar; // 当前字符
	private static int lineNo; // 行号

	public static LinkedList<Token> lexicalAnalyse(BufferedReader br) throws IOException, LexerException {
		lineNo = 1;
		mBufferedReader = br;
		LinkedList<Token> tokenList = new LinkedList<Token>();
		StringBuilder sb = new StringBuilder();
		readChar();
		while (currentInt != -1) {
			// 消耗空白字符
			if (currentChar == '\n' || currentChar == '\r' || currentChar == '\t' || currentChar == '\f'
					|| currentChar == ' ') {
				readChar();
				continue;
			}
			
			switch (currentChar) {
			case ',':
				tokenList.add(new Token(TokenType.COMMA, ",",lineNo));
				readChar();
				continue;
			case ';':
				tokenList.add(new Token(TokenType.SEMI, ";",lineNo));
				readChar();
				continue;
			case '+':
				tokenList.add(new Token(TokenType.PLUS, "+",lineNo));
				readChar();
				continue;
			case '-':
				tokenList.add(new Token(TokenType.MINUS, "-",lineNo));
				readChar();
				continue;
			case '*':
				tokenList.add(new Token(TokenType.MUL, "*",lineNo));
				readChar();
				continue;
			case '%':
				tokenList.add(new Token(TokenType.PERCENT, "%",lineNo));
				readChar();
				continue;
			case '(':
				tokenList.add(new Token(TokenType.LPARENT,"( ",lineNo));
				readChar();
				continue;
			case ')':
				tokenList.add(new Token(TokenType.RPARENT,")", lineNo));
				readChar();
				continue;
			case '[':
				tokenList.add(new Token(TokenType.LBRACKET, "[",lineNo));
				readChar();
				continue;
			case ']':
				tokenList.add(new Token(TokenType.RBRACKET, "]",lineNo));
				readChar();
				continue;
			case '{':
				tokenList.add(new Token(TokenType.LBRACE, "{",lineNo));
				readChar();
				continue;
			case '}':
				tokenList.add(new Token(TokenType.RBRACE,"}", lineNo));
				readChar();
				continue;
			case '"':
				tokenList.add(new Token(TokenType.REFRENCE,"\"",lineNo));
				readChar();
				StringBuilder sbb = new StringBuilder();
				while(currentChar != '"'){
					sbb.append(currentChar);
					readChar();
				}
				tokenList.add(new Token(TokenType.STRING, sbb.toString(),lineNo));
				tokenList.add(new Token(TokenType.REFRENCE,"\"",lineNo));
				readChar();
				continue;
			case '!':
				readChar();
				if(currentChar == '='){
					tokenList.add(new Token (TokenType.NEQ,"!=" , lineNo));
					readChar();
				}else{
					tokenList.add(new Token (TokenType.NOT,"!" , lineNo));
				}
				continue;
			case '/':
				readChar();
				if (currentChar == '*') {
					tokenList.add(new Token (TokenType.LCOMMENT,"/*",lineNo));
					readChar();
					while (true) {
						if (currentChar == '*') {
							readChar();
							if (currentChar == '/') {
								tokenList.add(new Token(TokenType.RCOMMENT,"*/",lineNo));
								readChar();
								break;
							}
						} else {
							if(currentInt != -1)
								readChar();
							else{
//								try {
//									throw new LexerException("Lack of \"*\\\"");
//								} catch (LexerException e) {
//									e.printStackTrace();
//								}
								break;
							}
								
						}
					}
					continue;
				} else if (currentChar == '/') {
					tokenList.add(new Token(TokenType.SINGLE_COMMENT,"//",lineNo));
					while (currentChar != '\n') {
						readChar();
					}
					continue;
				} else {
					// 除号
					tokenList.add(new Token(TokenType.DIV, "/",lineNo));
					continue;
				}

			case '=':
				readChar();
				if (currentChar == '=') {
					tokenList.add(new Token(TokenType.EQ, "==",lineNo));
					readChar();
				} else {
					tokenList.add(new Token(TokenType.ASSIGN, "=",lineNo));
				}
				continue;

			case '>':
				readChar();
				if (currentChar == '=') {
					tokenList.add(new Token(TokenType.GET,">=", lineNo));
					readChar();
				} else {
					tokenList.add(new Token(TokenType.GT,">", lineNo));
				}
				continue;

			case '<':
				readChar();
				if (currentChar == '=') {
					tokenList.add(new Token(TokenType.LET,"<=", lineNo));
					readChar();
				} else {
					tokenList.add(new Token(TokenType.LT, "<",lineNo));
				}
				continue;

			case '0':
				sb.append('0');
				readChar();
				// 十六进制
				if (currentChar == 'X' || currentChar == 'x') {
					sb.append(currentChar);
					readChar();
					while ((currentChar >= '0' && currentChar <= '9') || (currentChar >= 'A' || currentChar <= 'F')) {
						sb.append(currentChar);
						readChar();
					}
					tokenList.add(new Token(TokenType.LITERAL_HEXI, sb.toString(), lineNo));
					sb.delete(0, sb.length());
				} else if (!Character.isDigit(currentChar) && currentChar != '.') {
						tokenList.add(new Token (TokenType.LITERAL_INT, sb.toString(),lineNo));
						sb.delete(0, sb.length());
				}
				continue;
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			case '.':
				boolean isDouble = false;// 是否小数
				while ((currentChar >= '0' && currentChar <= '9') || currentChar == '.') {
					if (currentChar == '.') {
						if (isDouble) {
							break;
						} else {
							isDouble = true;
						}
					}
					sb.append(currentChar);
					readChar();
				}
				if(sb.toString().matches("^[-+]?\\d+(\\.\\d+)?$")){
				if (isDouble) {
					tokenList.add(new Token(TokenType.LITERAL_DOUBLE, sb.toString(), lineNo));
				} else {
					tokenList.add(new Token(TokenType.LITERAL_INT, sb.toString(), lineNo));
				}
				}else{
//					try {
//						throw new LexerException("LineNo:   "+ lineNo +"   Invalid number: " + sb.toString());
//					} catch (LexerException e) {
//						e.printStackTrace();
//					}
				}
				sb.delete(0, sb.length());
				continue;
			default:
//			if (!Character.isLetter(currentChar) && currentChar != '_')
//					try {
//						throw new LexerException("第" + lineNo + "行      " +"Invalid Character： " + currentChar);
//					} catch (LexerException e) {
//						e.printStackTrace();
//					}
			}
			
			
			// 字符组成的标识符,包括保留字和ID
			if ((currentChar >= 'a' && currentChar <= 'z') || (currentChar >= 'A' && currentChar <= 'Z')
					|| currentChar == '_') {

				while ((currentChar >= 'a' && currentChar <= 'z') || (currentChar >= 'A' && currentChar <= 'Z')
						|| currentChar == '_' || (currentChar >= '0' && currentChar <= '9')) {
					sb.append(currentChar);
					readChar();
				}
				Token token = new Token(lineNo);
				String sbString = sb.toString();
				if (sbString.equals("if")) {
					token.setType(TokenType.IF);
					token.setValue("if");
				} else if (sbString.equals("else")) {
					token.setType(TokenType.ELSE);
					token.setValue("else");
				} else if (sbString.equals("while")) {
					token.setType(TokenType.WHILE);
					token.setValue("while");
				} else if (sbString.equals("break")) {
					token.setType(TokenType.BREAK);
					token.setValue("break");
				} else if (sbString.equals("read")) {
					token.setType(TokenType.READ);
					token.setValue("read");
				} else if (sbString.equals("write")) {
					token.setType(TokenType.WRITE);
					token.setValue("write");
				} else if (sbString.equals("int")) {
					token.setType(TokenType.INT);
					token.setValue("int");
				} else if(sbString.equals("print")){
					token.setType(TokenType.PRINT);
					token.setValue("print");
				}else if(sbString.equals("for")){
					token.setType(TokenType.FOR);
					token.setValue("for");
				} else if (sbString.equals("double")) {
					token.setType(TokenType.DOUBLE);
					token.setValue("double");
				} else {
					if (sbString.matches("^\\w+$") && sbString.substring(0,1).matches("[A-Za-z]")) {
						token.setType(TokenType.ID);
						token.setValue(sbString);
					} else {
//							try{
//							throw new LexerException(
//									"第" + token.getLineNo() + "行     " + "Invalid Identifier" + ":  " +sbString);
//							}catch(LexerException e1){
//								e1.printStackTrace();
//							}
					}
				}
				sb.delete(0, sb.length());
				tokenList.add(token);
				continue;
			}
			readChar();
		}
		return tokenList;
	}

	/**
	 * 从输入流中读取下一个字符，并处理换行
	 * 
	 * @throws IOException
	 */
	private static void readChar() throws IOException {
		currentChar = (char) (currentInt = mBufferedReader.read());
		if (currentChar == '\n') {
			lineNo++;
		}
	}

}
