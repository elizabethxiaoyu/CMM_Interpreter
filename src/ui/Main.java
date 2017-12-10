package ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
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
import model.TreeNodeType;

public class Main {

	private static String filestr = null;
	final static Display display = new Display();
	final static Shell shell = new Shell(display);
	public static final  StyledText resultdata2  = new StyledText(shell, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
	public static final  StyledText input  = new StyledText(shell, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
	public static final  StyledText variable  = new StyledText(shell, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
	public static final  StyledText  inputTag = new StyledText(shell, SWT.MULTI | SWT.BORDER | SWT.WRAP);
	public static final  StyledText  resultdata2Tag = new StyledText(shell, SWT.MULTI | SWT.BORDER | SWT.WRAP);
	public static final  StyledText  variableTag = new StyledText(shell, SWT.MULTI | SWT.BORDER | SWT.WRAP);
	public static List<TreeNode> tree;
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
		
		MenuItem saveasItem = new MenuItem(filemenu, SWT.PUSH);
		saveasItem.setText("保存(&A)\tCtrl+Shift+S");
		saveasItem.setAccelerator(SWT.CTRL + SWT.SHIFT + 'S');
		
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
		
		MenuItem debug = new MenuItem(menu, SWT.CASCADE);
		debug.setText("调试");
		Menu debugmenu = new Menu(shell, SWT.DROP_DOWN);
		debug.setMenu(debugmenu);
		MenuItem debugItem = new MenuItem(debugmenu, SWT.PUSH);
		debugItem.setText("调试执行");
		MenuItem resumeItem = new MenuItem(debugmenu, SWT.PUSH);
		resumeItem.setText("继续调试");
		
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
				resultdata2.setText("");
				input.setText("");
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
		saveasItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(shell, SWT.SAVE);
				fd.setText("另存为");
				fd.setFilterExtensions(filterExt);
				String filename = fd.open();
				if (filename != null) {
					File file = new File(filename);
					try {
						if (!file.exists()) {
							file.createNewFile();
						}
						
			            FileOutputStream fileOutputStream=new FileOutputStream(file.getAbsoluteFile());  
			            OutputStreamWriter outputWriter=new OutputStreamWriter(fileOutputStream,"UTF-8"); 
			            outputWriter.write(codedata.getText());
//						FileWriter fw = new FileWriter(file.getAbsoluteFile(),"UTF-8");
//						BufferedWriter bw = new BufferedWriter(fw);
//						bw.write(codedata.getText());
//						bw.close();
//						fw.close();
						outputWriter.close();  
			            fileOutputStream.close();  
						filestr = filename;
						
						resultdata2.setText("");
						input.setText("");
						//并打开展示到code面板上
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
//								Display display = codedata.getDisplay();
//								display.asyncExec(new Runnable() {
//									public void run() {
									codedata.setText(sb.toString());
//									}
//								});
								lineStyler.parseBlockComments(sb.toString());
							} catch (FileNotFoundException e1) {
							} catch (IOException e1) {
							}
						}
					} catch (IOException e1) {
						// e1.printStackTrace();
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
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
								tree = SyntaxParser.syntaxAnalyse(l);
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
							Interpreter.result.setLength(0);
						}
					});
		
					
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
				} catch (IOException | LexerException e1) {
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
				Display_Tree dt;
	
				try {
					dt = new Display_Tree();
					dt.setVisible(true);
				} catch (IOException e1) {
					resultdata2.append(e1.getMessage());
					//e1.printStackTrace();
				} catch (LexerException e1) {
					resultdata2.append(e1.getMessage());
					//e1.printStackTrace();
				} catch (ParserException e1) {
					resultdata2.append(e1.getMessage());
					//e1.printStackTrace();
				}
				
			}


			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		};
		parseItem.addSelectionListener(parserListener);
		
		final SelectionListener debugListener = new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) { }

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				new Thread() {
					public void run() {
						Display display =  resultdata2.getDisplay();
						display.asyncExec(new Runnable() {

							@Override
							public void run() {
								resultdata2.setText("Result:"+"\n");
								
								try {
									if(tree == null) {
										LinkedList<Token> l = SyntaxParser.getTokenList(filestr);
										tree = SyntaxParser.syntaxAnalyse(l);
										tree = Interpreter.interpreter(tree);
									}
									else {
										tree = Interpreter.interpreterSubTrees(tree);
									}
									if(tree != null)
										tree.get(0).setInterrupt(false);
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
								Interpreter.result.setLength(0);
								variable.setText(Interpreter.getVariables());
							}
							
						});
					}
				}.start();
			}
			
		};
		debugItem.addSelectionListener(debugListener);
		
		final SelectionListener resumeListener = new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) { }

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if(tree == null) return;
				try {
					tree = Interpreter.interpreterSubTrees(tree);
					resultdata2.append(Interpreter.result.toString());
					System.out.println(Interpreter.result.toString());
					Interpreter.result.setLength(0);
					variable.setText(Interpreter.getVariables());
				} catch (InterpretException e) {
					e.printStackTrace();
				}
			}
			
		};
		resumeItem.addSelectionListener(resumeListener);
		
		aboutItem.addSelectionListener(new SelectionListener() {

			
			public void widgetSelected(SelectionEvent e) {
				JFrame frame = new JFrame("Love CMM");
				AboutInterface f = new AboutInterface();
				frame.add(f);
				f.init();
				
				frame.setLocationRelativeTo(null);  
		        frame.setSize(350, 310);  
		        frame.setVisible(true);
		        frame.setLocationRelativeTo(null);
		        
//				StringBuilder sb = new StringBuilder();
//				sb.append("CMM解释器" + System.getProperty("line.separator") + System.getProperty("line.separator"));
//				sb.append("作者: AliceLiu" + System.getProperty("line.separator") + System.getProperty("line.separator"));
//				resultdata2.setText(sb.toString());
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
		
		ToolItem debugToolItem = new ToolItem(toolbar, SWT.PUSH);
		debugToolItem.setImage(new Image(display, "res" + File.separator + "ic_debug.png"));
		debugToolItem.setToolTipText("Debug");
		
		ToolItem resumeToolItem = new ToolItem(toolbar, SWT.PUSH);
		resumeToolItem.setImage(new Image(display, "res" + File.separator + "ic_resume.png"));
		resumeToolItem.setToolTipText("Resume");
		
		ToolItem quitToolItem = new ToolItem(toolbar, SWT.PUSH);
		quitToolItem.setImage(new Image(display, "res" + File.separator + "ic_quit.png"));
		quitToolItem.setToolTipText("Exit");

		toolbar.setSize(400, 36);
		openToolItem.addSelectionListener(openListener);
		lexerToolItem.addSelectionListener(lexerListener);
		parserToolItem.addSelectionListener(parserListener);
		runToolItem.addSelectionListener(runListener);
		debugToolItem.addSelectionListener(debugListener);
		resumeToolItem.addSelectionListener(resumeListener);
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
		codedata.setAlwaysShowScrollBars(false);
		codedata.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				lineStyler.parseBlockComments(codedata.getText());
			}
		});
		codedata.addLineStyleListener(lineStyler);
		
		//双击设置、删除断点
		codedata.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent me) {
				int line = codedata.getLineAtOffset(codedata.getCaretOffset());
				try {
					if(tree == null) {
						LinkedList<Token> l = SyntaxParser.getTokenList(filestr);
						tree = SyntaxParser.syntaxAnalyse(l);
					}
				} catch (LexerException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ParserException e) {
					e.printStackTrace();
				}
				if(codedata.getLineBackground(line) != null) {
					codedata.setLineBackground(line, 1, null);
					//删除中断
					Iterator<TreeNode> iterator = tree.iterator();
					while (iterator.hasNext()) {
						TreeNode subTree = iterator.next();
						deleteInterrupt(subTree, line);
					}
				}
				else {
					codedata.setLineBackground(line, 1, shell.getDisplay().getSystemColor(SWT.COLOR_CYAN));
					//添加中断
					Iterator<TreeNode> iterator = tree.iterator();
					while (iterator.hasNext()) {
						TreeNode subTree = iterator.next();
						insertInterrupt(subTree, line + 1);
					}
				}
			}

			private void insertInterrupt(TreeNode subTree, int line) {
				if(subTree != null && subTree.getLineNo() == line) {
					subTree.setInterrupt(true);
					return;
				}
				if(subTree != null && subTree.getLeft() != null)
					insertInterrupt(subTree.getLeft(), line);
				if(subTree != null && subTree.getMiddle() != null)
					insertInterrupt(subTree.getMiddle(), line);
				if(subTree != null && subTree.getRight() != null)
					insertInterrupt(subTree.getRight(), line);
			}

			private void deleteInterrupt(TreeNode subTree, int line) {
				if(subTree != null && subTree.getInterrupt()
						&& subTree.getLineNo() == line) {
					subTree.setInterrupt(false);
				}
				if(subTree != null && subTree.getLeft() != null)
					deleteInterrupt(subTree.getLeft(), line);
				if(subTree != null && subTree.getMiddle() != null)
					deleteInterrupt(subTree.getMiddle(), line);
				if(subTree != null && subTree.getRight() != null)	
					deleteInterrupt(subTree.getRight(), line);
			}

			@Override
			public void mouseDown(MouseEvent arg0) { }

			@Override
			public void mouseUp(MouseEvent arg0) { }
			
		});
		
		fd = new FormData();
		fd.top = new FormAttachment(50, 24);
		fd.left = new FormAttachment(0, 1);
		fd.bottom = new FormAttachment(100, -1);
		fd.right = new FormAttachment(30, -1);
		inputTag.setLayoutData(fd);
		inputTag.setEditable(false);
		inputTag.setAlwaysShowScrollBars(false);
		inputTag.setText("Input box");
		Font font = new Font(shell.getDisplay(), "Courier", 15, SWT.BOLD);
		inputTag.setFont(font);
		org.eclipse.swt.graphics.Color green = display.getSystemColor(SWT.COLOR_BLUE);
		StyleRange[] ranges = new StyleRange[1];
		ranges[0] = new StyleRange(0, 9, green, null);
		inputTag.replaceStyleRanges(0, 9, ranges);
		
		fd = new FormData();
		fd.top = new FormAttachment(53, 24);
		fd.left = new FormAttachment(0, 1);
		fd.bottom = new FormAttachment(100, -1);
		fd.right = new FormAttachment(30, -1);
		input.setLayoutData(fd);
		input.setEditable(true);
		input.setAlwaysShowScrollBars(false);
		
		fd = new FormData();
		fd.top = new FormAttachment(50, 24);
		fd.left = new FormAttachment(30, 1);
		fd.bottom = new FormAttachment(100, -1);
		fd.right = new FormAttachment(70, -1);
		resultdata2Tag.setLayoutData(fd);
		resultdata2Tag.setEditable(false);
		resultdata2Tag.setAlwaysShowScrollBars(false);
		resultdata2Tag.setText("Output box");
		resultdata2Tag.setFont(font);
		ranges[0] = new StyleRange(0, 10, green, null);
		resultdata2Tag.replaceStyleRanges(0, 10, ranges);
		
		fd = new FormData();
		fd.top = new FormAttachment(53, 24);
		fd.left = new FormAttachment(30, 1);
		fd.bottom = new FormAttachment(100, -1);
		fd.right = new FormAttachment(70, -1);
		resultdata2.setLayoutData(fd);
		resultdata2.setEditable(true);
		resultdata2.setAlwaysShowScrollBars(false);
		resultdata2.setBackgroundImage(new Image(display,"res//background.png"));
		
		fd = new FormData();
		fd.top = new FormAttachment(50, 24);
		fd.left = new FormAttachment(70, 1);
		fd.bottom = new FormAttachment(100, -1);
		fd.right = new FormAttachment(100, -1);
		variableTag.setLayoutData(fd);
		variableTag.setEditable(false);
		variableTag.setAlwaysShowScrollBars(false);
		variableTag.setText("Variable");
		variableTag.setFont(font);
		ranges[0] = new StyleRange(0, 8, green, null);
		variableTag.replaceStyleRanges(0, 8, ranges);
		
		fd = new FormData();
		fd.top = new FormAttachment(53, 24);
		fd.left = new FormAttachment(70, 1);
		fd.bottom = new FormAttachment(100, -1);
		fd.right = new FormAttachment(100, -1);
		variable.setLayoutData(fd);
		variable.setEditable(true);
		variable.setAlwaysShowScrollBars(false);
		
		// 主要布局结束
		
		
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();

	}

}
