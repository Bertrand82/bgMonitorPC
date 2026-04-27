package bg.monitor;

import java.util.Date;

public class Memory {

	public Memory(long mTotal, long mFree) {
		memoryRamTotal = mTotal;
		memoryRamFree = mFree;
		this.memoryFree_percent=processMemoryFree_Mb();
	}
	public long memoryRamFree;
	public long memoryRamTotal;
	public double memoryFree_percent;
	public Date date;
	
	public long processMemoryFree_Mb() {
		return  (long) (memoryRamFree / (1024L * 1024L)); // MB
	}
	
	public double getMemoryFree_perCent() {
		return memoryFree_percent;
	}

	@Override
	public String toString() {
		return "memoryRamFree=" + memoryRamFree + ", memoryRamTotal=" + memoryRamTotal + " pourCent_free="+memoryFree_percent;
	}
	
}
