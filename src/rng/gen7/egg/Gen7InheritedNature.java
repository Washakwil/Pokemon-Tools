package rng.gen7.egg;

import java.awt.Color;

import pokemon.Gender;
import pokemon.Nature;

public class Gen7InheritedNature implements Comparable<Gen7InheritedNature> {

	private Nature nature;
	private Gender inheritNature;
	
	public Gen7InheritedNature(Nature nature, Gender inheritNature) {
		this.nature = nature;
		this.inheritNature = inheritNature;
	}
	@Override
	public String toString() {
		return nature.toString();
	}
	
	public Color getForeground() {
		return Gender.getColor(inheritNature);
	}
	@Override
	public int compareTo(Gen7InheritedNature o) {
		return nature.compareTo(o.nature);
	}
	
}
