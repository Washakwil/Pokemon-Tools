package rng;

import java.nio.IntBuffer;

public class RNGPool {
	
	private IntBuffer randList;
	private RNGState[] rngStateList;
	private RNG32 rng;
	private int mark;
	
	public RNGPool(RNG32 rng, int bufferSize) {
		randList = IntBuffer.allocate(bufferSize);
		rngStateList = new RNGState[bufferSize];
		this.rng = rng;
		for(int i = 0; i < bufferSize; i++) {
			rngStateList[i] = rng.getState();
			randList.put(rng.nextInt());
		}
		randList.position(0);
		mark();
	}
	
	public int getRand() {
		int rand = randList.get();
		checkPos();
		return rand;
	}

	public void advance(int f) {
		int newPos = randList.position() + f;
		if(newPos >= randList.limit()) newPos -= randList.limit();
		randList.position(newPos);
	}
	
	public int getCurrentRand() {
		return randList.get(randList.position());
	}
	
	public RNGState getState() {
		return rngStateList[randList.position()];
	}
	
	public void next() {
		reset();
		rngStateList[randList.position()] = rng.getState();
		randList.put(rng.nextInt());
		checkPos();
		mark();
	}
	
	public void next(int f) {
		reset();
		for(int i = 0; i < f; i++) {
			rngStateList[randList.position()] = rng.getState();
			randList.put(rng.nextInt());
			checkPos();
		}
		mark();
	}

	private void checkPos() {
		if(randList.position() == randList.limit()) randList.position(0);
	}

	public int getBufferSize() {
		return randList.capacity();
	}
	
	public int getAdvance() {
		if(mark < randList.position()) {
			return randList.position() - mark;
		} else {
			return randList.capacity() - mark + randList.position();
		}
	}
	
	public void mark() {
		mark = randList.position();
	}
	
	public void reset() {
		randList.position(mark);
	}
	
}
