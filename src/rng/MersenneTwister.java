package rng;

public class MersenneTwister implements RNG32 {

	private static final int N = 624;
	private static final int M = 397;
	private int[] mt = new int[N];
	private int mti;
	
	public MersenneTwister() {
		init((int) System.nanoTime());
	}
	
	public MersenneTwister(int seed) {
		init(seed);
	}

	@Override
	public void init(int seed) {
		mt[0] = seed;
		for(mti = 1; mti < N; mti++) {
			mt[mti] = (int) (1812433253 * (mt[mti - 1] ^ (mt[mti - 1] >>> 30)) + mti);
		}
		mti = 0;
	}

	@Override
	public void init(RNGState status) {
		if(status.getLength() != N) throw new IllegalArgumentException();
		this.mt = status.get();
	}
	
	@Override
	public int nextInt() {
		short kk = (short)(mti < N - 1 ? mti + 1 : 0);
		short jj = (short)(mti < N - M ? mti + M : mti + M - N);
		int y = (mt[mti] & 0x80000000) | (mt[kk] & 0x7fffffff);
		mt[mti] = mt[jj] ^ (y >>> 1);
		if((y & 1) == 1) mt[mti] ^= 0x9908b0df;
		
		y = mt[mti];
		y ^= y >>> 11;
		y ^= (y << 7) & 0x9d2c5680;
		y ^= (y << 15) & 0xefc60000;
		y ^= y >>> 18;
		
		mti = kk;
		
		return y;
	}

	@Override
	public void nextState() {
		nextInt();
	}

	@Override
	public RNGState getState() {
		return new RNGState(mt[mti]);
	}

	@Override
	public void next(int f) {
		for(int i = 0; i < f; i++) nextState();
	}
	
}
