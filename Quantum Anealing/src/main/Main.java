/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.util.Pair;
import quantum.anealing.QuantumAnealing;
import quantum.anealing.TestQuantumAnnealingAlgorithm;
import quantum.anealing.graph.Edge;
import quantum.anealing.graph.Graph;
import quantum.anealing.graph.Vertex;

/**
 *
 * @author shirin
 */
public class Main {

    private static final int SINK_LOAD = 10;            // w
    private static final int CONTROLLER_LOAD = 10;      // wPrime

    private static final List<Vertex> nodes = new ArrayList<>();        // V
    private static final List<Edge> edges = new ArrayList<>();          // E
    private List<Vertex> candidateSinks = new ArrayList<>();            // AS
    private List<Vertex> candidateControllers = new ArrayList<>();      //AC

    public static void main(String[] args) {
        Main m = new Main();
        Graph graph = m.initialize();
        
        TestQuantumAnnealingAlgorithm qaTest = new TestQuantumAnnealingAlgorithm();
        qaTest.execute(graph, m.candidateSinks, m.candidateControllers);
    }

    public Graph initializeGraph(int graphSize) {

        int vertexCount = 0;
        ArrayList<Pair<String, Pair<Integer, Integer>>> edgesPairList = new ArrayList<>();
        if (graphSize == 1) {
            vertexCount = 11;
            edgesPairList.add(new Pair<>("Edge_0", new Pair<>(0, 1)));
            edgesPairList.add(new Pair<>("Edge_1", new Pair<>(0, 2)));
            edgesPairList.add(new Pair<>("Edge_2", new Pair<>(0, 4)));
            edgesPairList.add(new Pair<>("Edge_3", new Pair<>(2, 6)));
            edgesPairList.add(new Pair<>("Edge_4", new Pair<>(2, 7)));
            edgesPairList.add(new Pair<>("Edge_5", new Pair<>(3, 7)));
            edgesPairList.add(new Pair<>("Edge_6", new Pair<>(5, 8)));
            edgesPairList.add(new Pair<>("Edge_7", new Pair<>(8, 9)));
            edgesPairList.add(new Pair<>("Edge_8", new Pair<>(7, 9)));
            edgesPairList.add(new Pair<>("Edge_9", new Pair<>(4, 9)));
            edgesPairList.add(new Pair<>("Edge_10", new Pair<>(9, 10)));
            edgesPairList.add(new Pair<>("Edge_11", new Pair<>(1, 10)));
        } else if (graphSize == 2) {
            vertexCount = 21;
            edgesPairList.add(new Pair<>("Edge_0", new Pair<>(0, 1)));
            edgesPairList.add(new Pair<>("Edge_1", new Pair<>(0, 2)));
            edgesPairList.add(new Pair<>("Edge_2", new Pair<>(0, 4)));
            edgesPairList.add(new Pair<>("Edge_3", new Pair<>(2, 6)));
            edgesPairList.add(new Pair<>("Edge_4", new Pair<>(2, 7)));
            edgesPairList.add(new Pair<>("Edge_5", new Pair<>(3, 7)));
            edgesPairList.add(new Pair<>("Edge_6", new Pair<>(5, 8)));
            edgesPairList.add(new Pair<>("Edge_7", new Pair<>(8, 9)));
            edgesPairList.add(new Pair<>("Edge_8", new Pair<>(7, 9)));
            edgesPairList.add(new Pair<>("Edge_9", new Pair<>(4, 9)));
            edgesPairList.add(new Pair<>("Edge_10", new Pair<>(9, 10)));
            edgesPairList.add(new Pair<>("Edge_11", new Pair<>(1, 10)));
            edgesPairList.add(new Pair<>("Edge_12", new Pair<>(10, 11)));
            edgesPairList.add(new Pair<>("Edge_13", new Pair<>(10, 12)));
            edgesPairList.add(new Pair<>("Edge_14", new Pair<>(10, 14)));
            edgesPairList.add(new Pair<>("Edge_15", new Pair<>(12, 16)));
            edgesPairList.add(new Pair<>("Edge_16", new Pair<>(12, 17)));
            edgesPairList.add(new Pair<>("Edge_17", new Pair<>(13, 17)));
            edgesPairList.add(new Pair<>("Edge_18", new Pair<>(15, 18)));
            edgesPairList.add(new Pair<>("Edge_19", new Pair<>(18, 19)));
            edgesPairList.add(new Pair<>("Edge_20", new Pair<>(17, 19)));
            edgesPairList.add(new Pair<>("Edge_21", new Pair<>(14, 19)));
            edgesPairList.add(new Pair<>("Edge_22", new Pair<>(19, 20)));
            edgesPairList.add(new Pair<>("Edge_23", new Pair<>(11, 20)));
            edgesPairList.add(new Pair<>("Edge_24", new Pair<>(11, 18)));
            edgesPairList.add(new Pair<>("Edge_25", new Pair<>(17, 20)));
            edgesPairList.add(new Pair<>("Edge_26", new Pair<>(10, 4)));
            edgesPairList.add(new Pair<>("Edge_27", new Pair<>(10, 5)));

        }

        for (int i = 0; i < vertexCount; i++) {
            Vertex location = new Vertex("Node_" + i, "Node_" + i, SINK_LOAD, CONTROLLER_LOAD);
            nodes.add(location);
        }

        edgesPairList.stream().forEach((edge) -> {
            addLane(edge.getKey(), edge.getValue().getKey(), edge.getValue().getValue());
        });

        Graph graph = new Graph(nodes, edges);
        return graph;
    }

    private void addLane(String laneId, int sourceLocNo, int destLocNo) {
        Edge lane1 = new Edge(laneId, nodes.get(sourceLocNo), nodes.get(destLocNo), 1);
        edges.add(lane1);
        Edge lane2 = new Edge(laneId, nodes.get(destLocNo), nodes.get(sourceLocNo), 1);
        edges.add(lane2);
    }

    private void addLane(String laneId, int sourceLocNo, int destLocNo, int duration) {
        Edge lane = new Edge(laneId, nodes.get(sourceLocNo), nodes.get(destLocNo), duration);
        edges.add(lane);
    }

    private Graph initialize() {
        Main m = new Main();
        Graph graph = m.initializeGraph(2);
        candidateSinks = Arrays.asList(
                graph.getVertexes().get(1),
                graph.getVertexes().get(2),
                graph.getVertexes().get(3),
                graph.getVertexes().get(4),
                graph.getVertexes().get(5),
                graph.getVertexes().get(6)
        );
        candidateControllers = Arrays.asList(
                graph.getVertexes().get(1),
                graph.getVertexes().get(2),
                graph.getVertexes().get(3),
                graph.getVertexes().get(4),
                graph.getVertexes().get(5),
                graph.getVertexes().get(6),
                graph.getVertexes().get(7),
                graph.getVertexes().get(8),
                graph.getVertexes().get(9)
        );
        return graph;
    }
}
