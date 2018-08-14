package main;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import iv.GuiIVCalculator;
import key.bv.GuiBVBreaker;
import pokemon.Game;
import rng.RNGState;
import rng.gen4.GuiGen4SeedToTime;
import rng.gen4.id.GuiGen4IDRNG;
import rng.gen4.id.cutecharm.GuiGen4CCIDs;
import rng.gen7.egg.GuiGen7EggSeedFinder;
import rng.gen7.egg.GuiGen7EggRNG;
import util.gui.UnsignedIntTextField;

public class GuiMainMenu extends JFrame implements ActionListener {

	private static final long serialVersionUID = -1513610873424267499L;
	public static final Image ICON_IMAGE = new ImageIcon("resources/icon.png").getImage();
	
	private JTabbedPane generation, gen4Tools, gen7Tools, otherTools;
	
	private JButton newProfile, deleteProfile, editProfile;
	private JComboBox<Profile> profiles;
	private JComboBox<Game> gameSelection;
	private UnsignedIntTextField tsvInput;
	private JCheckBox shinyCharm;

	private GuiGen4SeedToTime gen4SeedToTime;
	private GuiGen4IDRNG gen4IDRNG;
	private GuiGen4CCIDs gen4CCIDs;
	
	private GuiGen7EggRNG gen7EggRNG;
	private GuiGen7EggSeedFinder gen7EggSeedFinder;
	
	private GuiIVCalculator ivCalculator;
	private GuiBVBreaker bvBreaker;

	public GuiMainMenu() {
		
		super("Pokémon-Tools");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setIconImage(GuiMainMenu.ICON_IMAGE);
		
		generation = new JTabbedPane();
		gen4Tools = new JTabbedPane();
		gen7Tools = new JTabbedPane();
		otherTools = new JTabbedPane();
		
		newProfile = new JButton("New Profile");
		newProfile.addActionListener(this);
		deleteProfile = new JButton("Delete Profile");
		deleteProfile.addActionListener(this);
		editProfile = new JButton("Edit Profile");
		editProfile.addActionListener(this);
		
		profiles = Profile.loadProfiles();
		profiles.addActionListener(this);
		profiles.setSelectedItem(null);
		
		gameSelection = new JComboBox<Game>(Game.values());
		gameSelection.addActionListener(this);
		
		tsvInput = new UnsignedIntTextField(0, 0xFFF, 0, 4);
		
		shinyCharm = new JCheckBox("Shiny Charm");
		shinyCharm.setHorizontalAlignment(JCheckBox.CENTER);
		
		gen4SeedToTime = new GuiGen4SeedToTime();
		gen4IDRNG = new GuiGen4IDRNG(this);
		gen4CCIDs = new GuiGen4CCIDs(this);

		gen7EggRNG = new GuiGen7EggRNG(this);
		gen7EggSeedFinder = new GuiGen7EggSeedFinder(this);
		
		ivCalculator = new GuiIVCalculator(this);
		bvBreaker = new GuiBVBreaker(this);

		setLayout(new BorderLayout(10, 10));
		JPanel profiles = new JPanel(new GridLayout(1, 10, 10, 10));
		profiles.setBorder(BorderFactory.createTitledBorder("Profiles"));
		
		profiles.add(new JLabel("Profile:", JLabel.RIGHT));
		profiles.add(this.profiles);
		profiles.add(newProfile);
		profiles.add(deleteProfile);
		profiles.add(editProfile);
		profiles.add(new JLabel("Game:", JLabel.RIGHT));
		profiles.add(gameSelection);
		profiles.add(new JLabel("TSV:", JLabel.RIGHT));
		profiles.add(tsvInput);
		profiles.add(shinyCharm);
		
		gen4Tools.addTab("Seed To Time", gen4SeedToTime);
		gen4Tools.addTab("ID RNG", gen4IDRNG);
		gen4Tools.addTab("Cute Charm Glitch", gen4CCIDs);
		
		gen7Tools.addTab("Egg RNG", gen7EggRNG);
		gen7Tools.addTab("Egg Seed Finder", gen7EggSeedFinder);
		
		otherTools.addTab("IV Calculator", ivCalculator);
		otherTools.addTab("BV Breaker", bvBreaker);
		
		generation.addTab("Gen 4", gen4Tools);
		generation.addTab("Gen 7", gen7Tools);
		generation.addTab("Other", otherTools);
		
		add(profiles, BorderLayout.NORTH);
		add(generation, BorderLayout.CENTER);
		
		pack();
		setLocationRelativeTo(null);
		setExtendedState(MAXIMIZED_BOTH);
		setVisible(true);
	}

