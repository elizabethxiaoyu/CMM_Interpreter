package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;

import model.TokenType;
import model.TreeNode;

public class TreeControl extends JScrollPane {
	private TreeNode tree;
	private LinkedList<TreeNode> trees; // 表达式森林
	private TreeView View;
	private static int count = 0;

	public TreeControl(LinkedList<TreeNode> trees) {
		this.trees = trees;
		this.View = new TreeView();
		setUI();
	}

	public static int getCount() {
		return count;
	}

	public static void setCount(int count) {
		TreeControl.count = count;
	}

	private void setUI() {
		this.setLayout(new ScrollPaneLayout());

		this.getViewport().add(View);
		View.setBounds(0, 0, 10000, 1000);
		View.setPreferredSize(new Dimension(10000,10000));
		this.setBounds(0, 0, 500, 500);
		this.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
		this.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_ALWAYS);
	}

	class TreeView extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int radius = 20;
		private int vGap = 60;

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			tree = trees.get(0);
			int x = 0;
			int hGap =500;
			int treeNum = 1;
			Iterator<TreeNode> iterator = trees.iterator();
			while (iterator.hasNext()) {
				
				tree = iterator.next();
				int height = this.calHeight(tree);
				System.out.println("height  "+height);
				x +=1200;
				hGap =1000;
				count = 0;
				displayTree(g, tree, x, 30, hGap);
				treeNum++;
			}
			this.setVisible(true);
		}

		int countLeft = 0;
		int countRight = 0;

		private void displayTree(Graphics g, TreeNode root, int x, int y, int hGap) {

			g.drawOval(x - radius, y - radius, 2 * radius, 2 * radius);
			if (root != null) {
				if (root.getValue() == null) {
					if(root.getDataType() == TokenType.PLUS)
						g.drawString("+" + "", x - 16, y + 4);
					else if(root.getDataType() == TokenType.MINUS)
						g.drawString("-"+ "", x - 16, y + 4);
					else if(root.getDataType() == TokenType.MUL)
						g.drawString("*" + "", x - 16, y + 4);
					else if(root.getDataType() == TokenType.DIV)
						g.drawString("/" + "", x - 16, y + 4);
					else
					g.drawString(root.getType() + "", x - 16, y + 4);
				} else {
					g.drawString(root.getValue() + "", x , y + 4);
				}
				hGap /= 1.5;
				if (root.getLeft() != null) {
					connectLeftChild(g, x, y, x - hGap, y + vGap);
					displayTree(g, root.getLeft(), (int)(x -hGap), y + vGap, hGap );

				}
				
				if (root.getMiddle() != null) {
					connectMiddleChild(g, x, y, x, y + vGap);
					displayTree(g, root.getMiddle(), x, y + vGap, hGap );

				}
				
				if (root.getRight() != null) {
					connectRightChild(g, x, y, x + hGap, y + vGap);
					displayTree(g, root.getRight(), x + (int)(hGap), y + vGap, hGap );

				}
				hGap *= 1.5;
			}

		}

		private void connectRightChild(Graphics g, int x1, int y1, int x2, int y2) {
			double d = Math.sqrt(vGap * vGap + (x2 - x1) * (x2 - x1));
			int a1 = (int) (radius * vGap / d);
			int a2 = (int) (radius * (x1 - x2) / d);
			int x11 = (int) (x1 - a2);
			int y11 = (int) (y1 + a1);
			int x21 = (int) (x2 + a2);
			int y21 = (int) (y2 - a1);
			g.drawLine(x11, y11, x21, y21);

		}

		private void connectMiddleChild(Graphics g, int x1, int y1, int x2, int y2) {
			double d = Math.sqrt(vGap * vGap + (x2 - x1) * (x2 - x1));
			int a1 = (int) (radius * vGap / d);
			int x11 = (int) (x1);
			int y11 = (int) (y1 + a1);
			int x21 = (int) (x2);
			int y21 = (int) (y2 - a1);
			g.drawLine(x11, y11, x21, y21);

		}

		private void connectLeftChild(Graphics g, int x1, int y1, int x2, int y2) {
			double d = Math.sqrt(vGap * vGap + (x2 - x1) * (x2 - x1));
			int a1 = (int) (radius * vGap / d);
			int a2 = (int) (radius * (x2 - x1) / d);
			int x11 = (int) (x1 + a2);
			int y11 = (int) (y1 + a1);
			int x21 = (int) (x2 - a2);
			int y21 = (int) (y2 - a1);
			g.drawLine(x11, y11, x21, y21);

		}
		public   int calHeight(TreeNode root){
			if(root == null)
				return 0;
			return Math.max(calHeight(root.getLeft()) + 1, calHeight(root.getRight()) + 1);
		}
	}

}