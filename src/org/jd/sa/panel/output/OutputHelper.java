package org.jd.sa.panel.output;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultRowSorter;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.jd.sa.panel.input.InputPanel;
import org.jd.sa.panel.process.ProcessPanel;
import org.jd.sa.process.FileStructure;
import org.jd.sa.resource.Resources;

public class OutputHelper {

	private static DefaultMutableTreeNode _completeNode;

	protected DefaultMutableTreeNode getResultNodes(
			final FileStructure fileStructure) {

		DefaultMutableTreeNode parentNode = null;

		if (fileStructure.isFile())
			return new DefaultMutableTreeNode(fileStructure.getPath());

		parentNode = new DefaultMutableTreeNode(fileStructure.getPath());

		if (fileStructure.isChildPresent()) {
			Resources.sort(fileStructure.getChildren());
			for (FileStructure subStructure : fileStructure.getChildren())
				parentNode.add(getResultNodes(subStructure));
		}

		return parentNode;
	}

	protected void populateCompleteNode() {
		_completeNode = getCompleteNode(ProcessPanel.fileStructure);

	}

	private DefaultMutableTreeNode getCompleteNode(
			final FileStructure fileStructure) {

		DefaultMutableTreeNode node = null;

		if (fileStructure.isFile()) {
			DefaultMutableTreeNode leaf = new DefaultMutableTreeNode();
			leaf.setUserObject(new OutputStructure(fileStructure.getPath(),
					fileStructure.getSize(), "File"));
			return leaf;
		} else {
			node = new DefaultMutableTreeNode(new OutputStructure(
					fileStructure.getPath(), fileStructure.getSize(), "Folder"));
			if (fileStructure.isChildPresent()) {
				Resources.sort(fileStructure.getChildren());
				for (FileStructure subStructure : fileStructure.getChildren())
					node.add(getCompleteNode(subStructure));
			}
		}

		return node;
	}

	protected Object[][] prepareTableData(String nodePath, boolean isHeader) {
		OutputStructure os = null;
		DefaultMutableTreeNode foundNode = null;
		Enumeration<?> nodeBfs = _completeNode.breadthFirstEnumeration();

		while (nodeBfs.hasMoreElements()) {
			foundNode = (DefaultMutableTreeNode) nodeBfs.nextElement();
			os = (OutputStructure) foundNode.getUserObject();
			if (nodePath.equalsIgnoreCase(os.getPath().getAbsolutePath()))
				break;
		}

		Object[][] data = null;
		if (foundNode != null) {

			if (isHeader) {
				os = (OutputStructure) foundNode.getUserObject();
				data = new Object[1][4];
				data[0][0] = os.getPath().getParent() == null ? os.getPath()
						.getAbsolutePath() : os.getPath().getName();
				data[0][1] = os.getPath().getAbsolutePath();
				if (os.getPath().getName().isEmpty()) {
					data[0][2] = "Drive Partition";
				} else {
					data[0][2] = os.getType();
				}
				String[] sizeWithUnit = Resources.getNormalizedSize(os
						.getSize());
				data[0][3] = sizeWithUnit[0] + " " + sizeWithUnit[1];
			} else {
				int childCount = foundNode.getChildCount();
				if (childCount > 0) {
					data = new Object[childCount][5];

					for (int index = 0; index < childCount; index++) {
						DefaultMutableTreeNode currElem = (DefaultMutableTreeNode) foundNode
								.getChildAt(index);
						os = (OutputStructure) currElem.getUserObject();
						data[index][0] = os.getPath().getName();
						data[index][1] = os.getPath().getAbsolutePath();
						data[index][2] = os.getType();
						String[] sizeWithUnit = Resources.getNormalizedSize(os
								.getSize());
						data[index][3] = sizeWithUnit[0];
						data[index][4] = sizeWithUnit[1];
					}
				}
			}
		}

		return data;
	}

