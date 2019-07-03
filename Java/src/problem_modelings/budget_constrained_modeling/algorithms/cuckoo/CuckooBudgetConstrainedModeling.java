package problem_modelings.budget_constrained_modeling.algorithms.cuckoo;


import base_algorithms.Cuckoo.CuckooModelingInterface;
import base_algorithms.Cuckoo.CuckooPlainOldData;
import base_algorithms.Cuckoo.model.Cuckoo;
import base_algorithms.Cuckoo.model.CuckooDataAndBehaviour;
import problem_modelings.budget_constrained_modeling.Utils;
import problem_modelings.budget_constrained_modeling.model_specifications.BudgetConstrainedModelAbstract;
import problem_modelings.budget_constrained_modeling.model_specifications.BudgetConstrainedModelPlainOldData;

import java.util.*;

public class CuckooBudgetConstrainedModeling extends BudgetConstrainedModelAbstract implements CuckooModelingInterface {

    CuckooPlainOldData cuckooPlainOldData;

    public CuckooBudgetConstrainedModeling(BudgetConstrainedModelPlainOldData modelPlainOldData, CuckooPlainOldData cuckooPlainOldData) {
        super(modelPlainOldData);
        this.cuckooPlainOldData = cuckooPlainOldData;
    }

    @Override
    public double calculateCost(CuckooDataAndBehaviour cuckooDataAndBehaviours) {
        int maxL = super.calculateMaxL();

        boolean[] controllerXSpinVariables = ((CuckooBudgetConstrainedModelingDataAndBehaviour) cuckooDataAndBehaviours).controllerXSpinVariables;

        // TODO: Remove last argument (0) - Replace with appropriate value
        int reliabilityEnergy = Utils.getReliabilityEnergy(
                modelPlainOldData.graph,
                modelPlainOldData.controllerY,
                modelPlainOldData.candidateControllers, controllerXSpinVariables,
                modelPlainOldData.maxControllerCoverage, 0
        );

        // TODO: Remove last argument (0) - Replace with appropriate value
        double loadBalancingEnergy = Utils.getLoadBalancingEnergy(
                modelPlainOldData.graph,
                modelPlainOldData.controllerY,
                modelPlainOldData.candidateControllers, modelPlainOldData.tempControllerXSpinVariables,
                modelPlainOldData.maxControllerLoad, modelPlainOldData.maxControllerCoverage, 0
        );

        double lMaxEnergy = Utils.getMaxLEnergy(maxL);
        return reliabilityEnergy + loadBalancingEnergy + lMaxEnergy;
    }

    // TODO: Revise generateEggs and generateEggsByElr

    @Override
    public List<Cuckoo> generateEggs(Cuckoo matureCuckoo) throws Exception {
        if (!matureCuckoo.isMature()) {
            throw new Exception("Using generateEggs is not valid for not mature cuckoos!");
        }
        List<Cuckoo> eggs = new ArrayList<>();
        CuckooBudgetConstrainedModelingDataAndBehaviour dataAndBehaviour = (CuckooBudgetConstrainedModelingDataAndBehaviour) matureCuckoo.getCuckooDataAndBehaviour();
        matureCuckoo.getMatureCuckoo().setELR(dataAndBehaviour.controllerXSpinVariables.length / 2);

        for (int i = 0; i < matureCuckoo.getMatureCuckoo().getNumberOfEggs(); i++) {
            eggs.add(generateEggByElr(matureCuckoo));
        }

        return eggs;
    }

    @Override
    public Cuckoo generateEggByElr(Cuckoo matureCuckoo) throws Exception {
        if (!matureCuckoo.isMature()) {
            throw new Exception("Using generateEggByElr is not valid for not mature cuckoos!");
        }

        Random random = new Random();

        CuckooBudgetConstrainedModelingDataAndBehaviour dataAndBehaviour = (CuckooBudgetConstrainedModelingDataAndBehaviour) matureCuckoo.getCuckooDataAndBehaviour();

        boolean[] tempCandidateController = dataAndBehaviour.controllerXSpinVariables.clone();
        int maxElr = matureCuckoo.getMatureCuckoo().getELR();
        int candidateElr = random.nextInt(Math.min(maxElr, tempCandidateController.length));
        Set<Integer> controllerInversionIndices = new HashSet<>();

        while (controllerInversionIndices.size() < candidateElr) {
            int index = random.nextInt(tempCandidateController.length);
            controllerInversionIndices.add(index);

            boolean prevValue = tempCandidateController[index];
            tempCandidateController[index] = !prevValue;
        }

        CuckooDataAndBehaviour cuckooDataAndBehaviour = new CuckooBudgetConstrainedModelingDataAndBehaviour(tempCandidateController);

        return new Cuckoo(false, cuckooDataAndBehaviour);
    }

    @Override
    public Cuckoo generateInitialRandomCuckoos() {
        boolean[] controllersSpinVariables = new boolean[modelPlainOldData.candidateControllers.size()];
        for (int i = 0; i < modelPlainOldData.candidateControllers.size(); i++) {
            double probability = Math.random();
            controllersSpinVariables[i] = (probability < .5);
        }
        CuckooDataAndBehaviour cuckooDataAndBehaviour = new CuckooBudgetConstrainedModelingDataAndBehaviour(controllersSpinVariables);
        Cuckoo cuckoo = new Cuckoo(true, cuckooDataAndBehaviour);
        cuckoo.setCost(calculateCost(cuckooDataAndBehaviour));
        return cuckoo;
    }

    @Override
    public void printGeneratedSolution() {
        super.printGeneratedSolution(modelPlainOldData.tempControllerXSpinVariables);
    }

    @Override
    public CuckooPlainOldData getData() {
        return cuckooPlainOldData;
    }
}
