package quantum.anealing;

import javafx.util.Pair;
import main.BaseAlgorithm;
import main.Utils;
import main.model.Graph;
import main.model.Vertex;

import java.util.List;
import java.util.Random;

public class QuantumAnnealing extends BaseAlgorithm {

    private final int trotterReplicas;   // P
    private final float temperatureQuantum;    // TQ
    private float temperature;                        // T
    private final float temperatureInitial;           // T
    private final int monteCarloSteps;
    private float tunnlingField;
    private final float tunnlingFieldInitial;
    private final float tunnlingFiledFinal;
    private final float tunnlingFiledEvaporation;
    private final float coolingRate = .7f;

    private Pair<Double, Double> prevEnergyPair;

    public QuantumAnnealing(
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
            int trotterReplicas,
            float temperature,
            int monteCarloSteps,
            float tunnlingFieldInitial,
            float tunnlingFieldFinal,
            float tunnlingFieldEvaporation
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

        this.replicasOfSinkXSpinVariables = new boolean[trotterReplicas][candidateSinks.size()];
        this.replicasOfControllerXSpinVariables = new boolean[trotterReplicas][candidateControllers.size()];
        this.trotterReplicas = trotterReplicas;
        this.temperatureQuantum = temperature;
        this.temperature = temperature;
        this.temperatureInitial = temperature;
        this.monteCarloSteps = monteCarloSteps;
        this.tunnlingField = tunnlingFieldInitial;
        this.tunnlingFieldInitial = tunnlingFieldInitial;
        this.tunnlingFiledFinal = tunnlingFieldFinal;
        this.tunnlingFiledEvaporation = tunnlingFieldEvaporation;

        if (main.Main.DO_PRINT_STEPS) {
            Utils.printProblemSpecifications(graph, candidateSinks, sinkYSpinVariables, candidateControllers, controllerYSpinVariables);
        }
    }