	public void setTSV(int tsv) {
		tsvInput.setValue(tsv);
	}

	public int getTSV() {
		return tsvInput.getValue();
	}

	public void setStatus(RNGState rngState) {
		gen7EggRNG.setStatus(rngState);
	}

	public RNGState getStatus() {
		return gen7EggRNG.getStatus();
	}

	public boolean hasShinyCharm() {
		return shinyCharm.isSelected();
	}

	public void openIDRNG(String tid, String sid, String pid, String tsv) {
		gen4IDRNG.setIDSearchInput(tid, sid, pid, tsv);
		gen4Tools.setSelectedComponent(gen4IDRNG);
	}

	public void openSeedToTime(int seed, int year) {
		gen4SeedToTime.setInput(seed, year);
		gen4Tools.setSelectedComponent(gen4SeedToTime);
		gen4SeedToTime.generate();
	}

	private void loadProfile(Profile profile) {
		Game game = profile.getGame();
		int gen = game.getGeneration();
		gameSelection.setSelectedItem(game);
		if(game.shinyCharm()) shinyCharm.setSelected(profile.getShinyCharm());
		tsvInput.setValue(profile.getTsv());
		if(gen >= 6) bvBreaker.setBVKeyPath(profile.getBVKey());
		if(gen == 7) setStatus(profile.getEggRNGStatus());
	}
	
	public void update() {
		Game game = (Game) gameSelection.getSelectedItem();
		int gen = game.getGeneration();
		tsvInput.setMax(gen >= 6 ? 0xFFF : 0x1FFF);
		shinyCharm.setEnabled(game.shinyCharm());
	}

	public int getIndexOf(Profile profile) {
		for(int i = 0; i < profiles.getItemCount(); i++) {
			if(profiles.getItemAt(i).getName().equals(profile.getName())) {
				return i;
			}
		}
		return -1;
	}

	public JComboBox<Profile> getProfiles() {
		return profiles;
	}
	
	public Game getSelectedGame() {
		return (Game) gameSelection.getSelectedItem();
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		
		Object source = ae.getSource();
		
		if(source == newProfile) {
			
			Profile profile = new Profile("", (Game) gameSelection.getSelectedItem(), gen7EggRNG.getStatus(), shinyCharm.isSelected(), tsvInput.getValue(), bvBreaker.getBVKeyPath());
			GuiProfileEditor profileEditor = new GuiProfileEditor(this, profile);
			profileEditor.setLocationRelativeTo(newProfile);
			
		} else if(source == deleteProfile) {
			
			int index = profiles.getSelectedIndex();
			if(index > -1) {
				profiles.removeItemAt(index);
				if(profiles.getItemCount() > 0) profiles.setSelectedIndex(0);
				Profile.savePofiles(profiles);
			}
			
		} else if(source == editProfile) {
			
			Profile profile = (Profile) profiles.getSelectedItem();
			if(profile != null) {
				GuiProfileEditor profileEditor = new GuiProfileEditor(this, profile);
				profileEditor.setLocationRelativeTo(editProfile);
			}
			
		} else if(source == profiles) {
			
			Profile profile = (Profile) profiles.getSelectedItem();
			if(profile != null) loadProfile(profile);
			
		} else if(source == gameSelection) {
			
			ivCalculator.loadJudgeValues(getSelectedGame());
			update();
			
		}
		
	}
	
}
