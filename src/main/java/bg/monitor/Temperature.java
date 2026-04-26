package bg.monitor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Temperature {

	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");

	public Temperature(Date date_, double score, String input) {
		value = score;
		name = input;
		date = date_;
	}

	Double value;
	String name;
	Date date;

	@Override
	public String toString() {
		return "date=" + sdf.format(date) + ", temperature=" + value + " Celsius , nom=" + name + "";
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	

}
