package rng.gen4.id;

import rng.MersenneTwister;
import rng.Search;

public class Gen4IDRNG {
	
	public int[] getIDs(int seed) {
		MersenneTwister mt = new MersenneTwister(seed);
		mt.nextInt();
		int ids = mt.nextInt();
		return new int[] {ids & 0xFFFF, ids >>> 16};
	}

	public class IDSearch implements Search {
		
		private GuiGen4IDRNG output;
		private Gen4IDFilter filter;
		private int year, minDelay, maxDelay;
		private boolean cancel = false;
		
		public IDSearch(GuiGen4IDRNG output, int year, int minDelay, int maxDelay, Gen4IDFilter filter) {
			year -= 2000;
			this.output = output;
			this.year = year;
			this.minDelay = minDelay;
			this.maxDelay = maxDelay;
			this.filter = filter;
		}
		
		@Override
		public void run() {
			int minSeedCCCC = (year + minDelay) & 0xFFFF;
			int maxSeedCCCC = (year + maxDelay) & 0xFFFF;
			int maxSeedCount = (maxSeedCCCC - minSeedCCCC + 1) * 0x1800;
			int seedCount = 0;
			for(int seedCCCC = minSeedCCCC; seedCCCC <= maxSeedCCCC; seedCCCC++) {
				for(int seedAA = 0; seedAA < 0x100; seedAA++) {
					for(int seedBB = 0; seedBB < 24; seedBB++) {
						int seed = (seedAA << 24) | (seedBB << 16) | seedCCCC;
						int[] ids = getIDs(seed);
						if(filter.isAccepted(ids[0], ids[1])) output.addToTable(seed, ids[0], ids[1], seedCCCC - year, 0);
						seedCount++;
						output.setProgress((int) (seedCount * 100 / maxSeedCount));
						if(cancel) {
							output.cancelSearch();
							return;
						}
					}
				}
			}
			output.cancelSearch();
		}

		public void cancel() {
			cancel = true;
		}

		@Override
		public void start() {
			new Thread(this).start();
		}
		
	}

	public class SeedSearch implements Search {
		
		private GuiGen4IDRNG output;
		private int otid, year, month, day, hour, minute, minDelay, maxDelay;
		private boolean cancel;
		
		public SeedSearch(GuiGen4IDRNG output, int otid, int year, int month, int day, int hour, int minute, int minDelay, int maxDelay) {
			this.output = output;
			this.otid = otid;
			this.year = year;
			this.month = month;
			this.day = day;
			this.hour = hour;
			this.minute = minute;
			this.minDelay = minDelay;
			this.maxDelay = maxDelay;
			cancel = false;
		}
		
		@Override
		public void run() {
			year -= 2000;
			int minSeedCCCC = (year + minDelay) & 0xFFFF;
			int maxSeedCCCC = (year + maxDelay) & 0xFFFF;
			int maxSeedCount = (maxSeedCCCC - minSeedCCCC + 1) * 60;
			int seedAAMin = (month * day + minute) & 0xFF;
			int seedBB = hour;
			int seedCount = 0;
			for(int second = 0; second < 60; second++) {
				int seedAA = (seedAAMin + second) & 0xFF;
				for(int seedCCCC = minSeedCCCC; seedCCCC <= maxSeedCCCC; seedCCCC++) {
					int seed = (seedAA << 24) | (seedBB << 16) | seedCCCC;
					int[] ids = getIDs(seed);
					if(ids[0] == otid) output.addToTable(seed, otid, ids[1], seedCCCC - year, second);
					seedCount++;
					output.setProgress((int) (seedCount * 100 / maxSeedCount));
					if(cancel) {
						output.cancelSearch();
						return;
					}
				}
			}
			output.cancelSearch();
		}

		public void cancel() {
			cancel = true;
		}

		@Override
		public void start() {
			new Thread(this).start();
		}
		
	}
	
}
