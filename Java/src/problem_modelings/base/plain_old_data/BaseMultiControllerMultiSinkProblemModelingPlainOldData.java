package problem_modelings.base.plain_old_data;

import main.model.Vertex;

import java.util.List;

public abstract class BaseMultiControllerMultiSinkProblemModelingPlainOldData extends BaseProblemModelingPlainOldData {

    // Problem Specifications
    public List<Vertex> candidateSinks;             // AS
    public List<Vertex> candidateControllers;       // AC

    public int sensorSinkMaxDistance;               // Lmax
    public int sensorControllerMaxDistance;         // LPrimeMax

    public boolean[][] sinkYSpinVariables;          // SY (Y Spin Variable)
    public boolean[][] controllerYSpinVariables;    // SYPrime (Y Spin Variable)

    // Solution Spin Variables
    public boolean[] sinkXSpinVariables;            // SX (X Spin Variable)
    public boolean[] controllerXSpinVariables;      // SXPrime (X Spin Variable)

    // Temp Spin Variables
    public boolean[] tempSinkXSpinVariables;        // SX (X Spin Variable)
    public boolean[] tempControllerXSpinVariables;  // SXPrime (X Spin Variable)

    public int maxSinkCoverage;                     // K
    public int maxControllerCoverage;               // KPrime
    public int maxSinkLoad;                         // W
    public int maxControllerLoad;                   // WPrime
    public int costSink;
    public int costController;
    public float costReductionFactor;
}
