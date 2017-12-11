package cmm;

import exception.InterpretException;
import exception.LexerException;
import exception.ParserException;
import model.*;
import util.Util;


import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;

public class GenerateMidCode {
    private static int mLevel;
    private static int mLine;
    private static LinkedList<FourElementType> codes;
    private static SymbolTable symbolTable;
    private static ListIterator<TreeNode> iterator = null;

    public static LinkedList<FourElementType> generateCode(String filename) throws ParserException, InterpretException, LexerException {
        mLine = -1;//代码编号从0开始
        mLevel = 0;
        codes = new LinkedList<FourElementType>();
        try {
            //得到语法树
            LinkedList<TreeNode> nodeList = Util.getNodeList(Util.getTokenList(filename));
            symbolTable = SymbolTable.getSymbolTable();
            symbolTable.newTable();
            GenerateMidCode generator = new GenerateMidCode();
            iterator = (ListIterator<TreeNode>) nodeList.iterator();
            while(iterator.hasNext()){
                TreeNode node = iterator.next();
                if(node != null){
                    generator.interpret(node);
                }
            }
            symbolTable.deleteTable();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        for(FourElementType fourElementType : codes){
            System.out.println(fourElementType);
        }
        return codes;
    }

    private void interpret(TreeNode node) throws InterpretException {
        while (true) {
            switch (node.getType()) {
                case  IF_STMT:
                    interpretIfStmt(node);
                    break;
                case  WHILE_STMT:
                {
                    int jmpline = mLine + 1;
                    FourElementType falsejmp = new FourElementType(FourElementType.JUMP, interpretExp(node.getLeft()), null, null);
                    codes.add(falsejmp);
                    mLine++;
                    codes.add(new FourElementType(FourElementType.IN, null, null, null));
                    mLine++;
                    mLevel++;
                    interpret(node.getMiddle());
                    SymbolTable.getSymbolTable().deregister(mLevel);
                    mLevel--;
                    codes.add(new FourElementType(FourElementType.OUT, null, null, null));
                    mLine++;
                    codes.add(new FourElementType(FourElementType.JUMP, null, null, jmpline + ""));
                    mLine++;
                    falsejmp.setForth(String.valueOf(mLine + 1));
                    break;
                }
                case  READ_STMT:
                {
                    String varname = node.getMiddle().getLeft().getValue();
                    SymbolType type = symbolTable.getSymbolType(varname);
                    switch (type) {
                        case  SINGLE_INT:
                        case SINGLE_DOUBLE:
                            codes.add(new FourElementType(FourElementType.READ, null, null, varname));
                            mLine++;
                            break;
                        case  ARRAY_INT:
                        case ARRAY_DOUBLE:
                            codes.add(new FourElementType(FourElementType.READ, null, null, varname + "[" + interpretExp(node.getMiddle().getLeft().getMiddle()) + "]"));
                            mLine++;
                            break;
                        case  TEMP:
                        default:
                            throw new InterpretException("输入语句有误");
                    }
                    break;
                }
                case  WRITE_STMT:
                    codes.add(new FourElementType(FourElementType.WRITE, null, null, interpretExp(node.getMiddle())));
                    mLine++;
                    break;
                case  DECLARE_STMT:
                {
                    SymbolTable table = SymbolTable.getSymbolTable();
                    TreeNode var = node.getMiddle();
                    if (node.getLeft().getMiddle() == null) {//单值
                        while(var != null) {
                            String value = null;
                            if (var.getMiddle() != null) {
                                value = interpretExp(var.getMiddle());
                            }
                            if (node.getLeft().getLeft().getType() == TreeNodeType.INT) {
                                codes.add(new FourElementType(FourElementType.INT, value, null, var.getLeft().getValue()));
                                mLine++;
                                Symbol symbol = new Symbol(var.getLeft().getValue(), SymbolType.SINGLE_INT, mLevel);
                                table.register(symbol, var.getLineNo());
                            } else if (node.getLeft().getLeft().getType() == TreeNodeType.DOUBLE) {
                                codes.add(new FourElementType(FourElementType.REAL, value, null, var.getLeft().getValue()));
                                mLine++;
                                Symbol symbol = new Symbol(var.getLeft().getValue(), SymbolType.SINGLE_DOUBLE, mLevel);
                                table.register(symbol, var.getLineNo());
                            }
                            var = var.getRight();
                        }
                    } else {
                        String len = interpretExp(node.getLeft().getMiddle().getMiddle());
                        while(var != null) {
                            if (node.getLeft().getLeft().getType() == TreeNodeType.INT) {
                                codes.add(new FourElementType(FourElementType.INT, null, len, var.getLeft().getValue()));
                                mLine++;
                                Symbol symbol = new Symbol(var.getLeft().getValue(), SymbolType.ARRAY_INT, mLevel);
                                table.register(symbol, var.getLineNo());
                            } else {
                                codes.add(new FourElementType(FourElementType.REAL, null, len, var.getLeft().getValue()));
                                mLine++;
                                Symbol symbol = new Symbol(var.getLeft().getValue(), SymbolType.ARRAY_DOUBLE, mLevel);
                                table.register(symbol, var.getLineNo());
                            }
                            var = var.getRight();
                        }
                    }

                    break;
                }
                case  ASSIGN_STMT:
                {
                    String value = interpretExp(node.getLeft());

                    TreeNode var = node.getLeft();
                    if (var.getLeft().getMiddle() == null) {//单值
                        codes.add(new FourElementType(FourElementType.ASSIGN, value, null, var.getLeft().getValue()));
                        mLine++;
                    } else {
                        String index = interpretExp(var.getLeft().getMiddle());

                        codes.add(new FourElementType(FourElementType.ASSIGN, value, null, var.getLeft().getValue() + "[" + index + "]"));
                        mLine++;
                    }
                    break;
                }
                default:
                    break;
            }
            symbolTable.clearTempNames();
            if (node.getNext() != null) {
                node = node.getNext();
            } else {
                break;
            }
        }
    }

    private void interpretIfStmt(TreeNode node) throws InterpretException {
        if (node.getType() == TreeNodeType.IF_STMT) {
            //条件跳转 jmp 条件  null 目标  条件为假时跳转
            FourElementType falsejmp = new FourElementType(FourElementType.JUMP, interpretExp(node.getLeft()), null, null);
            codes.add(falsejmp);
            mLine++;
            codes.add(new FourElementType(FourElementType.IN, null, null, null));
            mLine++;
            mLevel++;
            interpret(node.getMiddle());
            SymbolTable.getSymbolTable().deregister(mLevel);
            mLevel--;
            codes.add(new FourElementType(FourElementType.OUT, null, null, null));
            mLine++;
            if (node.getRight() != null) {
                FourElementType outjump = new FourElementType(FourElementType.JUMP, null, null, null);
                codes.add(outjump);
                mLine++;
                falsejmp.setForth(String.valueOf(mLine + 1));
                codes.add(new FourElementType(FourElementType.IN, null, null, null));
                mLine++;
                mLevel++;
                interpret(node.getRight());
                codes.add(new FourElementType(FourElementType.OUT, null, null, null));
                mLine++;
                SymbolTable.getSymbolTable().deregister(mLevel);
                mLevel--;
                outjump.setForth(String.valueOf(mLine + 1));
            } else {
                falsejmp.setForth(String.valueOf(mLine + 1));
            }
        }
    }

    private String interpretExp(TreeNode node) throws InterpretException {
        if (node.getType() == TreeNodeType.EXP) {
            if(node.getMiddle() != null){
                return interpretLogicExp(node);
            }
            node = node.getLeft();
            if(node.getMiddle() != null){
                return interpretAddtiveExp(node);
            }
            node = node.getLeft();
            if(node.getMiddle() != null){
                return interpretTermExp(node);
            }
            throw new InterpretException("复合表达式非法");
        } else if (node.getType() == TreeNodeType.FACTOR) {
            if (node.getDataType() == TokenType.MINUS) {
                String temp = symbolTable.getTempSymbol().getName();
                codes.add(new FourElementType(FourElementType.MINUS, interpretExp(node.getLeft()), null, temp));
                mLine++;
                return temp;
            } else {
                return interpretExp(node.getLeft());
            }
        } else if (node.getType() == TreeNodeType.VAR) {
            if (node.getLeft() == null) {//单值
                if (symbolTable.getSymbolType(node.getValue()) == SymbolType.SINGLE_INT || symbolTable.getSymbolType(node.getValue()) == SymbolType.SINGLE_DOUBLE) {
                    return node.getValue();
                }
            } else {
                if (symbolTable.getSymbolType(node.getValue()) == SymbolType.ARRAY_INT || symbolTable.getSymbolType(node.getValue()) == SymbolType.ARRAY_DOUBLE) {
                    String temp = symbolTable.getTempSymbol().getName();
                    String index = interpretExp(node.getLeft());
                    codes.add(new FourElementType(FourElementType.ASSIGN, node.getValue() + "[" + index + "]", null, temp));
                    mLine++;
                    return temp;
                }
            }
        } else if (node.getType() == TreeNodeType.LITREAL) {
            return node.getValue();
        }
        throw new InterpretException("表达式非法");
    }

    private String interpretLogicExp(TreeNode node) throws InterpretException {
        String temp = symbolTable.getTempSymbol().getName();
        switch (node.getMiddle().getDataType()) {
            case GT:
                codes.add(new FourElementType(FourElementType.GT, interpretAddtiveExp(node.getLeft()), interpretAddtiveExp(node.getRight()), temp));
                break;
            case GET:
                codes.add(new FourElementType(FourElementType.GET, interpretAddtiveExp(node.getLeft()), interpretAddtiveExp(node.getRight()), temp));
                break;
            case LT:
                codes.add(new FourElementType(FourElementType.LT, interpretAddtiveExp(node.getLeft()), interpretAddtiveExp(node.getRight()), temp));
                break;
            case LET:
                codes.add(new FourElementType(FourElementType.LET, interpretAddtiveExp(node.getLeft()), interpretAddtiveExp(node.getRight()), temp));
                break;
            case EQ:
                codes.add(new FourElementType(FourElementType.EQ, interpretAddtiveExp(node.getLeft()), interpretAddtiveExp(node.getRight()), temp));
                break;
            case NEQ:
                codes.add(new FourElementType(FourElementType.NEQ, interpretAddtiveExp(node.getLeft()), interpretAddtiveExp(node.getRight()), temp));
                break;
            default:
                throw new InterpretException("逻辑比较非法");
        }
        mLine++;
        return temp;
    }

    private String interpretAddtiveExp(TreeNode node) throws InterpretException {
        String temp = symbolTable.getTempSymbol().getName();
        switch (node.getMiddle().getDataType()) {
            case PLUS:
                codes.add(new FourElementType(FourElementType.PLUS, interpretTermExp(node.getLeft()), interpretTermExp(node.getRight()), temp));

                break;
            case MINUS:
                codes.add(new FourElementType(FourElementType.MINUS, interpretTermExp(node.getLeft()), interpretTermExp(node.getRight()), temp));

                break;
            default:
                throw new InterpretException("算数运算非法");
        }
        mLine++;
        return temp;
    }

    /**
     * 修正存储结构带来的整数乘除法从右往左的计算错误
     * 注意term的ParserTree left一定是factor
     * @param node
     * @return
     * @throws InterpretException
     */
    private String interpretTermExp(TreeNode node) throws InterpretException {
        String opcode = getOpcode(node.getMiddle().getDataType());
        String temp1 = symbolTable.getTempSymbol().getName();
        if (node.getRight().getType() == TreeNodeType.FACTOR) {
            codes.add(new FourElementType(opcode, interpretExp(node.getLeft()), interpretExp(node.getRight()), temp1));
            mLine++;
        } else {
            codes.add(new FourElementType(opcode, interpretExp(node.getLeft()), interpretExp(node.getRight().getLeft()), temp1));
            mLine++;
            node = node.getRight();
            String temp2 = null;
            while (node.getRight() != null && node.getRight().getType() != TreeNodeType.FACTOR) {
                opcode = getOpcode(node.getMiddle().getDataType());
                temp2 = symbolTable.getTempSymbol().getName();
                codes.add(new FourElementType(opcode, temp1, interpretExp(node.getRight().getLeft()), temp2));
                mLine++;
                node = node.getRight();
                temp1 = temp2;
            }
            opcode = getOpcode(node.getMiddle().getDataType());
            temp2 = symbolTable.getTempSymbol().getName();
            codes.add(new FourElementType(opcode, temp1, interpretExp(node.getRight()), temp2));
            mLine++;
            temp1 = temp2;
        }
        return temp1;
    }

    private String getOpcode(TokenType op) {
        if (op == TokenType.MUL) {
            return FourElementType.MUL;
        } else {//Token.DIV
            return FourElementType.DIV;
        }
    }

}

