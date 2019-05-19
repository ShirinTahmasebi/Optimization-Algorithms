package simulated.anealing;

import main.model.Vertex;
import main.model.Graph;
import java.util.List;

public class TestSimulatedAnnealingAlgorithm {

    private static final float COST_REDUCTION_FACTOR = 0.75f;
    private static final float TEMPERATURE_INITIAL = 100;              // T Initial
    private static final float TEMPERATURE_FINAL = 1;                // T Final
    private static final float TEMPERATURE_COOLING_RATE = .75f;         // T Cooling Rate
    private static final int MONTE_CARLO_STEP = 50;   // M
    SimulatedAnealing sa;

    public TestSimulatedAnnealingAlgorithm(Graph graph,
            List<Vertex> candidateSinks,
            List<Vertex> candidateControllers,
            int sensorSinkMaxDistance,
            int sensorControllerMaxDistance,
            int maxSinkCoverage,
            int maxControllerCoverage,
            int maxSinkLoad,
            int maxControllerLoad,
            int costSink,
            int costController) {
        sa = new SimulatedAnealing(
                graph,
                candidateSinks,
                candidateControllers,
                sensorSinkMaxDistance,
                sensorControllerMaxDistance,
                maxSinkCoverage,
                maxControllerCoverage,
                maxSinkLoad,
                maxControllerLoad,
                costSink,
                costController,
                COST_REDUCTION_FACTOR,
                TEMPERATURE_INITIAL,
                TEMPERATURE_FINAL,
                TEMPERATURE_COOLING_RATE,
                MONTE_CARLO_STEP
        );
    }

    public double execute() {
        return sa.execute();
    }
}
