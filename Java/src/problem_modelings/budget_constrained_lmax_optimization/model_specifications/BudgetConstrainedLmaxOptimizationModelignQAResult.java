package problem_modelings.budget_constrained_lmax_optimization.model_specifications;

import base_algorithms.quantum_annealing.QAResultBase;

public class BudgetConstrainedLmaxOptimizationModelignQAResult implements QAResultBase {
    public long lMax;
    public double summationOfDistanceToNearestControllers;

    public BudgetConstrainedLmaxOptimizationModelignQAResult(int maxL, int toNearestControllerEnergy) {
        this.lMax = maxL;
        this.summationOfDistanceToNearestControllers = toNearestControllerEnergy;
    }
}
