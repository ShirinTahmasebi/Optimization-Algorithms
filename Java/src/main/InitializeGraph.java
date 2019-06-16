package main;

import javafx.util.Pair;
import main.model.Edge;
import main.model.Graph;
import main.model.Vertex;

import java.util.*;

import static main.Utils.writeObjectToFile;

public class InitializeGraph {
    private static final List<Vertex> nodes = new ArrayList<>();        // V
    private static final List<Edge> edges = new ArrayList<>();          // E
    private final List<Vertex> candidateSinks = new ArrayList<>();            // AS
    private final List<Vertex> candidateControllers = new ArrayList<>();      // AC
    private Graph graph;

    private boolean[][] sinkYSpinVariables;           // SY (Y Spin Variable)
    private boolean[][] controllerYSpinVariables;     // SYPrime (Y Spin Variable)

    public static void main(String[] args) {
        InitializeGraph initializeGraph = new InitializeGraph();
        initializeGraph.initialize();
        initializeGraph.writeObjectsToFile();
    }

    private void writeObjectsToFile() {
        writeObjectToFile(graph, Utils.FILE_NAME_GRAPH + main.Parameters.Common.GRAPH_SIZE);
        writeObjectToFile(candidateSinks, Utils.FILE_NAME_CANDIDATE_SINKS + main.Parameters.Common.GRAPH_SIZE);
        writeObjectToFile(candidateControllers, Utils.FILE_NAME_CANDIDATE_CONTROLLERS + main.Parameters.Common.GRAPH_SIZE);
        writeObjectToFile(sinkYSpinVariables, Utils.FILE_NAME_SINK_Y_SPIN_VARIABLES + main.Parameters.Common.GRAPH_SIZE);
        writeObjectToFile(controllerYSpinVariables, Utils.FILE_NAME_CONTROLLER_Y_SPIN_VARIABLES + main.Parameters.Common.GRAPH_SIZE);
    }

    public Graph initializeGraph(int graphSize) {
        int vertexCount = 0;
        int candidateSinksNumber = 0;
        int candidateControllersNumber = 0;
        ArrayList<Pair<String, Pair<Integer, Integer>>> edgesPairList = new ArrayList<>();
        if (graphSize == 1) {
            // Candidate Sink = 4 (20/5)
            // Candidate Controller = 2 (20/10)
            vertexCount = 20;
            candidateSinksNumber = 6;
            candidateControllersNumber = 9;
        } else if (graphSize == 2) {
            // Candidate Sink = 8 (40/5)
            // Candidate Controller = 4 (40/10)
            vertexCount = 40;
            candidateSinksNumber = vertexCount / 5;
            candidateControllersNumber = vertexCount / 10;
        } else if (graphSize == 3) {
            // Candidate Sink = 16 (80/5)
            // Candidate Controller = 8 (80/10)
            vertexCount = 80;
            candidateSinksNumber = vertexCount / 5;
            candidateControllersNumber = vertexCount / 10;
        } else if (graphSize == 4) {
            // Candidate Sink = 32 (160/5)
            // Candidate Controller = 16 (160/10)
            vertexCount = 150;
            candidateSinksNumber = 50;
            candidateControllersNumber = 40;
        } else if (graphSize == 5) {
            // Candidate Sink = 32 (200/5)
            // Candidate Controller = 16 (160/10)
            vertexCount = 200;
            candidateSinksNumber = 70;
            candidateControllersNumber = 60;
        }

        for (int i = 0; i < vertexCount; i++) {
            int ithNodeNeighborsCount = vertexCount / 2;
            Set<Integer> neighborsNumberSet = new HashSet<>();

            while (neighborsNumberSet.size() < ithNodeNeighborsCount) {
                int nextInt = new Random().nextInt(vertexCount);
                if (nextInt != i) {
                    neighborsNumberSet.add(nextInt);
                }
            }

            for (int neighborNumber : neighborsNumberSet) {
                edgesPairList.add(new Pair<>("Edge_" + i + "_To_" + neighborNumber, new Pair<>(i, neighborNumber)));
            }
        }

        for (int i = 0; i < vertexCount; i++) {
            Vertex location = new Vertex("Node_" + i, "Node_" + i, main.Parameters.Common.SINK_LOAD, main.Parameters.Common.CONTROLLER_LOAD);
            nodes.add(location);
        }

        edgesPairList.stream().forEach((edge) -> addLane(edge.getKey(), edge.getValue().getKey(), edge.getValue().getValue()));

        Set<Integer> candidateSinksNumberSet = new HashSet<>();
        Set<Integer> candidateControllerNumberSet = new HashSet<>();

        while (candidateSinksNumberSet.size() < candidateSinksNumber) {
            int nextInt = new Random().nextInt(vertexCount);
            candidateSinksNumberSet.add(nextInt);
        }

        while (candidateControllerNumberSet.size() < candidateControllersNumber) {
            int nextInt = new Random().nextInt(vertexCount);
            candidateControllerNumberSet.add(nextInt);
        }

        candidateControllerNumberSet.stream().forEach((candidateControllerNumber) -> candidateControllers.add(nodes.get(candidateControllerNumber)));

        candidateSinksNumberSet.stream().forEach((candidateSinkNumber) -> candidateSinks.add(nodes.get(candidateSinkNumber)));

        return new Graph(nodes, edges);
    }


    private void addLane(String laneId, int sourceLocNo, int destLocNo) {
        Edge lane1 = new Edge(laneId, nodes.get(sourceLocNo), nodes.get(destLocNo), 1);
        edges.add(lane1);
        Edge lane2 = new Edge(laneId, nodes.get(destLocNo), nodes.get(sourceLocNo), 1);
        edges.add(lane2);
    }

    @SuppressWarnings("unused")
    private void addLane(String laneId, int sourceLocNo, int destLocNo, int duration) {
        Edge lane = new Edge(laneId, nodes.get(sourceLocNo), nodes.get(destLocNo), duration);
        edges.add(lane);
    }

    private Graph initialize() {
        Graph graph = initializeGraph(main.Parameters.Common.GRAPH_SIZE);

        this.controllerYSpinVariables = new boolean[graph.getVertexes().size()][candidateControllers.size()];
        this.sinkYSpinVariables = new boolean[graph.getVertexes().size()][candidateSinks.size()];

        Utils.initializeSpinVariables(
                graph,
                candidateSinks,
                candidateControllers,
                main.Parameters.Common.SENSOR_SINK_MAX_DISTANCE,
                main.Parameters.Common.SENSOR_CONTROLLER_MAX_DISTANCE,
                sinkYSpinVariables,
                controllerYSpinVariables
        );

        this.graph = graph;
        return graph;
    }
}
