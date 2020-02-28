package problem_modelings.budget_constrained_lmax_optimization;

import main.ModelNoEnum;
import main.Parameters;
import main.model.Graph;
import main.model.Vertex;

import java.util.*;

public interface Utils {


    static double getReliabilityEnergy(
            Graph graph, int[][] controllerY, List<Vertex> candidateControllers,
            boolean[] tempControllerXSpinVariables, int maxControllerCoverage, int maxL) {
        boolean[][] controllerYSpinVariables = calculateSpinVariableFromControllerY(controllerY, maxL);
        int sensorNumbers = getSensorsCount(graph, candidateControllers, tempControllerXSpinVariables);
        double currentCoverage = totalCoverControllersScore(graph, maxControllerCoverage, controllerYSpinVariables, candidateControllers, tempControllerXSpinVariables);
        double bestCaseCoverage = maxControllerCoverage * sensorNumbers;

        // Since the coverage range is from (0, bestCaseCoverage), current coverage is between these two bounds. 
        // Note: 0 means there is no coverage and bestCaseCoverage occurs when all nodes are covered with ideal number of controllers 
        // Thus, to score and scale the current coverage from 0 to 1, we should divide how far the current coverage is from the best case to total range 
        // It means: total scaled cost equals to best (total range) - current / best(total range) 
        return 1 - (currentCoverage / bestCaseCoverage);
    }

    static double getLoadBalancingEnergy(
            Graph graph, int[][] controllerY,
            List<Vertex> candidateControllers, boolean[] tempControllerXSpinVariables,
            int maxControllerLoad, int maxControllerCoverage, int maxL, int[][] sensorToSensorWorkload
    ) {
        boolean[][] controllerYSpinVariables = calculateSpinVariableFromControllerY(controllerY, maxL);
        return getControllersLoadBalancingEnergy(
                graph, controllerYSpinVariables,
                candidateControllers, tempControllerXSpinVariables,
                maxControllerLoad, maxControllerCoverage, sensorToSensorWorkload
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

    static double getControllersLoadBalancingEnergy(
            Graph graph, boolean[][] controllerYSpinVariables,
            List<Vertex> candidateControllers, boolean[] tempControllerXSpinVariables,
            int maxControllerLoad, int maxControllerCoverage, int[][] sensorToSensorWorkload) {
        float currentControllerLoadEnergy = 0;
        for (int j = 0; j < candidateControllers.size(); j++) {
            float totalLoadToJthController = calculateLoadToJthController(j, graph, controllerYSpinVariables,
                    tempControllerXSpinVariables, candidateControllers, sensorToSensorWorkload
            );
            float bestControllerLoad = maxControllerLoad / (maxControllerCoverage - 1);
            currentControllerLoadEnergy += Math.max(0, totalLoadToJthController - bestControllerLoad);
        }

        int selectedControllersCount = 0;
        for (boolean tempControllerXSpinVariable : tempControllerXSpinVariables) {
            if (tempControllerXSpinVariable) {
                selectedControllersCount++;
            }
        }

        // Worst case: Foreach controller, maximum load is sent by all sensors.
        double worstCase = selectedControllersCount * maxControllerLoad * graph.getVertexes().size();
        return currentControllerLoadEnergy / worstCase;
    }

    // j is controller's index in candidateControllers (Not graph node index)
    static float calculateLoadToJthController(
            int j, Graph graph, boolean[][] controllerYSpinVariables,
            boolean[] tempControllerXSpinVariables, List<Vertex> candidateControllers, int[][] sensorToSensorWorkload) {
        float totalLoadToJthController = 0;
        Vertex controller = candidateControllers.get(j);
        int vertexIndexById = graph.getVertexIndexById(controller.getId());

        for (int i = 0; i < graph.getVertexes().size(); i++) {
            double controllerSyncOverhead = sensorToSensorWorkload[i][vertexIndexById];

            Vertex graphNode = graph.getVertexes().get(i);
            if (!Utils.isNodeSelectedAsController(graphNode.getId(), tempControllerXSpinVariables, candidateControllers)) {
                boolean condition = controllerYSpinVariables[i][j] && tempControllerXSpinVariables[j];
                if (condition) {
                    totalLoadToJthController += controllerSyncOverhead
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

    static double getControllerSynchronizationCost(Graph graph, int[][] controllerY, List<Vertex> candidateControllers, boolean[] tempControllerXSpinVariables, int[][] sensorsLoadToControllers) {
        if (Parameters.Common.MODEL_NO != ModelNoEnum.BUDGET_CONSTRAINED_CONTROLLER_OVERHEAD) {
            return 0.;
        }

        List<Double> overheads = new ArrayList<>();
        List<Double> delays = new ArrayList<>();
        List<Double> highestPossibleDelayOverhead = new ArrayList<>();

        double totalControllerSyncDelayOverhead = .0;

        for (int i = 0; i < tempControllerXSpinVariables.length; i++) {
            if (tempControllerXSpinVariables[i]) {
                // For each selected controller like controller1
                Vertex controller1 = candidateControllers.get(i);
                int vertexIndexById1 = graph.getVertexIndexById(controller1.getId());
                double tempControllerSyncDelayOverhead = .0;
                for (int j = 0; j < tempControllerXSpinVariables.length; j++) {
                    if (tempControllerXSpinVariables[j]) {
                        Vertex controller2 = candidateControllers.get(j);
                        int vertexIndexById2 = graph.getVertexIndexById(controller2.getId());
                        if (vertexIndexById1 != vertexIndexById2) {
                            double controllerSyncOverhead = sensorsLoadToControllers[vertexIndexById1][vertexIndexById2];
                            double controllerSyncDelay = controllerY[vertexIndexById1][j];

                            overheads.add(controllerSyncOverhead);
                            delays.add(controllerSyncDelay);

                            tempControllerSyncDelayOverhead += controllerSyncDelay * controllerSyncOverhead;
                        }
                    }
                }

                double delayOverhead = Collections.max(overheads) * Collections.max(delays);
                highestPossibleDelayOverhead.add(delayOverhead);
                totalControllerSyncDelayOverhead += tempControllerSyncDelayOverhead / delayOverhead;

                overheads = new ArrayList<>();
                delays = new ArrayList<>();
            }
        }

        return totalControllerSyncDelayOverhead / Collections.max(highestPossibleDelayOverhead);
    }
}
