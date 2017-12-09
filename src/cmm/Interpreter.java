package cmm;

import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Pattern;

import exception.InterpretException;
import model.Symbol;
import model.SymbolType;
import model.TokenType;
import model.TreeNode;
import model.TreeNodeType;
import model.Value;

public class Interpreter {
	static int mLevel = 0;
	static SymbolTable symbolTable = SymbolTable.getSymbolTable();
	static java.util.regex.Pattern pattern = Pattern.compile("(-)+[0-9]*(\\.?)[0-9]*"); // 小数
	static java.util.regex.Pattern pattern2 = Pattern.compile("[-]?[0-9]*(\\.?)[0]"); // 整数的小数形式
	public static StringBuilder result = new StringBuilder();

	public static void interpreter(LinkedList<TreeNode> trees) throws InterpretException {

		for (TreeNode root : trees) {
			if (root != null)
				interpreterStmt(root);
		}
	}

	private static void interpreterStmt(TreeNode root) throws InterpretException {
		switch (root.getType()) {
		case IF_STMT:
			interpreterIF(root);
			break;
		case WHILE_STMT:
			interpreterWHILE(root);
			break;
		case PRINT_STMT:
			interpreterPRINT(root);
			break;
		case BREAK_STMT:
			break;
		case READ_STMT:
			interpreterREAD(root);
			break;
		case WRITE_STMT:
			interpreterWRITE(root);
			break;
		case DECLARE_STMT:
			interpreterDECLARE(root);
			break;
		case ASSIGN_STMT:
			interpreterASSIGN(root);
			break;
		case FOR_STMT:
			interpreterFOR(root);
			break;
		default:
			break;

		}

	}

	/**
	 * int a = 1; write(a); double b; b = 2; write(b); int c = 3.4; write(c);
	 * double[2] d; write(d[1]);
	 * 
	 * d[1] = 9; write(d[1]); int[3] e ; write(e[0]); e[3] = 8;
	 * 
	 *
	 * 
	 * @param root
	 * @throws InterpretException
	 */
	private static void interpreterASSIGN(TreeNode root) throws InterpretException {
		TreeNode node = root.getLeft();
		if (node.getLeft().getType() == TreeNodeType.ID) {
			SymbolType type = symbolTable.getSymbolType(node.getLeft().getValue());
			Value value = new Value();

			interpreterExpr(root.getRight());
			if (type == SymbolType.SINGLE_INT) {
				value.setType(SymbolType.SINGLE_INT);
				value.setInt((int) root.getRight().getData());
			} else if (type == SymbolType.SINGLE_DOUBLE) {
				value.setType(SymbolType.SINGLE_DOUBLE);
				value.setDouble(root.getRight().getData());
			} else if (type == SymbolType.SINGLE_STRING) {
				value.setType(SymbolType.SINGLE_STRING);
				value.setString(root.getRight().getString());
			}

			symbolTable.setSymbolValue(node.getLeft().getValue(), value);

		} else if (node.getLeft().getType() == TreeNodeType.ARRAY) {
			interpreterExpr(node.getLeft().getMiddle());
			interpreterExpr(root.getRight());
			SymbolType type = symbolTable.getSymbolType(node.getLeft().getValue());
			if (type == SymbolType.ARRAY_INT) {

				int length = symbolTable.getSymbolValue(node.getLeft().getValue()).getArrayInt().length;
				if ((int) (node.getLeft().getMiddle().getData()) < 0
						|| (int) (node.getLeft().getMiddle().getData()) >= length) {
					try {
						throw new InterpretException("line " + node.getLeft().getMiddle().getLineNo()
								+ "  The index of the array is illegal.");
					} catch (InterpretException e) {
						e.printStackTrace();
					}
				} else {
					symbolTable.setSymbolValue(node.getLeft().getValue(), (int) (root.getRight().getData()),
							(int) (node.getLeft().getMiddle().getData()));
				}

			} else if (type == SymbolType.ARRAY_DOUBLE) {
				int length = symbolTable.getSymbolValue(node.getLeft().getValue()).getArrayDouble().length;
				if ((int) (node.getLeft().getMiddle().getData()) < 0
						|| (int) (node.getLeft().getMiddle().getData()) >= length) {

					try {
						throw new InterpretException("line " + node.getLeft().getMiddle().getLineNo()
								+ "  The index of the array is illegal.");
					} catch (InterpretException e) {
						e.printStackTrace();
					}
				} else {
					symbolTable.setSymbolValue(node.getLeft().getValue(), root.getRight().getData(),
							(int) (node.getLeft().getMiddle().getData()));

				}
			}else if (type == SymbolType.ARRAY_STRING) {
				int length = symbolTable.getSymbolValue(node.getLeft().getValue()).getArrayString().length;
				if ((int) (node.getLeft().getMiddle().getData()) < 0
						|| (int) (node.getLeft().getMiddle().getData()) >= length) {
					try {
						throw new InterpretException("line " + node.getLeft().getMiddle().getLineNo()
								+ "  The index of the array is illegal.");
					} catch (InterpretException e) {
						e.printStackTrace();
					}
				} else {
					symbolTable.setSymbolValue(node.getLeft().getValue(), root.getRight().getString(),
							(int) (node.getLeft().getMiddle().getData()));

				}
			}
		}
	}

