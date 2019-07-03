package problem_modelings.budget_constrained_modeling.algorithms;


import base_algorithms.Cuckoo.CuckooModelingInterface;
import base_algorithms.Cuckoo.CuckooPlainOldData;
import base_algorithms.Cuckoo.model.Cuckoo;
import base_algorithms.Cuckoo.model.CuckooDataAndBehaviour;
import problem_modelings.budget_constrained_modeling.model_specifications.BudgetConstrainedModelAbstract;
import problem_modelings.budget_constrained_modeling.model_specifications.BudgetConstrainedModelPlainOldData;

import java.util.List;

public class CuckooBudgetConstrainedModeling extends BudgetConstrainedModelAbstract implements CuckooModelingInterface {

    CuckooPlainOldData cuckooPlainOldData;

    public CuckooBudgetConstrainedModeling(BudgetConstrainedModelPlainOldData modelPlainOldData, CuckooPlainOldData cuckooPlainOldData) {
        super(modelPlainOldData);
        this.cuckooPlainOldData = cuckooPlainOldData;
    }

    @Override
    public double calculateCost(CuckooDataAndBehaviour cuckooDataAndBehaviours) {
        return 0;
    }

    @Override
    public List<Cuckoo> generateEggs(Cuckoo matureCuckoo) throws Exception {
        return null;
    }

    @Override
    public Cuckoo generateEggByElr(Cuckoo matureCuckoo) throws Exception {
        return null;
    }

    @Override
    public Cuckoo generateInitialRandomCuckoos() {
        return null;
    }

    @Override
    public void printGeneratedSolution() {

    }

    @Override
    public CuckooPlainOldData getData() {
        return null;
    }
}
