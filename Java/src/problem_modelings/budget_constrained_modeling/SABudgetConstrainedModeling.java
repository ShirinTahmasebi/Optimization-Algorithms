package problem_modelings.budget_constrained_modeling;

import algorithms.simulated_annealing.SAModelingInterface;
import algorithms.simulated_annealing.SAPlainOldData;
import problem_modelings.modeling_types.BudgetConstrainedModelAbstract;

public class SABudgetConstrainedModeling extends BudgetConstrainedModelAbstract implements SAModelingInterface {
    @Override
    public void resetDynamicVariables() {

    }

    @Override
    public void generateInitialSpinVariablesAndEnergy() {

    }

    @Override
    public void generateNeighbor() {

    }

    @Override
    public double calculateCost() {
        return 0;
    }

    @Override
    public void acceptSolution() {

    }

    @Override
    public void printGeneratedSolution() {

    }

    @Override
    public SAPlainOldData getData() {
        return null;
    }
}
