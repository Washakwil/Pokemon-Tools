package main;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import key.bv.BVBreaker;
import key.bv.GuiBVBreaker;
import pokemon.Game;
import rng.RNGState;
import util.gui.SwingUtil;
import util.gui.UnsignedIntTextField;

public class GuiProfileEditor extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = -7368978057090474524L;

	private GuiMainMenu guiMain;
	
	private JPanel status;
	private JTextField nameInput, keyInput;
	private UnsignedIntTextField tsvInput;
	private UnsignedIntTextField[] eggRNGStatus;
	private JCheckBox shinyCharm;
	private JButton chooseKey, useCurrentStatus, ok, cancel;
	private JComboBox<Game> gameSelection;

	private Profile profile;


	public GuiProfileEditor(GuiMainMenu guiMain, Profile profile) {
		
		super("Profile Editor");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setIconImage(GuiMainMenu.ICON_IMAGE);
		
		this.guiMain = guiMain;
		this.profile = profile;
		
		nameInput = new JTextField();
		keyInput = new JTextField();
		tsvInput = new UnsignedIntTextField(0, 0xFFF, 0, 4);
		eggRNGStatus = new UnsignedIntTextField[4];
		for(int i = 0; i < 4; i++) {
			eggRNGStatus[i] = new UnsignedIntTextField(0, 0xFFFFFFFF, 0, 8);
		}
		shinyCharm = new JCheckBox("Shiny Charm");
		shinyCharm.setHorizontalAlignment(JCheckBox.CENTER);
		
		chooseKey = new JButton("Browse");
		chooseKey.addActionListener(this);
		useCurrentStatus = new JButton("Use Current Status");
		useCurrentStatus.addActionListener(this);
		ok = new JButton("OK");
		ok.addActionListener(this);
		cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		
		gameSelection = new JComboBox<Game>(Game.values());
		gameSelection.addActionListener(this);
		
		setLayout(new BorderLayout(10, 10));
		JPanel otherInfo = new JPanel(new GridLayout(5, 3, 10, 10));
		status = new JPanel(new GridLayout(5, 2, 10, 10));
		status.setBorder(BorderFactory.createTitledBorder("Egg RNG Status"));
		
		otherInfo.add(shinyCharm);
		otherInfo.add(new JLabel("Game:", JLabel.RIGHT));
		otherInfo.add(gameSelection);
		otherInfo.add(new JLabel("Name:", JLabel.CENTER));
		otherInfo.add(new JLabel("TSV:", JLabel.CENTER));
		otherInfo.add(new JLabel("BV Key:", JLabel.CENTER));
		otherInfo.add(nameInput);
		otherInfo.add(tsvInput);
		otherInfo.add(keyInput);
		SwingUtil.addEmptyLabels(otherInfo, 2);
		otherInfo.add(chooseKey);
		otherInfo.add(new JLabel());
		otherInfo.add(cancel);
		otherInfo.add(ok);
		
		status.add(new JLabel());
		status.add(useCurrentStatus);
		for(int i = 0; i < 4; i++) {
			status.add(new JLabel("[" + i + "]", JLabel.RIGHT));
			status.add(eggRNGStatus[i]);
		}
		
		add(status, BorderLayout.NORTH);
		add(otherInfo, BorderLayout.SOUTH);
		
		pack();
		setResizable(false);
		setVisible(true);
		
		if(profile != null) {
			nameInput.setText(profile.getName());
			gameSelection.setSelectedItem(profile.getGame());
			keyInput.setText(profile.getBVKey());
			tsvInput.setValue(profile.getTsv());
			setEggRNGStatus(profile.getEggRNGStatus());
			shinyCharm.setSelected(profile.getShinyCharm());
		}
		
		update();
	}

	private void update() {
		Game game = (Game) gameSelection.getSelectedItem();
		int gen = game .getGeneration();
		SwingUtil.enableJPanel(status, gen == 7);
		keyInput.setEnabled(gen >= 6);
		shinyCharm.setEnabled(game.shinyCharm());
		tsvInput.setMax(gen >= 6 ? 0xFFF : 0x1FFF);
	}
	
	public void setEggRNGStatus(RNGState status) {
		for(int i = 0; i < 4; i++) {
			this.eggRNGStatus[i].setHexValue(status.get(i));
		}
	}

	public RNGState getStatus() {
		int[] s = new int[4];
		for(int i = 0; i < 4; i++) {
			s[i] = this.eggRNGStatus[i].getHexValue();
		}
		return new RNGState(s);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		
		Object source = ae.getSource();
		
		if(source == chooseKey) {
			
			SwingUtil.openFile(GuiBVBreaker.DEFAULT_KEY_DIRECTORY, keyInput, this, BVBreaker.getKeyFileFilter());
			
		} else if(source == useCurrentStatus) {
			
			setEggRNGStatus(guiMain.getStatus());
			
		} else if(source == ok) {

			String name = nameInput.getText();
			Game game = (Game) gameSelection.getSelectedItem();
			RNGState eggRNGStatus = getStatus();
			int tsv = tsvInput.getValue();
			boolean shinyCharm = this.shinyCharm.isSelected();
			String bvKey = keyInput.getText();
			Profile newProfile = new Profile(name, game, eggRNGStatus, shinyCharm, tsv, bvKey);
			JComboBox<Profile> profiles = guiMain.getProfiles();
			int index = guiMain.getIndexOf(newProfile);
			
			if(name.length() == 0) {
				index = 0;
				for(int i = 1; index > -1; i++) {
					newProfile.setName("Profile " + i);
					index = guiMain.getIndexOf(newProfile);
				}
			}
			
			if(index > -1)  {
				int overwrite = JOptionPane.OK_OPTION;
				if(profile.getName().length() == 0) {
					overwrite = JOptionPane.showConfirmDialog(this, "The Profile " + newProfile.getName() + " alraedy exists. Would you like to overwrite it?");
				}
				if(overwrite == JOptionPane.OK_OPTION) {
					profiles.removeItemAt(index);
					profiles.insertItemAt(newProfile, index);
				} else return;
			} else {
				if(profile.getName().length() == 0) {
					profiles.addItem(newProfile);
				} else {
					index = guiMain.getIndexOf(profile);
					profiles.removeItemAt(index);
					profiles.insertItemAt(newProfile, index);
				}
			}
			profiles.setSelectedItem(newProfile);
			Profile.savePofiles(profiles);
			dispose();
			
		} else if(source == cancel) {
			
			dispose();
			
		} else if(source == gameSelection) {
			
			update();
			
		}
		
	}

}
