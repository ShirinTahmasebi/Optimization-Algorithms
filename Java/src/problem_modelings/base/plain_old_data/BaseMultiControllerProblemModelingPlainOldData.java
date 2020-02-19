package problem_modelings.base.plain_old_data;

import main.model.Graph;
import main.model.Vertex;

import java.util.List;

public abstract class BaseMultiControllerProblemModelingPlainOldData extends BaseProblemModelingPlainOldData {

    // Problem Specifications
    public List<Vertex> candidateControllers;       // AC
    public int sensorControllerMaxDistance;         // LMax
    // TODO: Rename this field: controllerYSpinVariables
    public boolean[][] controllerYSpinVariable;     // SY (Y Spin Variable)

    // Solution Spin Variables
    public boolean[] controllerXSpinVariables;      // SX (X Spin Variable)

    // Temp Spin Variables
    public boolean[] tempControllerXSpinVariables;  // SX (X Spin Variable)

    public int maxControllerCoverage;               // K
    public int maxControllerLoad;                   // W
    public int costController;

    public BaseMultiControllerProblemModelingPlainOldData(
            Graph graph,
            List<Vertex> candidateControllers,
            int sensorControllerMaxDistance,
            int maxControllerCoverage,
            int maxControllerLoad,
            int costController) {
        super(graph);

        this.candidateControllers = candidateControllers;
        this.sensorControllerMaxDistance = sensorControllerMaxDistance;

        this.controllerXSpinVariables = new boolean[candidateControllers.size()];
        this.tempControllerXSpinVariables = new boolean[candidateControllers.size()];
        this.maxControllerCoverage = maxControllerCoverage;
        this.maxControllerLoad = maxControllerLoad;
        this.costController = costController;
    }
}