	private static void interpreterDECLARE(TreeNode root) throws InterpretException {
		if (root.getLeft().getLeft().getType() == TreeNodeType.INT) {
			if (root.getLeft().getMiddle() != null) {
				Symbol symbol = new Symbol(root.getMiddle().getLeft().getValue(), SymbolType.ARRAY_INT, mLevel);
				Value value = new Value();
				int array[] = new int[Integer.valueOf(root.getLeft().getMiddle().getMiddle().getValue())];
				value.setArrayInt(array);
				value.setType(SymbolType.ARRAY_INT);
				symbol.setValue(value);
				symbolTable.register(symbol, root.getMiddle().getLeft().getLineNo());
			} else {
				Symbol symbol = new Symbol(root.getMiddle().getLeft().getValue(), SymbolType.SINGLE_INT, mLevel);
				Value value = new Value();
				value.setType(SymbolType.SINGLE_INT);
				TreeNode node = root.getMiddle().getLeft().getLeft();
				if (node != null && node.getType() == TreeNodeType.INTIA) {
					interpreterExpr(node.getMiddle());
					value.setInt((int) node.getMiddle().getData());
				}
				// 默认初始化为0
				else {
					value.setInt(0);
				}
				symbol.setValue(value);
				symbolTable.register(symbol, root.getMiddle().getLeft().getLineNo());
			}
		} else if (root.getLeft().getLeft().getType() == TreeNodeType.DOUBLE) {
			if (root.getLeft().getMiddle() != null) {
				Symbol symbol = new Symbol(root.getMiddle().getLeft().getValue(), SymbolType.ARRAY_DOUBLE, mLevel);
				Value value = new Value();
				double array[] = new double[Integer.valueOf(root.getLeft().getMiddle().getMiddle().getValue())];
				value.setArrayDouble(array);
				value.setType(SymbolType.ARRAY_DOUBLE);
				symbol.setValue(value);
				symbolTable.register(symbol, root.getMiddle().getLeft().getLineNo());
			} else {
				Symbol symbol = new Symbol(root.getMiddle().getLeft().getValue(), SymbolType.SINGLE_DOUBLE, mLevel);
				Value value = new Value();
				value.setType(SymbolType.SINGLE_DOUBLE);
				TreeNode node = root.getMiddle().getLeft().getLeft();
				if (node != null && node.getType() == TreeNodeType.INTIA) {
					interpreterExpr(node.getMiddle());
					value.setDouble(node.getMiddle().getData());
				} else {
					// 默认初始化为0.0
					value.setDouble(0.0);
				}
				symbol.setValue(value);
				symbolTable.register(symbol, root.getMiddle().getLeft().getLineNo());

			}
		} else if (root.getLeft().getLeft().getType() == TreeNodeType.STRING) {
			if (root.getLeft().getMiddle() != null) {
				Symbol symbol = new Symbol(root.getMiddle().getLeft().getValue(), SymbolType.ARRAY_STRING, mLevel);
				Value value = new Value();
				String array[] = new String[Integer.valueOf(root.getLeft().getMiddle().getMiddle().getValue())];
				value.setArrayString(array);
				value.setType(SymbolType.ARRAY_STRING);
				symbol.setValue(value);
				symbolTable.register(symbol, root.getMiddle().getLeft().getLineNo());
			
			} else {
				Symbol symbol = new Symbol(root.getMiddle().getLeft().getValue(), SymbolType.SINGLE_STRING, mLevel);
				Value value = new Value();
				value.setType(SymbolType.SINGLE_STRING);
				TreeNode node = root.getMiddle().getLeft().getLeft();
				if (node != null && node.getType() == TreeNodeType.INTIA) {
					value.setString(node.getMiddle().getValue());
				}
				// 默认初始化为""
				else {
					value.setString("");
				}
				symbol.setValue(value);
				symbolTable.register(symbol, root.getMiddle().getLeft().getLineNo());
			}

		}
	}

