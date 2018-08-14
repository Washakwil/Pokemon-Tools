package pokemon;

import java.util.ArrayList;

import util.Util;

public enum Nature {
	
	HARDY,
	LONELY,
	BRAVE,
	ADAMANT,
	NAUGHTY,
	BOLD,
	DOCILE,
	RELAXED,
	IMPISH,
	LAX,
	TIMID,
	HASTY,
	SERIOUS,
	JOLLY,
	NAIVE,
	MODEST,
	MILD,
	QUIET,
	BASHFUL,
	RASH,
	CALM,
	GENTLE,
	SASSY,
	CAREFUL,
	QUIRKY;
	
	@Override
	public String toString() {
		return Util.capitalize(name());
	}
	
	public int[] getEffect() {
		int[] effect = new int[] {0,0,0,0,0};
		effect[ordinal() / 5] += 1;
		effect[ordinal() % 5] -= 1;
		int speed = effect[2];
		effect[2] = effect[3];
		effect[3] = effect[4];
		effect[4] = speed;
		return effect;
	}

	public static Nature getNature(int value) {
		return values()[Integer.remainderUnsigned(value, 25)];
	}

	public static Nature[] getNatures(String text) {
		ArrayList<Nature> al = new ArrayList<Nature>();
		String[] natureStrings = text.split(",");
		for(String natureString : natureStrings) {
			for(Nature nature : values()) {
				if(natureString.trim().equals(nature.toString())) {
					al.add(nature);
				}
			}
		}
		return al.toArray(new Nature[al.size()]);
	}
}
