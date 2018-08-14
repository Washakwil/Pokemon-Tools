package rng.gen7.egg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import main.GuiMainMenu;
import pokemon.Ability;
import pokemon.Gender;
import pokemon.GenderRatio;
import pokemon.Nature;
import pokemon.PokemonType;
import rng.RNGState;
import rng.TinyMT;
import rng.gen7.egg.Gen7EggRNG.Result;
import util.Util;
import util.gui.DeselectableButtonGroup;
import util.gui.ResultsTable;
import util.gui.SwingUtil;
import util.gui.UnsignedIntTextField;

public class GuiGen7EggRNG extends JPanel implements ActionListener, PopupMenuListener {
	
	private static final long serialVersionUID = 6888700264438818065L;
	
	private GuiMainMenu guiMain;
	private UnsignedIntTextField minFrame, maxFrame, minEgg, maxEgg, targetFrame;
	private JTextField filterNatureText, filterHPTypeText;
	private UnsignedIntTextField[] status, maleIVs, femaleIVs, filterMinIVs, filterMaxIVs;
	private JCheckBox maleDitto, femaleDitto, homogeneous, masuda, nidoran, shinyOnly, abilityFilterSelection,
			genderFilterSelection, ballFilterSelection, showRNGData, enableFilters;
	private JComboBox<Nature> maleNature, femaleNature, filterNatureSelection;
	private JComboBox<PokemonType> filterHPTypeSelection;
	private JComboBox<Gen7BreedingItem> maleItem, femaleItem;
	private JComboBox<Ability> maleAbility, femaleAbility, abilityFilter;
	private JComboBox<GenderRatio> genderRatio;
	private JComboBox<Gender> genderFilter, ballFilter;
	private JButton calculate, filterNatureReset, filterHPTypeReset;
	private JRadioButton frameRange, eggRange, shortestPath;
	private ResultsTable results;
	private JMenuItem setTarget, setCurrent, setCurrentAfter;
	private JPopupMenu tableMenu;
	private Gen7EggRNG eggRNG;