	protected void adjustCoulmnWidth(JTable table, int columnSize) {

		TableColumn column = null;
		for (int i = 0; i < columnSize; i++) {
			column = table.getColumnModel().getColumn(i);
			if (i == 0)
				column.setPreferredWidth(250);
			else
				column.setPreferredWidth(80);

		}
	}

	protected Action createLocationOpenAction(String menuItemName,
			File clickedPath, JPanel panel) {
		return new AbstractAction(menuItemName) {
			private static final long serialVersionUID = 3490828000264984302L;

			public void actionPerformed(ActionEvent e) {
				openOrMarkPath(clickedPath, panel);
			}
		};
	}

	protected Action scanAction(File clickedPath, JPanel panel,
			JButton refreshBtn) {

		if (clickedPath.isFile())
			return null;

		return new AbstractAction("Scan this path") {

			private static final long serialVersionUID = 3652552281097715828L;

			public void actionPerformed(ActionEvent e) {
				if (!clickedPath.exists()) {
					JOptionPane.showMessageDialog(panel,
							"The selected item no longer exists.", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				String[] options = { "Proceed", "Cancel" };
				String message = "The follwoing path will be scanned. Proceed?\n\n"
						+ clickedPath.getAbsolutePath() + "\n";

				int choice = JOptionPane.showOptionDialog(panel, message,
						"Confirm Scan", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, options, null);

				if (JOptionPane.OK_OPTION == choice) {
					InputPanel.setSelectedPath(clickedPath.getAbsolutePath());
					refreshBtn.doClick();
				}
			}
		};
	}

	protected void openOrMarkPath(File location, JPanel panel) {
		if (!location.exists()) {
			JOptionPane.showMessageDialog(panel,
					"The selected item no longer exists.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (location.getParent() != null && location.isHidden()) {
			JOptionPane.showMessageDialog(panel,
					"The selected item is hidden.", "Warning",
					JOptionPane.WARNING_MESSAGE);
		}

		String command = location.isFile() ? "explorer /select," : "explorer ";
		try {
			Runtime.getRuntime().exec(command + location.getAbsolutePath());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	protected void sortBySize(SortOrder sortOrder, JScrollPane tableScrollpane) {
		JTable table = (JTable) tableScrollpane.getViewport().getComponent(0);
		DefaultRowSorter<?, ?> sorter = ((DefaultRowSorter<?, ?>) table
				.getRowSorter());

		int totalRow = table.getRowCount();
		TableModel model = table.getModel();
		for (int i = 0; i < totalRow; i++) {
			double dSize = Double.valueOf((String) model.getValueAt(i, 3));
			String unit = (String) model.getValueAt(i, 4);
			long bytes = Resources.getGeneralizedSize(dSize, unit);
			model.setValueAt(bytes, i, 3);
		}
		ArrayList<SortKey> list = new ArrayList<>();
		SortKey sortedRow = new RowSorter.SortKey(3, sortOrder);
		list.add(sortedRow);
		sorter.setSortKeys(list);
		sorter.sort();

		for (int i = 0; i < totalRow; i++) {
			long bytes = (Long) model.getValueAt(i, 3);
			String[] sizeAndUnit = Resources.getNormalizedSize(bytes);
			model.setValueAt(sizeAndUnit[0], i, 3);
		}
		table.validate();
		table.repaint();
	}

	protected TreePath getTreePath(DefaultMutableTreeNode node, String path) {

		File f;
		DefaultMutableTreeNode foundNode;
		Enumeration<?> nodeDfs = node.depthFirstEnumeration();

		while (nodeDfs.hasMoreElements()) {
			foundNode = (DefaultMutableTreeNode) nodeDfs.nextElement();
			f = (File) foundNode.getUserObject();
			if (path.equalsIgnoreCase(f.getAbsolutePath()))
				return new TreePath(foundNode.getPath());
		}
		return null;
	}
}
