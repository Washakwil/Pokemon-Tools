package rng.gen7.egg;

import pokemon.Ability;
import pokemon.Gender;
import pokemon.Nature;
import pokemon.PokemonType;
import pokemon.ShinyState;
import rng.gen7.egg.Gen7EggRNG.Result;

public class Gen7EggFilter {

	int[] minIVs, maxIVs;
	Nature[] natures;
	PokemonType[] hpTypes;
	Gender gender, ball;
	Ability ability;
	boolean shinyOnly;
	
	public Gen7EggFilter(int[] minIVs, int[] maxIVs, Nature[] natures, PokemonType[] hpTypes, Gender gender, Gender ball,
			Ability ability, boolean shinyOnly) {
		this.minIVs = minIVs;
		this.maxIVs = maxIVs;
		this.natures = natures;
		this.hpTypes = hpTypes;
		this.gender = gender;
		this.ball = ball;
		this.ability = ability;
		this.shinyOnly = shinyOnly;
	}
	
	public boolean isAccepted(Result result) {
		
		if(gender != null && gender != result.getGender()) return false;
		
		if(ball != null  && ball != result.getInheritBall()) return false;
		
		if(ability != null && ability != result.getAbility()) return false;
		
		if(shinyOnly && result.getShiny() == ShinyState.NOT_SHINY) return false;
		
		for(int i = 0; i < 6; i++) {
			int iv = result.getIV(i);
			if(iv < minIVs[i] || iv > maxIVs[i]) return false;
		}
		
		boolean accept = true;
		for(int i = 0; i < natures.length; i++) {
			accept = natures[i] == result.getNature();
			if(accept) break;
		}
		if(!accept) return false;
		
		for(int i = 0; i < hpTypes.length; i++) {
			accept = hpTypes[i] == result.getHpType();
			if(accept) break;
		}
		if(!accept) return false;

		return true;
	}
	
}