	public GuiGen7EggRNG(GuiMainMenu guiMain) {
		
		final String[] STAT_STRINGS = new String[] {"HP", "Atk", "Def", "SpA", "SpD", "Spe"};

		this.guiMain = guiMain;
		
		minFrame = new UnsignedIntTextField(0, 100000, 0);
		maxFrame = new UnsignedIntTextField(0, 100000, 10000);
		minEgg = new UnsignedIntTextField(1, 1000, 1);
		minEgg.setEnabled(false);
		maxEgg = new UnsignedIntTextField(1, 1000, 100);
		maxEgg.setEnabled(false);
		targetFrame = new UnsignedIntTextField(0, 100000, 1000);
		targetFrame.setEnabled(false);
		
		filterNatureText = new JTextField();
		filterHPTypeText = new JTextField();
		
		status = new UnsignedIntTextField[4];
		for(int i = 0; i < 4; i++) {
			status[i] = new UnsignedIntTextField(0, 0xFFFFFFFF, 0, 8);
		}
		maleIVs = new UnsignedIntTextField[6];
		femaleIVs = new UnsignedIntTextField[6];
		for(int i = 0; i < 6; i++) {
			maleIVs[i] = new UnsignedIntTextField(0, 0x1F, 0x1F);
			maleIVs[i].setForeground(Color.BLUE);
			femaleIVs[i] = new UnsignedIntTextField(0, 0x1F, 0x1F);
			femaleIVs[i].setForeground(Color.RED);
		}
		
		filterMinIVs = new UnsignedIntTextField[6];
		filterMaxIVs = new UnsignedIntTextField[6];
		for(int i = 0; i < 6; i++) {
			filterMinIVs[i] = new UnsignedIntTextField(0, 0x1F, 0);
			filterMaxIVs[i] = new UnsignedIntTextField(0, 0x1F, 0x1F);
		}
		
		maleDitto = new JCheckBox(Gender.MALE.toString());
		maleDitto.setHorizontalAlignment(JCheckBox.CENTER);
		maleDitto.setForeground(Color.BLUE);
		femaleDitto = new JCheckBox(Gender.FEMALE.toString());
		femaleDitto.setHorizontalAlignment(JCheckBox.CENTER);
		femaleDitto.setForeground(Color.RED);
		homogeneous = new JCheckBox("Same Dex Number");
		homogeneous.setHorizontalAlignment(JCheckBox.CENTER);
		new DeselectableButtonGroup(maleDitto, femaleDitto, homogeneous);
		
		masuda = new JCheckBox("Masuda Method");
		masuda.setHorizontalAlignment(JCheckBox.CENTER);
		nidoran = new JCheckBox("Nidoran");
		nidoran.setHorizontalAlignment(JCheckBox.CENTER);
		shinyOnly = new JCheckBox("Shiny Only");
		shinyOnly.setHorizontalAlignment(JCheckBox.CENTER);
		abilityFilterSelection = new JCheckBox("Ability:");
		abilityFilterSelection.setHorizontalAlignment(JCheckBox.RIGHT);
		abilityFilterSelection.addActionListener(this);
		genderFilterSelection = new JCheckBox("Gender:");
		genderFilterSelection.setHorizontalAlignment(JCheckBox.RIGHT);
		genderFilterSelection.addActionListener(this);
		ballFilterSelection = new JCheckBox("Ball:");
		ballFilterSelection.setHorizontalAlignment(JCheckBox.RIGHT);
		ballFilterSelection.addActionListener(this);
		showRNGData = new JCheckBox("Show RNG Data");
		showRNGData.setHorizontalAlignment(JCheckBox.RIGHT);
		enableFilters = new JCheckBox("Enable Filters", true);
		enableFilters.setHorizontalAlignment(JCheckBox.CENTER);
		
		maleNature = new JComboBox<Nature>(Nature.values());
		femaleNature = new JComboBox<Nature>(Nature.values());
		filterNatureSelection = new JComboBox<Nature>(Nature.values());
		filterNatureSelection.addActionListener(this);
		filterHPTypeSelection = new JComboBox<PokemonType>(PokemonType.getHPTypes());
		filterHPTypeSelection.addActionListener(this);
		maleItem = new JComboBox<Gen7BreedingItem>(Gen7BreedingItem.values());
		femaleItem = new JComboBox<Gen7BreedingItem>(Gen7BreedingItem.values());
		maleAbility = new JComboBox<Ability>(Ability.values());
		femaleAbility = new JComboBox<Ability>(Ability.values());
		abilityFilter = new JComboBox<Ability>(Ability.values());
		abilityFilter.setEnabled(false);
		genderRatio = new JComboBox<GenderRatio>(GenderRatio.values());
		genderRatio.addActionListener(this);
		genderRatio.setSelectedItem(GenderRatio.MALE1_FEMALE1);
		genderFilter = new JComboBox<Gender>(Gender.getGenders());
		genderFilter.setEnabled(false);
		ballFilter = new JComboBox<Gender>(Gender.getGenders());
		ballFilter.setEnabled(false);
		
		calculate = new JButton("Calculate");
		calculate.addActionListener(this);
		filterNatureReset = new JButton("Clear");
		filterNatureReset.addActionListener(this);
		filterHPTypeReset = new JButton("Clear");
		filterHPTypeReset.addActionListener(this);
		
		ButtonGroup bg = new ButtonGroup();
		frameRange = new JRadioButton("Frame Range:", true);
		frameRange.setHorizontalAlignment(JRadioButton.RIGHT);
		frameRange.addActionListener(this);
		bg.add(frameRange);
		eggRange = new JRadioButton("Egg Number Range:");
		eggRange.setHorizontalAlignment(JRadioButton.RIGHT);
		eggRange.addActionListener(this);
		bg.add(eggRange);
		shortestPath = new JRadioButton("Path to Target Frame:");
		shortestPath.setHorizontalAlignment(JRadioButton.RIGHT);
		shortestPath.addActionListener(this);
		bg.add(shortestPath);
		
		results = new ResultsTable(new Object[] {"Egg", "Frame", "Frame-Adv.", "Shiny", "PID", "PSV", "HP", "Atk", "Def",
				"SpA", "SpD", "Spe", "HPType", "Nature", "Gender", "Ability", "Ball", "EC", "Rand", "status[0]",
				"status[2]", "status[3]", "status[4]"});
		
		setTarget = new JMenuItem("Set as Target Frame");
		setTarget.addActionListener(this);
		setCurrent = new JMenuItem("Set as Current Status");
		setCurrent.addActionListener(this);
		setCurrentAfter = new JMenuItem("Set Status after recieving this Egg as Current");
		setCurrentAfter.addActionListener(this);
		tableMenu = new JPopupMenu();
		tableMenu.add(setTarget);
		tableMenu.add(setCurrent);
		tableMenu.add(setCurrentAfter);
		tableMenu.addPopupMenuListener(this);
		results.setComponentPopupMenu(tableMenu);
		
		setLayout(new BorderLayout(10, 10));
		JPanel parents = new JPanel(new GridLayout(3, 12, 10, 10));
		parents.setBorder(BorderFactory.createTitledBorder("Parents Info"));
		JPanel west = new JPanel(new BorderLayout(10, 10));
		JPanel filters = new JPanel(new BorderLayout(10, 10));
		filters.setBorder(BorderFactory.createTitledBorder("Filters"));
		JPanel filterIVs = new JPanel(new GridLayout(6, 4, 10, 10));
		filterIVs.setBorder(BorderFactory.createTitledBorder("IVs"));
		JPanel otherFilters = new JPanel(new GridLayout(4, 4, 10, 10));
		JPanel rng = new JPanel(new BorderLayout(10, 10));
		rng.setBorder(BorderFactory.createTitledBorder("RNG Info"));
		JPanel status = new JPanel(new GridLayout(4, 2, 10, 10));
		status.setBorder(BorderFactory.createTitledBorder("Status"));
		JPanel otherRNGInfo = new JPanel(new GridLayout(3, 4, 10, 10));
		
		parents.add(new JLabel("Gender ratio:", JLabel.RIGHT));
		parents.add(genderRatio);
		parents.add(new JLabel("Ditto", JLabel.CENTER));
		for(int i = 0; i < STAT_STRINGS.length; i++) {
			parents.add(new JLabel(STAT_STRINGS[i], JLabel.CENTER));
		}
		parents.add(new JLabel("Nature", JLabel.CENTER));
		parents.add(new JLabel("Ability", JLabel.CENTER));
		parents.add(new JLabel("Item", JLabel.CENTER));
		
		parents.add(new JLabel());
		parents.add(nidoran);
		parents.add(maleDitto);
		SwingUtil.add(parents, maleIVs);
		parents.add(maleNature);
		parents.add(maleAbility);
		parents.add(maleItem);
		
		parents.add(masuda);
		parents.add(homogeneous);
		parents.add(femaleDitto);
		SwingUtil.add(parents, femaleIVs);
		parents.add(femaleNature);
		parents.add(femaleAbility);
		parents.add(femaleItem);
		
		
		for(int i = 0; i < STAT_STRINGS.length; i++) {
			filterIVs.add(new JLabel(STAT_STRINGS[i], JLabel.RIGHT));
			filterIVs.add(filterMinIVs[i]);
			filterIVs.add(new JLabel("\u2192", JLabel.CENTER));
			filterIVs.add(filterMaxIVs[i]);
		}

		otherFilters.add(abilityFilterSelection);
		otherFilters.add(abilityFilter);
		otherFilters.add(shinyOnly);
		otherFilters.add(enableFilters);
		otherFilters.add(genderFilterSelection);
		otherFilters.add(genderFilter);
		otherFilters.add(ballFilterSelection);
		otherFilters.add(ballFilter);
		otherFilters.add(new JLabel("Nature:", JLabel.RIGHT));
		otherFilters.add(filterNatureText);
		otherFilters.add(filterNatureSelection);
		otherFilters.add(filterNatureReset);
		otherFilters.add(new JLabel("HPType:", JLabel.RIGHT));
		otherFilters.add(filterHPTypeText);
		otherFilters.add(filterHPTypeSelection);
		otherFilters.add(filterHPTypeReset);
		
		filters.add(otherFilters, BorderLayout.NORTH);
		filters.add(filterIVs, BorderLayout.SOUTH);
		
		for(int i = 0; i < 4; i++) {
			status.add(new JLabel("[" + i + "]", JLabel.RIGHT));
			status.add(this.status[i]);
		}
		
		otherRNGInfo.add(frameRange);
		otherRNGInfo.add(minFrame);
		otherRNGInfo.add(new JLabel("\u2192", JLabel.CENTER));
		otherRNGInfo.add(maxFrame);
		otherRNGInfo.add(eggRange);
		otherRNGInfo.add(minEgg);
		otherRNGInfo.add(new JLabel("\u2192", JLabel.CENTER));
		otherRNGInfo.add(maxEgg);
		otherRNGInfo.add(shortestPath);
		otherRNGInfo.add(targetFrame);
		otherRNGInfo.add(showRNGData);
		otherRNGInfo.add(calculate);
		
		rng.add(status, BorderLayout.NORTH);
		rng.add(otherRNGInfo, BorderLayout.SOUTH);
		
		west.add(filters, BorderLayout.NORTH);
		west.add(rng, BorderLayout.SOUTH);
		
		add(parents, BorderLayout.NORTH);
		add(west, BorderLayout.WEST);
		add(new JScrollPane(results), BorderLayout.CENTER);
	}

