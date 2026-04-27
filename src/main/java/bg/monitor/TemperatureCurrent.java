package bg.monitor;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TemperatureCurrent {
	private static final Logger LOG = LogManager.getLogger(TemperatureCurrent.class);

	public TemperatureCurrent() {
		this.updateTemperature();
	}
	
	List<Temperature> listTemperature;
	
	public void updateTemperature() {
		this.listTemperature=UtilTemperature.getListTemperature();
	}
	
	public Double getTemperatureThermal_zone0() {
		Temperature temp=  getTemperatureByName("thermal_zone0");
		return (temp==null)? 0d:temp.getValue();
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

    public void logTemperature() {
		for(Temperature temp: listTemperature) {
			LOG.info(temp.toString2());
		}
    }
}
