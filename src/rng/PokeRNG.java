package rng;

public class PokeRNG extends LCRNG {

	public PokeRNG(int seed) {
		super(seed, 0x41c64e6d, 0x6073);
	}
	
}