package main;

import main.model.Graph;
import main.model.Vertex;

import java.util.List;

public abstract class BaseAlgorithm {
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
    protected final LineChartEx lineChartEx;

    public BaseAlgorithm(
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
    }
}
