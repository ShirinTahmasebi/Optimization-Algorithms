package base_algorithms.simulated_annealing;


import base_algorithms.Cost;

public interface SAModelingInterface {

    void resetDynamicVariables();

    void generateInitialSpinVariablesAndEnergy() throws Exception;

    void generateNeighbor();

    Cost calculateCost();

    void acceptSolution();

    void printGeneratedSolution();

    SAPlainOldData getData();
}
