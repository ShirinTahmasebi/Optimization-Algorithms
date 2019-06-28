package algorithms.simulated_annealing;

import main.Parameters;
import main.model.Vertex;
import main.model.Graph;

import java.util.List;

public class TestSimulatedAnnealingAlgorithm {

    SimulatedAnnealing sa;

    public TestSimulatedAnnealingAlgorithm(
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
        sa = new SimulatedAnnealing(
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
                Parameters.Common.COST_REDUCTION_FACTOR,
                main.Parameters.SimulatedAnnealing.TEMPERATURE_INITIAL,
                main.Parameters.SimulatedAnnealing.TEMPERATURE_FINAL,
                main.Parameters.SimulatedAnnealing.TEMPERATURE_COOLING_RATE,
                main.Parameters.SimulatedAnnealing.MONTE_CARLO_STEP
        );
    }

    public double execute() {
        return sa.execute();
    }
}
