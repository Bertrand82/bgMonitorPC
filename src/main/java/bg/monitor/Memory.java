package bg.monitor;

import java.util.Date;

public class Memory {

	public Memory(long mTotal, long mFree) {
		memoryRamTotal = mTotal;
		memoryRamFree = mFree;
	}
	public long memoryRamFree;
	public long memoryRamTotal;
	public Date date;
	
	public long getMemoryFree_Mb() {
		return  (long) (memoryRamFree / (1024L * 1024L)); // MB
	}
	
	public double getMemoryFree_perCent() {
		if (memoryRamTotal==0d) {
			return -1d;
		}
		if (memoryRamTotal==-1d) {
			return -2d;
		}
		if (memoryRamFree==-1d) {
			return -3d;
		}
		double r  =((double) memoryRamFree)/memoryRamTotal;
		return r;
	}

	@Override
	public String toString() {
		return "Memory [memoryRamFree=" + memoryRamFree + ", memoryRamTotal=" + memoryRamTotal + "]";
	}
	
}
