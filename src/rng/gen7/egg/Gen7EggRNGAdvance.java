package rng.gen7.egg;

import java.awt.Color;

public class Gen7EggRNGAdvance {

	private boolean accept;
	private int frameAdvance;
	
	public Gen7EggRNGAdvance(boolean accept, int frameAdvance) {
		this.accept = accept;
		this.frameAdvance = frameAdvance;
	}
	
	public Gen7EggRNGAdvance(int frameAdvance) {
		this(frameAdvance > 1, frameAdvance);
	}

	@Override
	public String toString() {
		String str = accept ? "Accept" : "Reject";
		return str + " (" + frameAdvance  + ")";
	}
	
	public Color getColor() {
		return accept ? Color.GREEN : Color.RED;
	}
	
}
