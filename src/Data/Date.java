package Data;

public class Date {
	
	public Date(){}
	public Date(String date)
	{
		year = Integer.parseInt(date.substring(0, date.indexOf(".")));
		date = date.substring(date.indexOf(".")+1);
		month = Integer.parseInt(date.substring(0, date.indexOf(".")));
		date = date.substring(date.indexOf(".")+1);
		this.date = Integer.parseInt(date.substring(0, date.indexOf(" ")));
		
		date = date.substring(date.indexOf(" ")+1);
		hours = Integer.parseInt(date.substring(0, date.indexOf(":")));
		date = date.substring(date.indexOf(":")+1);
		minutes = Integer.parseInt(date.substring(0, date.indexOf(":")));
		date = date.substring(date.indexOf(":")+1);
		seconds = Integer.parseInt(date);
	}
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getDate() {
		return date;
	}
	public void setDate(int date) {
		this.date = date;
	}
	public int getHours() {
		return hours;
	}
	public void setHours(int hours) {
		this.hours = hours;
	}
	public int getMinutes() {
		return minutes;
	}
	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}
	public int getSeconds() {
		return seconds;
	}
	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}
	
	public String toString()
	{
		String temp = String.format("%4d.%02d.%02d %02d:%02d:%02d", year, month, date, hours, minutes, seconds);
		
		return temp;
	}
	
	private int year;
	private int month;
	private int date;
	
	private int hours;
	private int minutes;
	private int seconds;
	
	
}
