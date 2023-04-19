package info6205.Graph.Utils;

import info6205.Graph.Edge;
import info6205.Graph.Node;
import info6205.Graph.Problems.TravellingSalesMan.GraphImpl.LatLongId;
import info6205.Graph.UndirectedEdgeWeighedListGraph;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class PointPlotter extends JFrame {
    private final ArrayList<Pair<Double, Color>> xValues;
    private final ArrayList<Pair<Double, Color>> yValues;
    private final ArrayList<Pair<Double, Color>> x1Values;
    private final ArrayList<Pair<Double, Color>> y1Values;
    private final ArrayList<Pair<Double, Color>> x2Values;
    private final ArrayList<Pair<Double, Color>> y2Values;

    private final double xPresicision = 1000;
    private final double yPresicision = 1000;
    private final double xAxisMove = -51.5;
    private final double yAxisMove = 0.15;

    private static PointPlotter pointPlotter;

    public PointPlotter() {
        super("Point Plotter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 1000);
        setLocationRelativeTo(null);

        xValues = new ArrayList<>();
        yValues = new ArrayList<>();
        x1Values = new ArrayList<>();
        y1Values = new ArrayList<>();
        x2Values = new ArrayList<>();
        y2Values = new ArrayList<>();
    }

    public static void addPointOuter(double x1, double y1, Color color) {
        pointPlotter.addPoint(x1, y1, color);
    }

    public void addPoint(double x, double y, Color color) {
        xValues.add(new Pair<>(x + xAxisMove, color));
        yValues.add(new Pair<>(y + yAxisMove, color));
        repaint();
    }

    public static void addLineOuter(double x1, double y1, double x2, double y2, Color color) {
        pointPlotter.addLine(x1, y1, x2, y2, color);
    }

    public void addLine(double x1, double y1, double x2, double y2, Color color) {
        x1Values.add(new Pair<>(x1 + xAxisMove, color));
        y1Values.add(new Pair<>(y1 + yAxisMove, color));
        x2Values.add(new Pair<>(x2 + xAxisMove, color));
        y2Values.add(new Pair<>(y2 + yAxisMove, color));
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        // Set up the coordinate system
        g2d.translate(getWidth() / 2, getHeight() / 2);
        g2d.scale(1, -1);

        // Draw the x-axis
        g2d.drawLine(-getWidth() / 2, 0, getWidth() / 2, 0);
        for (int i = -getWidth() / 2; i < getWidth() / 2; i += 50) {
            g2d.drawLine(i, -5, i, 5);
        }

        // Draw the y-axis
        g2d.drawLine(0, -getHeight() / 2, 0, getHeight() / 2);
        for (int i = -getHeight() / 2; i < getHeight() / 2; i += 50) {
            g2d.drawLine(-5, i, 5, i);
        }

        // Plot the points
        //g2d.setColor(Color.RED);
        for (int i = 0; i < xValues.size(); i++) {
            g2d.setColor(xValues.get(i).getSecond());
            int x = (int) Math.round(xValues.get(i).getFirst() * xPresicision);
            int y = (int) Math.round(yValues.get(i).getFirst() * yPresicision);
            g2d.fillOval(x - 2, y - 2, 4, 4);
        }

        // Draw the lines
        //g2d.setColor(Color.BLUE);
        for (int i = 0; i < x1Values.size(); i++) {
            g2d.setColor(x1Values.get(i).getSecond());
            int x1 = (int) Math.round(x1Values.get(i).getFirst() * xPresicision);
            int y1 = (int) Math.round(y1Values.get(i).getFirst() * yPresicision);
            int x2 = (int) Math.round(x2Values.get(i).getFirst() * xPresicision);
            int y2 = (int) Math.round(y2Values.get(i).getFirst() * yPresicision);
            g2d.drawLine(x1, y1, x2, y2);
        }
    }

    public static void plotGraph(UndirectedEdgeWeighedListGraph<String, LatLongId, Double> graph) {
        if (pointPlotter == null) {
            pointPlotter = new PointPlotter();
            pointPlotter.setVisible(true);
        }

        for (Node<String, LatLongId> node : graph.getNodes()) {
            pointPlotter.addPoint(node.getKey().getValue().getLatitude(), node.getKey().getValue().getLongitude(), Color.RED);
        }

        for (Edge<Node<String, LatLongId>, Double> edge : graph.getEdges().keySet()) {
            pointPlotter.addLine(edge.getFirstNode().getKey().getValue().getLatitude(), edge.getFirstNode().getKey().getValue().getLongitude(),
                    edge.getSecondNode().getKey().getValue().getLatitude(), edge.getSecondNode().getKey().getValue().getLongitude(), Color.BLUE);
        }
    }


    public static void plotEdges(java.util.List<Edge<Node<String, LatLongId>, Double>> edgeList) {
        //java.util.List<Edge<Node<String, LatLongId>, Double>> edgeList =
        if (pointPlotter == null) {
            pointPlotter = new PointPlotter();
            pointPlotter.setVisible(true);
        }

        for (Edge<Node<String, LatLongId>, Double> edge : edgeList) {
            pointPlotter.addLine(edge.getFirstNode().getKey().getValue().getLatitude(), edge.getFirstNode().getKey().getValue().getLongitude(),
                    edge.getSecondNode().getKey().getValue().getLatitude(), edge.getSecondNode().getKey().getValue().getLongitude(), Color.GREEN);
        }
    }

    public static void initializePointPlotter() {
        pointPlotter = new PointPlotter();
        pointPlotter.setVisible(true);
    }
}
