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
        CuckooBudgetConstrainedModelingDataAndBehaviour castedCuckooDataAndBehaviours = (CuckooBudgetConstrainedModelingDataAndBehaviour) cuckooDataAndBehaviours;
        int maxL = super.calculateMaxL(castedCuckooDataAndBehaviours);
        int summationOfLMax = super.calculateDistanceToNearestControllerEnergy(castedCuckooDataAndBehaviours);

        boolean[] controllerXSpinVariables = castedCuckooDataAndBehaviours.controllerXSpinVariables;

        int reliabilityEnergy = Utils.getReliabilityEnergy(
                modelPlainOldData.graph,
                modelPlainOldData.controllerY,
                modelPlainOldData.candidateControllers, controllerXSpinVariables,
                modelPlainOldData.maxControllerCoverage, maxL
        );

        double loadBalancingEnergy = Utils.getLoadBalancingEnergy(
                modelPlainOldData.graph,
                modelPlainOldData.controllerY,
                modelPlainOldData.candidateControllers, modelPlainOldData.tempControllerXSpinVariables,
                modelPlainOldData.maxControllerLoad, modelPlainOldData.maxControllerCoverage, maxL
        );

        double lMaxEnergy = Utils.getMaxLEnergy(maxL);
        double distanceToNearestControllerEnergy = Utils.getSummationOfMaxLEnergy(summationOfLMax);

        return reliabilityEnergy + loadBalancingEnergy + lMaxEnergy + distanceToNearestControllerEnergy;
    }

    // TODO: Revise generateEggs and generateEggsByElr

    @Override
    public List<Cuckoo> generateEggs(Cuckoo matureCuckoo) throws Exception {
        if (!matureCuckoo.isMature()) {
            throw new Exception("Using generateEggs is not valid for not mature cuckoos!");
        }
        List<Cuckoo> eggs = new ArrayList<>();
        CuckooBudgetConstrainedModelingDataAndBehaviour dataAndBehaviour =
                (CuckooBudgetConstrainedModelingDataAndBehaviour) matureCuckoo.getCuckooDataAndBehaviour();
        matureCuckoo.getMatureCuckoo().setELR(dataAndBehaviour.getMaxELR());

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
        int candidateElr = random.nextInt(maxElr);
        int reverseFromFalseToTrueCount = random.nextInt(Math.min(candidateElr, modelPlainOldData.totalBudget / modelPlainOldData.costController));

        List<Integer> trueIndices = new ArrayList<>();
        List<Integer> falseIndices = new ArrayList<>();

        for (int i = 0; i < modelPlainOldData.tempControllerXSpinVariables.length; i++) {
            if (modelPlainOldData.tempControllerXSpinVariables[i]) {
                trueIndices.add(i);
            } else {
                falseIndices.add(i);
            }
        }

        Set<Integer> controllerInversionIndicesFromFalseToTrue = new HashSet<>();
        Set<Integer> controllerInversionIndicesFromTrueToFalse = new HashSet<>();

        while (controllerInversionIndicesFromFalseToTrue.size() < reverseFromFalseToTrueCount) {
            int index = random.nextInt(falseIndices.size());
            controllerInversionIndicesFromFalseToTrue.add(index);
        }

        while (controllerInversionIndicesFromTrueToFalse.size() < reverseFromFalseToTrueCount) {
            int index = random.nextInt(trueIndices.size());
            controllerInversionIndicesFromTrueToFalse.add(index);
        }

        controllerInversionIndicesFromFalseToTrue.forEach(index -> tempCandidateController[index] = true);
        controllerInversionIndicesFromTrueToFalse.forEach(index -> tempCandidateController[index] = false);

        CuckooDataAndBehaviour cuckooDataAndBehaviour = new CuckooBudgetConstrainedModelingDataAndBehaviour(tempCandidateController);

        return new Cuckoo(false, cuckooDataAndBehaviour);
    }

    @Override
    public Cuckoo generateInitialRandomCuckoos() {
        boolean[] controllersSpinVariables = new boolean[modelPlainOldData.candidateControllers.size()];
        Set<Integer> trueIndices = new HashSet<>();

        while (trueIndices.size() < modelPlainOldData.totalBudget / modelPlainOldData.costController) {
            Random random = new Random();
            int randTrueIndex = random.nextInt(controllersSpinVariables.length);
            trueIndices.add(randTrueIndex);
        }

        trueIndices.forEach(trueIndex -> controllersSpinVariables[trueIndex] = true);

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
