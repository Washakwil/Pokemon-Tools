package rng.gen7.egg;

import java.util.ArrayList;
import java.util.Collections;

import pokemon.Ability;
import pokemon.Gender;
import pokemon.Nature;
import pokemon.ShinyState;
import pokemon.PokemonType;
import rng.RNGPool;
import rng.RNGState;
import rng.TinyMT;

public class Gen7EggRNG {
	
	private RNGPool rngPool;
	private Gen7EggSetting setting;
	private GuiGen7EggRNG output;

	public Gen7EggRNG(GuiGen7EggRNG output, RNGState status, Gen7EggSetting setting) {
		this.output = output;
		rngPool = new RNGPool(new TinyMT(status), 100);
		this.setting = setting;
	}
	
	public void generateFrame(int minFrame, int maxFrame, Gen7EggFilter filter) {
		boolean ignoreFilter = filter == null;
		for(int frame = 0; frame < minFrame; frame++) {
			rngPool.next();
		}
		for(int frame = minFrame; frame <= maxFrame; frame++) {
			Result result = new Result(frame, rngPool, setting);
			if(ignoreFilter || filter.isAccepted(result)) output.addToTable(result);
			rngPool.next();
		}
	}
	
	public void generateEgg(int minEgg, int maxEgg, Gen7EggFilter filter) {
		boolean ignoreFilter = filter == null;
		for(int egg = 1, frame = 0; egg <= maxEgg; egg++) {
			Result result = new Result(frame, rngPool, setting);
			if(egg >= minEgg && (ignoreFilter || filter.isAccepted(result))) output.addToTable(result, egg);
			rngPool.next(result.getFrameAdvance());
			frame += result.getFrameAdvance();
		}
	}
	
	public void generateShorstestPath(int targetFrame) {
		targetFrame++;
		Result[] results = new Result[targetFrame];
		for(int frame = 0; frame < targetFrame; frame++) {
			Result result = new Result(frame, rngPool, setting);
			results[frame] = result;
			rngPool.next();
		}
		int[] pre = new int[targetFrame];
		int[] w = new int[targetFrame];
		for (int i = 1; i < targetFrame; i++) {
			w[i] = Integer.MAX_VALUE;
		}
		for (int i = 0; i < targetFrame; i++)
		{
			if (i != 0 && w[i] > w[i - 1] + 1)
			{
				pre[i] = i - 1;
				w[i] = w[i - 1] + 1;
			}
			for (int j = i, k = i + results[i].getFrameAdvance(); k < targetFrame; j = k, k = j + results[j].getFrameAdvance())
			{
				if (w[k] > w[j] + 1)
				{
					pre[k] = j;
					w[k] = w[j] + 1;
				}
			}
		}
		ArrayList<Integer> frameIndexList = new ArrayList<Integer>();
		for (int node = targetFrame - 1; node > 0; node = pre[node]) {
			frameIndexList.add(node);
		}
		frameIndexList.add(0);
		Collections.reverse(frameIndexList);
		int length = frameIndexList.size();
		for(int i = 1; i <= length; i++) {
			int index = frameIndexList.get(i - 1);
			Result result = results[index];
			boolean accept = i == length || frameIndexList.get(i) - index > 1;
			if(!accept) result.frameAdvance = 1;
			output.addToTable(result, i, true);
		}
	}
	
	public class Result {
		
		private int frame, frameAdvance, rand, ec;
		private int pid, psv;
		private ShinyState shiny;
		private RNGState rngState;
		private Nature nature;
		private int[] ivs = new int[6];
		private Gender[] inheritIvs = new Gender[6];
		private Gender inheritNature;
		private Gender inheritBall;
		private Gender gender;
		private PokemonType hpType;
		private Ability ability;
		
