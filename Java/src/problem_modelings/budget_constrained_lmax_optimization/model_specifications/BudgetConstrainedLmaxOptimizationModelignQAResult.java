package problem_modelings.budget_constrained_lmax_optimization.model_specifications;

import base_algorithms.quantum_annealing.QAResultBaseInterface;

public class BudgetConstrainedLmaxOptimizationModelignQAResult implements QAResultBaseInterface {
    public long lMax;
    public double summationOfDistanceToNearestControllers;

    public BudgetConstrainedLmaxOptimizationModelignQAResult(int maxL, int toNearestControllerEnergy) {
        this.lMax = maxL;
        this.summationOfDistanceToNearestControllers = toNearestControllerEnergy;
    }
}