	private static void interpreterWRITE(TreeNode root) throws InterpretException {
		if (root.getMiddle().getType() == TreeNodeType.LITERAL_STRING) {
			result.append(root.getMiddle().getValue() + "\n");
		} else if (interpreterExpr(root.getMiddle()).getString() == null) {
			String s = String.valueOf(interpreterExpr(root.getMiddle()).getData());
			if (pattern2.matcher(s).matches()
					&& interpreterExpr(root.getMiddle()).getDataType() == TokenType.LITERAL_INT) {
				System.out.println(((int) Double.parseDouble(s)));
				result.append(String.valueOf(((int) Double.parseDouble(s))) + "\n");
			} else {
				System.out.println(s);
				result.append(s + "\n");
			}
		} else {
			result.append(root.getMiddle().getString() + "\n");
		}
	}

	// 输出不换行
	private static void interpreterPRINT(TreeNode root) throws InterpretException {
		if (root.getMiddle().getType() == TreeNodeType.LITERAL_STRING) {
			result.append(root.getMiddle().getValue() + "\n");
		} else {
			String s = String.valueOf(interpreterExpr(root.getMiddle()).getData());
			if (pattern2.matcher(s).matches()
					&& interpreterExpr(root.getMiddle()).getDataType() == TokenType.LITERAL_INT) {
				System.out.print(((int) Double.parseDouble(s)));
				result.append(String.valueOf(((int) Double.parseDouble(s))) + "\n");
			} else {
				System.out.print(s);
				result.append(s + "\n");
			}
		}
	}

	/**
	 * int a; read(a); write(a);
	 * 
	 * double b = 2.0; read(b); write(b);
	 * 
	 * double[2] c; read(c[0]); write(c[0]);
	 * 
	 * int[2] d; read(d); write(d[0]);
	 * 
	 * @param root
	 * @throws InterpretException
	 */
	private static void interpreterREAD(TreeNode root) throws InterpretException {

		SymbolType type = symbolTable.getSymbolType(root.getMiddle().getLeft().getValue());
		Value value = new Value(type);
		// Scanner sc = new Scanner(System.in); //注释部分是采用控制台输入的方式
		String input = ui.Main.input.getText();

		switch (type) {
		case SINGLE_INT:
			try {
				// value.setInt(sc.nextInt());
				value.setInt(Integer.parseInt(input));
				symbolTable.setSymbolValue(root.getMiddle().getLeft().getValue(), value);
			} catch (InputMismatchException e) {
				try {
					throw new InterpretException("The input is not an integer.");
				} catch (InterpretException e1) {
					e1.printStackTrace();
				}
			}
			break;
		case SINGLE_DOUBLE:
			// value.setDouble(sc.nextDouble());
			value.setDouble(Double.parseDouble(input));
			symbolTable.setSymbolValue(root.getMiddle().getLeft().getValue(), value);
			break;
		case ARRAY_INT:
			if (root.getMiddle().getLeft().getLeft() == null) {// 赋值整个数组
				int[] array = symbolTable.getSymbolValue(root.getMiddle().getLeft().getValue()).getArrayInt();
				// for (int i = 0; i < array.length; i++)
				// array[i] = sc.nextInt();
				String[] result = input.split("\r\n");
				for (int i = 0; i < array.length; i++) {
					array[i] = Integer.parseInt(result[i]);
				}

			} else {// 赋值数组中某个元素
				interpreterExpr(root.getMiddle().getLeft().getMiddle());
				// symbolTable.setSymbolValue(root.getMiddle().getLeft().getValue(),
				// sc.nextInt(),
				// (int) root.getMiddle().getLeft().getMiddle().getData());
				symbolTable.setSymbolValue(root.getMiddle().getLeft().getValue(), Integer.parseInt(input),
						(int) root.getMiddle().getLeft().getMiddle().getData());
			}
			break;
		case ARRAY_DOUBLE:
			if (root.getMiddle().getLeft().getLeft() == null) {// 赋值整个数组
				double[] array2 = symbolTable.getSymbolValue(root.getMiddle().getLeft().getValue()).getArrayDouble();
				// for (int i = 0; i < array2.length; i++)
				// array2[i] = sc.nextDouble();
				String[] result = input.split("\r\n");
				for (int i = 0; i < array2.length; i++) {
					array2[i] = Double.parseDouble(result[i]);
				}

			} else {
				// 赋值数组中某个元素
				interpreterExpr(root.getMiddle().getLeft().getMiddle());
				// symbolTable.setSymbolValue(root.getMiddle().getLeft().getValue(),
				// sc.nextDouble(),
				// (int) root.getMiddle().getLeft().getMiddle().getData());
				symbolTable.setSymbolValue(root.getMiddle().getLeft().getValue(), Double.parseDouble(input),
						(int) root.getMiddle().getLeft().getMiddle().getData());
			}
			break;
		default:
			try {
				throw new InterpretException("The input occurs an error.");
			} catch (InterpretException e1) {
				e1.printStackTrace();
			}
		}

	}

