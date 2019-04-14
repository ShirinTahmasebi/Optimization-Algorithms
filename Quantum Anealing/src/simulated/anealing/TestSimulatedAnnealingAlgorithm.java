package simulated.anealing;

import main.model.Vertex;
import main.model.Graph;
import java.util.List;

public class TestSimulatedAnnealingAlgorithm {

    private static final float COST_REDUCTION_FACTOR = 0.75f;
    private static final int TROTTER_REPLICAS = 50;     // P
    private static final float TEMPERATURE = 50f;         // T
    private static final int MONTE_CARLO_STEP = 50;   // M
    private static final float TUNNLING_FIELD_INITIAL = 1f;
    private static final float TUNNLING_FIELD_FINAL = .5f;
    private static final float TUNNLING_FIELD_EVAPORATION = .9f;

    public void execute(
            Graph graph,
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


    }
}
