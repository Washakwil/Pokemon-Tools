package iv;

import pokemon.Game;

public class Judge {

	public static final Judge[] GEN_4_JUDGES = new Judge[] {
			new Judge("None", -1, 32),
			new Judge("Rather Decent (0-15)", 0, 15),
			new Judge("Very good (16-25)", 16, 25),
			new Judge("Fantastic (26-30)", 26, 30),
			new Judge("Can't be better (31)", 31, 31),
	};

	public static final Judge[] GEN_5_JUDGES = new Judge[] {
			new Judge("None", -1, 32),
			new Judge("Rather Decent (0-15)", 0, 15),
			new Judge("Good (16-25)", 16, 25),
			new Judge("Fantastic (26-30)", 26, 30),
			new Judge("Can't be better (31)", 31, 31),
	};

	public static final Judge[] GEN_6_JUDGES = new Judge[] {
			new Judge("None", -1, 32),
			new Judge("No good (0)", 0, 0),
			new Judge("Rather Decent (1-15)", 0, 15),
			new Judge("Good (16-25)", 16, 25),
			new Judge("Fantastic (26-30)", 26, 30),
			new Judge("Can't be beat (31)", 31, 31),
	};

	public static final Judge[] GEN_7_JUDGES = new Judge[] {
			new Judge("None", -1, 32),
			new Judge("No good (0)", 0, 0),
			new Judge("Decent (1-15)", 1, 15),
			new Judge("Pretty good (16-25)", 16, 25),
			new Judge("Very good (26-29)", 26, 29),
			new Judge("Fantastic (30)", 30, 30),
			new Judge("Best (31)", 31, 31),
	};
	
	private int minValue, maxValue;
	private String text;

	private Judge(String text, int minValue, int maxValue) {
		this.text = text;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	@Override
	public String toString() {
		return text;
	}

	public boolean matches(int iv) {
		if(minValue < 0) return true;
		return iv >= minValue && iv <= maxValue;
	}

	public static Judge[] getJudges(Game game) {
		int gen = game.getGeneration();
		if(gen == 4) return GEN_4_JUDGES;
		if(gen == 5) return GEN_5_JUDGES;
		if(gen == 6) return GEN_6_JUDGES;
		if(gen == 7) return GEN_7_JUDGES;
		return new Judge[0];
	}
	
}
