package problem_modelings.budget_constrained_lmax_optimization.algorithms.cuckoo;


import base_algorithms.cuckoo.model.CuckooDataAndBehaviour;

public class CuckooBudgetConstrainedCostOptimizationModelingDataAndBehaviour extends CuckooDataAndBehaviour {
    public boolean[] controllerXSpinVariables;       // SXPrime (X Spin Variable)

    public CuckooBudgetConstrainedCostOptimizationModelingDataAndBehaviour(boolean[] controllerXSpinVariables) {
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