	/**
	 * 解析WHILE语句 int b = 1; while(b <= 2){ b = b+1 ; write(b); } write(b);
	 * 
	 * @param root
	 * @throws InterpretException
	 */
	private static void interpreterWHILE(TreeNode root) throws InterpretException {
		while (interpreterExpr(root.getLeft()).getBoolean() == true) {
			mLevel++;
			interpreterStmtList(root.getMiddle());
			symbolTable.deregister(mLevel);
			mLevel--;
		}
	}
	
	/**
	 * 解析FOR语句int sum = 0; for(int i = 0; i < 5; i = i + 1){ sum = sum + i; } write(sum);
	 * 
	 * @param root
	 * @throws InterpretException
	 */
	private static void interpreterFOR(TreeNode root) throws InterpretException {
		interpreterStmt(root.getLeft().getLeft());
		while (interpreterExpr(root.getLeft().getMiddle()).getBoolean() == true) {
			mLevel++;
			interpreterStmtList(root.getMiddle());
			symbolTable.deregister(mLevel);
			mLevel--;
			interpreterStmt(root.getLeft().getRight());
		}
	}
	

	/**
	 * 解释语法树中IF结点 int a = 1; if(a < 2){ a = a+1; } write(a);
	 * 
	 * @param root
	 * @throws InterpretException
	 */
	private static void interpreterIF(TreeNode root) throws InterpretException {
		if (root.getValue().equals("if")) {
			boolean flag = interpreterExpr(root.getLeft()).getBoolean();

			if (flag == true) {
				mLevel++;
				interpreterStmtList(root.getMiddle());
				symbolTable.deregister(mLevel);
				mLevel--;
			} else {
				if (root.getRight() != null) {
					interpreterElse(root.getRight());
				}
			}
		}
	}

	/**
	 * 解释语法树中的ELSE结点 int a = 2; if(a < 2){ a = a+1; }else{a = a+2;} write(a);
	 * 
	 * @param right
	 * @throws InterpretException
	 */
	private static void interpreterElse(TreeNode right) throws InterpretException {
		mLevel++;
		interpreterStmtList(right.getLeft());
		symbolTable.deregister(mLevel);
		mLevel--;

	}

	private static void interpreterStmtList(TreeNode middle) throws InterpretException {
		TreeNode temp = middle;
		while (temp.getLeft() != null) {
			interpreterStmt(temp.getLeft());
			temp = temp.getMiddle();
		}

	}

