package ui;


import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JApplet;
import javax.swing.JFrame;

import cmm.SyntaxParser;
import exception.LexerException;
import exception.ParserException;
import model.Token;
import model.TreeNode;
import util.Util;
public class Display_Tree  extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * @throws LexerException 
	 * @throws IOException 
	 * @throws ParserException 
	 * 
	 */
	//	public String fileName
	public static String fileName  = "H:\\常用程序\\eclipse\\workspace\\CMM_SyntaxParser v1.1\\test\\test.cmm";
	public Display_Tree() throws IOException, LexerException, ParserException{
			LinkedList<Token> tokenList;
		
			tokenList = SyntaxParser.getTokenList(fileName);
			LinkedList<TreeNode> treeNodeList =  Util.getNodeList(tokenList);
			System.out.println(treeNodeList.size());
			TreeControl tc = new TreeControl(treeNodeList);
			this.setBounds(0, 0, 500, 500);
			add(tc);
		
		
	}
	

}