	public void addToTable(Result result) {
		addToTable(result, 0);
	}

	public void addToTable(Result result, int eggNumber) {
		addToTable(result, eggNumber, false);
	}

	public void addToTable(Result result, int eggNumber, boolean path) {
		Object[] data = new Object[results.getModel().getColumnCount()];
		int index = 0;
		data[index++] = eggNumber;
		data[index++] = result.getFrame();
		if(path) {
			data[index++] = new Gen7EggRNGAdvance(result.getFrameAdvance());
		} else {
			data[index++] = result.getFrameAdvance();
		}
		data[index++] = result.getShiny();
		data[index++] = Util.add0s(Integer.toHexString(result.getPID()), 8);
		data[index++] = Util.add0s(result.getPSV(), 4);
		for(int i = 0; i < 6; i++) {
			data[index++] = result.getInheritedIV(i);
		}
		data[index++] = result.getHpType();
		data[index++] = result.getInheritedNature();
		data[index++] = result.getGender();
		data[index++] = result.getAbility();
		data[index++] = result.getInheritBall();
		data[index++] = Util.add0s(Integer.toHexString(result.getEC()), 8);
		data[index++] = Util.add0s(Integer.toHexString(result.getRand()), 8);
		for(int i = 0; i < 4; i++) {
			data[index++] = Util.add0s(Integer.toHexString(result.getRngState().get(i)), 8);
		}
		results.getModel().addRow(data);
	}
	
