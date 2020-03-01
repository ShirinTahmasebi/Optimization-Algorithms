package base_algorithms.quantum_annealing;

import base_algorithms.Cost;
import main.BaseAlgorithm;
import main.LineChartEx;
import main.Parameters;

public class QAAlgorithm implements BaseAlgorithm {

    private QAModelingInterface qaModelingInterface;
    private QAPlainOldData qaPlainOldData;
    private final LineChartEx lineChartEx;

    public QAAlgorithm(QAModelingInterface qaModelingInterface) {
        this.qaModelingInterface = qaModelingInterface;
        this.qaPlainOldData = qaModelingInterface.getData();
        this.lineChartEx = new LineChartEx();
    }

    public Cost execute() throws Exception {
        // Reset Dynamic Values
        qaModelingInterface.resetDynamicVariables();

        // Generate replicas (Fill replicasOfSinkXSpinVariables, replicasOfControllerXSpinVariables )
        qaModelingInterface.generateReplicasOfSolutions();
        qaModelingInterface.generateInitialSpinVariablesAndEnergy();

        int counter = 0;
        Cost minEnergyPair = new Cost()
                .setBudgetCostEnergy(Integer.MAX_VALUE)
                .setKineticEnergy(Integer.MAX_VALUE)
                .setLmaxCost(Integer.MAX_VALUE)
                .setSummationOfLMaxCost(Integer.MAX_VALUE)
                .setSynchronizationCost(Integer.MAX_VALUE)
                .setLoadBalancingCost(Integer.MAX_VALUE)
                .setReliabilityCost(Integer.MAX_VALUE);

        // Do while tunneling field is favorable
        do {
            // For each replica
            for (int ro = 0; ro < qaPlainOldData.trotterReplicas; ro++) {
                qaModelingInterface.getAReplica(ro);
                //  For each MonteCarlo step
                for (int step = 0; step < qaPlainOldData.monteCarloSteps; step++) {
                    counter++;
                    // Generate neighbor
                    qaModelingInterface.generateNeighbor();
                    // Calculate energy of temp solution
                    Cost cost = qaModelingInterface.calculateCost(ro);
                    double energy = qaModelingInterface.calculateEnergyFromCost(cost);
                    double prevEnergy = qaModelingInterface.calculateEnergyFromCost(qaPlainOldData.prevEnergyPair);
                    double minEnergy = qaModelingInterface.calculateEnergyFromCost(minEnergyPair);
                    if (energy < minEnergy) {
                        minEnergyPair = cost;
                    }
                    if (cost.getPotentialEnergy() < qaPlainOldData.prevEnergyPair.getPotentialEnergy() || energy < prevEnergy) {
                        // If energy has decreased: accept solution
                        qaPlainOldData.prevEnergyPair = cost;
                        qaModelingInterface.acceptSolution();
                    } else {
                        // Else with given probability decide to accept or not
                        double baseProb = Math.exp((prevEnergy - energy) / qaPlainOldData.temperature);
                        if (Parameters.Common.DO_PRINT_STEPS) {
                            System.out.println("BaseProp " + baseProb);
                        }
                        double rand = Math.random();
                        if (rand < baseProb) {
                            qaPlainOldData.prevEnergyPair = cost;
                            qaModelingInterface.acceptSolution();
                        }
                    }
                    lineChartEx.addToEnergySeries(
                            counter,
                            qaModelingInterface.calculateEnergyFromCost(qaPlainOldData.prevEnergyPair),
                            energy,
                            qaModelingInterface.calculateEnergyFromCost(minEnergyPair)
                    );
                } // End of for
            } // End of for
            // Update tunneling field
            qaPlainOldData.tunnelingField *= qaPlainOldData.tunnelingFiledEvaporation;
            qaPlainOldData.temperature *= qaPlainOldData.coolingRate;
        } while (qaPlainOldData.tunnelingField > qaPlainOldData.tunnelingFiledFinal); // End of do while

        if (Parameters.Common.DO_PRINT_INSTANCES) {
            // Final solution is in: sinkXSpinVariables and controllerXSpinVariables
            System.out.println("Counter: " + counter);
            System.out.println("Accepted Energy: " + qaModelingInterface.calculateEnergyFromCost(qaPlainOldData.prevEnergyPair));
            System.out.println("Accepted Potential Energy: " + qaPlainOldData.prevEnergyPair.getPotentialEnergy());
            System.out.println("Min Energy: " + qaModelingInterface.calculateEnergyFromCost(minEnergyPair));
            System.out.println("Final Temperature: " + qaPlainOldData.temperature);
            lineChartEx.drawChart();
        }

        qaModelingInterface.printGeneratedSolution();
        return qaPlainOldData.prevEnergyPair;
    }
}
