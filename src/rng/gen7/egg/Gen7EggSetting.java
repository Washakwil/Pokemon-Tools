package rng.gen7.egg;

import pokemon.Ability;
import pokemon.Gender;
import pokemon.GenderRatio;
import pokemon.Nature;

public class Gen7EggSetting {

	protected int tsv;
	protected Gender ditto;
	protected boolean homogeneous;
	protected Nature maleNature;
	protected Nature femaleNature;
	protected int[] maleIVs;
	protected int[] femaleIVs;
	protected Gen7BreedingItem maleItem;
	protected Gen7BreedingItem femaleItem;
	protected GenderRatio genderRatio;
	protected boolean nidoran;
	protected boolean considerOtherTSV;
	protected int[] otherTSVs;

	protected boolean everstone;
	protected boolean bothEverstone;
	protected boolean power;
	protected boolean bothPower;
	protected int malePower;
	protected int femalePower;
	protected int pidRerollCount;
	protected int inheritIVsCount;
	protected Ability inheritAbility;
	protected boolean fixedGender;
	
	public Gen7EggSetting(int tsv, Gender ditto, boolean homogeneous, Nature maleNature, Nature femaleNature,
			int[] maleIVs, int[] femaleIVs, Gen7BreedingItem maleItem, Gen7BreedingItem femaleItem, GenderRatio genderRatio,
			Ability maleAbility, Ability femaleAbility, boolean shinyCharm, boolean masuda, boolean nidoran,
			boolean considerOtherTSV, int[] otherTSVs) {
		this.tsv = tsv;
		this.ditto = ditto;
		this.homogeneous = homogeneous;
		this.maleNature = maleNature;
		this.femaleNature = femaleNature;
		this.maleIVs = maleIVs;
		this.femaleIVs = femaleIVs;
		this.maleItem = maleItem;
		this.femaleItem = femaleItem;
		this.genderRatio = genderRatio;
		this.nidoran = nidoran;
		this.considerOtherTSV = considerOtherTSV;
		this.otherTSVs = otherTSVs;
		
		everstone = maleItem == Gen7BreedingItem.EVERSTONE || femaleItem == Gen7BreedingItem.EVERSTONE;
		bothEverstone = maleItem == Gen7BreedingItem.EVERSTONE && femaleItem == Gen7BreedingItem.EVERSTONE;
		power = maleItem.isPowerItem() || femaleItem.isPowerItem();
		bothPower = maleItem.isPowerItem() && femaleItem.isPowerItem();
		malePower = maleItem.getPower();
		femalePower = femaleItem.getPower();
		pidRerollCount = 0;
		if(shinyCharm) pidRerollCount += 2;
		if(masuda) pidRerollCount += 6;
		if(maleItem == Gen7BreedingItem.DESTINY_KNOT || femaleItem == Gen7BreedingItem.DESTINY_KNOT) inheritIVsCount = 5;
		else inheritIVsCount = 3;
		if(power) inheritIVsCount--;
		inheritAbility = ditto == Gender.FEMALE ? maleAbility : femaleAbility;
		fixedGender = genderRatio.isFixed();
	}
	
}
