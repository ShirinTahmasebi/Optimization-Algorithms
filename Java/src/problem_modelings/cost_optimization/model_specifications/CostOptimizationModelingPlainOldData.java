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
        this.controllerYSpinVariables = controllerYSpinVariables;
        this.sinkYSpinVariables = sinkYSpinVariables;
        this.tempControllerXSpinVariables = new boolean[candidateControllers.size()];
        this.tempSinkXSpinVariables = new boolean[candidateSinks.size()];
        this.sinkXSpinVariables = new boolean[candidateSinks.size()];
        this.controllerXSpinVariables = new boolean[candidateControllers.size()];

        this.graph = graph;
        this.candidateSinks = candidateSinks;
        this.candidateControllers = candidateControllers;
        this.sensorSinkMaxDistance = sensorSinkMaxDistance;
        this.sensorControllerMaxDistance = sensorControllerMaxDistance;

        this.maxSinkCoverage = maxSinkCoverage;
        this.maxControllerCoverage = maxControllerCoverage;
        this.maxSinkLoad = maxSinkLoad;
        this.maxControllerLoad = maxControllerLoad;
        this.costSink = costSink;
        this.costController = costController;
        this.costReductionFactor = costReductionFactor;
        this.lineChartEx = new LineChartEx();
    }
}
