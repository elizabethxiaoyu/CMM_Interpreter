package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

import org.eclipse.swt.widgets.Text;

import cmm.Lexer;
import cmm.SyntaxParser;
import exception.LexerException;
import exception.ParserException;
import model.Token;
import model.TreeNode;
import model.TreeNodeType;
import ui.Display_Tree;

public class Util {
	public static void println(String arg0) {
		System.out.println(arg0);
	}

	public static void println(char arg0) {
		System.out.println(arg0);
	}

	public static void println(Object arg0) {
		System.out.println(arg0);
	}

private static void printException(Text text, Exception e){
	text.append(e.getMessage());
	text.append("\n");
}
	/**
	 * 打印结点，带有缩进
	 * 
	 * @param text
	 *            文本面板
	 * @param node
	 *            需要打印的结点
	 * @param indent
	 *            缩进
	 */
	private static void printNodeWithIntent(Text text, TreeNode node, int indent) {
		//if (node.getType() != TreeNodeType.NULL) {
			int t = indent;
			while (t > 0) {
				text.append("    ");
				t--;
			}
			text.append(node.toString());
			text.append(System.getProperty("line.separator"));
			switch (node.getType()) {
			case IF_STMT:
				if (node.getMiddle() != null) {
					t = indent;
					while (t > 0) {
						text.append("    ");
						t--;
					}
					text.append("  THEN:");
					text.append(System.getProperty("line.separator"));
					printNodeWithIntent(text, node.getMiddle(), indent + 1);
				}
				if (node.getRight() != null) {
					t = indent;
					while (t > 0) {
						text.append("    ");
						t--;
					}
					text.append("  ELSE:");
					text.append(System.getProperty("line.separator"));
					printNodeWithIntent(text, node.getRight(), indent + 1);
				}
				break;
			case WHILE_STMT:
				if (node.getMiddle() != null) {
					printNodeWithIntent(text, node.getMiddle(), indent + 1);
				}
				break;
			default:
				break;
			}
			if (node.getNext() != null) {
				printNodeWithIntent(text, node.getNext(), indent);
			}
		}
		
	
	/**
	 * 打开一个文件输入流，然后读取文件，然后将其进行词法分析，返回词法单元链表
	 * @param filestr
	 * @return
	 * @throws IOException
	 * @throws LexerException 
	 */
	public static LinkedList<Token> getTokenList(String filestr) throws IOException, LexerException {
		FileReader fr;
		fr = new FileReader(filestr);
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filestr), "UTF-8"));
		LinkedList<Token> tokenList = Lexer.lexicalAnalyse(br);
		br.close();
		fr.close();
		return tokenList;
	}
	/**
	 * 语法分析后将词法单元链表生成抽象语法树链表
	 * @param tokenList
	 * @return
	 * @throws ParserException
	 */
	public static LinkedList<TreeNode> getNodeList(LinkedList<Token> tokenList) throws ParserException {
		LinkedList<TreeNode> nodeList = SyntaxParser.syntaxAnalyse(tokenList);   
		return nodeList;
	}
}