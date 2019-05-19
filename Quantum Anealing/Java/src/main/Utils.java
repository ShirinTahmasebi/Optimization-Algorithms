package main;

import java.util.LinkedList;
import java.util.List;
import main.dijkstra.DijkstraAlgorithm;
import main.model.Graph;
import main.model.Vertex;

public class Utils {

    public static int getReliabilityEnergy(
            Graph graph, boolean[][] sinkYSpinVariables, boolean[][] controllerYSpinVariables,
            List<Vertex> candidateSinks, boolean[] tempSinkXSpinVariables,
            List<Vertex> candidateControllers, boolean[] tempControllerXSpinVariables,
            int maxSinkCoverage, int maxControllerCoverage
    ) {
        int sensorNumbers = getSensorsCount(
                graph,
                candidateSinks, tempSinkXSpinVariables,
                candidateControllers, tempControllerXSpinVariables
        );
        return (maxSinkCoverage * sensorNumbers
                - totalCoverSinksScore(
                        graph, maxSinkCoverage, sinkYSpinVariables,
                        candidateSinks, tempSinkXSpinVariables,
                        candidateControllers, tempControllerXSpinVariables
                )) + (maxControllerCoverage * sensorNumbers
                - totalCoverControllersScore(
                        graph, maxControllerCoverage, controllerYSpinVariables,
                        candidateSinks, tempSinkXSpinVariables,
                        candidateControllers, tempControllerXSpinVariables
                ));
    }

    public static float getLoadBalancingEnergy(
            Graph graph, boolean[][] sinkYSpinVariables, boolean[][] controllerYSpinVariables,
            List<Vertex> candidateSinks, boolean[] tempSinkXSpinVariables,
            List<Vertex> candidateControllers, boolean[] tempControllerXSpinVariables,
            int maxSinkLoad, int maxSinkCoverage,
            int maxControllerLoad, int maxControllerCoverage
    ) {
        float sinksLoadBalancingEnergy = getSinksLoadBalancingEnergy(
                graph, sinkYSpinVariables,
                candidateSinks, tempSinkXSpinVariables,
                candidateControllers, tempControllerXSpinVariables,
                maxSinkLoad, maxSinkCoverage
        );

        float controllersLoadBalancingEnergy
                = getControllersLoadBalancingEnergy(
                        graph, controllerYSpinVariables,
                        candidateSinks, tempSinkXSpinVariables,
                        candidateControllers, tempControllerXSpinVariables,
                        maxControllerLoad, maxControllerCoverage
                );
        return sinksLoadBalancingEnergy + controllersLoadBalancingEnergy;
    }

    public static float getCostEnergy(
            List<Vertex> candidateSinks, boolean[] tempSinkXSpinVariables,
            List<Vertex> candidateControllers, boolean[] tempControllerXSpinVariables,
            int costSink, int costController, float costReductionFactor
    ) {
        for (int i = 0; i < candidateSinks.size(); i++) {
            for (int j = 0; j < candidateControllers.size(); j++) {
                if (tempSinkXSpinVariables[i] && tempControllerXSpinVariables[j]) {
                    float cost = (costSink + costController) * costReductionFactor;
                    return cost;
                } else if (tempSinkXSpinVariables[i]) {
                    return costSink;
                } else if (tempControllerXSpinVariables[j]) {
                    return costController;
                }
            }
        }
        return 0;
    }

    public static int getSensorsCount(
            Graph graph,
            List<Vertex> candidateSinks, boolean[] tempSinkXSpinVariables,
            List<Vertex> candidateControllers, boolean[] tempControllerXSpinVariables
    ) {
        int sensorCount = 0;
        for (int i = 0; i < graph.getVertexes().size(); i++) {
            Vertex graphNode = graph.getVertexes().get(i);
            if (!Utils.isNodeSelectedAsSinkOrController(
                    graphNode.getId(), tempSinkXSpinVariables, candidateSinks,
                    tempControllerXSpinVariables, candidateControllers)) {
                sensorCount++;
            }
        }
        return sensorCount;
    }