		public Result(int frame, RNGPool rngPool, Gen7EggSetting setting) {
			
			//RNG
			this.frame = frame;
			rngState = rngPool.getState();
			rand = rngPool.getCurrentRand();
			
			//Gender
			if(setting.nidoran) {
				gender = Gender.getGender(rngPool.getRand());
			} else if(setting.fixedGender) {
				gender = setting.genderRatio.getFixedGender();
			} else {
				gender = setting.genderRatio.getGender(rngPool.getRand());
			}
			
			//Nature
			nature = Nature.getNature(rngPool.getRand());
			
			if(setting.bothEverstone) {
				inheritNature = Gender.getGender(rngPool.getRand());
			} else if(setting.everstone) {
				inheritNature = setting.maleItem == Gen7BreedingItem.EVERSTONE ? Gender.MALE : Gender.FEMALE;
			}
			
			if(inheritNature != null) {
				nature = inheritNature == Gender.MALE ? setting.maleNature : setting.femaleNature;
			}
			
			//Ability
			ability = setting.inheritAbility.getInheritedAbility(rngPool.getRand());
			
			//PowerItem
			if(setting.power) {
				if(setting.bothPower) {
					if((rngPool.getRand() & 1) == 0) {
						inheritIvs[setting.malePower] = Gender.MALE;
					} else {
						inheritIvs[setting.femalePower] = Gender.FEMALE;
					}
				} else if(setting.malePower >= 0) {
					inheritIvs[setting.malePower] = Gender.MALE;
				} else {
					inheritIvs[setting.femalePower] = Gender.FEMALE;
				}
			}
			
			//InheritIV
			for(int i = 0; i < setting.inheritIVsCount; i++) {
				int iv;
				do {
					iv = Integer.remainderUnsigned(rngPool.getRand(), 6);
				} while(inheritIvs[iv] != null);
				inheritIvs[iv] = Gender.getGender(rngPool.getRand());
			}
			
			//IV
			for(int i = 0; i < 6; i++) {
				ivs[i] = rngPool.getRand() & 0x1F;
				if(inheritIvs[i] != null) {
					if(inheritIvs[i] == Gender.MALE) ivs[i] = setting.maleIVs[i];
					else ivs[i] = setting.femaleIVs[i];
				}
			}
			
			//HP
			hpType = PokemonType.getHPType(ivs);
			
			//Encryption Constant
			ec = rngPool.getRand();
			
			//PID
			boolean shiny = false;
			
			for(int i = setting.pidRerollCount; i > 0; i--) {
				pid = rngPool.getRand();
				psv = ((pid >>> 16) ^ (pid & 0xFFFF)) >>> 4;
				shiny = setting.tsv == psv;
				if(shiny) break;
			}
			
			//OtherTSVs
			if(setting.considerOtherTSV) {
				for(int i = 0; i < setting.otherTSVs.length; i++) {
					shiny = setting.otherTSVs[i] == psv;
					if(shiny) break;
				}
			}
			
			this.shiny = ShinyState.getShinyState(shiny);
			
			//Ball
			if(setting.homogeneous) {
				inheritBall = Integer.remainderUnsigned(rngPool.getRand(), 100) < 50 ? Gender.FEMALE : Gender.MALE;
			} else inheritBall = setting.ditto == Gender.FEMALE ? Gender.MALE : Gender.FEMALE;
			
			rngPool.advance(2);
			
			//Advancement
			frameAdvance = rngPool.getAdvance();
		}

		public int getFrame() {
			return frame;
		}

		public int getFrameAdvance() {
			return frameAdvance;
		}

		public int getRand() {
			return rand;
		}

		public int getEC() {
			return ec;
		}

		public int getPID() {
			return pid;
		}

		public int getPSV() {
			return psv;
		}

		public ShinyState getShiny() {
			return shiny;
		}

		public RNGState getRngState() {
			return rngState;
		}

		public Nature getNature() {
			return nature;
		}

		public Gen7InheritedNature getInheritedNature() {
			return new Gen7InheritedNature(nature, inheritNature);
		}

		public int getIV(int i) {
			return ivs[i];
		}

		public Gen7InheritedIV getInheritedIV(int i) {
			return new Gen7InheritedIV(ivs[i], inheritIvs[i]);
		}

		public Gender getInheritBall() {
			return inheritBall;
		}

		public Gender getGender() {
			return gender;
		}

		public PokemonType getHpType() {
			return hpType;
		}

		public Ability getAbility() {
			return ability;
		}
		
	}
	
}
