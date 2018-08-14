package rng;

public interface RNG32 {

	public void init(int seed);
	public void init(RNGState status);
	public void next(int f);
	public void nextState();
	public int nextInt();
	public RNGState getState();
	
}
