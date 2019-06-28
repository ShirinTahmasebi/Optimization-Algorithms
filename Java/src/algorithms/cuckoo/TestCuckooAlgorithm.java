package algorithms.cuckoo;

import main.model.Graph;
import main.model.Vertex;

import java.util.List;

public class TestCuckooAlgorithm {

    CuckooAlgorithm cuckoo;

    private static final float COST_REDUCTION_FACTOR = 0.75f;

    public TestCuckooAlgorithm(
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
            int costController) {
        cuckoo = new CuckooAlgorithm(
                graph,
                candidateSinks,
                candidateControllers,
                sinkYSpinVariables,
                controllerYSpinVariables,
                sensorSinkMaxDistance,
                sensorControllerMaxDistance,
                maxSinkCoverage,
                maxControllerCoverage,
                maxSinkLoad,
                maxControllerLoad,
                costSink,
                costController,
                COST_REDUCTION_FACTOR
        );
    }

    public double execute() {
        return cuckoo.execute();
    }
}
