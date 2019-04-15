package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class LineChartEx extends JFrame {

    private final XYSeries selectedSeries = new XYSeries("Selected Energy");
    private final XYSeries visitedSeries = new XYSeries("Visited Energy");
    private final XYSeries minimumSeries = new XYSeries("Minimum Energy");

    public void addToSelectedEnergy(int iterationNumber,
            double selectedEnergy, double visitedEnergy, double minEnergy, double minTotalEnergy) {
        selectedSeries.add(iterationNumber, selectedEnergy);
        visitedSeries.add(iterationNumber, visitedEnergy);
        minimumSeries.add(iterationNumber, minEnergy);
    }

    public void drawChart() {
        SwingUtilities.invokeLater(() -> {
            this.setVisible(true);
        });
    }

    public LineChartEx() {
        initUI();
    }

    private void initUI() {
        XYDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.WHITE);
        add(chartPanel);

        pack();
        setTitle("Line chart");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private XYDataset createDataset() {
        XYSeriesCollection dataset = new XYSeriesCollection();

        dataset.addSeries(selectedSeries);
        dataset.addSeries(visitedSeries);
        dataset.addSeries(minimumSeries);

        return dataset;
    }

    private JFreeChart createChart(XYDataset dataset) {

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Energy Level",
                "Iteration Numbers",
                "Energy",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(1, Color.BLUE);
        renderer.setSeriesPaint(2, Color.GREEN);

        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesStroke(1, new BasicStroke(2.0f));
        renderer.setSeriesStroke(2, new BasicStroke(2.0f));

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.WHITE);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

        chart.getLegend().setFrame(BlockBorder.NONE);

        chart.setTitle(new TextTitle("Energy Level",
                new Font("Serif", java.awt.Font.BOLD, 18)
        )
        );

        return chart;

    }

    public static void main(String[] args) {
        LineChartEx chartEx = new LineChartEx();
        chartEx.addToSelectedEnergy(1, 1, 1, 1, 1);
        chartEx.addToSelectedEnergy(5, 5, 5, 5, 5);
        chartEx.drawChart();

        LineChartEx chartEx1 = new LineChartEx();
        chartEx1.addToSelectedEnergy(1, 1, 1, 1, 1);
        chartEx1.addToSelectedEnergy(5, 5, 5, 5, 5);
        chartEx1.addToSelectedEnergy(10, 1, 1, 1, 1);
        chartEx1.addToSelectedEnergy(11, 0, 0, 0, 0);
        chartEx1.drawChart();
    }
}
