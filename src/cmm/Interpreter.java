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
	static java.util.regex.Pattern pattern = Pattern.compile("(-)+[0-9]*(\\.?)[0-9]*");
	static java.util.regex.Pattern pattern2 = Pattern.compile("[-]?[0-9]*(\\.?)[0]");
	public static StringBuilder result = new StringBuilder();

	public static void interpreter(LinkedList<TreeNode> tree) throws InterpretException {

		for (TreeNode root : tree) {
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
		case BREAK_STMT: // 这里还要测试
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
	 * 目前的问题是给iNT类型的赋值小数，会自动截断 还有1.0这样的小数直接显示1
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
							throw new InterpretException("The index of the array is illegal.");
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
					throw new InterpretException("The index of the array is illegal.");
				} else {
					symbolTable.setSymbolValue(node.getLeft().getValue(), root.getRight().getData(),
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
				symbolTable.register(symbol);
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
				symbolTable.register(symbol);
			}
		} else if (root.getLeft().getLeft().getType() == TreeNodeType.DOUBLE) {
			if (root.getLeft().getMiddle() != null) {
				Symbol symbol = new Symbol(root.getMiddle().getLeft().getValue(), SymbolType.ARRAY_DOUBLE, mLevel);
				Value value = new Value();
				double array[] = new double[Integer.valueOf(root.getLeft().getMiddle().getMiddle().getValue())];
				value.setArrayDouble(array);
				value.setType(SymbolType.ARRAY_DOUBLE);
				symbol.setValue(value);
				symbolTable.register(symbol);
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
				symbolTable.register(symbol);

			}
		}
	}

	private static void interpreterWRITE(TreeNode root) throws InterpretException {
		String s = String.valueOf(interpreterExpr(root.getMiddle()).getData());
		if (pattern2.matcher(s).matches()) {
			System.out.println(((int) Double.parseDouble(s)));
			result.append(String.valueOf(((int) Double.parseDouble(s))) + "\n");
		} else {
			System.out.println(s);
			result.append(s + "\n");
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
		Scanner sc = new Scanner(System.in);

		switch (type) {
		case SINGLE_INT:
			try {
				value.setInt(sc.nextInt());
				symbolTable.setSymbolValue(root.getMiddle().getLeft().getValue(), value);
			} catch (InputMismatchException e) {
				throw new InterpretException("The input is not an integer.");
			}
			break;
		case SINGLE_DOUBLE:
			value.setDouble(sc.nextDouble());
			symbolTable.setSymbolValue(root.getMiddle().getLeft().getValue(), value);
			break;
		case ARRAY_INT:
			if (root.getMiddle().getLeft().getLeft() == null) {// 赋值整个数组
				int[] array = symbolTable.getSymbolValue(root.getMiddle().getLeft().getValue()).getArrayInt();
				for (int i = 0; i < array.length; i++)
					array[i] = sc.nextInt();
			} else {// 赋值数组中某个元素
				interpreterExpr(root.getMiddle().getLeft().getMiddle());
				symbolTable.setSymbolValue(root.getMiddle().getLeft().getValue(), sc.nextInt(),
						(int) root.getMiddle().getLeft().getMiddle().getData());
			}
			break;
		case ARRAY_DOUBLE:
			if (root.getMiddle().getLeft().getLeft() == null) {// 赋值整个数组
				double[] array2 = symbolTable.getSymbolValue(root.getMiddle().getLeft().getValue()).getArrayDouble();
				for (int i = 0; i < array2.length; i++)
					array2[i] = sc.nextDouble();
			} else {
				// 赋值数组中某个元素
				interpreterExpr(root.getMiddle().getLeft().getMiddle());
				symbolTable.setSymbolValue(root.getMiddle().getLeft().getValue(), sc.nextDouble(),
						(int) root.getMiddle().getLeft().getMiddle().getData());
			}
			break;
		default:
			throw new InterpretException("输入语句有误");
		}

	}

	/**
	 * int b = 1; while(b <= 2){ b = b+1 ; write(b); } write(b);
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

	private static TreeNode interpreterExpr(TreeNode root) throws InterpretException {
		if (root.getLeft() == null) {
			if (root.getType() == TreeNodeType.ID) {
				if (symbolTable.getSymbolValue(root.getValue()).getType() == SymbolType.SINGLE_INT)
					root.setData(symbolTable.getSymbolValue(root.getValue()).getInt());
				else if (symbolTable.getSymbolValue(root.getValue()).getType() == SymbolType.SINGLE_DOUBLE)
					root.setData(symbolTable.getSymbolValue(root.getValue()).getDouble());
			} else {
				root.setData(Double.parseDouble(root.getValue()));
			}

			return root;
		}

		if (root.getType() == TreeNodeType.FACTOR) {
			if (root.getDataType() == TokenType.MINUS) {
				root.setData(0 - (interpreterExpr(root.getLeft()).getData()));
				return root;
			}
		}
		if (root.getType() == TreeNodeType.ARRAY) {
			if (symbolTable.getSymbolType(root.getValue()) == SymbolType.ARRAY_INT)
				root.setData(symbolTable
						.getSymbolValue(root.getValue(), (int) (interpreterExpr(root.getMiddle()).getData())).getInt());
			else
				root.setData(
						symbolTable.getSymbolValue(root.getValue(), (int) (interpreterExpr(root.getMiddle()).getData()))
								.getDouble());
			return root;
		}
		if (root.getMiddle() != null) { // 是运算表达式
			TokenType op = root.getMiddle().getDataType();
			switch (op) {
			case PLUS:
			case MINUS:
				root.setData(interpreterExpr(root.getLeft()).getData() + interpreterExpr(root.getRight()).getData());
				break;
			case MUL:
				root.setData(interpreterExpr(root.getLeft()).getData() * interpreterExpr(root.getRight()).getData());
				break;
			case DIV:
				if (interpreterExpr(root.getRight()).getData() != 0)
					root.setData(
							interpreterExpr(root.getLeft()).getData() / interpreterExpr(root.getRight()).getData());
				else
					throw new InterpretException("Divided by 0 !");
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
			root.setData(interpreterExpr(root.getLeft()).getData());
		}

		return root;
	}

}
