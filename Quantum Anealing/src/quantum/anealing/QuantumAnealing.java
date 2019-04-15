package quantum.anealing;

import main.LineChartEx;
import main.model.Vertex;
import main.model.Graph;
import java.util.List;
import java.util.Random;
import javafx.util.Pair;
import main.Utils;

public class QuantumAnealing {

    // Problem Specifications
    private final Graph graph;

    private final List<Vertex> candidateSinks;            // AS
    private final List<Vertex> candidateControllers;      //AC

    private final int sensorSinkMaxDistance;              // Lmax
    private final int sensorControllerMaxDistance;        // LPrimeMax

    private final boolean[][] sinkYSpinVariables;           // SY (Y Spin Variable)
    private final boolean[][] controllerYSpinVariables;     // SYPrime (Y Spin Variable)

    // Solution Spin Variables
    private boolean[] sinkXSpinVariables;             // SX (X Spin Variable)
    private boolean[] controllerXSpinVariables;       // SXPrime (X Spin Variable)
    private final boolean[][] replicasOfSinkXSpinVariables;
    private final boolean[][] replicasOfControllerXSpinVariables;

    // Temp Spin Variables
    private boolean[] tempSinkXSpinVariables;           // SX (X Spin Variable)           
    private boolean[] tempControllerXSpinVariables;     // SXPrime (X Spin Variable)

    private final int maxSinkCoverage;          // K
    private final int maxControllerCoverage;    // KPrime
    private final int maxSinkLoad;          // W
    private final int maxControllerLoad;    // WPrime
    private final int costSink;
    private final int costController;
    private final float costReductionFactor;
    private final int trotterReplicas;   // P
    private final float temperatureQuantum;    // TQ
    private float temperature;           // T
    private final int monteCarloSteps;
    private float tunnlingField;
    private final float tunnlingFiledFinal;
    private final float tunnlingFiledEvaporation;
    private final float coolingRate = .7f;

    private Pair<Double, Double> prevEnergyPair;

    private final LineChartEx lineChartEx;

    public QuantumAnealing(
            Graph graph,
            List candidateSinks,
            List candidateControllers,
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
        this.controllerYSpinVariables = new boolean[graph.getVertexes().size()][candidateControllers.size()];
        this.sinkYSpinVariables = new boolean[graph.getVertexes().size()][candidateSinks.size()];
        this.tempControllerXSpinVariables = new boolean[candidateControllers.size()];
        this.tempSinkXSpinVariables = new boolean[candidateSinks.size()];
        this.sinkXSpinVariables = new boolean[candidateSinks.size()];
        this.controllerXSpinVariables = new boolean[candidateControllers.size()];
        this.replicasOfSinkXSpinVariables = new boolean[trotterReplicas][candidateSinks.size()];
        this.replicasOfControllerXSpinVariables = new boolean[trotterReplicas][candidateControllers.size()];

        this.graph = graph;
        this.candidateSinks = candidateSinks;
        this.candidateControllers = candidateControllers;
        this.sensorSinkMaxDistance = sensorSinkMaxDistance;
        this.sensorControllerMaxDistance = sensorControllerMaxDistance;

        this.maxSinkCoverage = maxSinkCovrage;
        this.maxControllerCoverage = maxControllerCoverage;
        this.maxSinkLoad = maxSinkLoad;
        this.maxControllerLoad = maxControllerLoad;
        this.costSink = costSink;
        this.costController = costController;
        this.costReductionFactor = costReductionFactor;
        this.trotterReplicas = trotterReplicas;
        this.temperatureQuantum = temperature;
        this.temperature = temperature;
        this.monteCarloSteps = monteCarloSteps;
        this.tunnlingField = tunnlingFieldInitial;
        this.tunnlingFiledFinal = tunnlingFieldFinal;
        this.tunnlingFiledEvaporation = tunnlingFieldEvaporation;

        lineChartEx = new LineChartEx();
        initializeSpinVariables();

        if (main.Main.DO_PRINT_STEPS) {
            Utils.printProblemSpecifications(graph, candidateSinks, sinkYSpinVariables, candidateControllers, controllerYSpinVariables);
        }
    }

    void execute() {
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
                    lineChartEx.addToSelectedEnergy(
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

        // Final solution is in: sinkXSpinVariables and controllerXSpinVariables
        System.out.println("Counter: " + counter);
        System.out.println("Accepted Energy: " + calculateEnergyFromPair(prevEnergyPair));
        System.out.println("Accepted Potential Energy: " + prevEnergyPair.getKey());
        System.out.println("Min Energy: " + calculateEnergyFromPair(minEnergyPair));
        System.out.println("Final Temperature: " + temperature);
        lineChartEx.drawChart();
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
        Pair<Double, Double> energyPair = calculateEnergy(-1);
        prevEnergyPair = energyPair;
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
        double energy;
        energy = kineticEnergy + potentialEnergy;

        return new Pair<>(potentialEnergy, kineticEnergy);
    }

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

    private void initializeSpinVariables() {
        // --- Initialize Y and YPrime Spin Variables
        for (int i = 0; i < graph.getVertexes().size(); i++) {
            for (int j = 0; j < candidateSinks.size(); j++) {
                sinkYSpinVariables[i][j] = false;
            }
        }

        for (int i = 0; i < graph.getVertexes().size(); i++) {
            for (int j = 0; j < candidateControllers.size(); j++) {
                controllerYSpinVariables[i][j] = false;
            }
        }

        for (int i = 0; i < graph.getVertexes().size(); i++) {
            for (int j = 0; j < candidateSinks.size(); j++) {
                // The following line can be replaced with vertexIndex = i - but I prefered to write this in the following way for more readability
                int vertexIndex1 = graph.getVertexIndexById(graph.getVertexes().get(i).getId());
                int vertexIndex2 = graph.getVertexIndexById(((Vertex) candidateSinks.get(j)).getId());
                sinkYSpinVariables[i][j] = Utils.isDistanceFavorable(graph, vertexIndex1, vertexIndex2, sensorSinkMaxDistance);
            }
        }

        for (int i = 0; i < graph.getVertexes().size(); i++) {
            for (int j = 0; j < candidateControllers.size(); j++) {
                // The following line can be replaced with vertexIndex = i - but I prefered to write this in the following way for more readability
                int vertexIndex1 = graph.getVertexIndexById(graph.getVertexes().get(i).getId());
                int vertexIndex2 = graph.getVertexIndexById(((Vertex) candidateControllers.get(j)).getId());
                controllerYSpinVariables[i][j] = Utils.isDistanceFavorable(graph, vertexIndex1, vertexIndex2, sensorControllerMaxDistance);
            }
        }
        // ---
    }

    private void generateReplicasOfSolutions() {
        for (int i = 0; i < trotterReplicas; i++) {
            // --- Select random configuration for replicas
            for (int j = 0; j < candidateSinks.size(); j++) {
                double probabilityOfOne = Math.random();
                replicasOfSinkXSpinVariables[i][j] = probabilityOfOne < .5;
            }
            for (int j = 0; j < candidateSinks.size(); j++) {
                double probabilityOfOne = Math.random();
                replicasOfControllerXSpinVariables[i][j] = probabilityOfOne < .5;
            }
        }
    }

    private double calculateEnergyFromPair(Pair<Double, Double> energyPair) {
        return energyPair.getKey() + energyPair.getValue();
    }
}
