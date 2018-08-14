package rng.gen4;

import java.time.YearMonth;

public class Gen4SeedToTime {

	private int seed, year;
	private GuiGen4SeedToTime output;

	public Gen4SeedToTime(GuiGen4SeedToTime output, int seed, int year) {
		this.seed = seed;
		this.year = year;
		this.output = output;
	}

	public void generate() {
		int seedAA = seed >>> 24;
		int seedBB = (seed >>> 16) & 0xFF;
		int seedCCCC = seed & 0xFFFF;
		int delay = seedCCCC + 2000 - year;
		int hour = seedBB;
		if(hour > 23) return;
		for(int month = 1; month <= 12; month++) {
			int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
			for(int day = 1; day <= daysInMonth; day++) {
				for(int minute = 0; minute < 60; minute++) {
					for(int second = 0; second < 60; second++) {
						if(seedAA == ((month * day + minute + second) & 0xFF)) {
							output.addToTable(delay, second, minute, hour, day, month, year);
						}
					}
				}
			}
		}
	}
	
}
