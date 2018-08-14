package rng.gen4.id.cutecharm;

import pokemon.Gender;
import pokemon.GenderRatio;
import pokemon.Nature;

public class Gen4CCEffect {
	
	private GuiGen4CCIDs output;
	
	public Gen4CCEffect(GuiGen4CCIDs output) {
		this.output = output;
	}
	
	public void idEffectSearch(int tid, int sid) {
		int tsv = (tid ^ sid) >>> 3;
		for(int pid = 0; pid < Nature.values().length; pid++) {
			int psv = ((pid & 0xFFFF) ^ (pid >>> 16)) >>> 3;
			if(tsv == psv) {
				output.addToTable(Gender.MALE, null, pid);
			}
		}
		for(GenderRatio gr : GenderRatio.getRandomGenderRatios(4)) {
			int minPID = gr.getCuteCharmHardyMalePID();
			for(int pid = minPID; pid < Nature.values().length + minPID; pid++) {
				int psv = ((pid & 0xFFFF) ^ (pid >>> 16)) >>> 3;
				if(tsv == psv) {
					output.addToTable(Gender.FEMALE, gr, pid);
				}
			}
		}
	}
	
	public void pidSearch(Gender ccGender, GenderRatio genderRatio, Nature nature) {
		int shinyPID = 0;
		if(ccGender == Gender.FEMALE) {
			shinyPID = genderRatio.getCuteCharmHardyMalePID();
		}
		shinyPID += nature.ordinal();
		idEffectSearch(shinyPID & 0xFFFF, shinyPID >>> 16);
	}
	
}
