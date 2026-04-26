package bg.monitor.gui;

import java.awt.BorderLayout;
import java.util.function.DoubleUnaryOperator;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import bg.monitor.Temperature;
import bg.monitor.TemperatureCurrent;


public class MonitorGUI {

	JFrame frame = new JFrame("Courbe Swing - échelle fixe");

	PanelSimpleCurve plot = new PanelSimpleCurve(120, // 2 minutes affichées
			20.0, // yMin fixe
			100.0 // yMax fixe
	);

	DoubleUnaryOperator ff = (t) ->20.0* Math.sin(t / 5.0);

	public MonitorGUI() {

		// Exemple de f(t): sin(t) (t en secondes)

		Timer timer = new Timer(1000, e -> {
			timerMonitor();
		});
		timer.start();

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(plot, BorderLayout.CENTER);
		frame.pack();

		frame.setVisible(true);

	}

	TemperatureCurrent temperatureCurrent = new TemperatureCurrent();
	private void timerMonitor() {
		//double t = System.currentTimeMillis() / 1000.0;
		//plot.addSample(ff.applyAsDouble(t));
		temperatureCurrent.updateTemperature();
		
		plot.addSample(temperatureCurrent.getTemperatureNvidia());

	}
}
