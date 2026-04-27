package bg.monitor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MemoryCurrent {

	private static final Logger LOG = LogManager.getLogger(TemperatureCurrent.class);
	Memory memory;

	public MemoryCurrent() {
		this.updateMemory();
	}
	
	public void updateMemory() {
		this.memory=UtilMemory.getMemory();
	}

	 public void logMemory() {
			LOG.info("memoryCurrent: {} C", this.memory);
	    }

	 public double getMemoryFreePerCent() {
		if (memory==null) {
			return -4;
		}
		return memory.getMemoryFree_perCent();
	 }
	
}
