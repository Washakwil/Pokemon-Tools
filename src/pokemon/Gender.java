package pokemon;

import java.awt.Color;

public enum Gender {

	MALE("♂"),
	FEMALE("♀"),
	GENDERLESS("-");
	
	private String s;
	
	private Gender(String s) {
		this.s = s;
	}
	
	@Override
	public String toString() {
		return s;
	}

	public static Gender getGender(int value) {
		if((value & 1) == 0) return MALE;
		else return FEMALE;
	}

	public static Color getColor(Gender gender) {
		if(gender == MALE) return Color.BLUE;
		else if(gender == FEMALE) return Color.RED;
		else return null;
	}

	public static Gender[] getGenders() {
		return new Gender[] {MALE, FEMALE};
	}
	
}
