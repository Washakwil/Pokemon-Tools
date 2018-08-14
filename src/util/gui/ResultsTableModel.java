package util.gui;

import javax.swing.table.DefaultTableModel;

public class ResultsTableModel extends DefaultTableModel {

	private static final long serialVersionUID = -5267357255679160910L;

	public ResultsTableModel(Object[] columnIdentifiers, int length) {
		super(columnIdentifiers, length);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if(getRowCount() > 0) {
			Object value = getValueAt(0, columnIndex);
			if(value != null) return value.getClass();
		}
		return Integer.class;
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
}
