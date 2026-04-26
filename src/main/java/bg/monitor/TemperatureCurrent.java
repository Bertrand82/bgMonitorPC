package bg.monitor;

import java.util.List;

public class TemperatureCurrent {

	public TemperatureCurrent() {
		this.updateTemperature();
	}
	
	List<Temperature> listTemperature;
	
	public void updateTemperature() {
		this.listTemperature=UtilTemperature.getListTemperature();
	}
	
	public Double getTemperatureNvidia() {
		Temperature temp=  getTemperatureByName(UtilTemperature.keyNvidia);
		return (temp==null)? 0d:temp.getValue();
	}
	public Temperature getTemperatureGpu() {
		return getTemperatureByName(UtilTemperature.keyNvidia);
	}
	
	public Temperature getTemperatureByName(String name) {
		if (listTemperature== null) {
			return null;
		}
		for (Temperature temp : listTemperature) {
			if (temp.getName() ==null) {
			}else if (temp.getName().equals(name)) {
				return temp;
			}
		}
		return null;
	}
}
