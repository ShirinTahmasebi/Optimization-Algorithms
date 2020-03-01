package main;

import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LineChartSimple extends JPanel {

    private final static int DEFAULT_WIDTH = 800;
    private final static int HEIGHT_DEFAULT = 400;
    private final static int PADDING = 25;
    private final static int LABEL_PADDING = 25;
    private final static int NUMBER_Y_DIVISIONS = 10;
    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);

    private final static int POINT_WIDTH = 4;
    private Color lineColor = new Color(44, 102, 230, 180);
    private Color pointColor = new Color(100, 100, 100, 180);
    private Color gridColor = new Color(200, 200, 200, 200);

    private List<Double> yValues;
    private List<Double> xValues;

    public LineChartSimple(List<Double> xValues, List<Double> yValues) {
        this.xValues = xValues;
        this.yValues = yValues;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double xScale = ((double) getWidth() - (2 * PADDING) - LABEL_PADDING) / (yValues.size() - 1);
        double yScale = ((double) getHeight() - 2 * PADDING - LABEL_PADDING) / (getMaxYValue() - getMinYValue());

        List<Point> graphPoints = new ArrayList<>();
        for (int i = 0; i < yValues.size(); i++) {
            int x1 = (int) (i * xScale + PADDING + LABEL_PADDING);
            int y1 = (int) ((getMaxYValue() - yValues.get(i)) * yScale + PADDING);
            graphPoints.add(new Point(x1, y1));
        }

        // draw white background
        g2.setColor(Color.WHITE);
        g2.fillRect(PADDING + LABEL_PADDING, PADDING, getWidth() - (2 * PADDING) - LABEL_PADDING, getHeight() - 2 * PADDING - LABEL_PADDING);
        g2.setColor(Color.BLACK);

        // create hatch marks and grid lines for y axis.
        for (int i = 0; i < NUMBER_Y_DIVISIONS + 1; i++) {
            int x0 = PADDING + LABEL_PADDING;
            int x1 = POINT_WIDTH + PADDING + LABEL_PADDING;
            int y0 = getHeight() - ((i * (getHeight() - PADDING * 2 - LABEL_PADDING)) / NUMBER_Y_DIVISIONS + PADDING + LABEL_PADDING);
            if (yValues.size() > 0) {
                g2.setColor(gridColor);
                g2.drawLine(PADDING + LABEL_PADDING + 1 + POINT_WIDTH, y0, getWidth() - PADDING, y0);
                g2.setColor(Color.BLACK);
                String yLabel = ((int) ((getMinYValue() + (getMaxYValue() - getMinYValue()) * ((i * 1.0) / NUMBER_Y_DIVISIONS)) * 100)) / 100.0 + "";
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(yLabel);
                g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
            }
            g2.drawLine(x0, y0, x1, y0);
        }

        // and for x axis
        for (int i = 0; i < xValues.size(); i++) {
            if (yValues.size() > 1) {
                int x0 = i * (getWidth() - PADDING * 2 - LABEL_PADDING) / (yValues.size() - 1) + PADDING + LABEL_PADDING;
                int y0 = getHeight() - PADDING - LABEL_PADDING;
                int y1 = y0 - POINT_WIDTH;
                if ((i % ((int) ((yValues.size() / 20.0)) + 1)) == 0) {
                    g2.setColor(gridColor);
                    g2.drawLine(x0, getHeight() - PADDING - LABEL_PADDING - 1 - POINT_WIDTH, x0, PADDING);
                    g2.setColor(Color.BLACK);
                    String xLabel = String.format("%.2f", xValues.get(i)) + "";
                    FontMetrics metrics = g2.getFontMetrics();
                    int labelWidth = metrics.stringWidth(xLabel);
                    g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
                }
                g2.drawLine(x0, y0, x0, y1);
            }
        }

        // create x and y axes 
        g2.drawLine(PADDING + LABEL_PADDING, getHeight() - PADDING - LABEL_PADDING, PADDING + LABEL_PADDING, PADDING);
        g2.drawLine(PADDING + LABEL_PADDING, getHeight() - PADDING - LABEL_PADDING, getWidth() - PADDING, getHeight() - PADDING - LABEL_PADDING);

        Stroke oldStroke = g2.getStroke();
        g2.setColor(lineColor);
        g2.setStroke(GRAPH_STROKE);
        for (int i = 0; i < graphPoints.size() - 1; i++) {
            int x1 = graphPoints.get(i).x;
            int y1 = graphPoints.get(i).y;
            int x2 = graphPoints.get(i + 1).x;
            int y2 = graphPoints.get(i + 1).y;
            g2.drawLine(x1, y1, x2, y2);
        }

        g2.setStroke(oldStroke);
        g2.setColor(pointColor);
        for (Point graphPoint : graphPoints) {
            int x = graphPoint.x - POINT_WIDTH / 2;
            int y = graphPoint.y - POINT_WIDTH / 2;
            int ovalW = POINT_WIDTH;
            int ovalH = POINT_WIDTH;
            g2.fillOval(x, y, ovalW, ovalH);
        }
    }

    private double getMinYValue() {
        double minYValue = Double.MAX_VALUE;
        for (Double value : yValues) {
            minYValue = Math.min(minYValue, value);
        }
        return minYValue;
    }

    private double getMaxYValue() {
        double maxYValue = Double.MIN_VALUE;
        for (Double value : yValues) {
            maxYValue = Math.max(maxYValue, value);
        }
        return maxYValue;
    }

    public static void drawChart(String graphName, List<Pair<Double, Double>> pointsList) {
        List<Double> xValues = new ArrayList<>();
        List<Double> yValues = new ArrayList<>();

        pointsList.stream().forEach(doubleDoublePair -> {
            xValues.add(doubleDoublePair.getKey());
            yValues.add(doubleDoublePair.getValue());
        });

        LineChartSimple mainPanel = new LineChartSimple(xValues, yValues);
        mainPanel.setPreferredSize(new Dimension(DEFAULT_WIDTH, HEIGHT_DEFAULT));
        JFrame frame = new JFrame(graphName);
        //noinspection MagicConstant
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);

        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }

    public static void main(String[] args) {
        List<Pair<Double, Double>> pointsList = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            double xValue = i * 0.1;
            double yValue = i * 10;
            pointsList.add(new Pair<>(xValue, yValue));
        }

        drawChart("Test Graph", pointsList);
    }
}