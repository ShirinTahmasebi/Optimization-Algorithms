package problem_modelings.budget_constrained_lmax_optimization.algorithms.cuckoo;


import base_algorithms.Cuckoo.model.CuckooDataAndBehaviour;

public class CuckooBudgetConstrainedLmaxOptimizationModelingDataAndBehaviour extends CuckooDataAndBehaviour {
    public boolean[] controllerXSpinVariables;       // SXPrime (X Spin Variable)

    public CuckooBudgetConstrainedLmaxOptimizationModelingDataAndBehaviour(boolean[] controllerXSpinVariables) {
        this.controllerXSpinVariables = controllerXSpinVariables;
    }

    @Override
    public int getEggsNumberLowerBound() {
        return controllerXSpinVariables.length / 3;
    }

    @Override
    public int getMaxELR() {
        return controllerXSpinVariables.length / 2;
    }
}