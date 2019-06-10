package cuckoo;

import main.BaseAlgorithm;
import main.Utils;
import main.model.Graph;
import main.model.Vertex;

import java.util.ArrayList;
import java.util.List;

public class CuckooAlgorithm extends BaseAlgorithm {

    private final int POPULATION = 30;      // Npop
    private int VARIABLES_COUNT;            // Nvar
    public static final int maxEggNumber = 20;
    public static final int minEggNumber = 5;

    private List<Cuckoo> matureCuckoos = new ArrayList<>();
    private List<Cuckoo> eggs = new ArrayList<>();

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

        VARIABLES_COUNT = candidateSinks.size() + candidateControllers.size();

        if (main.Main.DO_PRINT_STEPS) {
            Utils.printProblemSpecifications(graph, candidateSinks, sinkYSpinVariables, candidateControllers, controllerYSpinVariables);
        }
    }

    public double execute() {
        for (int i = 0; i < POPULATION; i++) {
            matureCuckoos.add(generateInitialRandomCuckoos());
        }

        for (Cuckoo matureCuckoo : matureCuckoos) {
            eggs.addAll(matureCuckoo.generateEggs());
        }
        return 1;
    }

    private Cuckoo generateInitialRandomCuckoos() {
        boolean[] controllersSpinVariables = new boolean[candidateControllers.size()];
        for (int i = 0; i < candidateControllers.size(); i++) {
            double probability = Math.random();
            controllersSpinVariables[i] = (probability < .5);
        }
        boolean[] sinkSpinVariables = new boolean[candidateSinks.size()];
        for (int i = 0; i < candidateSinks.size(); i++) {
            double probability = Math.random();
            sinkSpinVariables[i] = (probability < .5);
        }
        return new Cuckoo(true, sinkSpinVariables, controllersSpinVariables);
    }
}
