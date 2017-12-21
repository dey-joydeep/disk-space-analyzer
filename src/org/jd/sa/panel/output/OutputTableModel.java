package org.jd.sa.panel.output;

import javax.swing.JTable;
import javax.swing.table.TableModel;

public class OutputTableModel extends JTable {

	private static final long serialVersionUID = 8924482390955992511L;

	public OutputTableModel(TableModel model) {
		super(model);
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

}
