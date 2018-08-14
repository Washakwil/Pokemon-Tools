package rng;

public class LCRNG {

	private int seed;
	private final int MULT;
	private final int ADD;
	
	public LCRNG(int seed, int mult, int add) {
		MULT = mult;
		ADD = add;
		init(seed);
	}

	private void init(int seed) {
		setSeed(seed);
	}
	
	public int nextInt() {
		return seed = seed * MULT + ADD;
	}
	
	public void Advance(int frames) {
		for(int i = 0; i < frames; i++) {
			nextInt();
		}
	}
	
	public int getSeed() {
		return seed;
	}

	public void setSeed(int seed) {
		this.seed = seed;
	}
	
}
