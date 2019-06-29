package algorithms.quantum_annealing;

import javafx.util.Pair;

public interface QAModelingInterface {

    void resetDynamicVariables();

    void generateReplicasOfSolutions();

    void generateInitialSpinVariablesAndEnergy();

    void getAReplica(int replicaNumber);

    void generateNeighbor();

    double getKineticEnergy(int currentReplicaNum);

    Pair<Double, Double> calculateCost(int currentReplicaNum);

    double calculateEnergyFromPair(Pair<Double, Double> energyPair);

    void acceptSolution();

    void printGeneratedSolution();

    QAPlainOldData getData();
}
