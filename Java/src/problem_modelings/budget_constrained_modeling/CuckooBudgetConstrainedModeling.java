package problem_modelings.budget_constrained_modeling;


import algorithms_modeling.Cuckoo.CuckooModelingInterface;
import algorithms_modeling.Cuckoo.CuckooPlainOldData;
import algorithms_modeling.Cuckoo.model.Cuckoo;
import algorithms_modeling.Cuckoo.model.CuckooDataAndBehaviour;
import problem_modelings.modeling_types.BudgetConstrainedModelAbstract;

import java.util.List;

public class CuckooBudgetConstrainedModeling extends BudgetConstrainedModelAbstract implements CuckooModelingInterface {
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
