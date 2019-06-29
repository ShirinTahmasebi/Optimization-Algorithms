package problem_modelings.first_modeling.cuckoo;

import algorithms.Cuckoo.model.CuckooDataAndBehaviour;

public class CuckooFirstModelingDataAndBehaviour extends CuckooDataAndBehaviour {

    public boolean[] sinkXSpinVariables;             // SX (X Spin Variable)
    public boolean[] controllerXSpinVariables;       // SXPrime (X Spin Variable)

    public CuckooFirstModelingDataAndBehaviour(boolean[] sinkXSpinVariables, boolean[] controllerXSpinVariables) {
        this.sinkXSpinVariables = sinkXSpinVariables;
        this.controllerXSpinVariables = controllerXSpinVariables;
    }

    @Override
    public int getEggsNumberLowerBound() {
        return (controllerXSpinVariables.length + sinkXSpinVariables.length) / 3;
    }
}
