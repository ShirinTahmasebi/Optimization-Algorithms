package problem_modelings.budget_constrained_modeling.model_specifications;

import main.LineChartEx;
import main.model.Graph;
import main.model.Vertex;

import java.util.List;

public class BudgetConstrainedModelPlainOldData {
    // Problem Specifications
    public Graph graph;
    public List<Vertex> candidateControllers;           //AC
    public int sensorControllerMaxDistance;             // LMax
    public int[][] controllerY;                         // Y (Number of Hops)
    public boolean[][] controllerYSpinVariable;         // Y Spin Variabls (is distance favorable?)

    // Solution Spin Variables
    public boolean[] controllerXSpinVariables;          // SX (X Spin Variable)
    public boolean[][] replicasOfControllerXSpinVariables;

    // Temp Spin Variables
    public boolean[] tempControllerXSpinVariables;      // SX (X Spin Variable)

    public int maxControllerCoverage;                   // K
    public int maxControllerLoad;                       // W
    public int costController;
    public float costReductionFactor;
    public LineChartEx lineChartEx;

    public BudgetConstrainedModelPlainOldData(
            Graph graph,
            List<Vertex> candidateControllers,
            int[][] controllerY,
            int sensorControllerMaxDistance,
            int maxControllerCoverage,
            int maxControllerLoad,
            int costController,
            float costReductionFactor) {
        this.controllerY = controllerY;
        this.tempControllerXSpinVariables = new boolean[candidateControllers.size()];
        this.controllerXSpinVariables = new boolean[candidateControllers.size()];

        this.graph = graph;
        this.candidateControllers = candidateControllers;
        this.sensorControllerMaxDistance = sensorControllerMaxDistance;

        this.maxControllerCoverage = maxControllerCoverage;
        this.maxControllerLoad = maxControllerLoad;
        this.costController = costController;
        this.costReductionFactor = costReductionFactor;
        this.lineChartEx = new LineChartEx();
    }
}
