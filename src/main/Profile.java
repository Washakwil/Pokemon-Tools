package main;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.swing.JComboBox;

import pokemon.Game;
import rng.RNGState;
import util.Util;

public class Profile {

	public static final File PROFILES_FILE = new File("profiles");
	
	private String name, bvKey;
	private RNGState eggRNGStatus;
	private boolean shinyCharm;
	private int tsv;
	private Game game;
	
	public Profile(String name, Game game, RNGState eggRNGStatus, boolean shinyCharm, int tsv, String bvKey) {
		this.name = name;
		this.game = game;
		this.eggRNGStatus = eggRNGStatus;
		this.shinyCharm = shinyCharm;
		this.tsv = tsv;
		this.bvKey = bvKey;
	}
	
	public RNGState getEggRNGStatus() {
		return eggRNGStatus;
	}
	
	public int getTsv() {
		return tsv;
	}
	
	public boolean getShinyCharm() {
		return shinyCharm;
	}
	
	public String getName() {
		return name;
	}
	
	public String getBVKey() {
		return bvKey;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Game getGame() {
		return game;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public static JComboBox<Profile> loadProfiles() {
		try {
			if(!PROFILES_FILE.exists()) PROFILES_FILE.createNewFile();
			RandomAccessFile file = new RandomAccessFile(PROFILES_FILE, "r");
			int count = file.readInt();
			JComboBox<Profile> profiles = new JComboBox<Profile>();
			for(int i = 0; i < count; i++) {
				Game game = Game.getGame(file.readByte());
				RNGState eggRNGStatus = new RNGState(file.readInt(), file.readInt(), file.readInt(), file.readInt());
				int tsv = file.readInt();
				boolean shinyCharm = file.readBoolean();
				String name = Util.readString(file);
				String bvKey = Util.readString(file);
				profiles.addItem(new Profile(name, game, eggRNGStatus, shinyCharm, tsv, bvKey));
			}
			file.close();
			return profiles;
		} catch (IOException e) {
			return new JComboBox<Profile>();
		}
	}
	
	public static void savePofiles(JComboBox<Profile> profiles) {
		try {
			if(!PROFILES_FILE.exists()) PROFILES_FILE.createNewFile();
			RandomAccessFile file = new RandomAccessFile(PROFILES_FILE, "rw");
			file.writeInt(profiles.getItemCount());
			for(int i = 0; i < profiles.getItemCount(); i++) {
				Profile profile = profiles.getItemAt(i);
				file.writeByte(profile.game.ordinal());
				for(int j = 0; j < 4; j++) {
					file.writeInt(profile.eggRNGStatus.get(j));
				}
				file.writeInt(profile.tsv);
				file.writeBoolean(profile.shinyCharm);
				Util.writeString(file, profile.name);
				Util.writeString(file, profile.bvKey);
			}
			file.close();
		} catch (IOException e) {}
	}
	
}
