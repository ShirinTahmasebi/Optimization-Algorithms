package problem_modelings.budget_constrained_lmax_optimization.model_specifications;

import base_algorithms.simulated_annealing.SAResultBaseInterface;

public class BudgetConstrainedLmaxOptimizationModelignSAResult implements SAResultBaseInterface {
    public long lMax;
    public double summationOfDistanceToNearestControllers;

    public BudgetConstrainedLmaxOptimizationModelignSAResult(int maxL, int toNearestControllerEnergy) {
        this.lMax = maxL;
        this.summationOfDistanceToNearestControllers = toNearestControllerEnergy;
    }
}
