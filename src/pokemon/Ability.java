package pokemon;

import util.Util;

public enum Ability {

	ABILITY_1,
	ABILITY_2,
	HIDDEN_ABILITY;

	public Ability getInheritedAbility(int value) {
		value = Integer.remainderUnsigned(value, 100);
		switch(this) {
		case ABILITY_1:
			return value < 80 ? ABILITY_1 : ABILITY_2;
		case ABILITY_2:
			return value < 80 ? ABILITY_2 : ABILITY_1;
		case HIDDEN_ABILITY:
			if(value < 20) return ABILITY_1;
			else if(value < 40) return ABILITY_2;
			else return HIDDEN_ABILITY;
		default:
			return value < 50 ? ABILITY_1 : ABILITY_2;
		}
	}

	public static Ability getAbility(int value) {
		return (value & 1) == 0 ? Ability.ABILITY_1 : ABILITY_2;
	}

	@Override
	public String toString() {
		return Util.capitalize(name());
	}
	
}
