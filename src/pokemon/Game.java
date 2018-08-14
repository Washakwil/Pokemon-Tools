package pokemon;

import java.util.ArrayList;

import util.Util;

public enum Game {

	DIAMOND(4),
	PERL(4),
	PLATINUM(4),
	HEARTGOLD(4),
	SOULSILVER(4),
	BLACK(5),
	WHITE(5),
	BLACK_2(5),
	WHITE_2(5),
	X(6),
	Y(6),
	OMEGA_RUBY(6),
	ALPHA_SAPHIR(6),
	SUN(7),
	MOON(7),
	ULTRA_SUN(7),
	ULTRA_MOON(7);
	
	private int generation;

	private Game(int generation) {
		this.generation = generation;
	}
	
	public int getGeneration() {
		return generation;
	}
	
	public static Game[] getGames(int generation) {
		ArrayList<Game> games = new ArrayList<Game>();
		for(Game game : values()) {
			if(game.generation == generation) games.add(game);
		}
		return games.toArray(new Game[games.size()]);
	}
	
	@Override
	public String toString() {
		return Util.capitalize(name());
	}

	public static Game getGame(int index) {
		try {
			return values()[index];
		} catch(ArrayIndexOutOfBoundsException e) {
			return values()[0];
		}
	}

	public boolean shinyCharm() {
		return generation >= 6 || this == Game.BLACK_2 || this == Game.WHITE_2;
	}
	
}
