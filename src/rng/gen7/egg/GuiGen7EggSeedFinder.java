package rng.gen7.egg;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import main.GuiMainMenu;
import pokemon.Nature;
import rng.RNGState;
import rng.gen7.egg.Gen7EggSeedFinder.NatureSearch;
import util.Util;
import util.gui.ResultsTable;

public class GuiGen7EggSeedFinder extends JPanel implements ActionListener, PopupMenuListener {

	private static final long serialVersionUID = -1878852860177852376L;
	
	private GuiMainMenu guiMain;
	private Gen7EggSeedFinder eggSeedFinder;
	private NatureSearch[] natureSearch;
	private long seedCount;
	private JTextField natureText;
	private JTextArea magikarpInput;
	private JComboBox<Nature> natureSelection;
	private JButton startNatureSearch, startMagikarpSearch, cancelNatureSearch, natureReset;
	private JCheckBox shinyCharm;
	private JProgressBar progress;
	private ResultsTable results;
	private JMenuItem setCurrent;
	private JPopupMenu tableMenu;

	public GuiGen7EggSeedFinder(GuiMainMenu guiMain) {
		
		this.guiMain = guiMain;
		
		eggSeedFinder = new Gen7EggSeedFinder();
		
		natureText = new JTextField();
		
		magikarpInput = new JTextArea();

		natureSelection = new JComboBox<Nature>(Nature.values());
		natureSelection.addActionListener(this);
		
		startNatureSearch = new JButton("Search");
		startNatureSearch.addActionListener(this);
		cancelNatureSearch = new JButton("Cancel");
		cancelNatureSearch.addActionListener(this);
		cancelNatureSearch.setEnabled(false);
		startMagikarpSearch = new JButton("Search");
		startMagikarpSearch.addActionListener(this);
		natureReset = new JButton("Clear");
		natureReset.addActionListener(this);
		
		shinyCharm = new JCheckBox("Shiny Charm");
		shinyCharm.setHorizontalAlignment(JCheckBox.CENTER);
		
		progress = new JProgressBar();
		progress.setStringPainted(true);
		
		results = new ResultsTable(new Object[] {"Seed", "status[0]", "status[1]", "status[2]", "status[3]"});
		
		setCurrent = new JMenuItem("Set as Current Status");
		setCurrent.addActionListener(this);
		tableMenu = new JPopupMenu();
		tableMenu.add(setCurrent);
		tableMenu.addPopupMenuListener(this);
		results.setComponentPopupMenu(tableMenu);
		
		setLayout(new BorderLayout(10, 10));
		JPanel input = new JPanel(new GridLayout(1, 2, 10, 10));
		JPanel natureSearch = new JPanel(new GridLayout(2, 4, 10, 10));
		natureSearch.setBorder(BorderFactory.createTitledBorder("8 Eggs (Only new save)"));
		JPanel magikarpSearch = new JPanel(new BorderLayout(10, 10));
		magikarpSearch.setBorder(BorderFactory.createTitledBorder("127 Eggs"));
		
		natureSearch.add(new JLabel("Nature List:", JLabel.RIGHT));
		natureSearch.add(natureText);
		natureSearch.add(natureSelection);
		natureSearch.add(natureReset);
		natureSearch.add(new JLabel());
		natureSearch.add(shinyCharm);
		natureSearch.add(cancelNatureSearch);
		natureSearch.add(startNatureSearch);
		
		magikarpSearch.add(new JScrollPane(magikarpInput), BorderLayout.CENTER);
		magikarpSearch.add(startMagikarpSearch, BorderLayout.EAST);
		
		input.add(natureSearch);
		input.add(magikarpSearch);
		
		add(input, BorderLayout.NORTH);
		add(new JScrollPane(results), BorderLayout.CENTER);
		add(progress, BorderLayout.SOUTH);
		
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		
		Object source = ae.getSource();
		
		if(source == setCurrent) {
			
			int selectedRow = results.getSelectedRow();
			int column = 1;
			int[] status = new int[4];
			for(int i = 0; i < 4; i++) {
				status[i] = Integer.parseUnsignedInt(results.getValueAt(selectedRow, column + i).toString(), 16);
			}
			guiMain.setStatus(new RNGState(status));
			
		} else if(source == natureSelection) {
			
			Nature nature = (Nature) natureSelection.getSelectedItem();
			String text = natureText.getText();
			if(text.length() > 0) text += ", ";
			text += nature;
			natureText.setText(text);
			
		} else if(source == natureReset) {
			
			natureText.setText("");
			
		} else if(source == startNatureSearch) {
			
			Nature[] natures = Nature.getNatures(natureText.getText());
			if(natures.length == 8) {
				
				startNatureSearch.setEnabled(false);
				startMagikarpSearch.setEnabled(false);
				cancelNatureSearch.setEnabled(true);
				results.reset();
				seedCount = 0;
				natureSearch = new NatureSearch[NatureSearch.THREAD_COUNT];
				int seedDifference = (int) (0x100000000l / NatureSearch.THREAD_COUNT);
				int min = 0;
				int max = seedDifference - 1;
				for(int i = 0; i < NatureSearch.THREAD_COUNT; i++) {
					natureSearch[i] = eggSeedFinder.new NatureSearch(this, natures, shinyCharm.isSelected(), min, max);
					natureSearch[i].start();
					min += seedDifference;
					max += seedDifference;
				}
				
			} else {
				
				JOptionPane.showMessageDialog(this, "Please input 8 natures (You have " + natures.length + ")", "Wrong number of Natures", JOptionPane.ERROR_MESSAGE);
			
			}
			
		} else if(source == cancelNatureSearch) {
			
				cancelSearch();
			
		} else if(source == startMagikarpSearch) {
			
			String input = magikarpInput.getText();
			input = input.replaceAll("[^mf]", "");
			if(input.length() == 127) {
				results.reset();
				results.hideColumns(0, 1);
				eggSeedFinder.magikarp127(this, input);
			} else {
				JOptionPane.showMessageDialog(this, "Please input 127 'm' and 'f' (You have " + input.length() + ")", "Wrong number of Characters", JOptionPane.ERROR_MESSAGE);
			}
			
		}
		
	}

