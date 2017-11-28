package cmm;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import model.*;
import exception.*;

public class Main {
	/**
	 * 打开一个文件输入流，然后读取文件，然后将其进行词法分析，返回词法单元链表
	 * 
	 * @param filestr
	 * @return
	 * @throws IOException
	 * @throws InterpretException 
	 * @throws LexerException
	 */

	public static void main(String[] args) throws IOException, ParserException, InterpretException {

		try {
			LinkedList<Token> l = SyntaxParser.getTokenList(args[0]);
			LinkedList<TreeNode> tree = SyntaxParser.syntaxAnalyse(l);

//			System.out.println("前序遍历语法树结点为：");
//			Iterator<TreeNode> iterator = tree.iterator();
//			while (iterator.hasNext()) {
//				SyntaxParser.preOrderTraverse(iterator.next());
//			}
			Interpreter.interpreter(tree);
			
		} catch (LexerException e) {
			e.printStackTrace();
		} 

	}
}
