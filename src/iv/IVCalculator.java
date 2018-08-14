package iv;

import java.util.ArrayList;

public class IVCalculator {

	private Integer[][] iv = new Integer[6][];
	
	public IVCalculator(int[] stats, int[] baseStats, int[] ev, int[] natureEffect, int level) {
		for(int stat = 0; stat < stats.length; stat++) {
			int minIV;
			double nature = 1;
			if(stat > 0) nature = 1 + natureEffect[stat - 1] * 0.1d;
			if(stat == 0) minIV = (int) Math.ceil((stats[stat] - level - 10) * 100d / level) - ev[stat] / 4 - 2 * baseStats[stat];
			else minIV = (int) Math.ceil(Math.ceil(stats[stat]/nature - 5) * 100 / level) - ev[stat] / 4 - 2 * baseStats[stat];
			if(minIV < 0) minIV = 0;
			if(minIV > 31) minIV = 31;
			int maxIV;
			for(maxIV = minIV; maxIV < 32; maxIV++) {
				int statValue;
				if(stat == 0) statValue = (2 * baseStats[stat] + maxIV + ev[stat] / 4) * level / 100 + level + 10;
				else statValue = (int) Math.floor(((2 * baseStats[stat] + maxIV + ev[stat] / 4) * level / 100 + 5) * nature);
				if(statValue != stats[stat]) {
					maxIV--;
					break;
				}
				if(maxIV == 31) break;
			}
			iv[stat] = new Integer[maxIV - minIV + 1];
			for(int i = 0; i < iv[stat].length; i++) {
				iv[stat][i] = minIV + i;
			}
		}
	}
	
	public void filterDVs(Integer[][] lastIV, Characteristic characteristic, Judge[] judge) {
		for(int stat = 0; stat < iv.length; stat++) {
			ArrayList<Integer> possibleIVs = new ArrayList<>(iv[stat].length);
			for(int i = 0; i < iv[stat].length; i++) {
				possibleIVs.add(iv[stat][i]);
			}
			if(lastIV != null) {
				for(int i = possibleIVs.size() - 1; i >= 0; i--) {
					boolean remove = true;
					for(int j = 0; j < lastIV[stat].length; j++) {
						if(lastIV[stat][j] == possibleIVs.get(i)) {
							remove = false;
							break;
						}
						
					}
					if(remove) possibleIVs.remove(i);
				}
			}
			for(int i = possibleIVs.size() - 1; i >= 0; i--) {
				if(!characteristic.matches(stat, possibleIVs.get(i))) {
					possibleIVs.remove(i);
				}
			}
			for(int i = possibleIVs.size() - 1; i >= 0; i--) {
				if(!judge[stat].matches(possibleIVs.get(i))) {
					possibleIVs.remove(i);
				}
			}
			iv[stat] = possibleIVs.toArray(new Integer[possibleIVs.size()]);
		}
	}
	
	public Integer[][] getIV() {
		return iv;
	}

	public String getDVString(int stat) {
		if(iv[stat].length == 0) return "Invalid";
		else {
			String s = "";
			Integer[] possibleIVs = iv[stat];
			int lastIV = -2;
			for(int i = 0; i < possibleIVs.length; i++) {
				if(!(lastIV + 1 == possibleIVs[i])) {
					if(s.length() == 0) {
						s += possibleIVs[i];
					} else {
						if(s.endsWith("-")) s += lastIV;
						 s += ", " + possibleIVs[i];
					}
				} else {
					if(!s.endsWith("-")) s += "-";
					if(i == possibleIVs.length - 1) s += possibleIVs[i];
				}
				lastIV = possibleIVs[i];
			}
			return s;
		}
	}

	public void shedinja() {
		iv[0] = new Integer[32];
		for(int i = 0; i < iv[0].length; i++) {
			iv[0][i] = i;
		}
	}
	
}
