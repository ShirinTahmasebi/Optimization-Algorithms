package problem_modelings.budget_constrained_lmax_optimization.model_specifications;

import main.LineChartEx;
import main.model.Graph;
import main.model.Vertex;
import problem_modelings.base.plain_old_data.BaseMultiControllerProblemModelingPlainOldData;

import java.util.List;

public class BudgetConstrainedLmaxOptimizationModelingPlainOldData extends BaseMultiControllerProblemModelingPlainOldData {
    public int[][] controllerY;     // Y (Number of Hops)
    public int[][] distances;
    public boolean[][] replicasOfControllerXSpinVariables;
    public int totalBudget;
    public LineChartEx lineChartEx;

    public BudgetConstrainedLmaxOptimizationModelingPlainOldData(
            Graph graph,
            List<Vertex> candidateControllers,
            int[][] controllerY,
            int sensorControllerMaxDistance,
            int maxControllerCoverage,
            int maxControllerLoad,
            int costController,
            int totalBudget,
            int[][] distances) {
        this.controllerY = controllerY;
        this.tempControllerXSpinVariables = new boolean[candidateControllers.size()];
        this.controllerXSpinVariables = new boolean[candidateControllers.size()];
        this.distances = distances;

        this.graph = graph;
        this.candidateControllers = candidateControllers;
        this.sensorControllerMaxDistance = sensorControllerMaxDistance;

        this.maxControllerCoverage = maxControllerCoverage;
        this.maxControllerLoad = maxControllerLoad;
        this.costController = costController;
        this.totalBudget = totalBudget;
        this.lineChartEx = new LineChartEx();
    }
}
