package problem_modelings.modeling_types;

import main.LineChartEx;
import main.Utils;
import main.model.Graph;
import main.model.Vertex;
import problem_modelings.BaseProblemModeling;

import java.util.List;

public abstract class FirstModelAbstract extends BaseProblemModeling {

    // Problem Specifications
    protected Graph graph;

    protected List<Vertex> candidateSinks;            // AS
    protected List<Vertex> candidateControllers;      //AC

    protected int sensorSinkMaxDistance;              // Lmax
    protected int sensorControllerMaxDistance;        // LPrimeMax

    protected boolean[][] sinkYSpinVariables;           // SY (Y Spin Variable)
    protected boolean[][] controllerYSpinVariables;     // SYPrime (Y Spin Variable)

    // Solution Spin Variables
    protected boolean[] sinkXSpinVariables;             // SX (X Spin Variable)
    protected boolean[] controllerXSpinVariables;       // SXPrime (X Spin Variable)
    protected boolean[][] replicasOfSinkXSpinVariables;
    protected boolean[][] replicasOfControllerXSpinVariables;

    // Temp Spin Variables
    protected boolean[] tempSinkXSpinVariables;           // SX (X Spin Variable)
    protected boolean[] tempControllerXSpinVariables;     // SXPrime (X Spin Variable)

    protected int maxSinkCoverage;          // K
    protected int maxControllerCoverage;    // KPrime
    protected int maxSinkLoad;          // W
    protected int maxControllerLoad;    // WPrime
    protected int costSink;
    protected int costController;
    protected float costReductionFactor;
    protected LineChartEx lineChartEx;

    public FirstModelAbstract(
            Graph graph,
            List<Vertex> candidateSinks,
            List<Vertex> candidateControllers,
            boolean[][] sinkYSpinVariables,
            boolean[][] controllerYSpinVariables,
            int sensorSinkMaxDistance,
            int sensorControllerMaxDistance,
            int maxSinkCovrage,
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

        this.maxSinkCoverage = maxSinkCovrage;
        this.maxControllerCoverage = maxControllerCoverage;
        this.maxSinkLoad = maxSinkLoad;
        this.maxControllerLoad = maxControllerLoad;
        this.costSink = costSink;
        this.costController = costController;
        this.costReductionFactor = costReductionFactor;
        this.lineChartEx = new LineChartEx();

        if (main.Main.DO_PRINT_STEPS) {
            Utils.printProblemSpecifications(graph, candidateSinks, sinkYSpinVariables, candidateControllers, controllerYSpinVariables);
        }
    }

    public void printGeneratedSolution(boolean[] tempSinkXSpinVariables, boolean[] tempControllerXSpinVariables) {
        // --- Print temp lists
        System.out.println();
        System.out.println("Temp Sink X: ");
        for (boolean tempSinkXSpinVariable : tempSinkXSpinVariables) {
            System.out.print(tempSinkXSpinVariable + ", ");
        }

        System.out.println();
        System.out.println("Temp Controller X: ");
        for (boolean tempControllerXSpinVariable : tempControllerXSpinVariables) {
            System.out.print(tempControllerXSpinVariable + ", ");
        }

        System.out.println();
        System.out.println();
        // ---
    }
}