	public void setStatus(RNGState status) {
		for(int i = 0; i < 4; i++) {
			this.status[i].setHexValue(status.get(i));
		}
	}

	public RNGState getStatus() {
		int[] s = new int[4];
		for(int i = 0; i < 4; i++) {
			s[i] = this.status[i].getHexValue();
		}
		return new RNGState(s);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		
		Object source = ae.getSource();
		
		if(source == setTarget) {
			
			int selectedRow = results.getSelectedRow();
			targetFrame.setText(results.getValueAt(selectedRow, 1).toString());
			
		} else if(source == setCurrent) {
			
			int selectedRow = results.getSelectedRow();
			int column = 19;
			for(int i = 0; i < 4; i++) {
				status[i].setText(results.getValueAt(selectedRow, column + i).toString());
			}
			
		} else if(source == setCurrentAfter) {
			
			int selectedRow = results.getSelectedRow();
			int column = 19;
			int[] status = new int[4];
			for(int i = 0; i < 4; i++) {
				status[i] = Integer.parseUnsignedInt(results.getValueAt(selectedRow, column + i).toString(), 16);
			}
			int advance = Integer.parseUnsignedInt(results.getValueAt(selectedRow, 2).toString());
			TinyMT tinyMT = new TinyMT(new RNGState(status));
			tinyMT.next(advance);
			RNGState newStatus = tinyMT.getState();
			setStatus(newStatus);
			
		} else if(source == frameRange || source == eggRange || source == shortestPath) {
			
			minFrame.setEnabled(frameRange.isSelected());
			maxFrame.setEnabled(frameRange.isSelected());
			minEgg.setEnabled(eggRange.isSelected());
			maxEgg.setEnabled(eggRange.isSelected());
			targetFrame.setEnabled(shortestPath.isSelected());
			
		} else if(source == abilityFilterSelection) {
			
			abilityFilter.setEnabled(abilityFilterSelection.isSelected());
			
		} else if(source == genderFilterSelection) {
			
			genderFilter.setEnabled(genderFilterSelection.isSelected());
			
		} else if(source == ballFilterSelection) {
			
			ballFilter.setEnabled(ballFilterSelection.isSelected());
			
		} else if(source == filterNatureSelection) {
			
			String nature = ((Nature) filterNatureSelection.getSelectedItem()).toString();
			String text = filterNatureText.getText();
			text = Util.addIfNotExisting(text, nature);
			filterNatureText.setText(text);
			
		} else if(source == filterNatureReset) {
			
			filterNatureText.setText("");
			
		} else if(source == filterHPTypeSelection) {
			
			String type = ((PokemonType) filterHPTypeSelection.getSelectedItem()).toString();
			String text = filterHPTypeText.getText();
			text = Util.addIfNotExisting(text, type);
			filterHPTypeText.setText(text);
			
		} else if(source == filterHPTypeReset) {
			
			filterHPTypeText.setText("");
			
		} else if(source == genderRatio) {
			
			GenderRatio genderRatio = (GenderRatio) this.genderRatio.getSelectedItem();
			if(genderRatio.isFixed()) {
				if(genderRatio == GenderRatio.FEMALE_ONLY) {
					femaleDitto.setSelected(false);
					maleDitto.setEnabled(true);
					femaleDitto.setEnabled(false);
					homogeneous.setEnabled(false);
				} else {
					femaleDitto.setSelected(true);
					maleDitto.setEnabled(false);
					femaleDitto.setEnabled(false);
					homogeneous.setEnabled(false);
				}
			} else {
				femaleDitto.setSelected(false);
				maleDitto.setEnabled(true);
				femaleDitto.setEnabled(true);
				homogeneous.setEnabled(true);
			}
			
		} else if(source == calculate) {
			
			int tsv = guiMain.getTSV();
			Gender ditto = null;
			if(maleDitto.isSelected()) ditto = Gender.MALE;
			else if(femaleDitto.isSelected()) ditto = Gender.FEMALE;
			boolean homogeneous = this.homogeneous.isSelected();
			Nature maleNature = (Nature) this.maleNature.getSelectedItem();
			Nature femaleNature = (Nature) this.femaleNature.getSelectedItem();
			int[] maleIVs = new int[6];
			int[] femaleIVs = new int[6];
			for(int i = 0; i < 6; i++) {
				maleIVs[i] = this.maleIVs[i].getValue();
				femaleIVs[i] = this.femaleIVs[i].getValue();
			}
			Gen7BreedingItem maleItem = (Gen7BreedingItem) this.maleItem.getSelectedItem();
			Gen7BreedingItem femaleItem = (Gen7BreedingItem) this.femaleItem.getSelectedItem();
			GenderRatio genderRatio = (GenderRatio) this.genderRatio.getSelectedItem();
			Ability maleAbility = (Ability) this.maleAbility.getSelectedItem();
			Ability femaleAbility = (Ability) this.femaleAbility.getSelectedItem();
			boolean shinyCharm = guiMain.hasShinyCharm();
			boolean masuda = this.masuda.isSelected();
			boolean nidoran = this.nidoran.isSelected();
			
			Gen7EggSetting setting = new Gen7EggSetting(tsv, ditto, homogeneous, maleNature, femaleNature, maleIVs,
					femaleIVs, maleItem, femaleItem, genderRatio, maleAbility, femaleAbility, shinyCharm, masuda, nidoran,
					false, new int[0]);
			
			RNGState status = getStatus();
			
			eggRNG = new Gen7EggRNG(this, status, setting);
			
			Gen7EggFilter filter = null;
			if(enableFilters.isSelected()) {
				int[] minIVs = new int[6];
				int[] maxIVs = new int[6];
				for(int i = 0; i < 6; i++) {
					minIVs[i] = filterMinIVs[i].getValue();
					filterMaxIVs[i].setMin(minIVs[i]);
					filterMaxIVs[i].setStandard(minIVs[i]);
					maxIVs[i] = filterMaxIVs[i].getValue();
				}
				Nature[] natures = Nature.getNatures(filterNatureText.getText());
				PokemonType[] hpTypes = PokemonType.getTypes(filterHPTypeText.getText());
				Gender gender = genderFilterSelection.isSelected() ? (Gender) genderFilter.getSelectedItem() : null;
				Gender ball = ballFilterSelection.isSelected() ? (Gender) ballFilter.getSelectedItem() : null;
				Ability ability = abilityFilterSelection.isSelected() ? (Ability) abilityFilter.getSelectedItem() : null;
				boolean shinyOnly = this.shinyOnly.isSelected();
				filter = new Gen7EggFilter(minIVs, maxIVs, natures, hpTypes, gender, ball, ability, shinyOnly);
			}
			
			results.reset();
			if(!showRNGData.isSelected()) results.hideColumns(18, 5);
			if(!shinyCharm && !masuda) results.hideColumns(3, 3);
			if(!eggRange.isSelected() && !shortestPath.isSelected()) results.hideColumns(0, 1);
			
			if(frameRange.isSelected()) {
				
				int minFrame = this.minFrame.getValue();
				maxFrame.setMin(minFrame);
				maxFrame.setStandard(minFrame + 10000);
				int maxFrame = this.maxFrame.getValue();
				
				eggRNG.generateFrame(minFrame, maxFrame, filter);
				
			} else if(eggRange.isSelected()) {
				
				int minEgg = this.minEgg.getValue();
				maxEgg.setMin(minEgg);
				maxEgg.setStandard(minEgg + 100);
				int maxEgg = this.maxEgg.getValue();
				
				eggRNG.generateEgg(minEgg, maxEgg, filter);
				
			} else if(shortestPath.isSelected()) {
				
				int targetFrame = this.targetFrame.getValue();
				
				eggRNG.generateShorstestPath(targetFrame);
				
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
	
}
