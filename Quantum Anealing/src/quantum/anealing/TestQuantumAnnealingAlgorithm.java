package quantum.anealing;

import quantum.anealing.graph.Vertex;
import quantum.anealing.graph.Graph;
import quantum.anealing.graph.Edge;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestQuantumAnnealingAlgorithm {

    private static final int VERTICES_COUNT = 10;
    private static final int SENSOR_SINK_MAX_DISTANCE = 3;              // Lmax
    private static final int SENSOR_CONTROLLER_MAX_DISTANCE = 2;        // LPrimeMax
    private static final int MAX_SINK_COVERAGE = 6;             // k
    private static final int MAX_CONTROLLER_COVERAGE = 6;       // kPrime
    private static final int SINK_LOAD = 10;            // w
    private static final int CONTROLLER_LOAD = 10;      // wPrime
    private static final int SINK_MAX_LOAD = 30;            // W
    private static final int CONTROLLER_MAX_LOAD = 30;      // WPrime

    private static final List<Vertex> nodes = new ArrayList<>();        // V
    private static final List<Edge> edges = new ArrayList<>();          // E

    private List<Vertex> candidateSinks = new ArrayList<>();            // AS
    private List<Vertex> candidateControllers = new ArrayList<>();      //AC

    public static void main(String[] args) {
        TestQuantumAnnealingAlgorithm qaTest = new TestQuantumAnnealingAlgorithm();

        Graph graph = qaTest.initialize();
        QuantumAnealing qa = new QuantumAnealing(
                graph,
                qaTest.candidateSinks,
                qaTest.candidateControllers,
                SENSOR_SINK_MAX_DISTANCE,
                SENSOR_CONTROLLER_MAX_DISTANCE,
                MAX_SINK_COVERAGE,
                MAX_CONTROLLER_COVERAGE
        );

        qa.execute();
    }

    private Graph initialize() {
        Graph graph = initializeGraph();
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

    private Graph initializeGraph() {

        for (int i = 0; i < VERTICES_COUNT + 1; i++) {
            Vertex location = new Vertex("Node_" + i, "Node_" + i, SINK_LOAD, CONTROLLER_LOAD);
            nodes.add(location);
        }

        addLane("Edge_0", 0, 1);
        addLane("Edge_1", 0, 2);
        addLane("Edge_2", 0, 4);
        addLane("Edge_3", 2, 6);
        addLane("Edge_4", 2, 7);
        addLane("Edge_5", 3, 7);
        addLane("Edge_6", 5, 8);
        addLane("Edge_7", 8, 9);
        addLane("Edge_8", 7, 9);
        addLane("Edge_9", 4, 9);
        addLane("Edge_10", 9, 10);
        addLane("Edge_11", 1, 10);

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
}
