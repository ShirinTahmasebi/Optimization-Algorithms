package quantum.anealing;

import quantum.anealing.graph.Vertex;
import quantum.anealing.graph.Graph;
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
    private final boolean[] sinkXSpinVariables;             // SX (X Spin Variable)
    private final boolean[] controllerXSpinVariables;       // SXPrime (X Spin Variable)
    private final boolean[][] sinkYSpinVariables;           // SY (Y Spin Variable)
    private final boolean[][] controllerYSpinVariables;     // SYPrime (Y Spin Variable)

    // Test Spin Variables
    private final boolean[] tempSinkXSpinVariables;           // SX (X Spin Variable)           
    private final boolean[] tempControllerXSpinVariables;     // SXPrime (X Spin Variable)

    private final int maxSinkCoverage;          // K
    private final int maxControllerCoverage;    // KPrime

    public QuantumAnealing(
            Graph graph,
            List candidateSinks,
            List candidateControllers,
            int sensorSinkMaxDistance,
            int sensorControllerMaxDistance,
            int maxSinkCovrage,
            int maxControllerCoverage
    ) {
        this.controllerYSpinVariables = new boolean[graph.getVertexes().size()][candidateControllers.size()];
        this.sinkYSpinVariables = new boolean[graph.getVertexes().size()][candidateSinks.size()];
        this.tempControllerXSpinVariables = new boolean[candidateControllers.size()];
        this.tempSinkXSpinVariables = new boolean[candidateSinks.size()];
        this.sinkXSpinVariables = new boolean[candidateSinks.size()];
        this.controllerXSpinVariables = new boolean[candidateControllers.size()];

        this.graph = graph;
        this.candidateSinks = candidateSinks;
        this.candidateControllers = candidateControllers;
        this.sensorSinkMaxDistance = sensorSinkMaxDistance;
        this.sensorControllerMaxDistance = sensorControllerMaxDistance;

        this.maxSinkCoverage = maxSinkCovrage;
        this.maxControllerCoverage = maxControllerCoverage;
        initializeSpinVariables();

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

        for (int i = 0; i < graph.getVertexes().size(); i++) {
            for (int j = 0; j < candidateSinks.size(); j++) {
                System.out.print(sinkYSpinVariables[i][j] + ", ");
            }
            System.out.println();
        }

        System.out.println();
        System.out.println();

        System.out.println("Controller Y: ");

        for (int i = 0; i < graph.getVertexes().size(); i++) {
            for (int j = 0; j < candidateControllers.size(); j++) {
                System.out.print(controllerYSpinVariables[i][j] + ", ");
            }
            System.out.println();
        }
    }

    void execute() {
        int tempIterationsCount = 10;
        int energy;
        generateInitialTempSpinVariables();

        while (tempIterationsCount != 0) {
            tempIterationsCount--;
            generateNeighbour();
            energy = calculateEnergy();
        }
    }

    private void generateInitialTempSpinVariables() {
        // --- Initialize temp lists to false
        for (int i = 0; i < candidateControllers.size(); i++) {
            controllerXSpinVariables[i] = false;
            tempControllerXSpinVariables[i] = false;
        }

        for (int i = 0; i < candidateSinks.size(); i++) {
            sinkXSpinVariables[i] = false;
            tempSinkXSpinVariables[i] = false;
        }
        // ---

        // --- Select random configuration for temp lists
        for (int i = 0; i < sinkXSpinVariables.length; i++) {
            double probabilityOfOne = Math.random();
            sinkXSpinVariables[i] = probabilityOfOne < .5;
        }

        for (int i = 0; i < controllerXSpinVariables.length; i++) {
            double probabilityOfOne = Math.random();
            controllerXSpinVariables[i] = probabilityOfOne < .5;
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
        for (int i = 0; i < tempSinkXSpinVariables.length; i++) {
            System.out.print(tempSinkXSpinVariables[i] + ", ");
        }

        System.out.println();
        System.out.println("Temp Controller X: ");
        for (int i = 0; i < tempControllerXSpinVariables.length; i++) {
            System.out.print(tempControllerXSpinVariables[i] + ", ");
        }

        System.out.println();
        System.out.println();
        // ---
    }

    private void generateNeighbour() {
        Random random = new Random();
        int randInt = random.nextInt(tempSinkXSpinVariables.length + tempControllerXSpinVariables.length);

        if (randInt < tempSinkXSpinVariables.length) {
            // Change randInt-th item in sink array
            boolean prevValue = tempSinkXSpinVariables[randInt];
            tempSinkXSpinVariables[randInt] = !prevValue;
        } else {
            // Change index-th item in controller array
            int index = randInt - (tempSinkXSpinVariables.length - 1) - 1;
            boolean prevValue = tempControllerXSpinVariables[index];
            tempControllerXSpinVariables[index] = !prevValue;
        }
        printGeneratedSolution();

    }

    private int calculateEnergy() {
        int kineticEnergy = getKineticEnergy();
        int reliabilityEnergy = getReliabilityEnergy();
        int loadBalancingEnergy = getLoadBalancingEnergy();
        int costEnergy = getCostEnergy();
        int potentialEnergy;

        potentialEnergy = reliabilityEnergy + loadBalancingEnergy + costEnergy;
        int energy = kineticEnergy + potentialEnergy;
        return energy;
    }

    private int getKineticEnergy() {
        // TODO: Calculate Kinetic Energy
        return 0;
    }

    private int getReliabilityEnergy() {
        int sensorNumbers = getSensorsCount();
        return (maxSinkCoverage * sensorNumbers - totalCoverSinksScore())
                + (maxControllerCoverage * sensorNumbers - totalCoverControllersScore());
    }

    private int getLoadBalancingEnergy() {
        // TODO: Calculate Load Balancing Energy
        
        return 0;
    }

    private int getCostEnergy() {
        // TODO: Calculate Cost Energy
        return 0;
    }

    private int getSensorsCount() {
        int sensorCount = 0;
        for (int i = 0; i < graph.getVertexes().size(); i++) {
            if (!isNodeSelectedAsSinkOrController(graph.getVertexes().get(i).getId())) {
                sensorCount++;
            }
        }
        return sensorCount;
    }

    private int totalCoverSinksScore() {
        int score = 0;
        for (int i = 0; i < graph.getVertexes().size(); i++) {
            if (!isNodeSelectedAsSinkOrController(graph.getVertexes().get(i).getId())) {
                score += Math.min(maxSinkCoverage, coveredSinksCountByNode(i));
            }
        }
        return score;
    }

    private int totalCoverControllersScore() {
        int score = 0;
        for (int i = 0; i < graph.getVertexes().size(); i++) {
            if (!isNodeSelectedAsSinkOrController(graph.getVertexes().get(i).getId())) {
                score += Math.min(maxControllerCoverage, coveredControllersCountByNode(i));
            }
        }
        return score;
    }

    private int coveredSinksCountByNode(int nodeIndex) {
        int coveredSinks = 0;
        for (int j = 0; j < candidateSinks.size(); j++) {
            coveredSinks += (sinkYSpinVariables[nodeIndex][j] && tempSinkXSpinVariables[j]) ? 1 : 0;
        }
        return coveredSinks;
    }

    private int coveredControllersCountByNode(int nodeIndex) {
        int coveredControllers = 0;
        for (int j = 0; j < candidateControllers.size(); j++) {
            coveredControllers += (controllerYSpinVariables[nodeIndex][j] && tempControllerXSpinVariables[j]) ? 1 : 0;
        }
        return coveredControllers;
    }

    private void initializeSpinVariables() {
        // --- Initialize Y and YPrime Spin Variables
        for (int i = 0; i < graph.getVertexes().size(); i++) {
            for (int j = 0; j < candidateSinks.size(); j++) {
                sinkYSpinVariables[i][j] = false;
            }
        }

        for (int i = 0; i < graph.getVertexes().size(); i++) {
            for (int j = 0; j < candidateControllers.size(); j++) {
                controllerYSpinVariables[i][j] = false;
            }
        }

        for (int i = 0; i < graph.getVertexes().size(); i++) {
            for (int j = 0; j < candidateSinks.size(); j++) {
                // The following line can be replaced with vertexIndex = i - but I prefered to write this in the following way for more readability
                int vertexIndex1 = graph.getVertexIndexById(graph.getVertexes().get(i).getId());
                int vertexIndex2 = graph.getVertexIndexById(((Vertex) candidateSinks.get(j)).getId());
                sinkYSpinVariables[i][j] = isDistanceFavorable(vertexIndex1, vertexIndex2, sensorSinkMaxDistance);
            }
        }

        for (int i = 0; i < graph.getVertexes().size(); i++) {
            for (int j = 0; j < candidateControllers.size(); j++) {
                // The following line can be replaced with vertexIndex = i - but I prefered to write this in the following way for more readability
                int vertexIndex1 = graph.getVertexIndexById(graph.getVertexes().get(i).getId());
                int vertexIndex2 = graph.getVertexIndexById(((Vertex) candidateControllers.get(j)).getId());
                controllerYSpinVariables[i][j] = isDistanceFavorable(vertexIndex1, vertexIndex2, sensorControllerMaxDistance);
            }
        }
        // ---
    }

    private boolean isNodeSelectedAsSinkOrController(String id) {
        boolean isSinkOrController = false;
        for (int i = 0; i < tempSinkXSpinVariables.length; i++) {
            boolean tempSinkXSpinVariable = tempSinkXSpinVariables[i];
            if (tempSinkXSpinVariable) {
                String sinkId = candidateSinks.get(i).getId();
                if (sinkId.equals(id)) {
                    return true;
                }
            }
        }

        for (int i = 0; i < tempControllerXSpinVariables.length; i++) {
            boolean tempControllerXSpinVariable = tempControllerXSpinVariables[i];
            if (tempControllerXSpinVariable) {
                String sinkId = candidateControllers.get(i).getId();
                if (sinkId.equals(id)) {
                    return true;
                }
            }
        }
        return isSinkOrController;
    }

}
