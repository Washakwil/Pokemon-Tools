package pokemon;

public enum GenderRatio {
	
	MALE7_FEMALE1("♂:87,5% ♀:12,5%", 31),
	MALE3_FEMALE1("♂:75% ♀:25%", 63),
	MALE1_FEMALE1("♂:50% ♀:50%", 127),
	MALE1_FEMALE3("♂:25% ♀:75%", 191),
	MALE1_FEMALE7("♂:12,5% ♀:87,5%", 225),
	MALE_ONLY("♂:100% ♀:0%", 0),
	FEMALE_ONLY("♂:0% ♀:100%", 254),
	GENDERLESS("Genderless", 1);
	
	private String s;
	private int baseValue;

	private GenderRatio(String s, int baseValue) {
		this.s = s;
		this.baseValue = baseValue;
	}
	
	@Override
	public String toString() {
		return s;
	}
	
	public int getCuteCharmHardyMalePID() {
		return baseValue + 25 - baseValue % 25;
	}
	
	public Gender getFixedGender() {
		switch(this) {
		case MALE_ONLY:
			return Gender.MALE;
		case FEMALE_ONLY:
			return Gender.FEMALE;
		default:
			return Gender.GENDERLESS;
		}
	}

	public boolean isFixed() {
		switch(this) {
		case MALE_ONLY:
		case FEMALE_ONLY:
		case GENDERLESS:
			return true;
		default:
			return false;
		}
	}
	
	public Gender getGender(int value) {
		value = Integer.remainderUnsigned(value, 252);
		if(value < baseValue - 1) return Gender.FEMALE;
		else return Gender.MALE;
	}

	public static GenderRatio[] getRandomGenderRatios(int gen) {
		if(gen < 6) return new GenderRatio[] {MALE7_FEMALE1, MALE3_FEMALE1, MALE1_FEMALE1, MALE1_FEMALE3};
		else return new GenderRatio[] {MALE7_FEMALE1, MALE3_FEMALE1, MALE1_FEMALE1, MALE1_FEMALE3, MALE1_FEMALE7};
	}
	
}