	@Override
	public void popupMenuCanceled(PopupMenuEvent pme) {}

	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent pme) {}

	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent pme) {
		
		if(pme.getSource() == tableMenu) {
			
			Point point = results.getMousePosition();
			int row = results.rowAtPoint(point);
			int column = results.columnAtPoint(point);
			if(!results.isRowSelected(row)) {
				results.changeSelection(row, column, false, false);
			}
			
		}		
	}

	public void cancelSearch() {
		for(int i = 0; i < this.natureSearch.length; i++) {
			cancelSearch(natureSearch[i]);
		}
	}
	
	public synchronized void seedChecked() {
		seedCount++;
		progress.setValue((int) (seedCount * 100 / 0x100000000l));
	}

	public synchronized void cancelSearch(NatureSearch natureSearch) {
		natureSearch.cancel();
		for(int i = 0; i < this.natureSearch.length; i++) {
			if(!this.natureSearch[i].isCanceled()) return;
		}
		startNatureSearch.setEnabled(true);
		startMagikarpSearch.setEnabled(true);
		cancelNatureSearch.setEnabled(false);
	}

	public synchronized void addToTable(int seed, RNGState status) {
		Object[] data = new Object[results.getModel().getColumnCount()];
		int index = 0;
		data[index++] = Util.add0s(Integer.toHexString(seed), 8);
		for(int i = 0; i < 4; i++) {
			data[index++] = Util.add0s(Integer.toHexString(status.get(i)), 8);
		}
		results.getModel().addRow(data);
	}

}
