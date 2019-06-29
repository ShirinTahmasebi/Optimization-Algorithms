package problem_modelings.budget_constrained_modeling.algorithms;

import algorithms.simulated_annealing.SAModelingInterface;
import algorithms.simulated_annealing.SAPlainOldData;
import problem_modelings.budget_constrained_modeling.model_specifications.BudgetConstrainedModelAbstract;
import problem_modelings.budget_constrained_modeling.model_specifications.BudgetConstrainedModelPlainOldData;

public class SABudgetConstrainedModeling extends BudgetConstrainedModelAbstract implements SAModelingInterface {

    private SAPlainOldData saPlainOldData;

    public SABudgetConstrainedModeling(BudgetConstrainedModelPlainOldData modelPlainOldData, SAPlainOldData saPlainOldData) {
        super(modelPlainOldData);
        this.saPlainOldData = saPlainOldData;
    }

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
