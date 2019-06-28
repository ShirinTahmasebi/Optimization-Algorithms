package algorithms.simulated_annealing;

import main.BaseAlgorithm;
import main.model.Graph;

import java.util.List;
import java.util.Random;

import main.Utils;
import main.model.Vertex;

public class SimulatedAnnealing extends BaseAlgorithm {

    private float temperature;                    // T0
    private final float temperatureInitial;         // T1
    private final float temperatureFinal;         // T1
    private final float temperatureCoolingRate;
    private final int monteCarloSteps;
    private double prevEnergy;

    public SimulatedAnnealing(
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
            float costReductionFactor,
            float temperatureInitial,
            float temperatureFinal,
            float temperatureCoolingRate,
            int monteCarloSteps
    ) {
        super(
                graph,
                candidateSinks,
                candidateControllers,
                sinkYSpinVariables,
                controllerYSpinVariables,
                sensorSinkMaxDistance,
                sensorControllerMaxDistance,
                maxSinkCovrage,
                maxControllerCoverage,
                maxSinkLoad,
                maxControllerLoad,
                costSink,
                costController,
                costReductionFactor);

        this.temperature = temperatureInitial;
        this.temperatureInitial = temperatureInitial;
        this.temperatureFinal = temperatureFinal;
        this.temperatureCoolingRate = temperatureCoolingRate;
        this.monteCarloSteps = monteCarloSteps;

        if (main.Main.DO_PRINT_STEPS) {
            Utils.printProblemSpecifications(graph, candidateSinks, sinkYSpinVariables, candidateControllers, controllerYSpinVariables);
        }
    }

    double execute() {
        // Reset Dynamic Variables
        resetDynamicVariables();

        // Generate Initial Solution
        generateInitialSpinVariablesAndEnergy();

        int counter = 0;
        double minEnergy = Double.MAX_VALUE;

        // -- Do while temperature is favorable
        do {
            // ---- For each montecarlo step
            for (int step = 0; step < monteCarloSteps; step++) {
                counter++;
                // Generate neighbor
                generateNeighbour();
                // ------ Calculate potential energy of temp solution
                double energy = calculateCost();
                if (energy < minEnergy) {
                    minEnergy = energy;
                }
                if (energy < prevEnergy) {
                    // If energy has decreased: accept solution
                    prevEnergy = energy;
                    acceptSolution();
                } else {
                    // Else with given probability decide to accept or not   
                    double baseProb = Math.exp((prevEnergy - energy) / temperature);
                    if (main.Main.DO_PRINT_STEPS) {
                        System.out.println("BaseProp " + baseProb);
                    }
                    double rand = Math.random();
                    if (rand < baseProb) {
                        prevEnergy = energy;
                        acceptSolution();
                    }
                }
                lineChartEx.addToEnergySeries(
                        counter,
                        prevEnergy,
                        energy,
                        minEnergy,
                        4
                );
            } // End of for
            // Update temperature
            temperature *= temperatureCoolingRate;
        } while (temperature > temperatureFinal); // -- End of do while 

        if (main.Main.DO_PRINT_INSTANCES) {
            // Final solution is in: sinkXSpinVariables and controllerXSpinVariables
            System.out.println("Counter: " + counter);
            System.out.println("Accepted Energy: " + prevEnergy);
            System.out.println("Min Energy: " + minEnergy);
            System.out.println("Final Temperature: " + temperature);
            lineChartEx.drawChart();
        }

        Utils.printGeneratedSolution(tempSinkXSpinVariables, tempControllerXSpinVariables);
        return prevEnergy;
    }

    private void acceptSolution() {
        sinkXSpinVariables = tempSinkXSpinVariables.clone();
        controllerXSpinVariables = tempControllerXSpinVariables.clone();
    }

    private void resetDynamicVariables() {
        temperature = temperatureInitial;
        prevEnergy = 0;
        this.tempControllerXSpinVariables = new boolean[candidateControllers.size()];
        this.tempSinkXSpinVariables = new boolean[candidateSinks.size()];
        this.sinkXSpinVariables = new boolean[candidateSinks.size()];
        this.controllerXSpinVariables = new boolean[candidateControllers.size()];
    }

    private void generateInitialSpinVariablesAndEnergy() {
        // --- Initialize temp lists to false
        for (int i = 0; i < candidateControllers.size(); i++) {
            controllerXSpinVariables[i] = false;
        }

        for (int i = 0; i < candidateSinks.size(); i++) {
            sinkXSpinVariables[i] = false;
        }

        tempControllerXSpinVariables = controllerXSpinVariables.clone();
        tempSinkXSpinVariables = sinkXSpinVariables.clone();
        prevEnergy = calculateCost();
    }

    private void generateNeighbour() {
        Random random = new Random();
        int randInt = random.nextInt(tempSinkXSpinVariables.length + tempControllerXSpinVariables.length);

        if (randInt < tempSinkXSpinVariables.length) {
            // Change randInt-th item in sink array
            boolean prevValue = tempSinkXSpinVariables[randInt];
            tempSinkXSpinVariables[randInt] = !prevValue;
        } else {
            // Change index-th item in controller array
            int index = randInt - (tempSinkXSpinVariables.length - 1) - 1;
            boolean prevValue = tempControllerXSpinVariables[index];
            tempControllerXSpinVariables[index] = !prevValue;
        }
        if (main.Main.DO_PRINT_STEPS) {
            Utils.printGeneratedSolution(tempSinkXSpinVariables, tempControllerXSpinVariables);
        }
    }

    public double calculateCost() {
        int reliabilityEnergy = Utils.getReliabilityEnergy(
                graph,
                sinkYSpinVariables, controllerYSpinVariables,
                candidateSinks, tempSinkXSpinVariables,
                candidateControllers, tempControllerXSpinVariables,
                maxSinkCoverage, maxControllerCoverage
        );

        double loadBalancingEnergy = Utils.getLoadBalancingEnergy(
                graph,
                sinkYSpinVariables, controllerYSpinVariables,
                candidateSinks, tempSinkXSpinVariables,
                candidateControllers, tempControllerXSpinVariables,
                maxSinkLoad, maxSinkCoverage,
                maxControllerLoad, maxControllerCoverage
        );

        double costEnergy = Utils.getCostEnergy(
                candidateSinks, tempSinkXSpinVariables,
                candidateControllers, tempControllerXSpinVariables,
                costSink, costController, costReductionFactor
        );

        return reliabilityEnergy + loadBalancingEnergy + costEnergy;
    }
}
