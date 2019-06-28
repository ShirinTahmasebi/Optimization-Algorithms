package algorithms_modeling.simulated_annealing;

import main.LineChartEx;

public class SAAlgorithm {
    private SAModelingInterface saModelingInterface;
    private SAPlainOldData saPlainOldData;
    private final LineChartEx lineChartEx;

    public SAAlgorithm(SAModelingInterface saModelingInterface) {
        this.saModelingInterface = saModelingInterface;
        this.saPlainOldData = saModelingInterface.getData();
        this.lineChartEx = new LineChartEx();
    }

    public double execute() {
        // Reset Dynamic Variables
        saModelingInterface.resetDynamicVariables();

        // Generate Initial Solution
        saModelingInterface.generateInitialSpinVariablesAndEnergy();

        int counter = 0;
        double minEnergy = Double.MAX_VALUE;

        // -- Do while temperature is favorable
        do {
            // ---- For each MonteCarlo step
            for (int step = 0; step < saPlainOldData.monteCarloSteps; step++) {
                counter++;
                // Generate neighbor
                saModelingInterface.generateNeighbor();
                // ------ Calculate potential energy of temp solution
                double energy = saModelingInterface.calculateCost();
                if (energy < minEnergy) {
                    minEnergy = energy;
                }
                if (energy < saPlainOldData.prevEnergy) {
                    // If energy has decreased: accept solution
                    saPlainOldData.prevEnergy = energy;
                    saModelingInterface.acceptSolution();
                } else {
                    // Else with given probability decide to accept or not
                    double baseProb = Math.exp((saPlainOldData.prevEnergy - energy) / saPlainOldData.temperature);
                    if (main.Main.DO_PRINT_STEPS) {
                        System.out.println("BaseProp " + baseProb);
                    }
                    double rand = Math.random();
                    if (rand < baseProb) {
                        saPlainOldData.prevEnergy = energy;
                        saModelingInterface.acceptSolution();
                    }
                }
                lineChartEx.addToEnergySeries(
                        counter,
                        saPlainOldData.prevEnergy,
                        energy,
                        minEnergy,
                        4
                );
            } // End of for
            // Update temperature
            saPlainOldData.temperature *= saPlainOldData.temperatureCoolingRate;
        } while (saPlainOldData.temperature > saPlainOldData.temperatureFinal); // -- End of do while

        if (main.Main.DO_PRINT_INSTANCES) {
            // Final solution is in: sinkXSpinVariables and controllerXSpinVariables
            System.out.println("Counter: " + counter);
            System.out.println("Accepted Energy: " + saPlainOldData.prevEnergy);
            System.out.println("Min Energy: " + minEnergy);
            System.out.println("Final Temperature: " + saPlainOldData.temperature);
            lineChartEx.drawChart();
        }

        saModelingInterface.printGeneratedSolution();
        return saPlainOldData.prevEnergy;
    }
}
