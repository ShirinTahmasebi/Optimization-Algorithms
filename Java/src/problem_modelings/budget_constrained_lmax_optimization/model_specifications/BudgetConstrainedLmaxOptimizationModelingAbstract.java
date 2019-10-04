package problem_modelings.budget_constrained_lmax_optimization.model_specifications;

import base_algorithms.Cuckoo.model.CuckooDataAndBehaviour;
import main.Parameters;
import main.model.Graph;
import main.model.Vertex;
import problem_modelings.BaseProblemModeling;
import problem_modelings.budget_constrained_lmax_optimization.algorithms.cuckoo.CuckooBudgetConstrainedCostOptimizationModelingDataAndBehaviour;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BudgetConstrainedLmaxOptimizationModelingAbstract extends BaseProblemModeling {

    public static int L_MAX_COEFFICIENT = 100;
    public static int SUMMATION_OFL_MAX_COEFFICIENT = 10;

    public BudgetConstrainedLmaxOptimizationModelingPlainOldData modelPlainOldData;

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

    public void printGeneratedSolution(boolean[] tempControllerXSpinVariables) {
        // --- Print temp lists
        System.out.println();
        System.out.println("Temp Controller X: ");
        for (boolean tempControllerXSpinVariable : tempControllerXSpinVariables) {
            System.out.print(tempControllerXSpinVariable + ", ");
        }

        System.out.println();
        System.out.println();
        // ---
    }

    public void printProblemSpecifications(
            Graph graph,
            List<Vertex> candidateControllers, int[][] controllerY) {
        // Print graph
        graph.getVertexes().stream().forEach((vertex) -> System.out.println("Vertex: " + vertex.toString()));

        System.out.println();

        graph.getEdges().stream().forEach((edge) -> System.out.println("Edge: " + edge.toString()));

        System.out.println();

        // Print candidate controllers
        System.out.print("Candidate controller vertexes are: ");
        candidateControllers.stream().forEach((candidateControllerVertex) -> System.out.print(candidateControllerVertex.toString() + ", "));

        System.out.println();
        System.out.println();

        System.out.println("Controller Y: ");

        for (int i = 0; i < graph.getVertexes().size(); i++) {
            for (int j = 0; j < candidateControllers.size(); j++) {
                System.out.print(controllerY[i][j] + ", ");
            }
            System.out.println();
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
            nodeDistancesToSelectedControllers.add(Integer.MAX_VALUE);

            for (Integer controllersIndex : controllersIndices) {
                int vertexIndex1 = modelPlainOldData.graph.getVertexIndexById(vertex.getId());
                nodeDistancesToSelectedControllers.add(modelPlainOldData.distances[vertexIndex1][controllersIndex]);
            }

            nodeMinDistancesToSelectedControllers.add(Collections.min(nodeDistancesToSelectedControllers));
        });

        return Collections.max(nodeMinDistancesToSelectedControllers);
    }

    public int calculateMaxL(CuckooDataAndBehaviour cuckooDataAndBehaviour) {
        if (cuckooDataAndBehaviour instanceof CuckooBudgetConstrainedCostOptimizationModelingDataAndBehaviour) {
            return calculateMaxL((CuckooBudgetConstrainedCostOptimizationModelingDataAndBehaviour) cuckooDataAndBehaviour);
        } else {
            return Integer.MAX_VALUE;
        }
    }


    public int calculateDistanceToNearestControllerEnergy(CuckooDataAndBehaviour cuckooDataAndBehaviour) {
        if (cuckooDataAndBehaviour instanceof CuckooBudgetConstrainedCostOptimizationModelingDataAndBehaviour) {
            return calculateDistanceToNearestControllerEnergy((CuckooBudgetConstrainedCostOptimizationModelingDataAndBehaviour) cuckooDataAndBehaviour);
        } else {
            return Integer.MAX_VALUE;
        }
    }

    private int calculateMaxL(CuckooBudgetConstrainedCostOptimizationModelingDataAndBehaviour cuckooDataAndBehaviour) {

        List<Integer> nodeMinDistancesToSelectedControllers = new ArrayList<>();
        List<Integer> controllersIndices = new ArrayList<>();

        for (int i = 0; i < modelPlainOldData.candidateControllers.size(); i++) {
            if (cuckooDataAndBehaviour.controllerXSpinVariables[i]) {
                String controllerNodeId = modelPlainOldData.candidateControllers.get(i).getId();
                int vertexIndexById = modelPlainOldData.graph.getVertexIndexById(controllerNodeId);
                controllersIndices.add(vertexIndexById);
            }
        }

        List<Vertex> vertexes = modelPlainOldData.graph.getVertexes();

        vertexes.forEach(vertex -> {

            List<Integer> nodeDistancesToSelectedControllers = new ArrayList<>();
            nodeDistancesToSelectedControllers.add(Integer.MAX_VALUE);

            for (Integer controllersIndex : controllersIndices) {
                int vertexIndex1 = modelPlainOldData.graph.getVertexIndexById(vertex.getId());
                nodeDistancesToSelectedControllers.add(modelPlainOldData.distances[vertexIndex1][controllersIndex]);
            }

            nodeMinDistancesToSelectedControllers.add(Collections.min(nodeDistancesToSelectedControllers));
        });

        return Collections.max(nodeMinDistancesToSelectedControllers);
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
            nodeDistancesToSelectedControllers.add(Integer.MAX_VALUE);

            for (Integer controllersIndex : controllersIndices) {
                int vertexIndex = modelPlainOldData.graph.getVertexIndexById(vertex.getId());
                nodeDistancesToSelectedControllers.add(modelPlainOldData.distances[vertexIndex][controllersIndex]);
            }

            nodeMinDistancesToSelectedControllers.add(Collections.min(nodeDistancesToSelectedControllers));
        });

        return nodeMinDistancesToSelectedControllers.stream().mapToInt(Integer::intValue).sum();
    }

    private int calculateDistanceToNearestControllerEnergy(CuckooBudgetConstrainedCostOptimizationModelingDataAndBehaviour cuckooDataAndBehaviour) {

        List<Integer> nodeMinDistancesToSelectedControllers = new ArrayList<>();
        List<Integer> controllersIndices = new ArrayList<>();

        for (int i = 0; i < modelPlainOldData.candidateControllers.size(); i++) {
            if (cuckooDataAndBehaviour.controllerXSpinVariables[i]) {
                String controllerNodeId = modelPlainOldData.candidateControllers.get(i).getId();
                int vertexIndexById = modelPlainOldData.graph.getVertexIndexById(controllerNodeId);
                controllersIndices.add(vertexIndexById);
            }
        }

        List<Vertex> vertexes = modelPlainOldData.graph.getVertexes();

        vertexes.forEach(vertex -> {

            List<Integer> nodeDistancesToSelectedControllers = new ArrayList<>();
            nodeDistancesToSelectedControllers.add(Integer.MAX_VALUE);

            for (Integer controllersIndex : controllersIndices) {
                int vertexIndex = modelPlainOldData.graph.getVertexIndexById(vertex.getId());
                nodeDistancesToSelectedControllers.add(modelPlainOldData.distances[vertexIndex][controllersIndex]);
            }

            nodeMinDistancesToSelectedControllers.add(Collections.min(nodeDistancesToSelectedControllers));
        });

        return nodeMinDistancesToSelectedControllers.stream().mapToInt(Integer::intValue).sum();
    }
}
