package base_algorithms.quantum_annealing;

import javafx.util.Pair;
import main.LineChartEx;
import main.Parameters;

public class QAAlgorithm {

    private QAModelingInterface qaModelingInterface;
    private QAPlainOldData qaPlainOldData;
    private final LineChartEx lineChartEx;

    public QAAlgorithm(QAModelingInterface qaModelingInterface) {
        this.qaModelingInterface = qaModelingInterface;
        this.qaPlainOldData = qaModelingInterface.getData();
        this.lineChartEx = new LineChartEx();
    }

    public Pair<Double, QAResultBaseInterface> execute() {
        // Reset Dynamic Values
        qaModelingInterface.resetDynamicVariables();

        // Generate replicas (Fill replicasOfSinkXSpinVariables, replicasOfControllerXSpinVariables )
        qaModelingInterface.generateReplicasOfSolutions();
        qaModelingInterface.generateInitialSpinVariablesAndEnergy();

        int counter = 0;
        Pair<Double, Double> minEnergyPair = new Pair<>(Double.MAX_VALUE, Double.MAX_VALUE);
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
                    Pair<Double, Double> energyPair = qaModelingInterface.calculateCost(ro);
                    double energy = qaModelingInterface.calculateEnergyFromPair(energyPair);
                    double prevEnergy = qaModelingInterface.calculateEnergyFromPair(qaPlainOldData.prevEnergyPair);
                    double minEnergy = qaModelingInterface.calculateEnergyFromPair(minEnergyPair);
                    if (energy < minEnergy) {
                        minEnergyPair = energyPair;
                    }
                    if (energyPair.getKey() < qaPlainOldData.prevEnergyPair.getKey() || energy < prevEnergy) {
                        // If energy has decreased: accept solution
                        qaPlainOldData.prevEnergyPair = energyPair;
                        qaModelingInterface.acceptSolution();
                    } else {
                        // Else with given probability decide to accept or not
                        double baseProb = Math.exp((prevEnergy - energy) / qaPlainOldData.temperature);
                        if (Parameters.Common.DO_PRINT_STEPS) {
                            System.out.println("BaseProp " + baseProb);
                        }
                        double rand = Math.random();
                        if (rand < baseProb) {
                            qaPlainOldData.prevEnergyPair = energyPair;
                            qaModelingInterface.acceptSolution();
                        }
                    }
                    lineChartEx.addToEnergySeries(
                            counter,
                            qaModelingInterface.calculateEnergyFromPair(qaPlainOldData.prevEnergyPair),
                            energy,
                            qaModelingInterface.calculateEnergyFromPair(minEnergyPair),
                            4
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
            System.out.println("Accepted Energy: " + qaModelingInterface.calculateEnergyFromPair(qaPlainOldData.prevEnergyPair));
            System.out.println("Accepted Potential Energy: " + qaPlainOldData.prevEnergyPair.getKey());
            System.out.println("Min Energy: " + qaModelingInterface.calculateEnergyFromPair(minEnergyPair));
            System.out.println("Final Temperature: " + qaPlainOldData.temperature);
            lineChartEx.drawChart();
        }

        qaModelingInterface.printGeneratedSolution();
        return new Pair(qaPlainOldData.prevEnergyPair.getKey(), qaModelingInterface.getResult());
    }
}
