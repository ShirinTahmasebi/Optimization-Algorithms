package problem_modelings.base.plain_old_data;

import main.Parameters;
import main.model.Graph;
import main.model.Vertex;

import java.util.List;
import java.util.Random;

public abstract class BaseMultiControllerProblemModelingPlainOldData extends BaseProblemModelingPlainOldData {

    // Problem Specifications
    public List<Vertex> candidateControllers;       // AC
    public int sensorControllerMaxDistance;         // LMax
    public int[][] sensorsLoadToControllers;        // w[sensors][sensors]
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
        this.sensorsLoadToControllers = new int[graph.getVertexes().size()][candidateControllers.size()];
        this.maxControllerCoverage = maxControllerCoverage;
        this.maxControllerLoad = maxControllerLoad;
        this.costController = costController;

        Random random = new Random();

        // TODO: Write w matrix to file and load it from file
        for (int i = 0; i < graph.getVertexes().size(); i++) {
            for (int j = 0; j < graph.getVertexes().size(); j++) {
                int randLoad = random.nextInt(Parameters.Common.MAX_CONTROLLER_LOAD);
                sensorsLoadToControllers[i][j] = randLoad;
            }
        }
    }
}