    public static boolean isNodeSelectedAsSinkOrController(
            String id,
            boolean[] tempSinkXSpinVariables, List<Vertex> candidateSinks,
            boolean[] tempControllerXSpinVariables, List<Vertex> candidateControllers
    ) {
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

    public static float getSinksLoadBalancingEnergy(
            Graph graph, boolean[][] sinkYSpinVariables,
            List<Vertex> candidateSinks, boolean[] tempSinkXSpinVariables,
            List<Vertex> candidateControllers, boolean[] tempControllerXSpinVariables,
            int maxSinkLoad, int maxSinkCoverage) {
        float totalSinkLoadEnergy = 0;
        for (int j = 0; j < candidateSinks.size(); j++) {
            float totalLoadToJthSink
                    = calculateLoadToJthSink(
                            j, graph, sinkYSpinVariables,
                            tempSinkXSpinVariables, candidateSinks,
                            tempControllerXSpinVariables, candidateControllers
                    );
            float bestSinkLoad = maxSinkLoad / (maxSinkCoverage - 1);
            totalSinkLoadEnergy += Math.max(0, totalLoadToJthSink - bestSinkLoad);
        }
        return totalSinkLoadEnergy;
    }

    public static float getControllersLoadBalancingEnergy(
            Graph graph, boolean[][] controllerYSpinVariables,
            List<Vertex> candidateSinks, boolean[] tempSinkXSpinVariables,
            List<Vertex> candidateControllers, boolean[] tempControllerXSpinVariables,
            int maxControllerLoad, int maxControllerCoverage) {
        float totalControllerLoadEnergy = 0;
        for (int j = 0; j < candidateControllers.size(); j++) {
            float totalLoadToJthController
                    = calculateLoadToJthController(j, graph, controllerYSpinVariables,
                            tempSinkXSpinVariables, candidateSinks,
                            tempControllerXSpinVariables, candidateControllers
                    );
            float bestControllerLoad = maxControllerLoad / (maxControllerCoverage - 1);
            totalControllerLoadEnergy += Math.max(0, totalLoadToJthController - bestControllerLoad);
        }
        return totalControllerLoadEnergy;
    }

    // j is sink's index in candidateSinks (Not graph node index)
    public static float calculateLoadToJthSink(
            int j, Graph graph, boolean[][] sinkYSpinVariables,
            boolean[] tempSinkXSpinVariables, List<Vertex> candidateSinks,
            boolean[] tempControllerXSpinVariables, List<Vertex> candidateControllers
    ) {
        float totalLoadToJthSink = 0;
        for (int i = 0; i < graph.getVertexes().size(); i++) {
            Vertex graphNode = graph.getVertexes().get(i);
            if (!Utils.isNodeSelectedAsSinkOrController(
                    graphNode.getId(), tempSinkXSpinVariables, candidateSinks,
                    tempControllerXSpinVariables, candidateControllers)) {
                boolean condition = sinkYSpinVariables[i][j] && tempSinkXSpinVariables[j];
                if (condition) {
                    totalLoadToJthSink
                            += (float) graphNode.getSinkLoad()
                            / coveredSinksCountByNode(i, candidateSinks, sinkYSpinVariables, tempSinkXSpinVariables);
                }
            }
        }
        return totalLoadToJthSink;
    }

    // j is controller's index in candidateControllers (Not graph node index)
    public static float calculateLoadToJthController(
            int j, Graph graph, boolean[][] controllerYSpinVariables,
            boolean[] tempSinkXSpinVariables, List<Vertex> candidateSinks,
            boolean[] tempControllerXSpinVariables, List<Vertex> candidateControllers) {
        float totalLoadToJthController = 0;
        for (int i = 0; i < graph.getVertexes().size(); i++) {
            Vertex graphNode = graph.getVertexes().get(i);
            if (!Utils.isNodeSelectedAsSinkOrController(
                    graphNode.getId(), tempSinkXSpinVariables, candidateSinks,
                    tempControllerXSpinVariables, candidateControllers)) {
                boolean condition = controllerYSpinVariables[i][j] && tempControllerXSpinVariables[j];
                if (condition) {
                    totalLoadToJthController
                            += (float) graphNode.getControllerLoad()
                            / coveredControllersCountByNode(i, candidateControllers, controllerYSpinVariables, tempControllerXSpinVariables);
                }
            }
        }
        return totalLoadToJthController;
    }

    public static int coveredNodesCountByController(int controllerIndex, Graph graph,
            boolean[][] controllerYSpinVariables, boolean[] tempControllerXSpinVariables) {
        int coveredControllers = 0;
        for (int j = 0; j < graph.getVertexes().size(); j++) {
            coveredControllers += (controllerYSpinVariables[j][controllerIndex]
                    && tempControllerXSpinVariables[controllerIndex]) ? 1 : 0;
        }
        return coveredControllers;
    }

    public static int coveredSinksCountByNode(
            int nodeIndex,
            List<Vertex> candidateSinks,
            boolean[][] sinkYSpinVariables, boolean[] tempSinkXSpinVariables
    ) {
        int coveredSinks = 0;
        for (int j = 0; j < candidateSinks.size(); j++) {
            coveredSinks += (sinkYSpinVariables[nodeIndex][j] && tempSinkXSpinVariables[j]) ? 1 : 0;
        }
        return coveredSinks;
    }

    public static int coveredControllersCountByNode(
            int nodeIndex, List<Vertex> candidateControllers,
            boolean[][] controllerYSpinVariables, boolean[] tempControllerXSpinVariables
    ) {
        int coveredControllers = 0;
        for (int j = 0; j < candidateControllers.size(); j++) {
            coveredControllers += (controllerYSpinVariables[nodeIndex][j] && tempControllerXSpinVariables[j]) ? 1 : 0;
        }
        return coveredControllers;
    }

    public static int coveredNodesCountBySink(int sinkIndex, Graph graph,
            boolean[][] sinkYSpinVariables, boolean[] tempSinkXSpinVariables) {
        int coveredSinks = 0;
        for (int j = 0; j < graph.getVertexes().size(); j++) {
            coveredSinks += (sinkYSpinVariables[j][sinkIndex] && tempSinkXSpinVariables[sinkIndex]) ? 1 : 0;
        }
        return coveredSinks;
    }

    public static int totalCoverSinksScore(
            Graph graph, int maxSinkCoverage,
            boolean[][] sinkYSpinVariables,
            List<Vertex> candidateSinks, boolean[] tempSinkXSpinVariables,
            List<Vertex> candidateControllers, boolean[] tempControllerXSpinVariables
    ) {
        int score = 0;
        for (int i = 0; i < graph.getVertexes().size(); i++) {
            Vertex graphNode = graph.getVertexes().get(i);
            if (!Utils.isNodeSelectedAsSinkOrController(
                    graphNode.getId(), tempSinkXSpinVariables, candidateSinks,
                    tempControllerXSpinVariables, candidateControllers)) {
                score += Math.min(
                        maxSinkCoverage,
                        coveredSinksCountByNode(i, candidateSinks, sinkYSpinVariables, tempSinkXSpinVariables)
                );
            }
        }
        return score;
    }

    public static int totalCoverControllersScore(
            Graph graph, int maxControllerCoverage,
            boolean[][] controllerYSpinVariables,
            List<Vertex> candidateSinks, boolean[] tempSinkXSpinVariables,
            List<Vertex> candidateControllers, boolean[] tempControllerXSpinVariables
    ) {
        int score = 0;
        for (int i = 0; i < graph.getVertexes().size(); i++) {
            Vertex graphNode = graph.getVertexes().get(i);
            if (!Utils.isNodeSelectedAsSinkOrController(
                    graphNode.getId(), tempSinkXSpinVariables, candidateSinks,
                    tempControllerXSpinVariables, candidateControllers)) {
                score += Math.min(
                        maxControllerCoverage,
                        coveredControllersCountByNode(i, candidateControllers, controllerYSpinVariables, tempControllerXSpinVariables)
                );
            }
        }
        return score;
    }

    public static void printProblemSpecifications(
            Graph graph,
            List<Vertex> candidateSinks, boolean[][] sinkYSpinVariables,
            List<Vertex> candidateControllers, boolean[][] controllerYSpinVariables) {
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

    public static void printGeneratedSolution(boolean[] tempSinkXSpinVariables, boolean[] tempControllerXSpinVariables) {
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

    public static int getDistance(Graph graph, int firstNodeIndex, int secondeNodeIndex) {
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
        dijkstra.execute(graph.getVertexes().get(firstNodeIndex));
        LinkedList<Vertex> path = dijkstra.getPath(graph.getVertexes().get(secondeNodeIndex));
        return (path == null) ? 0 : path.size() - 1;
    }

    public static boolean isDistanceFavorable(Graph graph, int firstNodeIndex, int secondNodeIndex, int maxDistance) {
        return getDistance(graph, firstNodeIndex, secondNodeIndex) <= maxDistance;
    }
}