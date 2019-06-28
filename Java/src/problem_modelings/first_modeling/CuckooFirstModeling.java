package problem_modelings.first_modeling;

import main.model.Graph;
import main.model.Vertex;
import algorithms_modeling.Cuckoo.CuckooModelingInterface;
import problem_modelings.modeling_types.FirstModelAbstract;

import java.util.List;

public class CuckooFirstModeling extends FirstModelAbstract implements CuckooModelingInterface {

    public CuckooFirstModeling(
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
        super(
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
                costReductionFactor
        );
    }
}
