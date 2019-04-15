package simulated.anealing;

import main.model.Vertex;
import main.model.Graph;
import java.util.List;
import java.util.Random;
import main.Utils;
import main.LineChartEx;

public class SimulatedAnealing {

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
    private float temperature;                    // T0
    private final float temperatureFinal;         // T1
    private final float temperatureCoolingRate;
    private final int monteCarloSteps;
    private double prevEnergy;

    private final LineChartEx lineChartEx;

    public SimulatedAnealing(
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
            float temperatureInitial,
            float temperatureFinal,
            float temperatureCoolingRate,
            int monteCarloSteps
    ) {
        this.controllerYSpinVariables = new boolean[graph.getVertexes().size()][candidateControllers.size()];
        this.sinkYSpinVariables = new boolean[graph.getVertexes().size()][candidateSinks.size()];
        this.tempControllerXSpinVariables = new boolean[candidateControllers.size()];
        this.tempSinkXSpinVariables = new boolean[candidateSinks.size()];
        this.sinkXSpinVariables = new boolean[candidateSinks.size()];
        this.controllerXSpinVariables = new boolean[candidateControllers.size()];

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
        this.temperature = temperatureInitial;
        this.temperatureFinal = temperatureFinal;
        this.temperatureCoolingRate = temperatureCoolingRate;
        this.monteCarloSteps = monteCarloSteps;

        lineChartEx = new LineChartEx();

        initializeSpinVariables();

        if (main.Main.DO_PRINT_STEPS) {
            Utils.printProblemSpecifications(graph, candidateSinks, sinkYSpinVariables, candidateControllers, controllerYSpinVariables);
        }
    }

    void execute() {
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
                double energy = calculateEnergy();
                if (energy < minEnergy) {
                    minEnergy = energy;
                }
                if (energy < prevEnergy) {
                    // If energy has decreased: accept solution
                    prevEnergy = energy;
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
                        prevEnergy = energy;
                        sinkXSpinVariables = tempSinkXSpinVariables.clone();
                        controllerXSpinVariables = tempControllerXSpinVariables.clone();
                    }
                }
                lineChartEx.addToSelectedEnergy(
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

        // Final solution is in: sinkXSpinVariables and controllerXSpinVariables
        System.out.println("Counter: " + counter);
        System.out.println("Accepted Energy: " + prevEnergy);
        System.out.println("Min Energy: " + minEnergy);
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
        prevEnergy = calculateEnergy();
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

    private double calculateEnergy() {
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

        return potentialEnergy;
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
}
