package iv;

import util.Util;

public enum Characteristic {

	NONE,
	LOVES_TO_EAT,
	PROUD_OF_ITS_POWER,
	STURDY_BODY,
	HIGHLY_CURIOUS,
	STRONG_WILLED,
	LIKES_TO_RUN,
	TAKES_PLENTY_OF_SIESTAS,
	LIKES_TO_THRASH_ABOUT,
	CAPABLE_OF_TAKING_HITS,
	MISCHIEVOUS,
	SOMEWHAT_VAIN,
	ALERT_TO_SOUNDS,
	NODS_OFF_A_LOT,
	A_LITTLE_QUICK_TEMPERED,
	HIGHLY_PERSISTENT,
	THOROUGHLY_CUNNING,
	STRONGLY_DEFIANT,
	IMPETUOUS_AND_SILLY,
	SCATTERS_THINGS_OFTEN,
	LIKES_TO_FIGHT,
	GOOD_ENDURANCE,
	OFTEN_LOST_IN_THOUGHT,
	HATES_TO_LOSE,
	SOMEWHAT_OF_A_CLOWN,
	LIKES_TO_RELAX,
	QUICK_TEMPERED,
	GOOD_PERSEVERANCE,
	VERY_FINICKY,
	SOMEWHAT_STUBBORN,
	QUICK_TO_FLEE;
	
	@Override
	public String toString() {
		return Util.capitalizeFirst(name());
	}
	
	public boolean matches(int stat, int iv) {
		if(this == NONE) return true;
		int index = ordinal() - 1;
		if(index % 6 != stat) return true;
		return iv % 5 == index / 6;
	}
	
}