	/**
	 * 解析表达式结点
	 * 
	 * @param root
	 * @return
	 * @throws InterpretException
	 */
	private static TreeNode interpreterExpr(TreeNode root) throws InterpretException {
		if (root.getLeft() == null) { // 此表达式是标识符或字面值
			if (root.getType() == TreeNodeType.ID) {
		
				if (symbolTable.getSymbolValue(root.getValue()).getType() == SymbolType.SINGLE_INT) {
					root.setData(symbolTable.getSymbolValue(root.getValue()).getInt());
					root.setDataType(TokenType.LITERAL_INT);
				} else if (symbolTable.getSymbolValue(root.getValue()).getType() == SymbolType.SINGLE_DOUBLE) {
					root.setData(symbolTable.getSymbolValue(root.getValue()).getDouble());
					root.setDataType(TokenType.LITERAL_DOUBLE);
				} else {
					root.setString(symbolTable.getSymbolValue(root.getValue()).getString());
				}
				root.setBoolean(!(root.getData() == 0));
			} else {
				if (root.getType() != TreeNodeType.LITERAL_STRING) {
					if (pattern.matcher(root.getValue()).matches())
						root.setDataType(TokenType.LITERAL_DOUBLE);
					else
						root.setDataType(TokenType.LITERAL_INT);

					root.setData(Double.parseDouble(root.getValue()));

					root.setBoolean(!(root.getData() == 0));
				}else{
					root.setString(root.getValue());
				}
			}
			return root;
		}

		if (root.getType() == TreeNodeType.FACTOR) {
			if (root.getDataType() == TokenType.MINUS) {
				root.setData(0 - (interpreterExpr(root.getLeft()).getData()));
				root.setBoolean(!(root.getData() == 0));
				return root;
			} else {
				root.setString(root.getLeft().getString());
			}
		}
		if (root.getType() == TreeNodeType.ARRAY) {
			if (symbolTable.getSymbolType(root.getValue()) == SymbolType.ARRAY_INT) {
				root.setData(symbolTable
						.getSymbolValue(root.getValue(), (int) (interpreterExpr(root.getMiddle()).getData())).getInt());
				root.setDataType(TokenType.LITERAL_INT);
			} else if(symbolTable.getSymbolType(root.getValue()) == SymbolType.ARRAY_DOUBLE) {
				root.setData(
						symbolTable.getSymbolValue(root.getValue(), (int) (interpreterExpr(root.getMiddle()).getData()))
								.getDouble());
				root.setDataType(TokenType.LITERAL_DOUBLE);
			}else{
			
				root.setString(symbolTable.getSymbolValue(root.getValue(), (int) (interpreterExpr(root.getMiddle()).getData()))
								.getString());
			}
			root.setBoolean(!(root.getData() == 0));
			return root;
		}
		if (root.getMiddle() != null) { // 是运算表达式
			TokenType op = root.getMiddle().getDataType();
			switch (op) {
			case PLUS:
			case MINUS:
				TreeNode left = interpreterExpr(root.getLeft());
				TreeNode right = interpreterExpr(root.getRight());
				if (left.getDataType() == TokenType.LITERAL_DOUBLE || right.getDataType() == TokenType.LITERAL_DOUBLE)
					root.setDataType(TokenType.LITERAL_DOUBLE);
				else
					root.setDataType(TokenType.LITERAL_INT);
				root.setData(left.getData() + right.getData());
				root.setBoolean(!(root.getData() == 0));
				break;
			case MUL:
				TreeNode left1 = interpreterExpr(root.getLeft());
				TreeNode right1 = interpreterExpr(root.getRight());
				if (left1.getDataType() == TokenType.LITERAL_DOUBLE || right1.getDataType() == TokenType.LITERAL_DOUBLE)
					root.setDataType(TokenType.LITERAL_DOUBLE);
				else
					root.setDataType(TokenType.LITERAL_INT);
				root.setData(left1.getData() * right1.getData());
				root.setBoolean(!(root.getData() == 0));
				break;
			case DIV:
				TreeNode left2 = interpreterExpr(root.getLeft());
				TreeNode right2 = interpreterExpr(root.getRight());
				if (right2.getData() != 0) {
					if (left2.getDataType() == TokenType.LITERAL_DOUBLE
							|| right2.getDataType() == TokenType.LITERAL_DOUBLE) {
						root.setData(left2.getData() / right2.getData());
						root.setDataType(TokenType.LITERAL_DOUBLE);
					} else {
						root.setData((int) (left2.getData() / right2.getData()));
						root.setDataType(TokenType.LITERAL_INT);
					}
					root.setBoolean(!(root.getData() == 0));
				} else
					try {
						throw new InterpretException("line " + right2.getLineNo() + "Divided by 0 !");
					} catch (InterpretException e) {
						e.printStackTrace();
					}
				break;
			case EQ:
				root.setBoolean(
						interpreterExpr(root.getLeft()).getData() == interpreterExpr(root.getRight()).getData());
				break;
			case GET:
				root.setBoolean(
						interpreterExpr(root.getLeft()).getData() >= interpreterExpr(root.getRight()).getData());
				break;
			case GT:
				root.setBoolean(interpreterExpr(root.getLeft()).getData() > interpreterExpr(root.getRight()).getData());
				break;
			case LET:
				root.setBoolean(
						interpreterExpr(root.getLeft()).getData() <= interpreterExpr(root.getRight()).getData());
				break;
			case LT:
				root.setBoolean(interpreterExpr(root.getLeft()).getData() < interpreterExpr(root.getRight()).getData());
				break;
			case NEQ:
				root.setBoolean(
						interpreterExpr(root.getLeft()).getData() != interpreterExpr(root.getRight()).getData());
				break;
			}

		} else {// 是单值
			TreeNode left3 = interpreterExpr(root.getLeft());
			root.setData(left3.getData());
			root.setDataType(left3.getDataType());
			root.setString(left3.getString());
			root.setBoolean(!(root.getData() == 0));
		}

		return root;
	}

}
