package bg.monitor;


import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

public class UtilMemory {
	
	
	public static Memory getMemory() {
		long mTotal =getMemoryRAMTotal();
		long mFree = getMemoryRAMFree();
		Memory memoire = new Memory(mTotal, mFree);
		return memoire;
	}

	public static long getMemoryRAMTotal() {
		try {
			OperatingSystemMXBean os =
					(OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
			long total = os.getTotalMemorySize(); // bytes
			return total;
		} catch (Exception e) {
			
			e.printStackTrace();
			return -1;
		}
	}

	public static long getMemoryRAMFree() {
		try {
			OperatingSystemMXBean os =
					(OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
			long free = os.getFreeMemorySize(); // bytes
			
			return free;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
}

