package key.bv;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import main.GuiMainMenu;
import util.Util;
import util.gui.ResultsTable;
import util.gui.SwingUtil;

public class GuiBVBreaker extends JPanel implements ActionListener, PopupMenuListener {

	public static final File DEFAULT_BV_DIRECTORY = new File("BV");
	public static final File DEFAULT_KEY_DIRECTORY = new File("keys");
	private static final long serialVersionUID = 8764273962909657378L;
	private JButton calculateBV, calculateKey, chooseV1, chooseV2, chooseKey, chooseV;
	private JTextField v1Path, v2Path, keyPath, vPath;
	private ResultsTable results;
	private JPopupMenu tableMenu;
	private JMenuItem setTSV, saveKey;
	private GuiMainMenu guiMain;
	private BVBreaker bvBreaker;

	public GuiBVBreaker(GuiMainMenu guiMain) {
		
		this.guiMain = guiMain;
		
		calculateBV = new JButton("Calculate");
		calculateBV.addActionListener(this);
		calculateKey = new JButton("Calculate");
		calculateKey.addActionListener(this);
		chooseV1 = new JButton("Browse");
		chooseV1.addActionListener(this);
		chooseV2 = new JButton("Browse");
		chooseV2.addActionListener(this);
		chooseKey = new JButton("Browse");
		chooseKey.addActionListener(this);
		chooseV = new JButton("Browse");
		chooseV.addActionListener(this);
		
		v1Path = new JTextField();
		v2Path = new JTextField();
		keyPath = new JTextField();
		vPath = new JTextField();
		
		results = new ResultsTable(new Object[] {"Team", "Slot", "Pokemon", "Shiny", "G7TID", "TID", "SID", "TSV", "PSV"});
		
		setTSV = new JMenuItem("Set as TSV");
		setTSV.addActionListener(this);
		saveKey = new JMenuItem("Save Key");
		saveKey.addActionListener(this);
		tableMenu = new JPopupMenu();
		tableMenu.add(setTSV);
		tableMenu.add(saveKey);
		tableMenu.addPopupMenuListener(this);
		results.setComponentPopupMenu(tableMenu);
		
		setLayout(new BorderLayout(10, 10));
		JPanel input = new JPanel(new GridLayout(1, 2, 10, 10));
		JPanel video = new JPanel(new GridLayout(3, 3, 10, 10));
		video.setBorder(BorderFactory.createTitledBorder("Calculate Key with 2 Battle Videos"));
		JPanel key = new JPanel(new GridLayout(3, 3, 10, 10));
		key.setBorder(BorderFactory.createTitledBorder("Calculate Pokemon Data with existing Key"));
		
		video.add(new JLabel("Video 1", JLabel.RIGHT));
		video.add(v1Path);
		video.add(chooseV1);
		video.add(new JLabel("Video 2", JLabel.RIGHT));
		video.add(v2Path);
		video.add(chooseV2);
		SwingUtil.addEmptyLabels(video, 2);
		video.add(calculateBV);
		
		key.add(new JLabel("Key", JLabel.RIGHT));
		key.add(keyPath);
		key.add(chooseKey);
		key.add(new JLabel("Video", JLabel.RIGHT));
		key.add(vPath);
		key.add(chooseV);
		SwingUtil.addEmptyLabels(key, 2);
		key.add(calculateKey);
		
		input.add(video);
		input.add(key);
		
		add(input, BorderLayout.NORTH);
		add(new JScrollPane(results), BorderLayout.CENTER);
		
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		
		Object source = ae.getSource();
		
		if(source == chooseV1) {
			
			File defaultFile = DEFAULT_BV_DIRECTORY;
			File file;
			if((file = new File(v2Path.getText())).exists()) defaultFile = file;
			else if((file = new File(vPath.getText())).exists()) defaultFile = file;
			SwingUtil.openFile(defaultFile, v1Path, this, null);
			
		} else if(source == chooseV2) {
			
			File defaultFile = DEFAULT_BV_DIRECTORY;
			File file;
			if((file = new File(v1Path.getText())).exists()) defaultFile = file;
			else if((file = new File(vPath.getText())).exists()) defaultFile = file;
			SwingUtil.openFile(defaultFile, v2Path, this, null);
			
		} else if(source == chooseKey) {
			
			SwingUtil.openFile(DEFAULT_KEY_DIRECTORY, keyPath, this, BVBreaker.getKeyFileFilter());
			
		} else if(source == chooseV) {
			
			File defaultFile = DEFAULT_BV_DIRECTORY;
			File file;
			if((file = new File(v1Path.getText())).exists()) defaultFile = file;
			else if((file = new File(v2Path.getText())).exists()) defaultFile = file;
			SwingUtil.openFile(defaultFile, vPath, this, null);
			
		} else if(source == setTSV) {
			
			int selectedRow = results.getSelectedRow();
			guiMain.setTSV((int) results.getValueAt(selectedRow, 5));
			
		} else if(source == saveKey) {
			
			if(bvBreaker != null) {
				JFileChooser fileChooser = new JFileChooser(DEFAULT_KEY_DIRECTORY);
				fileChooser.setFileFilter(BVBreaker.getKeyFileFilter());
				if(fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					file = Util.forceFileNameExtension(file, "bin");
					int overwrite = JOptionPane.OK_OPTION;
					if(file.exists()) {
						overwrite = JOptionPane.showConfirmDialog(this, "The File " + file.getName() + " alraedy exists. Would you like to overwrite it?");
					}
					if(overwrite == JOptionPane.OK_OPTION) bvBreaker.save(file);
				}
			}
			
		} else if(source == calculateBV) {
			
			results.reset();
			bvBreaker = BVBreaker.createBVBreaker(v1Path.getText(), v2Path.getText(), guiMain.getSelectedGame());
			addTeams();
			
		} else if(source == calculateKey) {
			
			results.reset();
			bvBreaker = BVBreaker.createBVBreakerWithKey(keyPath.getText(), vPath.getText(), guiMain.getSelectedGame());
			addTeams();
			
		}
	}
	
	private void addTeams() {
		
		addTeam(false);
		addTeam(true);
		
	}
	
	private void addTeam(boolean opponent) {
		if(bvBreaker == null) return;
		for(int i = 0; i < 6; i++) {
			BVPokemon pokemon = bvBreaker.getBVPokemon(i, opponent);
			if(pokemon == null || pokemon.getSpecies() == null) return;
			addToTable(i, pokemon, opponent);
		}
	}

	private void addToTable(int slot, BVPokemon pokemon, boolean opponent) {
		results.getModel().addRow(new Object[] {
				opponent ? "Opponent" : "Own",
				slot + 1,
				pokemon,
				pokemon.getShinyState(),
				pokemon.getG7TID(),
				pokemon.getTID(),
				pokemon.getSID(),
				pokemon.getTSV(),
				pokemon.getPSV()
		});
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

	public String getBVKeyPath() {
		return keyPath.getText();
	}

	public void setBVKeyPath(String path) {
		if(new File(path).exists()) keyPath.setText(path);
	}
	
}
