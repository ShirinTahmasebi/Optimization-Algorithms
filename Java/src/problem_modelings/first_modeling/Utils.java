package problem_modelings.first_modeling;

import main.model.Graph;
import main.model.Vertex;

import java.util.List;

public interface Utils {
    static int getReliabilityEnergy(
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

    static float getLoadBalancingEnergy(
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

    static float getCostEnergy(
            List<Vertex> candidateSinks, boolean[] tempSinkXSpinVariables,
            List<Vertex> candidateControllers, boolean[] tempControllerXSpinVariables,
            int costSink, int costController, float costReductionFactor
    ) {
        for (int i = 0; i < candidateSinks.size(); i++) {
            for (int j = 0; j < candidateControllers.size(); j++) {
                if (tempSinkXSpinVariables[i] && tempControllerXSpinVariables[j]) {
                    return (costSink + costController) * costReductionFactor;
                } else if (tempSinkXSpinVariables[i]) {
                    return costSink;
                } else if (tempControllerXSpinVariables[j]) {
                    return costController;
                }
            }
        }
        return 0;
    }

    static int getSensorsCount(
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

    static boolean isNodeSelectedAsSinkOrController(
            String id,
            boolean[] tempSinkXSpinVariables, List<Vertex> candidateSinks,
            boolean[] tempControllerXSpinVariables, List<Vertex> candidateControllers
    ) {
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
        return false;
    }

    static float getSinksLoadBalancingEnergy(
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

    static float getControllersLoadBalancingEnergy(
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
    static float calculateLoadToJthSink(
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
    static float calculateLoadToJthController(
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

    @SuppressWarnings("unused")
    static int coveredNodesCountByController(int controllerIndex, Graph graph,
                                             boolean[][] controllerYSpinVariables, boolean[] tempControllerXSpinVariables) {
        int coveredControllers = 0;
        for (int j = 0; j < graph.getVertexes().size(); j++) {
            coveredControllers += (controllerYSpinVariables[j][controllerIndex]
                    && tempControllerXSpinVariables[controllerIndex]) ? 1 : 0;
        }
        return coveredControllers;
    }

    static int coveredSinksCountByNode(
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

    static int coveredControllersCountByNode(
            int nodeIndex, List<Vertex> candidateControllers,
            boolean[][] controllerYSpinVariables, boolean[] tempControllerXSpinVariables
    ) {
        int coveredControllers = 0;
        for (int j = 0; j < candidateControllers.size(); j++) {
            coveredControllers += (controllerYSpinVariables[nodeIndex][j] && tempControllerXSpinVariables[j]) ? 1 : 0;
        }
        return coveredControllers;
    }

    @SuppressWarnings("unused")
    static int coveredNodesCountBySink(int sinkIndex, Graph graph,
                                       boolean[][] sinkYSpinVariables, boolean[] tempSinkXSpinVariables) {
        int coveredSinks = 0;
        for (int j = 0; j < graph.getVertexes().size(); j++) {
            coveredSinks += (sinkYSpinVariables[j][sinkIndex] && tempSinkXSpinVariables[sinkIndex]) ? 1 : 0;
        }
        return coveredSinks;
    }

    static int totalCoverSinksScore(
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

    static int totalCoverControllersScore(
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
}