package problem_modelings.synchronization_overhead_lmax_optimization.model_specifications;

import main.LineChartEx;
import main.model.Graph;
import main.model.Vertex;
import problem_modelings.base.plain_old_data.BaseMultiControllerProblemModelingPlainOldData;

import java.util.List;

public class SynchronizationOverheadLmaxOptimizationModelingPlainOldData extends BaseMultiControllerProblemModelingPlainOldData {
    public int[][] controllerY;     // Y (Number of Hops)
    public int[][] distances;
    public boolean[][] replicasOfControllerXSpinVariables;
    public int totalBudget;
    public LineChartEx lineChartEx;

    public SynchronizationOverheadLmaxOptimizationModelingPlainOldData(
            Graph graph,
            List<Vertex> candidateControllers,
            int[][] controllerY,
            int sensorControllerMaxDistance,
            int maxControllerCoverage,
            int maxControllerLoad,
            int costController,
            int totalBudget,
            int[][] distances) {
        super(graph, candidateControllers, sensorControllerMaxDistance, maxControllerCoverage, maxControllerLoad, costController);
        this.controllerY = controllerY;
        this.distances = distances;
        this.totalBudget = totalBudget;
        this.lineChartEx = new LineChartEx();
    }
}
