package rng;

import java.util.Arrays;

import util.Util;

public class RNGState {

	private final int[] status;

	public RNGState(int... status) {
		this.status = Arrays.copyOf(status, status.length);
	}
	
	public int getLength() {
		return status.length;
	}
	
	public int[] get() {
		return Arrays.copyOf(status, status.length);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(status.length * 8);
		for(int i = 0; i < status.length; i++) {
			sb.append(Util.add0s(Integer.toHexString(status[i]), 8));
			if(i < status.length - 1) sb.append(", ");
		}
		return sb.toString();
	}

	public int get(int i) {
		return status[i];
	}
	
}
