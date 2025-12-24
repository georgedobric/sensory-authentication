import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;

public class Graph {
    private TimeSeries potSeries;

    public Graph() {
        potSeries = new TimeSeries("Rotation Percentage");
    }

    public JFreeChart createChart() {
        TimeSeriesCollection dataset = new TimeSeriesCollection(potSeries);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Potentiometer Readings over Time",
                "Time",
                "Rotation (%) [100% = Full] [0% = None]",
                dataset,
                false,
                true,
                false
        );

        XYPlot plot = (XYPlot) chart.getPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(0.0, 100.0);

        return chart;
    }

    public void updateChart(int potPercentage) {
        SwingUtilities.invokeLater(() -> {
            potSeries.addOrUpdate(new Second(), potPercentage);
            if (potSeries.getItemCount() > 10) {
                potSeries.delete(0, potSeries.getItemCount() - 11);
            }
        });
    }
}
