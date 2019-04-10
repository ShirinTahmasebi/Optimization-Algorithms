package quantum.anealing;

import quantum.anealing.graph.Vertex;
import quantum.anealing.graph.Graph;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import quantum.anealing.dijkstra.DijkstraAlgorithm;

public class QuantumAnealing {

    private final Graph graph;

    private final List<Vertex> candidateSinks;            // AS
    private final List<Vertex> candidateControllers;      //AC

    private final int sensorSinkMaxDistance;              // Lmax
    private final int sensorControllerMaxDistance;        // LPrimeMax

    // Solution Spin Variables
    private final List<Boolean> sinkXSpinVariables;             // SX (X Spin Variable)
    private final List<Boolean> controllerXSpinVariables;       // SXPrime (X Spin Variable)
    private final ArrayList<ArrayList<Boolean>> sinkYSpinVariables;           // SY (Y Spin Variable)
    private final ArrayList<ArrayList<Boolean>> controllerYSpinVariables;     // SYPrime (Y Spin Variable)

    // Test Spin Variables
    private final List<Boolean> tempSinkXSpinVariables;           // SX (X Spin Variable)           
    private final List<Boolean> tempControllerXSpinVariables;     // SXPrime (X Spin Variable)     

    public QuantumAnealing(
            Graph graph,
            List candidateSinks,
            List candidateControllers,
            int sensorSinkMaxDistance,
            int sensorControllerMaxDistance
    ) {
        this.controllerYSpinVariables = new ArrayList<>();
        this.sinkYSpinVariables = new ArrayList<>();
        this.tempControllerXSpinVariables = new ArrayList<>();
        this.tempSinkXSpinVariables = new ArrayList<>();
        this.sinkXSpinVariables = new ArrayList<>();
        this.controllerXSpinVariables = new ArrayList<>();

        this.graph = graph;
        this.candidateSinks = candidateSinks;
        this.candidateControllers = candidateControllers;
        this.sensorSinkMaxDistance = sensorSinkMaxDistance;
        this.sensorControllerMaxDistance = sensorControllerMaxDistance;

        // --- Initialize Y and YPrime Spin Variables
        for (int i = 0; i < graph.getVertexes().size(); i++) {
            ArrayList<Boolean> row = new ArrayList<>();
            for (int j = 0; j < candidateSinks.size(); j++) {
                row.add(false);
            }
            sinkYSpinVariables.add(row);
        }

        for (int i = 0; i < graph.getVertexes().size(); i++) {
            ArrayList<Boolean> row = new ArrayList<>();
            for (int j = 0; j < candidateControllers.size(); j++) {
                row.add(false);
            }
            controllerYSpinVariables.add(row);
        }

        for (int i = 0; i < sinkYSpinVariables.size(); i++) {
            for (int j = 0; j < candidateSinks.size(); j++) {
                sinkYSpinVariables.get(i).remove(j);
                sinkYSpinVariables.get(i).add(j, isDistanceFavorable(i, j, sensorSinkMaxDistance));
            }
        }

        for (int i = 0; i < controllerYSpinVariables.size(); i++) {
            for (int j = 0; j < candidateControllers.size(); j++) {
                controllerYSpinVariables.get(i).remove(j);
                controllerYSpinVariables.get(i).add(j, isDistanceFavorable(i, j, sensorControllerMaxDistance));
            }
        }
        // ---

        printProblemSpecifications();
    }

