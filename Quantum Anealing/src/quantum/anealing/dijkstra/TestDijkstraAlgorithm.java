/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.anealing.dijkstra;

import quantum.anealing.dijkstra.DijkstraAlgorithm;
import quantum.anealing.graph.Vertex;
import quantum.anealing.graph.Graph;
import quantum.anealing.graph.Edge;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TestDijkstraAlgorithm {

    private List<Vertex> nodes;
    private List<Edge> edges;

    public static void main(String[] args) {
        TestDijkstraAlgorithm testDijkstraAlgorithm = new TestDijkstraAlgorithm();
        testDijkstraAlgorithm.nodes = new ArrayList<>();
        testDijkstraAlgorithm.edges = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            Vertex location = new Vertex("Node_" + i, "Node_" + i);
            testDijkstraAlgorithm.nodes.add(location);
        }

        testDijkstraAlgorithm.addLane("Edge_0", 0, 1);
        testDijkstraAlgorithm.addLane("Edge_1", 0, 2);
        testDijkstraAlgorithm.addLane("Edge_2", 0, 4);
        testDijkstraAlgorithm.addLane("Edge_3", 2, 6);
        testDijkstraAlgorithm.addLane("Edge_4", 2, 7);
        testDijkstraAlgorithm.addLane("Edge_5", 3, 7);
        testDijkstraAlgorithm.addLane("Edge_6", 5, 8);
        testDijkstraAlgorithm.addLane("Edge_7", 8, 9);
        testDijkstraAlgorithm.addLane("Edge_8", 7, 9);
        testDijkstraAlgorithm.addLane("Edge_9", 4, 9);
        testDijkstraAlgorithm.addLane("Edge_10", 9, 10);
        testDijkstraAlgorithm.addLane("Edge_11", 1, 10);

        // Lets check from location Loc_1 to Loc_10
        Graph graph = new Graph(testDijkstraAlgorithm.nodes, testDijkstraAlgorithm.edges);
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
        dijkstra.execute(testDijkstraAlgorithm.nodes.get(6));
        LinkedList<Vertex> path = dijkstra.getPath(testDijkstraAlgorithm.nodes.get(10));

        for (Vertex vertex : path) {
            System.out.println(vertex);
        }

    }

    private void addLane(String laneId, int sourceLocNo, int destLocNo) {
        Edge lane1 = new Edge(laneId, nodes.get(sourceLocNo), nodes.get(destLocNo), 1);
        edges.add(lane1);
        Edge lane2 = new Edge(laneId, nodes.get(destLocNo), nodes.get(sourceLocNo), 1);
        edges.add(lane2);
    }
    
    private void addLane(String laneId, int sourceLocNo, int destLocNo,
            int duration) {
        Edge lane = new Edge(laneId, nodes.get(sourceLocNo), nodes.get(destLocNo), duration);
        edges.add(lane);
    }
}
