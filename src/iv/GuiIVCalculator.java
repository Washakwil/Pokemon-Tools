package iv;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import main.GuiMainMenu;
import pokemon.Game;
import pokemon.Nature;
import pokemon.Pokemon;
import util.gui.SwingUtil;
import util.gui.UnsignedIntTextField;

public class GuiIVCalculator extends JPanel implements ActionListener, FocusListener {
	
	private static final long serialVersionUID = -4423503553408021270L;
	private GuiMainMenu guiMain;
	private JLabel pokemonIcon;
	private JLabel[] natureEffect, iv;
	private UnsignedIntTextField dexNo, levelInput;
	private UnsignedIntTextField[] baseStatInput, currentStatInput, evInput = new UnsignedIntTextField[6];
	private JCheckBox samePokemon;
	private JButton calculate;
	private JComboBox<Pokemon> pokemonSelection;
	private JComboBox<Nature> natureSelection;
	private JComboBox<Characteristic> characteristic;
	private JComboBox<Judge>[] judgeInput;
	private IVCalculator ivCalc;
	
	@SuppressWarnings("unchecked")
	public GuiIVCalculator(GuiMainMenu guiMain) {

		final String[] STAT_STRINGS = new String[] {"HP", "Atk", "Def", "SpA", "SpD", "Spe"};
		
		this.guiMain = guiMain;
		
		pokemonIcon = new JLabel("", JLabel.RIGHT);

		natureEffect = new JLabel[5];
		for(int i = 0; i < natureEffect.length; i++) {
			natureEffect[i] = new JLabel("", JLabel.CENTER);
			Font font = natureEffect[i].getFont();
			natureEffect[i].setFont(new Font(font.getName(), Font.BOLD, 20));
		}
		iv = new JLabel[6];
		for(int i = 0; i < iv.length; i++) {
			iv[i] = new JLabel();
		}
		
		dexNo = new UnsignedIntTextField(1, Pokemon.getCount(), 1, 3);
		dexNo.setHorizontalAlignment(UnsignedIntTextField.RIGHT);
		dexNo.addActionListener(this);
		dexNo.addFocusListener(this);
		levelInput = new UnsignedIntTextField(1, 100, 50);
		
		baseStatInput = new UnsignedIntTextField[6];
		currentStatInput = new UnsignedIntTextField[6];
		evInput = new UnsignedIntTextField[6];
		for(int i = 0; i < 6; i++) {
			baseStatInput[i] = new UnsignedIntTextField(1, 0xFF, 100);
			currentStatInput[i] = new UnsignedIntTextField(1, 999, 100);
			currentStatInput[i].setText("");
			evInput[i] = new UnsignedIntTextField(0, 252, 0);
		}
		
		samePokemon = new JCheckBox("Same Pokémon, other level");
		
		calculate = new JButton("Calculate IVs");
		calculate.addActionListener(this);
		
		pokemonSelection = new JComboBox<Pokemon>(Pokemon.POKEMON);
		pokemonSelection.addActionListener(this);
		natureSelection = new JComboBox<Nature>(Nature.values());
		natureSelection.addActionListener(this);
		characteristic = new JComboBox<Characteristic>(Characteristic.values());
		
		judgeInput = new JComboBox[6];
		for(int i = 0; i < judgeInput.length; i++) {
			judgeInput[i] = new JComboBox<Judge>();
		}
		loadJudgeValues(guiMain.getSelectedGame());
		
		setLayout(new GridLayout(2, 1, 10, 10));
		JPanel stats = new JPanel(new GridLayout(7, 7, 10, 10));
		stats.setBorder(BorderFactory.createTitledBorder("Stats"));
		JPanel otherInput = new JPanel(new GridLayout(5, 2, 10, 10));
		otherInput.setBorder(BorderFactory.createTitledBorder("Pokemon Info"));
		
		otherInput.add(new JLabel("Pokédex number:", JLabel.RIGHT));
		otherInput.add(dexNo);
		otherInput.add(pokemonIcon);
		otherInput.add(pokemonSelection);
		otherInput.add(new JLabel("Level:", JLabel.RIGHT));
		otherInput.add(levelInput);
		otherInput.add(new JLabel("Characteristic:", JLabel.RIGHT));
		otherInput.add(characteristic);
		otherInput.add(samePokemon);
		otherInput.add(calculate);
		
		stats.add(new JLabel());
		for(int i = 0; i < 6; i++) {
			stats.add(new JLabel(STAT_STRINGS[i], JLabel.CENTER));
		}
		
		stats.add(new JLabel("Stats:", JLabel.RIGHT));
		SwingUtil.add(stats, currentStatInput);
		
		stats.add(new JLabel("Base Stats:", JLabel.RIGHT));
		SwingUtil.add(stats, baseStatInput);
		
		stats.add(new JLabel("EVs:", JLabel.RIGHT));
		SwingUtil.add(stats, evInput);
		
		stats.add(natureSelection);
		stats.add(new JLabel());
		SwingUtil.add(stats, natureEffect);
		
		stats.add(new JLabel("Judge:", JLabel.RIGHT));
		SwingUtil.add(stats, judgeInput);
		
		stats.add(new JLabel("IVs:", JLabel.RIGHT));
		SwingUtil.add(stats, iv);
		
		add(otherInput);
		add(stats);
		
		updatePokemon();
	}
	
