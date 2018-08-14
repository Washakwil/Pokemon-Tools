package rng.gen4.id;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import main.GuiMainMenu;
import rng.Search;
import util.Util;
import util.gui.ResultsTable;
import util.gui.SwingUtil;
import util.gui.UnsignedIntTextField;

public class GuiGen4IDRNG extends JPanel implements ActionListener, PopupMenuListener {

	private static final long serialVersionUID = 4566981103549444525L;
	private Calendar calendar;
	private Gen4IDRNG idRng;
	private Search idRngSearch, seedRngSearch;
	private GuiMainMenu guiMain;
	private JCheckBox tidSelection, sidSelection, pidSelection, tsvSelection;
	private UnsignedIntTextField tidInput, sidInput, pidInput, tsvInput, minuteInput, hourInput, dayInput, monthInput,
	idSearchYearInput, seedSearchYearInput, seedToIDYearInput, seedInput, obtainedTidInput, idSearchMinDelayInput,
	idSearchMaxDelayInput, seedSearchMinDelayInput, seedSearchMaxDelayInput;
	private ResultsTable results;
	private JButton startIDSearch, startSeedToID, startSeedSearch, cancelIDSearch, cancelSeedSearch;
	private JProgressBar progress;
	private JPopupMenu seedToTimeMenu;
	private JMenuItem seedToTimeMenuItem;
	private JPanel idSearch, seedSearch, seedToID;
	
