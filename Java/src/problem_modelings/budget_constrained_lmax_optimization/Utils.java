package problem_modelings.budget_constrained_lmax_optimization;

import main.model.Graph;
import main.model.Vertex;
import problem_modelings.budget_constrained_lmax_optimization.model_specifications.BudgetConstrainedLmaxOptimizationModelingAbstract;

import java.util.List;

public interface Utils {

    static int getReliabilityEnergy(
            Graph graph, int[][] controllerY, List<Vertex> candidateControllers,
            boolean[] tempControllerXSpinVariables, int maxControllerCoverage, int maxL
    ) {
        boolean[][] controllerYSpinVariables = calculateSpinVariableFromControllerY(controllerY, maxL);
        int sensorNumbers = getSensorsCount(
                graph,
                candidateControllers, tempControllerXSpinVariables
        );
        return (maxControllerCoverage * sensorNumbers
                - totalCoverControllersScore(
                graph, maxControllerCoverage, controllerYSpinVariables,
                candidateControllers, tempControllerXSpinVariables
        ));
    }

    static float getLoadBalancingEnergy(
            Graph graph, int[][] controllerY,
            List<Vertex> candidateControllers, boolean[] tempControllerXSpinVariables,
            int maxControllerLoad, int maxControllerCoverage, int maxL
    ) {
        boolean[][] controllerYSpinVariables = calculateSpinVariableFromControllerY(controllerY, maxL);
        return getControllersLoadBalancingEnergy(
                graph, controllerYSpinVariables,
                candidateControllers, tempControllerXSpinVariables,
                maxControllerLoad, maxControllerCoverage
        );
    }

    static boolean[][] calculateSpinVariableFromControllerY(int[][] controllerY, int maxL) {
        boolean[][] controllerYSpinVariables = new boolean[controllerY.length][controllerY[0].length];
        for (int i = 0; i < controllerY.length; i++) {
            for (int j = 0; j < controllerY[i].length; j++) {
                controllerYSpinVariables[i][j] = controllerY[i][j] <= maxL;
            }
        }
        return controllerYSpinVariables;
    }

    static int getSensorsCount(
            Graph graph,
            List<Vertex> candidateControllers, boolean[] tempControllerXSpinVariables
    ) {
        int sensorCount = 0;
        for (int i = 0; i < graph.getVertexes().size(); i++) {
            Vertex graphNode = graph.getVertexes().get(i);
            if (!Utils.isNodeSelectedAsController(
                    graphNode.getId(),
                    tempControllerXSpinVariables, candidateControllers)) {
                sensorCount++;
            }
        }
        return sensorCount;
    }

    static boolean isNodeSelectedAsController(
            String id,
            boolean[] tempControllerXSpinVariables, List<Vertex> candidateControllers
    ) {
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

    static float getControllersLoadBalancingEnergy(
            Graph graph, boolean[][] controllerYSpinVariables,
            List<Vertex> candidateControllers, boolean[] tempControllerXSpinVariables,
            int maxControllerLoad, int maxControllerCoverage) {
        float totalControllerLoadEnergy = 0;
        for (int j = 0; j < candidateControllers.size(); j++) {
            float totalLoadToJthController
                    = calculateLoadToJthController(j, graph, controllerYSpinVariables,
                    tempControllerXSpinVariables, candidateControllers
            );
            float bestControllerLoad = maxControllerLoad / (maxControllerCoverage - 1);
            totalControllerLoadEnergy += Math.max(0, totalLoadToJthController - bestControllerLoad);
        }
        return totalControllerLoadEnergy;
    }

    // j is controller's index in candidateControllers (Not graph node index)
    static float calculateLoadToJthController(
            int j, Graph graph, boolean[][] controllerYSpinVariables,
            boolean[] tempControllerXSpinVariables, List<Vertex> candidateControllers) {
        float totalLoadToJthController = 0;
        for (int i = 0; i < graph.getVertexes().size(); i++) {
            Vertex graphNode = graph.getVertexes().get(i);
            if (!Utils.isNodeSelectedAsController(graphNode.getId(), tempControllerXSpinVariables, candidateControllers)) {
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

    static int totalCoverControllersScore(
            Graph graph, int maxControllerCoverage,
            boolean[][] controllerYSpinVariables,
            List<Vertex> candidateControllers, boolean[] tempControllerXSpinVariables
    ) {
        int score = 0;
        for (int i = 0; i < graph.getVertexes().size(); i++) {
            Vertex graphNode = graph.getVertexes().get(i);
            if (!Utils.isNodeSelectedAsController(
                    graphNode.getId(),
                    tempControllerXSpinVariables, candidateControllers)) {
                score += Math.min(
                        maxControllerCoverage,
                        coveredControllersCountByNode(i, candidateControllers, controllerYSpinVariables, tempControllerXSpinVariables)
                );
            }
        }
        return score;
    }

    static double getMaxLEnergy(int maxL) {
        if (maxL == Integer.MAX_VALUE) return maxL;
        return (maxL < 0 ? maxL * -1 : maxL) * BudgetConstrainedLmaxOptimizationModelingAbstract.L_MAX_COEFFICIENT;
    }

    static double getSummationOfMaxLEnergy(int summationOfLMax) {
        return (summationOfLMax < 0 ? summationOfLMax * -1 : summationOfLMax) * BudgetConstrainedLmaxOptimizationModelingAbstract.SUMMATION_OFL_MAX_COEFFICIENT;
    }
}
