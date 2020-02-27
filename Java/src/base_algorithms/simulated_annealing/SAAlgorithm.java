package base_algorithms.simulated_annealing;

import base_algorithms.Cost;
import main.LineChartEx;
import main.Parameters;

public class SAAlgorithm {
    private SAModelingInterface saModelingInterface;
    private SAPlainOldData saPlainOldData;
    private final LineChartEx lineChartEx;

    public SAAlgorithm(SAModelingInterface saModelingInterface) {
        this.saModelingInterface = saModelingInterface;
        this.saPlainOldData = saModelingInterface.getData();
        this.lineChartEx = new LineChartEx();
    }

    public Cost execute() throws Exception {
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
                Cost energy = saModelingInterface.calculateCost();
                if (energy.getPotentialEnergy() < minEnergy) {
                    minEnergy = energy.getPotentialEnergy();
                }
                if (energy.getPotentialEnergy() < saPlainOldData.prevEnergy.getPotentialEnergy()) {
                    // If energy has decreased: accept solution
                    saPlainOldData.prevEnergy = energy;
                    saModelingInterface.acceptSolution();
                } else {
                    // Else with given probability decide to accept or not
                    double baseProb = Math.exp((- saPlainOldData.prevEnergy.getPotentialEnergy() + energy.getPotentialEnergy()) / saPlainOldData.temperature);
                    if (Parameters.Common.DO_PRINT_STEPS) {
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
                        saPlainOldData.prevEnergy.getPotentialEnergy(),
                        energy.getPotentialEnergy(),
                        minEnergy,
                        4
                );
            } // End of for
            // Update temperature
            saPlainOldData.temperature *= saPlainOldData.temperatureCoolingRate;
        } while (saPlainOldData.temperature > saPlainOldData.temperatureFinal); // -- End of do while

        if (Parameters.Common.DO_PRINT_INSTANCES) {
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
