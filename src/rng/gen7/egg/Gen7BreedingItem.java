package rng.gen7.egg;

import util.Util;

public enum Gen7BreedingItem {

	NONE(-1),
	EVERSTONE(-1),
	DESTINY_KNOT(-1),
	POWER_WEIGHT(0),
	POWER_BRACER(1),
	POWER_BELT(2),
	POWER_LENS(3),
	POWER_BAND(4),
	POWER_ANKLET(5);
	
	private int power;
	
	private Gen7BreedingItem(int power) {
		this.power = power;
	}
	
	public int getPower() {
		return power;
	}

	public boolean isPowerItem() {
		return power >= 0;
	}
	
	@Override
	public String toString() {
		return Util.capitalize(name());
	}
	
}
