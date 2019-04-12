package quantum.anealing;

import quantum.anealing.graph.Vertex;
import quantum.anealing.graph.Graph;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import main.Main;

public class TestQuantumAnnealingAlgorithm {

    private static final int SENSOR_SINK_MAX_DISTANCE = 3;              // Lmax
    private static final int SENSOR_CONTROLLER_MAX_DISTANCE = 2;        // LPrimeMax
    private static final int MAX_SINK_COVERAGE = 6;             // k
    private static final int MAX_CONTROLLER_COVERAGE = 6;       // kPrime
    private static final int MAX_SINK_LOAD = 30;        // W
    private static final int MAX_CONTROLLER_LOAD = 30;  // WPrime
    private static final int COST_SINK = 1;
    private static final int COST_CONTROLLER = 3;
    private static final float COST_REDUCTION_FACTOR = 0.75f;
    private static final int TROTTER_REPLICAS = 50;     // P
    private static final float TEMPERATURE = 50f;         // T
    private static final int MONTE_CARLO_STEP = 50;   // M
    private static final float TUNNLING_FIELD_INITIAL = 1f;
    private static final float TUNNLING_FIELD_FINAL = .5f;
    private static final float TUNNLING_FIELD_EVAPORATION = .9f;

    public void execute() {

    }

    public void execute(Graph graph, List<Vertex> candidateSinks, List<Vertex> candidateControllers) {

        QuantumAnealing qa = new QuantumAnealing(
                graph,
                candidateSinks,
                candidateControllers,
                SENSOR_SINK_MAX_DISTANCE,
                SENSOR_CONTROLLER_MAX_DISTANCE,
                MAX_SINK_COVERAGE,
                MAX_CONTROLLER_COVERAGE,
                MAX_SINK_LOAD,
                MAX_CONTROLLER_LOAD,
                COST_SINK,
                COST_CONTROLLER,
                COST_REDUCTION_FACTOR,
                TROTTER_REPLICAS,
                TEMPERATURE,
                MONTE_CARLO_STEP,
                TUNNLING_FIELD_INITIAL,
                TUNNLING_FIELD_FINAL,
                TUNNLING_FIELD_EVAPORATION
        );

        qa.execute();
    }
}