	public GuiGen4IDRNG(GuiMainMenu guiMain) {
		super();

		this.guiMain = guiMain;

		calendar = Calendar.getInstance();
		
		idRng = new Gen4IDRNG();
		
		tidSelection = new JCheckBox("Search for Trainer-ID:", true);
		tidSelection.setHorizontalAlignment(JCheckBox.RIGHT);
		tidSelection.addActionListener(this);
		tidSelection.setSelected(true);
		sidSelection = new JCheckBox("Search for Secret-ID:");
		sidSelection.setHorizontalAlignment(JCheckBox.RIGHT);
		sidSelection.addActionListener(this);
		pidSelection = new JCheckBox("Search for Shiny-PID (Hex):");
		pidSelection.setHorizontalAlignment(JCheckBox.RIGHT);
		pidSelection.addActionListener(this);
		tsvSelection = new JCheckBox("Search for TSV:");
		tsvSelection.setHorizontalAlignment(JCheckBox.RIGHT);
		tsvSelection.addActionListener(this);
		
		tidInput = new UnsignedIntTextField(0, 0xFFFF, 0, 5);
		sidInput = new UnsignedIntTextField(0, 0xFFFF, 0, 5);
		pidInput = new UnsignedIntTextField(0, 0xFFFFFFFF, 0, 8);
		tsvInput = new UnsignedIntTextField(0, 0x1FFF, 0, 4);
		
		minuteInput = new UnsignedIntTextField(0, 59, calendar.get(Calendar.MINUTE));
		hourInput = new UnsignedIntTextField(0, 59, calendar.get(Calendar.HOUR));
		dayInput = new UnsignedIntTextField(1, 31, calendar.get(Calendar.DAY_OF_MONTH));
		monthInput = new UnsignedIntTextField(1, 12, calendar.get(Calendar.MONTH) + 1);
		int year = calendar.get(Calendar.YEAR);
		idSearchYearInput = new UnsignedIntTextField(2000, 2099, year);
		seedSearchYearInput = new UnsignedIntTextField(2000, 2099, year);
		seedToIDYearInput = new UnsignedIntTextField(2000, 2099, year);
		seedInput = new UnsignedIntTextField(0, 0xFFFFFFFF, 0, 8);
		obtainedTidInput = new UnsignedIntTextField(0, 0xFFFF, 0, 5);
		idSearchMinDelayInput = new UnsignedIntTextField(0, 50000, 5000);
		idSearchMaxDelayInput = new UnsignedIntTextField(0, 50000, 6000);
		seedSearchMinDelayInput = new UnsignedIntTextField(0, 50000, 5000);
		seedSearchMaxDelayInput = new UnsignedIntTextField(0, 50000, 6000);
		
		results = new ResultsTable(new Object[] {"Seed (Hex)", "Trainer-ID", "Secret-ID", "Delay", "Seconds"});
		
		startIDSearch = new JButton("Search");
		startIDSearch.addActionListener(this);
		startSeedSearch = new JButton("Search");
		startSeedSearch.addActionListener(this);
		startSeedToID = new JButton("Search");
		startSeedToID.addActionListener(this);
		cancelIDSearch = new JButton("Cancel");
		cancelIDSearch.addActionListener(this);
		cancelIDSearch.setEnabled(false);
		cancelSeedSearch = new JButton("Cancel");
		cancelSeedSearch.addActionListener(this);
		cancelSeedSearch.setEnabled(false);
		
		progress = new JProgressBar();
		progress.setStringPainted(true);

		seedToTimeMenuItem = new JMenuItem("Generate Times...");
		seedToTimeMenuItem.addActionListener(this);
		seedToTimeMenu = new JPopupMenu();
		seedToTimeMenu.addPopupMenuListener(this);
		seedToTimeMenu.add(seedToTimeMenuItem);
		results.setComponentPopupMenu(seedToTimeMenu);
		
		setLayout(new BorderLayout(10, 10));
		JPanel input = new JPanel(new GridLayout(1, 3, 10, 10));
		idSearch = new JPanel(new GridLayout(9, 2, 10, 10));
		idSearch.setBorder(BorderFactory.createTitledBorder("ID/SID/Shiny PID Search"));
		seedSearch = new JPanel(new GridLayout(9, 2, 10, 10));
		seedSearch.setBorder(BorderFactory.createTitledBorder("Seed Search"));
		seedToID = new JPanel(new GridLayout(9, 2, 10, 10));
		seedToID.setBorder(BorderFactory.createTitledBorder("Seed To ID"));
		
		idSearch.add(tidSelection);
		idSearch.add(tidInput);
		idSearch.add(sidSelection);
		idSearch.add(sidInput);
		idSearch.add(pidSelection);
		idSearch.add(pidInput);
		idSearch.add(tsvSelection);
		idSearch.add(tsvInput);
		idSearch.add(new JLabel("Year:", JLabel.RIGHT));
		idSearch.add(idSearchYearInput);
		idSearch.add(new JLabel("Minimum Delay:", JLabel.RIGHT));
		idSearch.add(idSearchMinDelayInput);
		idSearch.add(new JLabel("Maximum Delay:", JLabel.RIGHT));
		idSearch.add(idSearchMaxDelayInput);
		SwingUtil.addEmptyLabels(idSearch, 2);
		idSearch.add(startIDSearch);
		idSearch.add(cancelIDSearch);
		
		seedSearch.add(new JLabel("Obtained Trainer-ID", JLabel.RIGHT));
		seedSearch.add(obtainedTidInput);
		seedSearch.add(new JLabel("Year:", JLabel.RIGHT));
		seedSearch.add(seedSearchYearInput);
		seedSearch.add(new JLabel("Month:", JLabel.RIGHT));
		seedSearch.add(monthInput);
		seedSearch.add(new JLabel("Day:", JLabel.RIGHT));
		seedSearch.add(dayInput);
		seedSearch.add(new JLabel("Hour:", JLabel.RIGHT));
		seedSearch.add(hourInput);
		seedSearch.add(new JLabel("Minute:", JLabel.RIGHT));
		seedSearch.add(minuteInput);
		seedSearch.add(new JLabel("Minimum Delay:", JLabel.RIGHT));
		seedSearch.add(seedSearchMinDelayInput);
		seedSearch.add(new JLabel("Maximum Delay:", JLabel.RIGHT));
		seedSearch.add(seedSearchMaxDelayInput);
		seedSearch.add(startSeedSearch);
		seedSearch.add(cancelSeedSearch);
		
		seedToID.add(new JLabel("Seed (Hex):", JLabel.RIGHT));
		seedToID.add(seedInput);
		seedToID.add(new JLabel("Year:", JLabel.RIGHT));
		seedToID.add(seedToIDYearInput);
		SwingUtil.addEmptyLabels(seedToID, 12);
		seedToID.add(startSeedToID);
		
		input.add(idSearch);
		input.add(seedSearch);
		input.add(seedToID);
		
		add(input, BorderLayout.NORTH);
		add(new JScrollPane(results), BorderLayout.CENTER);
		add(progress, BorderLayout.SOUTH);
		
		update();
		setProgress(0);
	}

