package problem_modelings.base.plain_old_data;

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
}
