package main;

import javafx.util.Pair;
import main.model.Edge;
import main.model.Graph;
import main.model.Vertex;

import java.util.*;
import java.util.stream.IntStream;

import static main.Utils.writeObjectToFile;

public class InitializeGraph {
    private static final List<Vertex> nodes = new ArrayList<>();        // V
    private static final List<Edge> edges = new ArrayList<>();          // E
    private final List<Vertex> candidateSinks = new ArrayList<>();            // AS
    private final List<Vertex> candidateControllers = new ArrayList<>();      // AC
    private Graph graph;

    private int[][] sinkY;           // Y (Number of Hops)
    private int[][] controllerY;     // YPrime (Number of Hops)
    private boolean[][] sinkYSpinVariables;           // SY (Y Spin Variable)
    private boolean[][] controllerYSpinVariables;     // SYPrime (Y Spin Variable)
    private int[][] distances;

    @SuppressWarnings("FieldCanBeLocal")
    private int isZeroIndexed = 1; // 1 = True 0 = False

    public static void main(String[] args) {
        InitializeGraph initializeGraph = new InitializeGraph();
        initializeGraph.initialize();
        initializeGraph.writeObjectsToFile();
    }

    private void writeObjectsToFile() {
        writeObjectToFile(graph, Utils.FILE_NAME_GRAPH + main.Parameters.Common.GRAPH_SIZE);
        writeObjectToFile(candidateSinks, Utils.FILE_NAME_CANDIDATE_SINKS + main.Parameters.Common.GRAPH_SIZE);
        writeObjectToFile(candidateControllers, Utils.FILE_NAME_CANDIDATE_CONTROLLERS + main.Parameters.Common.GRAPH_SIZE);
        writeObjectToFile(sinkY, Utils.FILE_NAME_SINK_Y + main.Parameters.Common.GRAPH_SIZE);
        writeObjectToFile(sinkYSpinVariables, Utils.FILE_NAME_SINK_Y_SPIN_VARIABLES + main.Parameters.Common.GRAPH_SIZE);
        writeObjectToFile(controllerY, Utils.FILE_NAME_CONTROLLER_Y + main.Parameters.Common.GRAPH_SIZE);
        writeObjectToFile(controllerYSpinVariables, Utils.FILE_NAME_CONTROLLER_Y_SPIN_VARIABLES + main.Parameters.Common.GRAPH_SIZE);
        writeObjectToFile(distances, Utils.FILE_NAME_DISTANCES + main.Parameters.Common.GRAPH_SIZE);
    }

    public Graph initializeGraph(int graphSize) {
        if (Parameters.Common.USE_RANDOM_GRAPH) {
            return initializeRandomGraph(graphSize);
        }

        return initializeSpecialGraph2(graphSize);
    }

