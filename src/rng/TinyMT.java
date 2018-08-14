package rng;

public class TinyMT implements RNG32 {
	
	public int[] status;
	public static final int mat1 = 0x8f7011ee;
	public static final int mat2 = 0xfc78ff1f;
	public static final int tmat = 0x3793fdff;
	
	private static final int MIN_LOOP = 8;
	private static final int PRE_LOOP = 8;
	
	public TinyMT(int seed) {
		init(seed);
	}
	
	public TinyMT(RNGState status) {
		init(status);
	}
	
	@Override
	public void init(RNGState status) {
		if(status.getLength() != 4) throw new IllegalArgumentException();
		this.status = status.get();
		period_certification();
	}

	@Override
	public void init(int seed) {
		status = new int[] {seed, mat1, mat2, tmat};
		
		for (int i = 1; i < MIN_LOOP; i++) {
			status[i & 3] ^= (int) i + 1812433253 * (status[(i - 1) & 3] ^ (status[(i - 1) & 3] >>> 30));
		}
		
		period_certification();
		
		for (int i = 0; i < PRE_LOOP; i++) {
			nextState();
		}
	}
	
	@Override
	public void nextState() {
		int y = status[3];
		int x = (status[0] & 0x7FFFFFFF) ^ status[1] ^ status[2];
		x ^= (x << 1);
		y ^= (y >>> 1) ^ x;
		status[0] = status[1];
		status[1] = status[2];
		status[2] = x ^ (y << 10);
		status[3] = y;
		
		if ((y & 1) == 1)
		{
			status[1] ^= mat1;
			status[2] ^= mat2;
		}
	}

	private int temper()
	{
		int t0 = status[3];
		int t1 = status[0] + (status[2] >>> 8);
		
		t0 ^= t1;
		if ((t1 & 1) == 1)
		{
			t0 ^= tmat;
		}
		return t0;
	}
	
	private void period_certification() {
		if ((status[0] & 0x7FFFFFFF) == 0 && status[1] == 0 && status[2] == 0 && status[3] == 0) {
			status = new int[] {'T', 'I', 'N', 'Y'};
		}
	}
	
	@Override
	public int nextInt() {
		nextState();
		return temper();
	}

	@Override
	public RNGState getState() {
		return new RNGState(status);
	}

	@Override
	public void next(int f) {
		for(int i = 0; i < f; i++) nextState();
	}
	
}
