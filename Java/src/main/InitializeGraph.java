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
    public int[][] sensorToSensorWorkload;        // w[sensors][sensors]

    public static void main(String[] args) {
        InitializeGraph initializeGraph = new InitializeGraph();
        initializeGraph.initialize();
        initializeGraph.writeObjectsToFile();
    }

    private void writeObjectsToFile() {
        writeObjectToFile(graph, Utils.FILE_NAME_GRAPH);
        writeObjectToFile(candidateSinks, Utils.FILE_NAME_CANDIDATE_SINKS);
        writeObjectToFile(candidateControllers, Utils.FILE_NAME_CANDIDATE_CONTROLLERS);
        writeObjectToFile(sinkY, Utils.FILE_NAME_SINK_Y);
        writeObjectToFile(sinkYSpinVariables, Utils.FILE_NAME_SINK_Y_SPIN_VARIABLES);
        writeObjectToFile(controllerY, Utils.FILE_NAME_CONTROLLER_Y);
        writeObjectToFile(controllerYSpinVariables, Utils.FILE_NAME_CONTROLLER_Y_SPIN_VARIABLES);
        writeObjectToFile(distances, Utils.FILE_NAME_DISTANCES);
        writeObjectToFile(sensorToSensorWorkload, Utils.FILE_NAME_SENSOR_TO_SENSOR_WORKLOAD);
    }

    public Graph initializeGraph(GraphSizeEnum graphSize) {
        if (Parameters.Common.USE_RANDOM_GRAPH) {
            return initializeRandomGraph(graphSize);
        }

        return initializeSpecialGraph(graphSize);
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private Graph initializeSpecialGraph(GraphSizeEnum graphSize) {
        List<Pair<Integer, Integer>> srcDes = new ArrayList<>();
        int vertexCount = 0;
        int candidateControllersNumber = 0;
        ArrayList<Pair<String, Pair<Integer, Integer>>> edgesPairList = new ArrayList<>();
        if (GraphSizeEnum.RANDOM_20_SPECIAL_NONE == graphSize) {
            throw new RuntimeException("Special graph size 1 is not implemented");
        } else if (GraphSizeEnum.RANDOM_40_SPECIAL_NONE == graphSize) {
            throw new RuntimeException("Special graph size 2 is not implemented");
        } else if (GraphSizeEnum.RANDOM_100_SPECIAL_104 == graphSize) {
            // Candidate Sink = 8 * 2
            // Candidate Controller = 8 * 2
            vertexCount = 104;
            candidateControllersNumber = 16;
        } else if (GraphSizeEnum.RANDOM_150_SPECIAL_169 == graphSize) {
            // Candidate Sink = 13 * 2
            // Candidate Controller = 13 * 2
            vertexCount = 169;
            candidateControllersNumber = 26;
        } else if (GraphSizeEnum.RANDOM_200_SPECIAL_195 == graphSize) {
            // Candidate Sink = 13 * 2
            // Candidate Controller = 13 * 2
            vertexCount = 195;
            candidateControllersNumber = 30;
        } else if (GraphSizeEnum.RANDOM_NONE_SPECIAL_143 == graphSize) {
            // Candidate Sink = 13 * 2
            // Candidate Controller = 13 * 2
            vertexCount = 143;
            candidateControllersNumber = 22;
        }

        List<Integer> A = new ArrayList<>();
        List<Integer> B = new ArrayList<>();
        List<Integer> C = new ArrayList<>();
        List<Integer> D = new ArrayList<>();
        List<Integer> E = new ArrayList<>();

        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
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

        Random random = new Random();

        sensorToSensorWorkload = new int[vertexCount][vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            for (int j = 0; j < vertexCount; j++) {
                int randLoad = random.nextInt(Parameters.Common.MAX_CONTROLLER_LOAD);
                sensorToSensorWorkload[i][j] = randLoad;
            }
        }

        return new Graph(nodes, edges);
    }

    private Graph initializeRandomGraph(GraphSizeEnum graphSize) {
        int vertexCount = 0;
        int candidateSinksNumber = 0;
        int candidateControllersNumber = 0;
        ArrayList<Pair<String, Pair<Integer, Integer>>> edgesPairList = new ArrayList<>();
        if (GraphSizeEnum.RANDOM_20_SPECIAL_NONE == graphSize) {
            vertexCount = 20;
            candidateSinksNumber = 6;
            candidateControllersNumber = 9;
        } else if (GraphSizeEnum.RANDOM_40_SPECIAL_NONE == graphSize) {
            vertexCount = 40;
            candidateSinksNumber = 16;
            candidateControllersNumber = 8;
        } else if (GraphSizeEnum.RANDOM_80_SPECIAL_NONE == graphSize) {
            vertexCount = 80;
            candidateSinksNumber = 32;
            candidateControllersNumber = 16;
        } else if (GraphSizeEnum.RANDOM_100_SPECIAL_104 == graphSize) {
            vertexCount = 100;
            candidateSinksNumber = 40;
            candidateControllersNumber = 20;
        } else if (GraphSizeEnum.RANDOM_150_SPECIAL_169 == graphSize) {
            vertexCount = 150;
            candidateSinksNumber = 60;
            candidateControllersNumber = 30;
        } else if (GraphSizeEnum.RANDOM_200_SPECIAL_195 == graphSize) {
            vertexCount = 200;
            candidateSinksNumber = 80;
            candidateControllersNumber = 40;
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

        Random random = new Random();

        sensorToSensorWorkload = new int[vertexCount][vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            for (int j = 0; j < vertexCount; j++) {
                int randLoad = random.nextInt(Parameters.Common.MAX_CONTROLLER_LOAD);
                sensorToSensorWorkload[i][j] = randLoad;
            }
        }

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