    double execute() {
        // Reset Dynamic Values
        temperature = temperatureInitial;
        tunnlingField = tunnlingFieldInitial;
        this.tempControllerXSpinVariables = new boolean[candidateControllers.size()];
        this.tempSinkXSpinVariables = new boolean[candidateSinks.size()];
        this.sinkXSpinVariables = new boolean[candidateSinks.size()];
        this.controllerXSpinVariables = new boolean[candidateControllers.size()];
        this.replicasOfSinkXSpinVariables = new boolean[trotterReplicas][candidateSinks.size()];
        this.replicasOfControllerXSpinVariables = new boolean[trotterReplicas][candidateControllers.size()];

        // Genreate replicas (Fill replicasOfSinkXSpinVariables, replicasOfControllerXSpinVariables )
        generateReplicasOfSolutions();
        generateInitialSpinVariablesAndEnergy();

        int counter = 0;
        Pair<Double, Double> minEnergyPair = new Pair<>(Double.MAX_VALUE, Double.MAX_VALUE);
        // Do while tunnlig field is favorable
        do {
            // For each replica
            for (int ro = 0; ro < trotterReplicas; ro++) {
                tempSinkXSpinVariables = replicasOfSinkXSpinVariables[ro].clone();
                tempControllerXSpinVariables = replicasOfControllerXSpinVariables[ro].clone();
                //  For each montecarlo step
                for (int step = 0; step < monteCarloSteps; step++) {
                    counter++;
                    // Generate neighbor
                    generateNeighbour();
                    // Calculate energy of temp solution
                    Pair<Double, Double> energyPair = calculateEnergy(ro);
                    double energy = calculateEnergyFromPair(energyPair);
                    double prevEnergy = calculateEnergyFromPair(prevEnergyPair);
                    double minEnergy = calculateEnergyFromPair(minEnergyPair);
                    if (energy < minEnergy) {
                        minEnergyPair = energyPair;
                    }
                    if (energyPair.getKey() < prevEnergyPair.getKey() || energy < prevEnergy) {
                        // If energy has decreased: accept solution
                        prevEnergyPair = energyPair;
                        sinkXSpinVariables = tempSinkXSpinVariables.clone();
                        controllerXSpinVariables = tempControllerXSpinVariables.clone();
                    } else {
                        // Else with given probability decide to accept or not   
                        double baseProb = Math.exp((prevEnergy - energy) / temperature);
                        if (main.Main.DO_PRINT_STEPS) {
                            System.out.println("BaseProp " + baseProb);
                        }
                        double rand = Math.random();
                        if (rand < baseProb) {
                            prevEnergyPair = energyPair;
                            sinkXSpinVariables = tempSinkXSpinVariables.clone();
                            controllerXSpinVariables = tempControllerXSpinVariables.clone();
                        }
                    }
                    lineChartEx.addToEnergySeries(
                            counter,
                            calculateEnergyFromPair(prevEnergyPair),
                            energy,
                            calculateEnergyFromPair(minEnergyPair),
                            4
                    );
                } // End of for
            } // End of for
            // Update tunnling field
            tunnlingField *= tunnlingFiledEvaporation;
            temperature *= coolingRate;
        } while (tunnlingField > tunnlingFiledFinal); // End of do while 

        if (main.Main.DO_PRINT_INSTANCES) {
            // Final solution is in: sinkXSpinVariables and controllerXSpinVariables
            System.out.println("Counter: " + counter);
            System.out.println("Accepted Energy: " + calculateEnergyFromPair(prevEnergyPair));
            System.out.println("Accepted Potential Energy: " + prevEnergyPair.getKey());
            System.out.println("Min Energy: " + calculateEnergyFromPair(minEnergyPair));
            System.out.println("Final Temperature: " + temperature);
            lineChartEx.drawChart();
        }

        Utils.printGeneratedSolution(tempSinkXSpinVariables, tempControllerXSpinVariables);
        return prevEnergyPair.getKey();
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
        prevEnergyPair = calculateEnergy(-1);
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

    private Pair<Double, Double> calculateEnergy(int currentReplicaNum) {
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

        double potentialEnergy = reliabilityEnergy + loadBalancingEnergy + costEnergy;
        double kineticEnergy = getKineticEnergy(currentReplicaNum);

        return new Pair<>(potentialEnergy, kineticEnergy);
    }

    @SuppressWarnings("unused")
    private double calculatePotentialEnergy(int currentReplicaNum) {
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

    private double getKineticEnergy(int currentReplicaNum) {
        if (currentReplicaNum + 1 >= trotterReplicas || currentReplicaNum < 0) {
            return 0;
        }

        // Calculate coupling among replicas
        float halfTemperatureQuantum = temperatureQuantum / 2;
        float angle = tunnlingField / (trotterReplicas * temperatureQuantum);

        double coupling = -halfTemperatureQuantum * Math.log(Math.tanh(angle));

        int sinkReplicaCoupling = 0;
        int controllerReplicaCoupling = 0;

        for (int i = 0; i < candidateSinks.size(); i++) {
            boolean areSpinVariablesTheSame = (replicasOfSinkXSpinVariables[currentReplicaNum][i] && replicasOfSinkXSpinVariables[currentReplicaNum + 1][i]);
            sinkReplicaCoupling = areSpinVariablesTheSame ? 1 : -1;
        }

        for (int i = 0; i < candidateControllers.size(); i++) {
            boolean areSpinVariablesTheSame
                    = (replicasOfControllerXSpinVariables[currentReplicaNum][i]
                    && replicasOfControllerXSpinVariables[currentReplicaNum + 1][i]);
            controllerReplicaCoupling = areSpinVariablesTheSame ? 1 : -1;
        }

        // Multiply sum of two final results with coupling
        return coupling * (sinkReplicaCoupling + controllerReplicaCoupling);
    }

    private void generateReplicasOfSolutions() {
        for (int i = 0; i < trotterReplicas; i++) {
            // --- Select random configuration for replicas
            for (int j = 0; j < candidateSinks.size(); j++) {
                double probabilityOfOne = Math.random();
                replicasOfSinkXSpinVariables[i][j] = probabilityOfOne < .5;
            }
            for (int j = 0; j < candidateControllers.size(); j++) {
                double probabilityOfOne = Math.random();
                replicasOfControllerXSpinVariables[i][j] = probabilityOfOne < .5;
            }
        }
    }

    private double calculateEnergyFromPair(Pair<Double, Double> energyPair) {
        return energyPair.getKey() + energyPair.getValue();
    }
}
