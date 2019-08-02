package problem_modelings.budget_constrained_modeling.model_specifications;

import base_algorithms.quantum_annealing.QAResultBase;

public class BudgetConstrainedQAResult implements QAResultBase {
    public long lMax;
    public double summationOfDistanceToNearestControllers;

    public BudgetConstrainedQAResult(int maxL, int toNearestControllerEnergy) {
        this.lMax = maxL;
        this.summationOfDistanceToNearestControllers = toNearestControllerEnergy;
    }
}
