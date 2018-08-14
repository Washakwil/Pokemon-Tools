package rng.gen7.egg;

import java.awt.Color;

import pokemon.Gender;

public class Gen7InheritedIV implements Comparable<Gen7InheritedIV> {

	private int iv;
	private Gender inheritIV;
	
	public Gen7InheritedIV(int iv, Gender inheritIV) {
		this.iv = iv;
		this.inheritIV = inheritIV;
	}
	
	@Override
	public String toString() {
		return Integer.toString(iv);
	}
	
	public Color getForeground() {
		return Gender.getColor(inheritIV);
	}

	@Override
	public int compareTo(Gen7InheritedIV o) {
		return Integer.compare(iv, o.iv);
	}
	
	public int getIV() {
		return iv;
	}
	
}