    private void printProblemSpecifications() {
        // Print graph
        graph.getVertexes().stream().forEach((vertex) -> {
            System.out.println("Vertex: " + vertex.toString());
        });

        System.out.println();

        graph.getEdges().stream().forEach((edge) -> {
            System.out.println("Edge: " + edge.toString());

        });

        System.out.println();

        // Print candidate sinks
        System.out.print("Candidate sink vertexes are: ");
        candidateSinks.stream().forEach((candidateSinkVertex) -> {
            System.out.print(candidateSinkVertex.toString() + ", ");
        });

        System.out.println();
        System.out.println();

        // Print candidate controllers
        System.out.print("Candidate controller vertexes are: ");
        candidateControllers.stream().forEach((candidateControllerVertex) -> {
            System.out.print(candidateControllerVertex.toString() + ", ");
        });

        System.out.println();
        System.out.println();

        System.out.println("Sink Y: ");
        sinkYSpinVariables.stream().forEach((tempSinkYSpinVariable) -> {
            System.out.println(tempSinkYSpinVariable + ", ");
        });

        System.out.println();
        System.out.println();

        System.out.println("Controller Y: ");
        controllerYSpinVariables.stream().forEach((tempControllerYSpinVariable) -> {
            System.out.println(tempControllerYSpinVariable + ", ");
        });
    }

    void execute() {
        int tempIterationsCount = 20;

        generateInitialTempSpinVariables();

        while (tempIterationsCount != 0) {
            tempIterationsCount--;
            generateNeighbour();
        }
    }

    private void generateInitialTempSpinVariables() {
        // --- Initialize temp lists to false
        for (int i = 0; i < candidateControllers.size(); i++) {
            tempControllerXSpinVariables.add(false);
        }

        for (int i = 0; i < candidateSinks.size(); i++) {
            tempSinkXSpinVariables.add(false);
        }
        // ---

        // --- Select random configuration for temp lists
        for (int i = 0; i < tempSinkXSpinVariables.size(); i++) {
            double probabilityOfOne = Math.random();
            tempSinkXSpinVariables.remove(i);
            tempSinkXSpinVariables.add(i, probabilityOfOne < .5);
        }

        for (int i = 0; i < tempControllerXSpinVariables.size(); i++) {
            double probabilityOfOne = Math.random();
            tempControllerXSpinVariables.remove(i);
            tempControllerXSpinVariables.add(i, probabilityOfOne < .5);
        }
        // ---

        printGeneratedSolution();
    }

    private int getDistance(int firstNodeIndex, int secondeNodeIndex) {
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
        dijkstra.execute(graph.getVertexes().get(firstNodeIndex));
        LinkedList<Vertex> path = dijkstra.getPath(graph.getVertexes().get(secondeNodeIndex));
        return (path == null) ? 0 : path.size() - 1;
    }

    private boolean isDistanceFavorable(int firstNodeIndex, int secondNodeIndex, int maxDistance) {
        return getDistance(firstNodeIndex, secondNodeIndex) <= maxDistance;
    }

    private void printGeneratedSolution() {
        // --- Print temp lists
        System.out.println();
        System.out.println("Temp Sink X: ");
        tempSinkXSpinVariables.stream().forEach((tempSinkXSpinVariable) -> {
            System.out.print(tempSinkXSpinVariable + ", ");
        });

        System.out.println();
        System.out.println("Temp Controller X: ");
        tempControllerXSpinVariables.stream().forEach((tempControllerXSpinVariable) -> {
            System.out.print(tempControllerXSpinVariable + ", ");
        });

        System.out.println();
        System.out.println();
        // ---
    }

    private void generateNeighbour() {
        Random random = new Random();
        int randInt = random.nextInt(tempSinkXSpinVariables.size() + tempControllerXSpinVariables.size());

        if (randInt < tempSinkXSpinVariables.size()) {
            // Change randInt-th item in sink array
            boolean prevValue = tempSinkXSpinVariables.get(randInt);
            tempSinkXSpinVariables.remove(randInt);
            tempSinkXSpinVariables.add(randInt, !prevValue);
        } else {
            int index = randInt - (tempSinkXSpinVariables.size() - 1) - 1;
            // Change index-th item in controller array
            boolean prevValue = tempControllerXSpinVariables.get(index);
            tempControllerXSpinVariables.remove(index);
            tempControllerXSpinVariables.add(index, !prevValue);
        }
        printGeneratedSolution();

    }

}
