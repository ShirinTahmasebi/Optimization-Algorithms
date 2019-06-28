package problem_modelings.algorithms_modeling.QA;

import javafx.util.Pair;

public interface QAModelingInterface {

    void initializeVariables();

    void resetDynamicVariables();

    void generateReplicasOfSolutions();

    void generateInitialSpinVariablesAndEnergy();

    void getAReplica(int replicaNumber);

    void generateNeighbor();

    Pair<Double, Double> calculateCost(int currentReplicaNum);

    double calculateEnergyFromPair(Pair<Double, Double> energyPair);

    void acceptSolution();

    void printGeneratedSolution();

    QAPlainOldData getData();
}
