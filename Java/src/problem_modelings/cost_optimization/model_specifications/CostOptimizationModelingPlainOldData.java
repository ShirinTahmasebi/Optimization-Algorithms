package problem_modelings.cost_optimization.model_specifications;

import main.LineChartEx;
import main.model.Graph;
import main.model.Vertex;
import problem_modelings.base.plain_old_data.BaseMultiControllerMultiSinkProblemModelingPlainOldData;

import java.util.List;

public class CostOptimizationModelingPlainOldData extends BaseMultiControllerMultiSinkProblemModelingPlainOldData {
    public boolean[][] replicasOfSinkXSpinVariables;
    public boolean[][] replicasOfControllerXSpinVariables;
    public LineChartEx lineChartEx;

    public CostOptimizationModelingPlainOldData(
            Graph graph,
            List<Vertex> candidateSinks,
            List<Vertex> candidateControllers,
            boolean[][] sinkYSpinVariables,
            boolean[][] controllerYSpinVariables,
            int sensorSinkMaxDistance,
            int sensorControllerMaxDistance,
            int maxSinkCoverage,
            int maxControllerCoverage,
            int maxSinkLoad,
            int maxControllerLoad,
            int costSink,
            int costController,
            float costReductionFactor) {
        super(
                graph,
                candidateSinks, candidateControllers,
                sinkYSpinVariables, controllerYSpinVariables,
                sensorSinkMaxDistance, sensorControllerMaxDistance,
                maxSinkCoverage, maxControllerCoverage,
                maxSinkLoad, maxControllerLoad,
                costSink, costController,
                costReductionFactor
        );
        this.lineChartEx = new LineChartEx();
    }
}
