package problem_modelings.budget_constrained_modeling.algorithms.cuckoo;


import base_algorithms.Cuckoo.model.CuckooDataAndBehaviour;

public class CuckooBudgetConstrainedModelingDataAndBehaviour extends CuckooDataAndBehaviour {
    public boolean[] controllerXSpinVariables;       // SXPrime (X Spin Variable)

    public CuckooBudgetConstrainedModelingDataAndBehaviour(boolean[] controllerXSpinVariables) {
        this.controllerXSpinVariables = controllerXSpinVariables;
    }

    @Override
    public int getEggsNumberLowerBound() {
        return controllerXSpinVariables.length / 3;
    }
}