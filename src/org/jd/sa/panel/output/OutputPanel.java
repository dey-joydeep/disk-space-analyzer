package org.jd.sa.panel.output;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import org.jd.sa.listner.ProcessListner;
import org.jd.sa.panel.process.ProcessPanel;
import org.jd.sa.resource.IconLoader;
import org.jd.sa.resource.IconLoader.Icons;
import org.jd.sa.resource.Resources;

public class OutputPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 413741578645819251L;

	private JPanel _leftPanel;
	private JPanel _rightPanel;
	private JPanel _topPanel;

	private JTree _tree;
	private JButton _refreshButton;

	private JScrollPane _tableScrollpane;

	private static final Object[] COLUMN_NAMES = { "Content", "", "Type",
			"Size", "Unit" };

	private JTextField[] summaryField = null;

	private final OutputHelper _helper = new OutputHelper();

	public OutputPanel() {
		this.setLayout(new BorderLayout());
		displayWaitingMessage();
	}

	private void displayWaitingMessage() {
		URL url = Resources.class.getResource("icons/loading.gif");
		Icon icon = new ImageIcon(url);
		JLabel label = new JLabel(icon);
		this.setVisible(true);
		this.add(label);
	}

	public void initializePanel() {
		this._leftPanel = new JPanel();
		this._leftPanel.setLayout(new BorderLayout());
		this.add(this._leftPanel, BorderLayout.WEST);

		this._rightPanel = new JPanel();
		this._rightPanel.setLayout(new BorderLayout());
		this.add(this._rightPanel, BorderLayout.CENTER);

		this._topPanel = new JPanel();
		this._topPanel.setLayout(new FlowLayout());
		this._topPanel.setPreferredSize(new Dimension(300, 50));
		this.add(this._topPanel, BorderLayout.NORTH);

		this.createRefreshButton();
		this.createReturnButton();
		this.createNavigationPane();
		this.remove(0);
		this.revalidate();
		this.repaint();
	}

	private void createNavigationPane() {
		_helper.populateCompleteNode();
		DefaultMutableTreeNode curDir = _helper
				.getResultNodes(ProcessPanel.fileStructure);
		_tree = new JTree(curDir);
		_tree.setSelectionPath(_tree.getPathForRow(0));
		renderRightPanel(((File) curDir.getUserObject()).getAbsolutePath());

		_tree.setCellRenderer(new CustomTreeCellRenderer());

		JScrollPane scrollpane = new JScrollPane();
		scrollpane.getViewport().add(_tree);
		scrollpane.setPreferredSize(new Dimension(220, Integer.MAX_VALUE));

		_tree.addMouseListener(new TreeMouseListner());
		_tree.addKeyListener(new TreeKeyListner());

		_tree.addTreeSelectionListener(new CustomTreeSelectionListener());

		this._leftPanel.add(scrollpane);
	}

	private class CustomTreeSelectionListener implements TreeSelectionListener {

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			TreePath treePath = e.getPath();
			_tree.scrollPathToVisible(treePath);
		}

	}

	private class TreeMouseListner extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {

			int selRow = _tree.getRowForLocation(e.getX(), e.getY());
			if (selRow < 0)
				return;

			TreePath selPath = _tree.getPathForLocation(e.getX(), e.getY());
			_tree.setSelectionPath(selPath);
			_tree.setSelectionRow(selRow);

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath
					.getLastPathComponent();

			File clickedPath = (File) node.getUserObject();

			String menuItemName = getFileFolderMenuName(clickedPath);

			renderRightPanel(clickedPath.getAbsolutePath());

			if (SwingUtilities.isRightMouseButton(e)) {
				displayPopupMenu(e.getComponent(), menuItemName, clickedPath,
						e.getX(), e.getY(), false);
			}
		}
	}

	private class TreeKeyListner extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) _tree
					.getLastSelectedPathComponent();
			File clickedPath = (File) node.getUserObject();
			String menuItemName = getFileFolderMenuName(clickedPath);
			TreePath treePath = _helper.getTreePath(node,
					clickedPath.getAbsolutePath());

			switch (e.getKeyCode()) {
			case 10:
				renderRightPanel(clickedPath.getAbsolutePath());
				break;
			case 525:
				Point p = _tree.getRowBounds(_tree.getRowForPath(treePath))
						.getLocation();
				displayPopupMenu(e.getComponent(), menuItemName, clickedPath,
						(int) p.getX(), (int) p.getY(), true);
				break;
			default:
				break;
			}
		}
	}

	private String getFileFolderMenuName(File clickedPath) {
		return clickedPath.isFile() ? "Open file location" : clickedPath
				.getParent() == null ? "Open " + clickedPath.getAbsolutePath()
				: "Open folder";
	}

	private void renderRightPanel(String nodePath) {
		removeSortButton();
		File f = new File(nodePath);
		if (f.isDirectory() && f.list().length > 0) {
			this.createSortButton();
		}
		createSummary(nodePath);
		createOutputTable(nodePath);
	}

	private void createSummary(String nodePath) {
		if (summaryField == null) {
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignOnBaseline(true);
			flowLayout.setAlignment(FlowLayout.LEFT);

			JPanel summaryPanel = new JPanel(flowLayout);
			JPanel labelPanel = new JPanel();
			JPanel fieldPanel = new JPanel();

			JLabel pathLabel = new JLabel("Path: ");
			JLabel typeLabel = new JLabel("Type: ");
			JLabel sizeLabel = new JLabel("Size: ");
			summaryField = new JTextField[4];
			for (int i = 0; i < summaryField.length; i++) {
				summaryField[i] = new JTextField();
				fieldPanel.add(summaryField[i]);
				summaryField[i].setEditable(false);

				if (i == 1) {
					summaryField[i].setVisible(false);
					continue;
				}

				if (i == 0) {
					summaryField[i]
							.setToolTipText("Double click to copy the full path");
					addPathSummaryMouseListner(summaryField[i], i);
				}

				summaryField[i].setOpaque(false);
				summaryField[i].setBackground(new Color(0, 0, 0, 0));
				summaryField[i].setBorder(BorderFactory.createEmptyBorder());
			}

			labelPanel.setLayout(new GridLayout(3, 1));
			fieldPanel.setLayout(new InvisibleGridLayout(3, 1));

			labelPanel.add(pathLabel);
			labelPanel.add(typeLabel);
			labelPanel.add(sizeLabel);

			summaryPanel.add(labelPanel);
			summaryPanel.add(fieldPanel);

			summaryPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			this._rightPanel.add(summaryPanel, BorderLayout.NORTH);
		}

		Object[][] data = _helper.prepareTableData(nodePath, true);

		for (int i = 0; i < summaryField.length; i++) {
			summaryField[i].setText(data[0][i].toString());
		}

		this._rightPanel.validate();
		this._rightPanel.repaint();
	}

	private void addPathSummaryMouseListner(JTextField field, int index) {
		field.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				if (me.getClickCount() != 2)
					return;

				JPanel panel = (JPanel) field.getParent();
				JTextField hiddenField = (JTextField) panel
						.getComponent(index + 1);
				String fullPath = hiddenField.getText();
				StringSelection selection = new StringSelection(fullPath);
				Clipboard clipboard = Toolkit.getDefaultToolkit()
						.getSystemClipboard();
				clipboard.setContents(selection, selection);
				System.out.println("String coppied to clipboard: " + fullPath);
			}
		});
	}

	private void createOutputTable(String nodePath) {

		Object[][] data = _helper.prepareTableData(nodePath, false);

		if (data != null) {
			if (this._tableScrollpane == null) {
				this._tableScrollpane = new JScrollPane();
			} else
				this._tableScrollpane.getViewport().remove(0);

			this._rightPanel.add(this._tableScrollpane, BorderLayout.CENTER);

			TableModel model = new DefaultTableModel(data, COLUMN_NAMES) {
				private static final long serialVersionUID = -4916418791813880533L;

				public Class<?> getColumnClass(int column) {
					if (column >= 0 && column <= getColumnCount())
						return getValueAt(0, column).getClass();
					else
						return Object.class;
				}
			};

			JTable table = new OutputTableModel(model);
			table.setFillsViewportHeight(true);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(
					model);
			table.setRowSorter(sorter);
			table.setAutoCreateRowSorter(false);
			for (int i = 0; i < table.getColumnCount(); i++) {
				sorter.setSortable(i, false);
			}
			this._helper.adjustCoulmnWidth(table, COLUMN_NAMES.length);

			TableCellRenderer defaultRenderer = table.getTableHeader()
					.getDefaultRenderer();
			table.getTableHeader().setDefaultRenderer(new TableCellRenderer() {

				@Override
				public Component getTableCellRendererComponent(JTable table,
						Object value, boolean isSelected, boolean hasFocus,
						int row, int column) {
					JLabel label = (JLabel) defaultRenderer
							.getTableCellRendererComponent(table, value,
									isSelected, hasFocus, row, column);
					label.setIcon(null);
					return label;
				}
			});

			TableColumnModel columnModel = table.getColumnModel();
			columnModel.removeColumn(columnModel.getColumn(1));
			table.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent me) {
					JTable table = (JTable) me.getSource();
					Point p = me.getPoint();
					int rowIndex = table.rowAtPoint(p);
					if (me.getClickCount() == 2 && rowIndex > -1) {
						String selectedPath = (String) model.getValueAt(
								table.convertRowIndexToModel(rowIndex), 1);
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) _tree
								.getLastSelectedPathComponent();
						if (!node.isRoot())
							node = (DefaultMutableTreeNode) node.getRoot();
						TreePath treePath = _helper.getTreePath(node,
								selectedPath);
						if (treePath != null) {
							_tree.setSelectionPath(treePath);
							renderRightPanel(selectedPath);
						}
					}
				}
			});

			this._tableScrollpane.getViewport().add(table);
			this._rightPanel.validate();
			this._rightPanel.repaint();
		} else {
			if (this._tableScrollpane != null) {
				this._tableScrollpane.getViewport().remove(0);
				this._rightPanel.remove(this._tableScrollpane);
				this._tableScrollpane = null;
			}
		}
	}

	private void displayPopupMenu(Component invoker, String menuItemName,
			File clickedPath, int x, int y, boolean isSourceKb) {
		JPopupMenu menu = new JPopupMenu();
		_tree.add(menu);

		Action openAction = _helper.createLocationOpenAction(menuItemName,
				clickedPath, _rightPanel);
		menu.add(openAction);

		Action scanAction = _helper.scanAction(clickedPath, _rightPanel,
				_refreshButton);
		if (scanAction != null)
			menu.add(scanAction);
		int row = _tree.getClosestRowForLocation(x, y);
		_tree.setSelectionRow(row);

		if (isSourceKb)
			y += 20;
		menu.show(invoker, x, y);
	}

	private void createRefreshButton() {
		_refreshButton = new JButton();
		this._refreshButton.addActionListener(this);
		this._refreshButton.setActionCommand("refresh_btn");
		this._refreshButton.setVisible(true);
		this._refreshButton.setToolTipText("Scan this path again");
		Icon icon = IconLoader.getIcon(Icons.REFRESH_BTN);
		this._refreshButton.setIcon(icon);
		this._refreshButton.setBorderPainted(false);
		this._refreshButton.setContentAreaFilled(false);
		this._refreshButton.setPreferredSize(new Dimension(icon.getIconWidth(),
				icon.getIconHeight()));
		this._refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		this._topPanel.add(this._refreshButton);
	}

	private void createReturnButton() {
		JButton returnButton = new JButton();
		returnButton.addActionListener(this);
		returnButton.setActionCommand("return_btn");
		returnButton.setVisible(true);
		returnButton.setToolTipText("Start analysation of another folder");
		Icon icon = IconLoader.getIcon(Icons.ANAYZE_BTN);
		returnButton.setIcon(icon);
		returnButton.setBorderPainted(false);
		returnButton.setContentAreaFilled(false);
		returnButton.setPreferredSize(new Dimension(icon.getIconWidth(), icon
				.getIconHeight()));
		returnButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		this._topPanel.add(returnButton);
	}

	private void createSortButton() {
		JButton sortButton = new JButton();
		sortButton.addActionListener(this);
		sortButton.setActionCommand("sort_desc_btn");
		sortButton.setVisible(true);
		sortButton
				.setToolTipText("Sort table data by size in descending order");
		Icon icon = IconLoader.getIcon(Icons.SORT_DESC_BTN);
		sortButton.setIcon(icon);
		sortButton.setPreferredSize(new Dimension(icon.getIconWidth(), icon
				.getIconHeight()));
		sortButton.setBorderPainted(false);
		sortButton.setContentAreaFilled(false);
		sortButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		this._topPanel.add(sortButton);
		this._topPanel.validate();
		this._topPanel.repaint();
	}

	private void removeSortButton() {
		Component[] components = this._topPanel.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof JButton) {
				JButton oldButton = (JButton) components[i];
				if (oldButton.getActionCommand().matches(
						"sort_desc_btn|sort_asc_btn")) {
					this._topPanel.remove(oldButton);
					this._topPanel.validate();
					this._topPanel.repaint();
					break;
				}
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "return_btn":
			ProcessListner.processToExecute = 1;
			break;
		case "refresh_btn":
			ProcessListner.processToExecute = 2;
			break;
		case "sort_asc_btn":
			_helper.sortBySize(SortOrder.ASCENDING, _tableScrollpane);
			JButton btn1 = ((JButton) e.getSource());
			btn1.setIcon(IconLoader.getIcon(Icons.SORT_DESC_BTN));
			btn1.setActionCommand("sort_desc_btn");
			btn1.setToolTipText("Sort table data by size in descending order");
			break;
		case "sort_desc_btn":
			_helper.sortBySize(SortOrder.DESCENDING, _tableScrollpane);
			JButton btn2 = ((JButton) e.getSource());
			btn2.setIcon(IconLoader.getIcon(Icons.SORT_ASC_BTN));
			btn2.setActionCommand("sort_asc_btn");
			btn2.setToolTipText("Sort table data by size in ascending order");
			break;
		default:
			break;
		}
	}

	private static class CustomTreeCellRenderer extends DefaultTreeCellRenderer {

		private static final long serialVersionUID = 5046834303185965184L;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, sel, expanded,
					leaf, row, hasFocus);

			// decide what icons you want by examining the node
			if (value instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

				IconLoader.initialize(tree);
				File f = (File) node.getUserObject();

				if (f.isFile()) {
					setIcon(IconLoader.getIcon(Icons.FILE));
				} else if (f.getParent() == null) {
					setIcon(IconLoader.getIcon(Icons.DISK));
				} else {
					if (node.isLeaf())
						setIcon(IconLoader.getIcon(Icons.FOLDER_OPEN));
					else {
						setOpenIcon(IconLoader.getIcon(Icons.FOLDER_OPEN));
						setClosedIcon(IconLoader.getIcon(Icons.FOLDER_CLOSE));
					}
				}

				if (node.isRoot())
					setText(f.getAbsolutePath());
				else
					setText(f.getName());

				IconLoader.update(this);
			}

			return this;
		}
	}
}