	public void update() {
		tidInput.setEnabled(tidSelection.isSelected());
		sidInput.setEnabled(sidSelection.isSelected());
		pidInput.setEnabled(pidSelection.isSelected());
		tsvInput.setEnabled(tsvSelection.isSelected());
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		
		Object source = ae.getSource();
		
		if(source instanceof JCheckBox) {
			
			update();
			
		} else if(source == startIDSearch) {
			
			int minDelay = idSearchMinDelayInput.getValue();
			idSearchMaxDelayInput.setMin(minDelay);
			idSearchMaxDelayInput.setStandard(minDelay + 1000);
			int maxDelay = idSearchMaxDelayInput.getValue();
			int year = idSearchYearInput.getValue();
			int tid = -1, sid = -1, tsv = -1;
			if(tidSelection.isSelected()) tid = tidInput.getValue();
			if(sidSelection.isSelected()) sid = sidInput.getValue();
			if(pidSelection.isSelected()) {
				int pid = pidInput.getHexValue();
				tsv = (pid >>> 16 ^ pid & 0xFFFF) >>> 3;
			}
			if(tsvSelection.isSelected()) tsv = tsvInput.getHexValue();
			startIDSearch.setEnabled(false);
			SwingUtil.enableJPanel(seedSearch, false);
			SwingUtil.enableJPanel(seedToID, false);
			cancelIDSearch.setEnabled(true);
			idRngSearch = idRng.new IDSearch(this, year, minDelay, maxDelay, new Gen4IDFilter(tid, sid, tsv));
			
			results.reset();
			results.hideColumns(4, 1);
			
			idRngSearch.start();
			
		} else if(source == startSeedSearch) {
			
			int minDelay = seedSearchMinDelayInput.getValue();
			seedSearchMaxDelayInput.setMin(minDelay);
			seedSearchMaxDelayInput.setStandard(minDelay + 1000);
			int maxDelay = seedSearchMaxDelayInput.getValue();
			int otid = obtainedTidInput.getValue();
			int year = seedSearchYearInput.getValue();
			int month = monthInput.getValue();
			int day = dayInput.getValue();
			int hour = hourInput.getValue();
			int minute = minuteInput.getValue();
			startSeedSearch.setEnabled(false);
			SwingUtil.enableJPanel(idSearch, false);
			SwingUtil.enableJPanel(seedToID, false);
			cancelSeedSearch.setEnabled(true);
			seedRngSearch = idRng.new SeedSearch(this, otid, year, month, day, hour, minute, minDelay, maxDelay);
			
			results.reset();
			
			seedRngSearch.start();
			
		} else if(source == startSeedToID) {
			
			results.reset();
			results.hideColumns(4, 1);
			int year = seedToIDYearInput.getValue();
			int seed = seedInput.getHexValue();
			int[] ids = idRng.getIDs(seed);
			int delay = (seed & 0xFFFF) + 2000 - year;
			addToTable(seed, ids[0], ids[1], delay , 0);
			
		} else if(source == seedToTimeMenuItem) {

			int selectedRow = results.getSelectedRow();
			int seed = Integer.parseUnsignedInt((String) results.getValueAt(selectedRow, 0), 16);
			int delay = (int) results.getValueAt(selectedRow, 3);
			int year = (seed & 0xFFFF) + 2000 - delay;
			guiMain.openSeedToTime(seed, year);
			
		} else if(source == cancelIDSearch || source == cancelSeedSearch) {
			
			cancelSearch();
			
		}
	}

	public void addToTable(int seed, int tid, int sid, int delay, int seconds) {
		results.getModel().addRow(new Object[] {
				Util.add0s(Integer.toHexString(seed), 8),
				Util.add0s(tid, 5),
				Util.add0s(sid, 5),
				delay,
				seconds
		});
	}
	
	public void setProgress(int percent) {
		progress.setValue(percent);
	}

	public void cancelSearch() {
		SwingUtil.enableJPanel(idSearch, true);
		SwingUtil.enableJPanel(seedSearch, true);
		SwingUtil.enableJPanel(seedToID, true);
		update();
		cancelIDSearch.setEnabled(false);
		cancelSeedSearch.setEnabled(false);
		if(idRngSearch != null) idRngSearch.cancel();
		if(seedRngSearch != null) seedRngSearch.cancel();
	}

	@Override
	public void popupMenuCanceled(PopupMenuEvent pme) {}

	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent pme) {}

	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent pme) {
		if(pme.getSource() == seedToTimeMenu) {
			Point point = results.getMousePosition();
			int row = results.rowAtPoint(point);
			int column = results.columnAtPoint(point);
			if(!results.isRowSelected(row)) {
				results.changeSelection(row, column, false, false);
			}
		}		
	}

	public void setIDSearchInput(String tid, String sid, String pid, String tsv) {
		boolean tidSelected = tid != null;
		tidSelection.setSelected(tidSelected);
		if(tidSelected) tidInput.setText(tid);
		boolean sidSelected = sid != null;
		sidSelection.setSelected(sidSelected);
		if(sidSelected) sidInput.setText(sid);
		boolean pidSelected = pid != null;
		pidSelection.setSelected(pidSelected);
		if(pidSelected) pidInput.setText(pid);
		boolean tsvSelected = tsv != null;
		pidSelection.setSelected(tsvSelected);
		if(tsvSelected) pidInput.setText(tsv);
		update();
	}
	
}
