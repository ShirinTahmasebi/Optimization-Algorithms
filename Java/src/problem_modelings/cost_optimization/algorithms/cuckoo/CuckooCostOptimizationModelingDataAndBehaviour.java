package problem_modelings.cost_optimization.algorithms.cuckoo;

import base_algorithms.Cuckoo.model.CuckooDataAndBehaviour;

public class CuckooCostOptimizationModelingDataAndBehaviour extends CuckooDataAndBehaviour {

    public boolean[] sinkXSpinVariables;             // SX (X Spin Variable)
    public boolean[] controllerXSpinVariables;       // SXPrime (X Spin Variable)

    public CuckooCostOptimizationModelingDataAndBehaviour(boolean[] sinkXSpinVariables, boolean[] controllerXSpinVariables) {
        this.sinkXSpinVariables = sinkXSpinVariables;
        this.controllerXSpinVariables = controllerXSpinVariables;
    }

    @Override
    public int getEggsNumberLowerBound() {
        return (controllerXSpinVariables.length + sinkXSpinVariables.length) / 3;
    }

    @Override
    public int getMaxELR() {
        return Math.min(controllerXSpinVariables.length, sinkXSpinVariables.length) / 2;
    }
}
