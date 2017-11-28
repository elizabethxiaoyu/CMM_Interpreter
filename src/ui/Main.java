package ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import cmm.Interpreter;
import cmm.SyntaxParser;
import util.*;
import exception.InterpretException;
import exception.LexerException;
import exception.ParserException;
import model.Token;
import model.TreeNode;

public class Main {

	private static String filestr = null;
	final static Display display = new Display();
	final static Shell shell = new Shell(display);
	public static final  StyledText resultdata2  = new StyledText(shell, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
	
	public static void main(String[] args) {
		final StyledText codedata = new StyledText(shell, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
		final JavaLineStyler lineStyler = new JavaLineStyler();
		shell.setSize(1366, 768);
		shell.setText("CMM_Interpreter");
		Image image = new Image(display, "res//icon.png"); 
		shell.setImage(image);
		// 菜单开始
		Menu menu = new Menu(shell, SWT.BAR);
		MenuItem file = new MenuItem(menu, SWT.CASCADE);
		file.setText("文件");
		Menu filemenu = new Menu(shell, SWT.DROP_DOWN);
		file.setMenu(filemenu);

		MenuItem openItem = new MenuItem(filemenu, SWT.PUSH);
		openItem.setText("打开(&O)\tCtrl+O");
		openItem.setAccelerator(SWT.CTRL + 'O');

		new MenuItem(filemenu, SWT.SEPARATOR);
		MenuItem exitItem = new MenuItem(filemenu, SWT.PUSH);
		exitItem.setText("退出");

		MenuItem run = new MenuItem(menu, SWT.CASCADE);
		run.setText("工具");
		Menu runmenu = new Menu(shell, SWT.DROP_DOWN);
		run.setMenu(runmenu);

		MenuItem lexicalItem = new MenuItem(runmenu, SWT.PUSH);
		lexicalItem.setText("词法分析");
		MenuItem parseItem = new MenuItem(runmenu, SWT.PUSH);
		parseItem.setText("语法分析");
		MenuItem runItem = new MenuItem(runmenu, SWT.PUSH);
		runItem.setText("解释执行");
		MenuItem help = new MenuItem(menu, SWT.CASCADE);
		help.setText("帮助");
		Menu helpmenu = new Menu(shell, SWT.DROP_DOWN);
		help.setMenu(helpmenu);
		MenuItem aboutItem = new MenuItem(helpmenu, SWT.PUSH);
		aboutItem.setText("关于");
		shell.setMenuBar(menu);
		// 菜单结束

		// 退出菜单
		SelectionListener exitListener =new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!shell.isDisposed()) {
					display.dispose();
				}
				return;
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		};
		exitItem.addSelectionListener(exitListener);

		// 打开
		final String[] filterExt = { "*.cmm", "*.txt", "*.*" };
		SelectionListener openListener = new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				fd.setText("打开");
				fd.setFilterExtensions(filterExt);
				filestr = fd.open();
				if (filestr != null) {
					try {
						// 从文件中读取数据到程序中
						BufferedReader br = new BufferedReader(
								new InputStreamReader(new FileInputStream(filestr), "UTF-8"));
						final StringBuilder sb = new StringBuilder();
						String content;
						while ((content = br.readLine()) != null) { // 每次读一行，读到content中
							sb.append(content); // 追加
							sb.append(System.getProperty("line.separator"));
						}
						br.close(); // 关闭输入流
						Display display = codedata.getDisplay();
						display.asyncExec(new Runnable() {
							public void run() {
								codedata.setText(sb.toString());
							}
						});
						lineStyler.parseBlockComments(sb.toString());
					} catch (FileNotFoundException e1) {
					} catch (IOException e1) {
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		};
		openItem.addSelectionListener(openListener);
		
		final SelectionListener runListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					Display display =  resultdata2.getDisplay();
					display.asyncExec(new Runnable() {
						public void run() {
							resultdata2.setText("Result:"+"\n");
							try {
								LinkedList<Token> l = SyntaxParser.getTokenList(filestr);
								LinkedList<TreeNode> tree = SyntaxParser.syntaxAnalyse(l);
								Interpreter.interpreter(tree);
								
							} catch (LexerException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							} catch (ParserException e) {
								e.printStackTrace();
							} catch (InterpretException e) {
								e.printStackTrace();
							} 
							resultdata2.append(Interpreter.result.toString());
							System.out.println(Interpreter.result.toString());
						}
					});
					//在完成语法分析后在此加可执行文件
					
				} catch (Exception e1) {
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		};
		runItem.addSelectionListener(runListener);
		
		
		final SelectionListener lexerListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				LinkedList<Token> tokenList;
				try {
					tokenList = Util.getTokenList(filestr);
					resultdata2.setText("");
					for (Token token : tokenList) {
						resultdata2.append(token.toStringWithLine());
						resultdata2.append(System.getProperty("line.separator"));
					}
				} catch (IOException e1) {
					resultdata2.setText(e1.toString());
					// e1.printStackTrace();
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		};
		lexicalItem.addSelectionListener(lexerListener);
		
		final SelectionListener parserListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Display_Tree.fileName = filestr;
				Display_Tree dt= new Display_Tree();
				dt.setVisible(true);
			}


			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		};
		parseItem.addSelectionListener(parserListener);
		
		aboutItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				StringBuilder sb = new StringBuilder();
				sb.append("CMM解释器" + System.getProperty("line.separator") + System.getProperty("line.separator"));
				sb.append("作者: AliceLiu" + System.getProperty("line.separator") + System.getProperty("line.separator"));
				resultdata2.setText(sb.toString());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		// 菜单事件结束
		// 工具栏开始
		ToolBar toolbar = new ToolBar(shell, SWT.HORIZONTAL);
		
		ToolItem openToolItem = new ToolItem(toolbar, SWT.PUSH);
		openToolItem.setImage(new Image(display, "res" + File.separator + "ic_open.png"));
		openToolItem.setToolTipText("Open");
		
		ToolItem lexerToolItem = new ToolItem(toolbar, SWT.PUSH);
		lexerToolItem.setImage(new Image(display, "res" + File.separator + "ic_lexer.png"));
		lexerToolItem.setToolTipText("Lexer");
		
		ToolItem parserToolItem = new ToolItem(toolbar, SWT.PUSH);
		parserToolItem.setImage(new Image(display, "res" + File.separator + "ic_parser.png"));
		parserToolItem.setToolTipText("Parser");
		
		ToolItem runToolItem = new ToolItem(toolbar, SWT.PUSH);
		runToolItem.setImage(new Image(display, "res" + File.separator + "ic_run.png"));
		runToolItem.setToolTipText("Run");
		
		ToolItem quitToolItem = new ToolItem(toolbar, SWT.PUSH);
		quitToolItem.setImage(new Image(display, "res" + File.separator + "ic_quit.png"));
		quitToolItem.setToolTipText("Exit");

		toolbar.setSize(400, 36);
		openToolItem.addSelectionListener(openListener);
		lexerToolItem.addSelectionListener(lexerListener);
		parserToolItem.addSelectionListener(parserListener);
		runToolItem.addSelectionListener(runListener);
		quitToolItem.addSelectionListener(exitListener);
		
		// 工具栏结束
		// 主要布局
		shell.setLayout(new FormLayout());
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				lineStyler.disposeColors();
				codedata.removeLineStyleListener(lineStyler);
			}
		});
		FormData fd = new FormData();
		fd.top = new FormAttachment(0, 40);
		fd.left = new FormAttachment(0, 1);
		fd.bottom = new FormAttachment(50, -1);
		fd.right = new FormAttachment(100, -1);
		codedata.setLayoutData(fd);
		codedata.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				lineStyler.parseBlockComments(codedata.getText());
			}
		});
		codedata.addLineStyleListener(lineStyler);

		fd = new FormData();
		fd.top = new FormAttachment(50, 24);
		fd.left = new FormAttachment(0, 1);
		fd.bottom = new FormAttachment(100, -1);
		fd.right = new FormAttachment(100, -1);
		resultdata2.setLayoutData(fd);
		resultdata2.setEditable(false);
		resultdata2.setBackgroundImage(new Image(display,"res//background.png"));
		// 主要布局结束

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();

	}

}
