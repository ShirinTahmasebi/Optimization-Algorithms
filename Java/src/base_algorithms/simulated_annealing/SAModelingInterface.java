package base_algorithms.simulated_annealing;

public interface SAModelingInterface {

    void resetDynamicVariables();

    void generateInitialSpinVariablesAndEnergy();

    void generateNeighbor();

    double calculateCost();

    void acceptSolution();

    void printGeneratedSolution();

    SAPlainOldData getData();
}
