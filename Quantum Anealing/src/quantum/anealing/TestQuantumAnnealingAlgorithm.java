package quantum.anealing;

import main.model.Vertex;
import main.model.Graph;
import java.util.List;

public class TestQuantumAnnealingAlgorithm {

    private static final float COST_REDUCTION_FACTOR = 0.75f;
    private static final int TROTTER_REPLICAS = 50;     // P
    private static final float TEMPERATURE = 100f;         // T
    private static final int MONTE_CARLO_STEP = 100;   // M
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

        QuantumAnealing qa = new QuantumAnealing(
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
