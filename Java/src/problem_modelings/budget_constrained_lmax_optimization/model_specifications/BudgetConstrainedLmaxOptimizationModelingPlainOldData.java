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
            int[][] distances,
            int[][] sensorToSensorWorkload) {
        super(graph, candidateControllers, sensorControllerMaxDistance, maxControllerCoverage, maxControllerLoad, costController, sensorToSensorWorkload);
        this.controllerY = controllerY;
        this.distances = distances;
        this.totalBudget = totalBudget;
        this.lineChartEx = new LineChartEx();
    }
}
