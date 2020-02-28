package problem_modelings.budget_constrained_lmax_optimization.model_specifications;

import base_algorithms.Cuckoo.model.CuckooDataAndBehaviour;
import main.Parameters;
import main.model.Vertex;
import problem_modelings.base.modeling.BaseMultiControllerProblemModeling;
import problem_modelings.budget_constrained_lmax_optimization.algorithms.cuckoo.CuckooBudgetConstrainedLmaxOptimizationModelingDataAndBehaviour;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BudgetConstrainedLmaxOptimizationModelingAbstract extends BaseMultiControllerProblemModeling<BudgetConstrainedLmaxOptimizationModelingPlainOldData> {

    public static int L_MAX_COEFFICIENT = 100;

    public BudgetConstrainedLmaxOptimizationModelingAbstract(BudgetConstrainedLmaxOptimizationModelingPlainOldData modelPlainOldData) {
        this.modelPlainOldData = modelPlainOldData;

        if (Parameters.Common.DO_PRINT_STEPS) {
            printProblemSpecifications(
                    modelPlainOldData.graph,
                    modelPlainOldData.candidateControllers,
                    modelPlainOldData.controllerY
            );
        }
    }

    public int calculateMaxL(boolean[] controllerXSpinVariable) {
        List<Integer> nodeMinDistancesToSelectedControllers = new ArrayList<>();
        List<Integer> controllersIndices = new ArrayList<>();

        for (int i = 0; i < modelPlainOldData.candidateControllers.size(); i++) {
            if (controllerXSpinVariable[i]) {
                String controllerNodeId = modelPlainOldData.candidateControllers.get(i).getId();
                int vertexIndexById = modelPlainOldData.graph.getVertexIndexById(controllerNodeId);
                controllersIndices.add(vertexIndexById);
            }
        }

        List<Vertex> vertexes = modelPlainOldData.graph.getVertexes();

        vertexes.forEach(vertex -> {

            List<Integer> nodeDistancesToSelectedControllers = new ArrayList<>();
            nodeDistancesToSelectedControllers.add(Integer.MIN_VALUE);

            for (Integer controllersIndex : controllersIndices) {
                int vertexIndex1 = modelPlainOldData.graph.getVertexIndexById(vertex.getId());
                nodeDistancesToSelectedControllers.add(modelPlainOldData.distances[vertexIndex1][controllersIndex]);
            }

            nodeMinDistancesToSelectedControllers.add(Collections.max(nodeDistancesToSelectedControllers));
        });

        return Collections.max(nodeMinDistancesToSelectedControllers);
    }

    public int calculateMaxL(CuckooDataAndBehaviour cuckooDataAndBehaviour) {
        if (cuckooDataAndBehaviour instanceof CuckooBudgetConstrainedLmaxOptimizationModelingDataAndBehaviour) {
            return calculateMaxL((CuckooBudgetConstrainedLmaxOptimizationModelingDataAndBehaviour) cuckooDataAndBehaviour);
        } else {
            return Integer.MAX_VALUE;
        }
    }


    public double calculateDistanceToNearestControllerEnergy(CuckooDataAndBehaviour cuckooDataAndBehaviour) {
        if (cuckooDataAndBehaviour instanceof CuckooBudgetConstrainedLmaxOptimizationModelingDataAndBehaviour) {
            return calculateDistanceToNearestControllerEnergy((CuckooBudgetConstrainedLmaxOptimizationModelingDataAndBehaviour) cuckooDataAndBehaviour);
        } else {
            return Integer.MAX_VALUE;
        }
    }

    private int calculateMaxL(CuckooBudgetConstrainedLmaxOptimizationModelingDataAndBehaviour cuckooDataAndBehaviour) {
        return calculateMaxL(cuckooDataAndBehaviour.controllerXSpinVariables);
    }

    public double calculateDistanceToNearestControllerEnergy(boolean[] controllerXSpinVariable) {
        List<Integer> nodesDistancesToFurthestCandidateControllers = new ArrayList<>();
        List<Integer> nodesDistancesToFurthestSelectedControllers = new ArrayList<>();
        List<Integer> candidateControllersIndices = new ArrayList<>();
        List<Integer> selectedControllersIndices = new ArrayList<>();

        for (int i = 0; i < modelPlainOldData.candidateControllers.size(); i++) {
            String controllerNodeId = modelPlainOldData.candidateControllers.get(i).getId();
            int vertexIndexById = modelPlainOldData.graph.getVertexIndexById(controllerNodeId);
            candidateControllersIndices.add(vertexIndexById);
            if (controllerXSpinVariable[i]) {
                selectedControllersIndices.add(vertexIndexById);
            }
        }

        List<Vertex> vertexes = modelPlainOldData.graph.getVertexes();

        vertexes.forEach(vertex -> {
            List<Integer> nodeDistancesToSelectedControllers = new ArrayList<>();
            nodeDistancesToSelectedControllers.add(Integer.MIN_VALUE);
            for (Integer controllersIndex : selectedControllersIndices) {
                int vertexIndex = modelPlainOldData.graph.getVertexIndexById(vertex.getId());
                nodeDistancesToSelectedControllers.add(modelPlainOldData.distances[vertexIndex][controllersIndex]);
            }

            nodesDistancesToFurthestSelectedControllers.add(Collections.max(nodeDistancesToSelectedControllers));
        });

        // Worst case is when for each node, the furthest candidate controller is in selected controller list.
        // Thus, we calculate the distance of each node to all candidate controllers (not just selected controllers) and then collect the maximum distance.
        vertexes.forEach(vertex -> {
            List<Integer> nodeDistancesToCandidateControllers = new ArrayList<>();
            nodeDistancesToCandidateControllers.add(Integer.MIN_VALUE);

            for (Integer controllersIndex : candidateControllersIndices) {
                int vertexIndex = modelPlainOldData.graph.getVertexIndexById(vertex.getId());
                nodeDistancesToCandidateControllers.add(modelPlainOldData.distances[vertexIndex][controllersIndex]);
            }

            nodesDistancesToFurthestCandidateControllers.add(Collections.max(nodeDistancesToCandidateControllers));
        });

        double worstCase = nodesDistancesToFurthestCandidateControllers.stream().mapToInt(Integer::intValue).sum();
        double currentCase = nodesDistancesToFurthestSelectedControllers.stream().mapToInt(Integer::intValue).sum();

        return currentCase / worstCase;
    }


    private double calculateDistanceToNearestControllerEnergy
            (CuckooBudgetConstrainedLmaxOptimizationModelingDataAndBehaviour cuckooDataAndBehaviour) {
        return calculateDistanceToNearestControllerEnergy(cuckooDataAndBehaviour.controllerXSpinVariables);
    }
}