	public void loadJudgeValues(Game game) {
		for(int i = 0; i < judgeInput.length; i++) {
			judgeInput[i].removeAllItems();
			Judge[] judges = Judge.getJudges(guiMain.getSelectedGame());
			for(int j = 0; j < judges.length; j++) {
				judgeInput[i].addItem(judges[j]);
			}
		}
	}

	private void updateDV() {
		if(ivCalc != null) {
			for(int i = 0; i < iv.length; i++) {
				iv[i].setText(ivCalc.getDVString(i));
			}
		}
	}

	private void updatePokemon() {
		Pokemon pokemon = (Pokemon) pokemonSelection.getSelectedItem();
		int[] baseStats = pokemon.getBaseStats(guiMain.getSelectedGame());
		for(int i = 0; i < baseStats.length; i++) {
			this.baseStatInput[i].setText(baseStats[i] + "");
		}
		pokemonIcon.setIcon(pokemon.getIcon());
		dexNo.setValue(pokemon.getDexNo());
	}

	private void updateNature() {
		int[] effect = ((Nature) natureSelection.getSelectedItem()).getEffect();
		for(int i = 0; i < effect.length; i++) {
			String s = "";
			if(effect[i] == 1) {
				s = "+";
				natureEffect[i].setForeground(Color.GREEN);
			}
			if(effect[i] == -1) {
				s = "-";
				natureEffect[i].setForeground(Color.RED);
			}
			natureEffect[i].setText(s);
		}
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		
		Object source = ae.getSource();
		
		if(source == pokemonSelection) {
			
			updatePokemon();
			
		} else if(source == dexNo) {
			
			pokemonSelection.setSelectedItem(Pokemon.getFirstIndex(dexNo.getValue()));
			updatePokemon();
			
		} else if(source == natureSelection) {
			
			updateNature();
			
		} else if(source == calculate) {
			
			Pokemon pokemon = (Pokemon) pokemonSelection.getSelectedItem();
			Nature nature = (Nature) natureSelection.getSelectedItem();
			int level = 1;
			int[] stats = new int[6], ev = new int[6], baseStats = new int[6], natureEffect = nature.getEffect();
			Judge[] judge = new Judge[6];
			boolean shedinja = pokemon.getDexNo() == 292;
			level = levelInput.getValue();
			for(int i = 0; i < stats.length; i++) {
				stats[i] = currentStatInput[i].getValue();
			}
			for(int i = 0; i < baseStats.length; i++) {
				baseStats[i] = baseStatInput[i].getValue();
			}
			for(int i = 0; i < ev.length; i++) {
				ev[i] = evInput[i].getValue();
			}
			for(int i = 0; i < judge.length; i++) {
				judge[i] = (Judge) judgeInput[i].getSelectedItem();
			}
			Integer[][] lastDv = null;
			if(samePokemon.isSelected() && ivCalc != null) lastDv = ivCalc.getIV();
			ivCalc = new IVCalculator(stats, baseStats, ev, natureEffect, level);
			if(shedinja) ivCalc.shedinja();
			ivCalc.filterDVs(lastDv, (Characteristic) characteristic.getSelectedItem(), judge);
			updateDV();
			
		}
	}

	@Override
	public void focusGained(FocusEvent fe) {}

	@Override
	public void focusLost(FocusEvent fe) {
		Object source = fe.getSource();
		if(source == dexNo) {
			pokemonSelection.setSelectedItem(Pokemon.getFirstIndex(dexNo.getValue()));
			updatePokemon();
		}
	}
	
}
