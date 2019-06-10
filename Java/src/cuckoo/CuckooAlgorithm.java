package cuckoo;

import main.LineChartEx;
import main.Utils;
import main.model.Graph;
import main.model.Vertex;

import java.util.List;

public class CuckooAlgorithm {

    // Problem Specifications
    private final Graph graph;

    private final List<Vertex> candidateSinks;            // AS
    private final List<Vertex> candidateControllers;      //AC

    private final int sensorSinkMaxDistance;              // Lmax
    private final int sensorControllerMaxDistance;        // LPrimeMax

    private final boolean[][] sinkYSpinVariables;           // SY (Y Spin Variable)
    private final boolean[][] controllerYSpinVariables;     // SYPrime (Y Spin Variable)

    // Solution Spin Variables
    private boolean[] sinkXSpinVariables;             // SX (X Spin Variable)
    private boolean[] controllerXSpinVariables;       // SXPrime (X Spin Variable)

    // Temp Spin Variables
    private boolean[] tempSinkXSpinVariables;           // SX (X Spin Variable)
    private boolean[] tempControllerXSpinVariables;     // SXPrime (X Spin Variable)

    private final int maxSinkCoverage;          // K
    private final int maxControllerCoverage;    // KPrime
    private final int maxSinkLoad;          // W
    private final int maxControllerLoad;    // WPrime
    private final int costSink;
    private final int costController;
    private final float costReductionFactor;

    private final LineChartEx lineChartEx;

    public CuckooAlgorithm(
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
            float costReductionFactor
    ) {
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

        lineChartEx = new LineChartEx();

        if (main.Main.DO_PRINT_STEPS) {
            Utils.printProblemSpecifications(graph, candidateSinks, sinkYSpinVariables, candidateControllers, controllerYSpinVariables);
        }
    }

    public double execute() {
        return 1;
    }
}