    private Graph initializeSpecialGraph1(int graphSize) {
        int vertexCount = 0;
        int candidateSinksNumber = 0;
        int candidateControllersNumber = 0;
        ArrayList<Pair<String, Pair<Integer, Integer>>> edgesPairList = new ArrayList<>();
        if (graphSize == 1) {
            throw new RuntimeException("Special graph size 1 is not implemented");
        } else if (graphSize == 2) {
            throw new RuntimeException("Special graph size 1 is not implemented");
        } else if (graphSize == 3) {
            // Candidate Sink = 6 * 2
            // Candidate Controller = 6 * 2
            vertexCount = 102;
            candidateSinksNumber = 12;
            candidateControllersNumber = 12;
        } else if (graphSize == 4) {
            // Candidate Sink = 9 * 2
            // Candidate Controller = 9 * 2
            vertexCount = 153;
            candidateSinksNumber = 18;
            candidateControllersNumber = 18;
        } else if (graphSize == 5) {
            // Candidate Sink = 12 * 2
            // Candidate Controller = 12 * 2
            vertexCount = 204;
            candidateSinksNumber = 24;
            candidateControllersNumber = 24;
        }

        List<Integer> A = new ArrayList<>();
        List<Integer> B = new ArrayList<>();
        List<Integer> C = new ArrayList<>();
        List<Integer> D = new ArrayList<>();

        for (int i = 0; i < candidateControllersNumber / 2; i++) {
            A.add(i);
        }

        for (int i = 0; i < A.size(); i++) {
            int sourceNumber = A.get(i);
            int neighborNumber = A.size() + i;
            edgesPairList.add(new Pair<>("Edge_" + sourceNumber + "_To_" + neighborNumber, new Pair<>(sourceNumber, neighborNumber)));
            System.out.println("from: " + sourceNumber + " to: " + neighborNumber);
            B.add(i, neighborNumber);
        }

        for (int i = 0; i < B.size(); i++) {
            int index = i + isZeroIndexed;
            int sourceNumber = B.get(i);
            int baseIndex = A.size() + B.size() + (index - 1) * 3 - isZeroIndexed;
            int[] destinationNumbers = {baseIndex + 1, baseIndex + 2, baseIndex + 3};

            for (int des : destinationNumbers) {
                edgesPairList.add(new Pair<>("Edge_" + sourceNumber + "_To_" + des, new Pair<>(sourceNumber, des)));
                System.out.println("from: " + sourceNumber + " to: " + des);
                C.add(des);
            }
        }

        for (int i = 0; i < C.size(); i++) {
            int index = i + isZeroIndexed;
            int sourceNumber = C.get(i);
            int baseIndex = A.size() + B.size() + C.size() + (index - 1) * 4 - isZeroIndexed;
            int[] destinationNumbers = {baseIndex + 1, baseIndex + 2, baseIndex + 3, baseIndex + 4};

            for (int des : destinationNumbers) {
                edgesPairList.add(new Pair<>("Edge_" + sourceNumber + "_To_" + des, new Pair<>(sourceNumber, des)));
                System.out.println("from: " + sourceNumber + " to: " + des);
                D.add(des);
            }
        }

        for (int i = 0; i < A.size() - 1; i++) {
            edgesPairList.add(new Pair<>("Edge_" + A.get(i) + "_To_" + A.get(i + 1), new Pair<>(A.get(i), A.get(i + 1))));
        }

        for (int i = 0; i < B.size() - 1; i++) {
            edgesPairList.add(new Pair<>("Edge_" + B.get(i) + "_To_" + B.get(i + 1), new Pair<>(B.get(i), B.get(i + 1))));
        }

        for (int i = 0; i < vertexCount; i++) {
            Vertex location = new Vertex("Node_" + i, "Node_" + i, main.Parameters.Common.SINK_LOAD, main.Parameters.Common.CONTROLLER_LOAD);
            nodes.add(location);
        }

        edgesPairList.stream().forEach((edge) -> addLane(edge.getKey(), edge.getValue().getKey(), edge.getValue().getValue()));

        Set<Integer> candidateSinksNumberSet = new HashSet<>();
        Set<Integer> candidateControllerNumberSet = new HashSet<>();

        A.forEach(integer -> {
            candidateSinksNumberSet.add(integer);
            candidateControllerNumberSet.add(integer);
        });

        B.forEach(integer -> {
            candidateSinksNumberSet.add(integer);
            candidateControllerNumberSet.add(integer);
        });

        candidateControllerNumberSet.stream().forEach((candidateControllerNumber) -> candidateControllers.add(nodes.get(candidateControllerNumber)));

        candidateSinksNumberSet.stream().forEach((candidateSinkNumber) -> candidateSinks.add(nodes.get(candidateSinkNumber)));

        return new Graph(nodes, edges);
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private Graph initializeSpecialGraph2(int graphSize) {
        List<Pair<Integer, Integer>> srcDes = new ArrayList<>();
        int vertexCount = 0;
        int candidateSinksNumber = 0;
        int candidateControllersNumber = 0;
        ArrayList<Pair<String, Pair<Integer, Integer>>> edgesPairList = new ArrayList<>();
        if (graphSize == 1) {
            throw new RuntimeException("Special graph size 1 is not implemented");
        } else if (graphSize == 2) {
            throw new RuntimeException("Special graph size 2 is not implemented");
        } else if (graphSize == 3) {
            // Candidate Sink = 8 * 2
            // Candidate Controller = 8 * 2
            vertexCount = 104;
            candidateSinksNumber = 16;
            candidateControllersNumber = 16;
        } else if (graphSize == 4) {
            // Candidate Sink = 13 * 2
            // Candidate Controller = 13 * 2
            vertexCount = 169;
            candidateSinksNumber = 26;
            candidateControllersNumber = 26;
        } else if (graphSize == 5) {

        }

        List<Integer> A = new ArrayList<>();
        List<Integer> B = new ArrayList<>();
        List<Integer> C = new ArrayList<>();
        List<Integer> D = new ArrayList<>();
        List<Integer> E = new ArrayList<>();
        List<Integer> F = new ArrayList<>();

        for (int i = 1; i <= candidateControllersNumber; i++) {
            A.add(i);
        }

        for (int i = 0; i < A.size(); i++) {
            int sourceNumber = A.get(i);
            int neighbor = A.size() + (i + 1);
            B.add(i, neighbor);
            srcDes.add(new Pair<>(sourceNumber, neighbor));
        }

        for (int i = 0; i < B.size(); i++) {
            int sourceNumber = B.get(i);
            int neighbor = A.size() + B.size() + (i + 1);
            C.add(i, neighbor);
            srcDes.add(new Pair<>(sourceNumber, neighbor));
        }

        for (int i = 0; i < C.size(); i++) {
            if (i % 2 == 0) {
                continue;
            }
            int index = i + 1;
            int sourceNumber = C.get(i);
            int base = A.size() + B.size() + C.size() + ((index / 2) - 1) * 1;
            int[] neighbors = {base + 1};

            for (int neighbor : neighbors) {
                D.add(neighbor);
                srcDes.add(new Pair<>(sourceNumber, neighbor));
            }
        }

        for (int i = 0; i < D.size(); i++) {
            int index = i + 1;
            int sourceNumber = D.get(i);
            int base = A.size() + B.size() + C.size() + D.size() + (index - 1) * 2;
            int[] neighbors = {base + 1, base + 2};

            for (int neighbor : neighbors) {
                E.add(neighbor);
                srcDes.add(new Pair<>(sourceNumber, neighbor));
            }
        }

        for (int i = 0; i < E.size(); i++) {
            int index = i + 1;
            int sourceNumber = E.get(i);
            int base = A.size() + B.size() + C.size() + D.size() + E.size() + (index - 1) * 2;
            int[] neighbors = {base + 1, base + 2};

            for (int neighbor : neighbors) {
                F.add(neighbor);
                srcDes.add(new Pair<>(sourceNumber, neighbor));
            }
        }

        for (int i = 0; i < A.size() - 1; i++) {
            srcDes.add(new Pair<>(A.get(i), A.get(i + 1)));
        }

        for (int i = 0; i < B.size() - 1; i++) {
            srcDes.add(new Pair<>(B.get(i), B.get(i + 1)));
        }

        for (int i = 0; i < C.size() - 1; i++) {
            srcDes.add(new Pair<>(C.get(i), C.get(i + 1)));
        }

        srcDes.forEach(sourceDestination -> {
            int sourceIndex = sourceDestination.getKey() - 1;
            int destIndex = sourceDestination.getValue() - 1;
            edgesPairList.add(new Pair<>("Edge_" + sourceIndex + "_To_" + destIndex, new Pair<>(sourceIndex, destIndex)));
        });

        for (int i = 0; i < vertexCount; i++) {
            Vertex location = new Vertex("Node_" + i, "Node_" + i, main.Parameters.Common.SINK_LOAD, main.Parameters.Common.CONTROLLER_LOAD);
            nodes.add(location);
        }

        edgesPairList.stream().forEach((edge) -> addLane(edge.getKey(), edge.getValue().getKey(), edge.getValue().getValue()));

        Set<Integer> candidateSinksNumberSet = new HashSet<>();
        Set<Integer> candidateControllerNumberSet = new HashSet<>();

        IntStream.range(0, A.size())
                .filter(i -> i % 2 == 0)
                .forEach(i -> {
                    candidateControllerNumberSet.add(A.get(i) - 1);
                    candidateControllerNumberSet.add(C.get(i) - 1);
                    candidateSinksNumberSet.add(A.get(i) - 1);
                    candidateSinksNumberSet.add(C.get(i) - 1);
                });

        candidateControllerNumberSet.stream().forEach((candidateControllerNumber) -> candidateControllers.add(nodes.get(candidateControllerNumber)));

        candidateSinksNumberSet.stream().forEach((candidateSinkNumber) -> candidateSinks.add(nodes.get(candidateSinkNumber)));

        return new Graph(nodes, edges);
    }

    private Graph initializeRandomGraph(int graphSize) {
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
            int ithNodeNeighborsCount = vertexCount / 20;
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

        this.controllerY = new int[graph.getVertexes().size()][candidateControllers.size()];
        this.sinkY = new int[graph.getVertexes().size()][candidateSinks.size()];
        this.controllerYSpinVariables = new boolean[graph.getVertexes().size()][candidateControllers.size()];
        this.sinkYSpinVariables = new boolean[graph.getVertexes().size()][candidateSinks.size()];
        this.distances = new int[graph.getVertexes().size()][graph.getVertexes().size()];

        Utils.initializeSpinVariables(
                graph,
                candidateSinks,
                candidateControllers,
                main.Parameters.Common.SENSOR_SINK_MAX_DISTANCE,
                main.Parameters.Common.SENSOR_CONTROLLER_MAX_DISTANCE,
                sinkY,
                sinkYSpinVariables,
                controllerY,
                controllerYSpinVariables,
                distances
        );

        this.graph = graph;
        return graph;
    }
}
