package rng.gen4.id.cutecharm;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import main.GuiMainMenu;
import pokemon.Ability;
import pokemon.Gender;
import pokemon.GenderRatio;
import pokemon.Nature;
import util.Util;
import util.gui.ResultsTable;
import util.gui.SwingUtil;
import util.gui.UnsignedIntTextField;

public class GuiGen4CCIDs extends JPanel implements ActionListener, PopupMenuListener {

	private static final long serialVersionUID = -5061857840448375473L;
	private Gen4CCEffect ccEffect;
	private GuiMainMenu guiMain;
	private JComboBox<Nature> natureSelection;
	private JComboBox<Gender> ccGenderSelection;
	private JComboBox<GenderRatio> genderRatioSelection;
	private UnsignedIntTextField tidInput, sidInput;
	private JButton calculatePID, calculateIDEffect, searchSeeds;
	private ResultsTable possibleEncounters;
	private JLabel lgenderRatio;
	private JPopupMenu searchSeedsMenu;
	private JMenuItem searchSeedsMenuItem;
	
	public GuiGen4CCIDs(GuiMainMenu guiMain) {
		super();
		
		this.guiMain = guiMain;

		ccEffect = new Gen4CCEffect(this);
		
		natureSelection = new JComboBox<Nature>(Nature.values());
		genderRatioSelection = new JComboBox<GenderRatio>(GenderRatio.getRandomGenderRatios(4));
		genderRatioSelection.setSelectedItem(GenderRatio.MALE1_FEMALE1);
		ccGenderSelection = new JComboBox<Gender>(Gender.getGenders());
		ccGenderSelection.addActionListener(this);
		
		tidInput = new UnsignedIntTextField(0, 0xFFFF, 0, 5);
		sidInput = new UnsignedIntTextField(0, 0xFFFF, 0, 5);
		
		calculatePID = new JButton("Calculate");
		calculatePID.addActionListener(this);
		calculateIDEffect = new JButton("Calculate");
		calculateIDEffect.addActionListener(this);
		searchSeeds = new JButton("Search Seeds...");
		searchSeeds.addActionListener(this);
		
		possibleEncounters = new ResultsTable(new Object[] {"Cute Charmer", "Gender ratio", "PID (Hex)", "Nature", "Ability"});
		
		lgenderRatio = new JLabel("Gender ratio of the shinies:", JLabel.RIGHT);

		searchSeedsMenuItem = new JMenuItem("Search Seeds...");
		searchSeedsMenuItem.addActionListener(this);
		searchSeedsMenu = new JPopupMenu();
		searchSeedsMenu.addPopupMenuListener(this);
		searchSeedsMenu.add(searchSeedsMenuItem);
		possibleEncounters.setComponentPopupMenu(searchSeedsMenu);
		
		setLayout(new BorderLayout(10, 10));
		JPanel input = new JPanel(new GridLayout(1, 2, 10, 10));
		JPanel pidSearchInput = new JPanel(new GridLayout(4, 2, 10, 10));
		pidSearchInput.setBorder(BorderFactory.createTitledBorder("Search ID/SID"));
		JPanel idEffectInput = new JPanel(new GridLayout(4, 2, 10, 10));
		idEffectInput.setBorder(BorderFactory.createTitledBorder("Search Cute Charm Shinies"));

		pidSearchInput.add(new JLabel("Gender of your Cute Charmer:", JLabel.RIGHT));
		pidSearchInput.add(ccGenderSelection);
		pidSearchInput.add(lgenderRatio);
		pidSearchInput.add(genderRatioSelection);
		pidSearchInput.add(new JLabel("Nature for shinies:", JLabel.RIGHT));
		pidSearchInput.add(natureSelection);
		pidSearchInput.add(calculatePID);

		idEffectInput.add(new JLabel("Trainer-ID:", JLabel.RIGHT));
		idEffectInput.add(tidInput);
		idEffectInput.add(new JLabel("Secret-ID:", JLabel.RIGHT));
		idEffectInput.add(sidInput);
		SwingUtil.addEmptyLabels(idEffectInput, 2);
		idEffectInput.add(calculateIDEffect);
		idEffectInput.add(searchSeeds);
		
		input.add(pidSearchInput);
		input.add(idEffectInput);
		
		add(input, BorderLayout.NORTH);
		add(new JScrollPane(possibleEncounters), BorderLayout.CENTER);
		
		updateEnabling();
	}

	private void updateEnabling() {
		boolean female = ccGenderSelection.getSelectedItem() == Gender.FEMALE;
		lgenderRatio.setEnabled(female);
		genderRatioSelection.setEnabled(female);
	}

	public void addToTable(Gender ccGender, GenderRatio genderRatio, int pid) {
		if(genderRatio == null) possibleEncounters.hideColumns(1, 1);
		possibleEncounters.getModel().addRow(new Object[] {
				ccGender,
				genderRatio,
				Util.add0s(Integer.toHexString(pid), 8),
				Nature.getNature(pid),
				Ability.getAbility(pid)
		});
	}
	
	public void resetTable() {
		possibleEncounters.reset();
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		
		Object source = ae.getSource();
		
		if(source == ccGenderSelection) {
			
			updateEnabling();
			
		} else if(source == calculateIDEffect) {
			
			int tid = tidInput.getValue();
			int sid = sidInput.getValue();
			
			resetTable();
			
			ccEffect.idEffectSearch(tid, sid);
			
		} else if(source  == calculatePID) {
			
			Gender ccGender = (Gender) ccGenderSelection.getSelectedItem();
			GenderRatio genderRatio = (GenderRatio) genderRatioSelection.getSelectedItem();
			Nature nature = (Nature) natureSelection.getSelectedItem();
			
			resetTable();
			
			ccEffect.pidSearch(ccGender, genderRatio, nature);
			
		} else if(source == searchSeedsMenuItem) {

			int selectedRow = possibleEncounters.getSelectedRow();
			String pid = (String) possibleEncounters.getValueAt(selectedRow, 2);
			guiMain.openIDRNG(null, null, pid, null);
			
		} else if(source == searchSeeds) {

			String tid = tidInput.getText();
			String sid = sidInput.getText();
			guiMain.openIDRNG(tid, sid, null, null);
			
		}
	}

	@Override
	public void popupMenuCanceled(PopupMenuEvent pme) {}

	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent pme) {}

	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent pme) {
		
		if(pme.getSource() == searchSeedsMenu) {
			
			Point point = possibleEncounters.getMousePosition();
			int row = possibleEncounters.rowAtPoint(point);
			int column = possibleEncounters.columnAtPoint(point);
			if(!possibleEncounters.isRowSelected(row)) {
				possibleEncounters.changeSelection(row, column, false, false);
			}
			
		}		
	}
	
}
