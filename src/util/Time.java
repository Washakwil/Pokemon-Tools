package util;

public class Time implements Comparable<Time> {

	int hour, minute, second;

	public Time(int hour, int minute, int second) {
		this.hour = hour;
		this.minute = minute;
		this.second = second;
	}

	@Override
	public int compareTo(Time o) {
		int result = Integer.compare(hour, o.hour);
		if(result == 0) result = Integer.compare(minute, o.minute);
		if(result == 0) result = Integer.compare(second, o.second);
		return result;
	}
	
	@Override
	public String toString() {
		return Util.add0s(hour, 2) + ":" + Util.add0s(minute, 2) + ":" + Util.add0s(second, 2);
	}
	
}
