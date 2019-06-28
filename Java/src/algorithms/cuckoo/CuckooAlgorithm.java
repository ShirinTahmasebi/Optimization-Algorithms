package algorithms.cuckoo;

import algorithms.cuckoo.model.Cuckoo;
import main.BaseAlgorithm;
import main.Parameters;
import main.Utils;
import main.model.Graph;
import main.model.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CuckooAlgorithm extends BaseAlgorithm {

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

        if (main.Main.DO_PRINT_STEPS) {
            Utils.printProblemSpecifications(graph, candidateSinks, sinkYSpinVariables, candidateControllers, controllerYSpinVariables);
        }
    }


    public double calculateCost(boolean[] sinkXSpinVariables, boolean[] controllerXSpinVariables) {
        int reliabilityEnergy = Utils.getReliabilityEnergy(
                graph,
                sinkYSpinVariables, controllerYSpinVariables,
                candidateSinks, sinkXSpinVariables,
                candidateControllers, controllerXSpinVariables,
                maxSinkCoverage, maxControllerCoverage
        );

        double loadBalancingEnergy = Utils.getLoadBalancingEnergy(
                graph,
                sinkYSpinVariables, controllerYSpinVariables,
                candidateSinks, sinkXSpinVariables,
                candidateControllers, controllerXSpinVariables,
                maxSinkLoad, maxSinkCoverage,
                maxControllerLoad, maxControllerCoverage
        );

        double costEnergy = Utils.getCostEnergy(
                candidateSinks, sinkXSpinVariables,
                candidateControllers, controllerXSpinVariables,
                costSink, costController, costReductionFactor
        );

        return reliabilityEnergy + loadBalancingEnergy + costEnergy;
    }

    public double execute() {
        matureCuckoos.clear();
        for (int step = 0; step < Parameters.Cuckoo.MONTE_CARLO_STEP; step++) {
            for (int i = 0; i < main.Parameters.Cuckoo.POPULATION; i++) {
                matureCuckoos.add(generateInitialRandomCuckoos());
            }

            for (int i = 0; i < 10; i++) {
                for (Cuckoo matureCuckoo : matureCuckoos) {
                    try {
                        eggs.addAll(matureCuckoo.generateEggs());
                    } catch (Exception ignored) {

                    }
                }
                for (Cuckoo cuckoo : eggs) {
                    cuckoo.setCost(calculateCost(cuckoo.sinkXSpinVariables, cuckoo.controllerXSpinVariables));
                }
                Collections.sort(eggs, new CuckooComparator());

                int highSubListBound = eggs.size() - (int) (eggs.size() * main.Parameters.Cuckoo.EGG_KILLING_RATE);
                List<Cuckoo> cuckooList = eggs.subList(0, highSubListBound);

                matureCuckoos.addAll(cuckooList);
                eggs.clear();

                if (matureCuckoos.size() > main.Parameters.Cuckoo.MAX_CUCKOO_NUMBERS) {
                    Collections.sort(matureCuckoos, new CuckooComparator());
                    matureCuckoos = matureCuckoos.subList(0, main.Parameters.Cuckoo.MAX_CUCKOO_NUMBERS);
                }

            }
        }

        return matureCuckoos.get(0).getCost();
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
        Cuckoo cuckoo = new Cuckoo(true, sinkSpinVariables, controllersSpinVariables);
        cuckoo.setCost(calculateCost(sinkSpinVariables, controllersSpinVariables));
        return cuckoo;
    }
}
