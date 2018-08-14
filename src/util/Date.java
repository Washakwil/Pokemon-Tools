package util;

public class Date implements Comparable<Date> {

	int year, month, day;

	public Date(int year, int month, int day) {
		this.year = year;
		this.month = month;
		this.day = day;
	}

	@Override
	public int compareTo(Date o) {
		int result = Integer.compare(year, o.year);
		if(result == 0) result = Integer.compare(month, o.month);
		if(result == 0) result = Integer.compare(day, o.day);
		return result;
	}
	
	@Override
	public String toString() {
		return Util.add0s(day, 2) + "." + Util.add0s(month, 2) + "." + year;
	}
	
}
