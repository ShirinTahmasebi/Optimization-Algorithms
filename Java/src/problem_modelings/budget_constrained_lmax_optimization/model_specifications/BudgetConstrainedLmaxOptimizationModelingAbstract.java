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


    public int calculateDistanceToNearestControllerEnergy(CuckooDataAndBehaviour cuckooDataAndBehaviour) {
        if (cuckooDataAndBehaviour instanceof CuckooBudgetConstrainedLmaxOptimizationModelingDataAndBehaviour) {
            return calculateDistanceToNearestControllerEnergy((CuckooBudgetConstrainedLmaxOptimizationModelingDataAndBehaviour) cuckooDataAndBehaviour);
        } else {
            return Integer.MAX_VALUE;
        }
    }

    private int calculateMaxL(CuckooBudgetConstrainedLmaxOptimizationModelingDataAndBehaviour cuckooDataAndBehaviour) {
        return calculateMaxL(cuckooDataAndBehaviour.controllerXSpinVariables);
    }

    public int calculateDistanceToNearestControllerEnergy(boolean[] controllerXSpinVariable) {
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
                int vertexIndex = modelPlainOldData.graph.getVertexIndexById(vertex.getId());
                nodeDistancesToSelectedControllers.add(modelPlainOldData.distances[vertexIndex][controllersIndex]);
            }

            nodeMinDistancesToSelectedControllers.add(Collections.max(nodeDistancesToSelectedControllers));
        });

        return nodeMinDistancesToSelectedControllers.stream().mapToInt(Integer::intValue).sum();
    }

    private int calculateDistanceToNearestControllerEnergy(CuckooBudgetConstrainedLmaxOptimizationModelingDataAndBehaviour cuckooDataAndBehaviour) {
        return calculateDistanceToNearestControllerEnergy(cuckooDataAndBehaviour.controllerXSpinVariables);
    }
}
