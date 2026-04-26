package bg.monitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

public final class UtilCpuTemperatureReader {

	public UtilCpuTemperatureReader() {
	}

	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");

	static class Temperature {
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

	}

	// Small demo
	public static void main(String[] args) throws Exception {
		List<Temperature> listTemperature1 = getListTemperature();
		listTemperature1.forEach(e -> System.out.println("" + e));
		
	}

	private static List<Temperature> getListTemperature() {
		List<Temperature> list = new ArrayList<UtilCpuTemperatureReader.Temperature>();
		list.addAll(getListTemperatureFromHwmon());
		list.addAll(getListTemperatureFromThermalZone());
		Temperature temp = readNvidiaGpuTemperatureViaNvidiaSmi();
		list.add(temp);
		return list;
	}

	private static List<Temperature> getListTemperatureFromHwmon() {
		List<Temperature> list = new ArrayList<UtilCpuTemperatureReader.Temperature>();
		File dir = new File("/sys/class/hwmon/hwmon1");
		for (File ff : dir.listFiles()) {
			String ffName = ff.getName();
			if (ffName.startsWith("temp") && ffName.endsWith("input")) {

				Temperature tt = readCpuTemperatureCelsius_(ff, "" + ffName);
				if (tt != null) {
					list.add(tt);
				}

			}
		}
		return list;
	}

	private static List<Temperature> getListTemperatureFromThermalZone() {
		List<Temperature> list = new ArrayList<UtilCpuTemperatureReader.Temperature>();
		try {
			List<File> listDir = readThermalZone();

			for (File dir : listDir) {
				String name = dir.getCanonicalFile().getName();
				File file = new File(dir, "temp");
				Temperature temp = readCpuTemperatureCelsius_(file, name);
				if (temp != null) {
					list.add(temp);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;

	}

	private static Temperature readCpuTemperatureCelsius_(File file, String name) {
		try {

			if (file.exists()) {
				String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);

				double value = Double.parseDouble(content.trim()) / 1000d;
				Date date = new Date();
				Temperature temperature = new Temperature(date, value, name);
				return temperature;
			} else {
				return null;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static List<File> readThermalZone() {
		List<File> listDir = new ArrayList<File>();
		File dir = new File("/sys/class/thermal");
		for (File f : dir.listFiles()) {
			if (f.getName().startsWith("thermal_zone")) {
				System.out.println("--" + f.getName());
				listDir.add(f);
			}
		}
		return listDir;
	}
	
	 private static Temperature readNvidiaGpuTemperatureViaNvidiaSmi() {
	        // Returns null if nvidia-smi not available or fails
	        try {
	            Process p = new ProcessBuilder(
	                    "nvidia-smi",
	                    "--query-gpu=temperature.gpu",
	                    "--format=csv,noheader,nounits"
	            ).redirectErrorStream(true).start();

	            String line;
	            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
	                line = br.readLine();
	            }

	            int exit = p.waitFor();
	            if (exit != 0 || line == null || line.isBlank()) return null;

	            double valueC = Double.parseDouble(line.trim()); // already in Celsius
	            return new Temperature(new Date(), valueC, "nvidia_gpu");
	        } catch (Exception e) {
	            return null;
	        }
	    }
}