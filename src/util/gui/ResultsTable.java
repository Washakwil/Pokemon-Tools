package util.gui;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class ResultsTable extends JTable {
	
	private static final long serialVersionUID = 2796466532300201060L;
	private TableRowSorter<TableModel> sorter;
	
	public ResultsTable(Object[] columnIdentifiers) {
		this(columnIdentifiers, 0);
	}
	
	public ResultsTable(Object[] columnIdentifiers, int length) {
		super(new ResultsTableModel(columnIdentifiers, length));
		sorter = new TableRowSorter<TableModel>(getModel());
		setRowSorter(sorter);
		setDefaultRenderer(Object.class, new ResultsTableCellRenderer());
	}

	private void showAllColumns() {
		for(int i = 0; i < getModel().getColumnCount(); i++) {
			TableColumn column = getColumnModel().getColumn(i);
			if(column.getWidth() == 0) {
				column.setMinWidth(15);
				column.setMaxWidth(Integer.MAX_VALUE);
				column.setPreferredWidth(75);
				column.setResizable(true);
			}
		}
	}
	
	@Override
	public ResultsTableModel getModel() {
		return (ResultsTableModel) super.getModel();
	}
	
	public TableRowSorter<TableModel> getSorter() {
		return sorter;
	}
	
	public void setSortable(int column, boolean sortable) {
		sorter.setSortable(column, sortable);
	}

	public void hideColumns(int beginIndex, int count) {
		for(int i = 0; i < count; i++) {
			TableColumn column = getColumnModel().getColumn(beginIndex + i);
			column.setMinWidth(0);
			column.setMaxWidth(0);
			column.setPreferredWidth(0);
			column.setResizable(false);
		}
	}

	public void reset() {
		getModel().setRowCount(0);
		showAllColumns();
	}
	
}
