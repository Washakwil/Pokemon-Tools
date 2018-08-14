package pokemon;

import java.util.ArrayList;

import util.Util;

public enum PokemonType {
		
	NORMAL,
	FIGHTING,
	FLYING,
	POISON,
	GROUND,
	ROCK,
	BUG,
	GHOST,
	STEEL,
	FIRE,
	WATER,
	GRASS,
	ELECTRIC,
	PSYCHIC,
	ICE,
	DRAGON,
	DARK,
	FAIRY;
	
	public static final float NO_EFFECT = 0;
	public static final float NOT_VERY_EFFECTIVE = 0.5f;
	public static final float NORMAL_EFFECTIVE = 1;
	public static final float SUPER_EFFECTIVE = 2;
	private static final int[] hpIndices = new int[] {0, 1, 2, 5, 3, 4};
	private PokemonType[] superEffective, notVeryEffective, noEffect;
	
	private void setEffective(PokemonType[] superEffective, PokemonType[] notVeryEffective, PokemonType[] noEffect) {
		this.superEffective = superEffective;
		this.notVeryEffective = notVeryEffective;
		this.noEffect = noEffect;
	}
	
	@Override
	public String toString() {
		return Util.capitalize(name());
	}
	
	public static PokemonType getHPType(int[] ivs) {
		int hp = 0;
		for(int i = 0; i < 6; i++) {
			hp |= (ivs[hpIndices[i]] & 1) << i;
		}
		hp *= 0xF;
		hp /= 0x3F;
		return values()[hp + 1];
	}
	
	public float getDamageMultiplier(PokemonType type1, PokemonType type2) {
		return this.getDamageMultiplier(type1) * this.getDamageMultiplier(type2);
	}
	
	public float getDamageMultiplier(PokemonType type) {
		for(PokemonType t : superEffective) {
			if(t == type) return SUPER_EFFECTIVE;
		}
		for(PokemonType t : notVeryEffective) {
			if(t == type) return NOT_VERY_EFFECTIVE;
		}
		for(PokemonType t : noEffect) {
			if(t == type) return NO_EFFECT;
		}
		return NORMAL_EFFECTIVE;
	}
	
	static {
		NORMAL.setEffective(new PokemonType[] {}, new PokemonType[] {ROCK, STEEL}, new PokemonType[] {GHOST});
		FIGHTING.setEffective(new PokemonType[] {NORMAL, ROCK, STEEL, ICE, DARK}, new PokemonType[] {FLYING, POISON, BUG, PSYCHIC, FAIRY}, new PokemonType[] {GHOST});
		FLYING.setEffective(new PokemonType[] {FIGHTING, BUG, GRASS}, new PokemonType[] {ROCK, STEEL, ELECTRIC}, new PokemonType[] {});
		POISON.setEffective(new PokemonType[] {GRASS, FAIRY}, new PokemonType[] {POISON, GROUND, ROCK, GHOST}, new PokemonType[] {STEEL});
		GROUND.setEffective(new PokemonType[] {POISON, ROCK, STEEL, FIRE, ELECTRIC}, new PokemonType[] {BUG, GRASS}, new PokemonType[] {FLYING});
		ROCK.setEffective(new PokemonType[] {FLYING, BUG, FIRE, ICE}, new PokemonType[] {FIGHTING, GROUND, STEEL}, new PokemonType[] {});
		BUG.setEffective(new PokemonType[] {GRASS, PSYCHIC, DARK}, new PokemonType[] {FIGHTING, FLYING, POISON, GHOST, STEEL, FIRE, FAIRY}, new PokemonType[] {});
		GHOST.setEffective(new PokemonType[] {GHOST, PSYCHIC}, new PokemonType[] {DARK}, new PokemonType[] {NORMAL});
		STEEL.setEffective(new PokemonType[] {ROCK, ICE, FAIRY}, new PokemonType[] {STEEL, FIRE, WATER, ELECTRIC}, new PokemonType[] {});
		FIRE.setEffective(new PokemonType[] {BUG, STEEL, GRASS, ICE}, new PokemonType[] {ROCK, FIRE, WATER, DRAGON}, new PokemonType[] {});
		WATER.setEffective(new PokemonType[] {GROUND, ROCK, FIRE}, new PokemonType[] {WATER, GRASS, DRAGON}, new PokemonType[] {});
		GRASS.setEffective(new PokemonType[] {GROUND, ROCK, WATER}, new PokemonType[] {FLYING, POISON, BUG, STEEL, FIRE, GRASS, DRAGON}, new PokemonType[] {});
		ELECTRIC.setEffective(new PokemonType[] {FLYING, WATER}, new PokemonType[] {GRASS, ELECTRIC, DRAGON}, new PokemonType[] {GROUND});
		PSYCHIC.setEffective(new PokemonType[] {FIGHTING, POISON}, new PokemonType[] {STEEL, PSYCHIC}, new PokemonType[] {DARK});
		ICE.setEffective(new PokemonType[] {FLYING, GROUND, GRASS, DRAGON}, new PokemonType[] {STEEL, FIRE, WATER, ICE}, new PokemonType[] {});
		DRAGON.setEffective(new PokemonType[] {DRAGON}, new PokemonType[] {STEEL}, new PokemonType[] {FAIRY});
		DARK.setEffective(new PokemonType[] {GHOST, PSYCHIC}, new PokemonType[] {FIGHTING, DARK, FAIRY}, new PokemonType[] {});
		FAIRY.setEffective(new PokemonType[] {FIGHTING, DRAGON, DARK}, new PokemonType[] {POISON, STEEL, FIRE}, new PokemonType[] {});
	}

	public static PokemonType[] getHPTypes() {
		PokemonType[] result = new PokemonType[16];
		for(int i = 0; i < result.length; i++) {
			result[i] = values()[i + 1];
		}
		return result;
	}

	public static PokemonType[] getTypes(String text) {
		ArrayList<PokemonType> al = new ArrayList<PokemonType>(values().length);
		for(PokemonType type : values()) {
			if(text.indexOf(type.toString()) > -1) {
				al.add(type);
			}
		}
		return al.toArray(new PokemonType[al.size()]);
	}
	
}