package bg.monitor.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.function.DoubleUnaryOperator;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import bg.monitor.Temperature;
import bg.monitor.TemperatureCurrent;


public class MonitorGUI {

	JFrame frame = new JFrame("Monitoring Temperature");

	PanelSimpleCurve plotNvidia = new PanelSimpleCurve(120, // 2 minutes affichées
			20.0, // yMin fixe
			100.0 ,// yMax fixe
			"nvidia"
	);
	PanelSimpleCurve plotThermal_zone0 = new PanelSimpleCurve(120, // 2 minutes affichées
			20.0, // yMin fixe
			100.0 ,// yMax fixe
			"Thermal_zone0"
	);


	DoubleUnaryOperator ff = (t) ->20.0* Math.sin(t / 5.0);

	public MonitorGUI() {

		// Exemple de f(t): sin(t) (t en secondes)

        JPanel panelCentre = new JPanel(new GridLayout(0, 1));
        panelCentre.add(plotNvidia);
        panelCentre.add(plotThermal_zone0);

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(panelCentre, BorderLayout.CENTER);
		frame.pack();

		frame.setVisible(true);
		
		Timer timer = new Timer(1000, e -> {
			timerMonitor();
		});
		timer.start();

	}

	TemperatureCurrent temperatureCurrent = new TemperatureCurrent();
	private void timerMonitor() {
		//double t = System.currentTimeMillis() / 1000.0;
		//plot.addSample(ff.applyAsDouble(t));
		temperatureCurrent.updateTemperature();
		
		plotNvidia.addSample(temperatureCurrent.getTemperatureNvidia());
		plotThermal_zone0.addSample(temperatureCurrent.getTemperatureThermal_zone0());
		temperatureCurrent.logTemperature();
	}
}
