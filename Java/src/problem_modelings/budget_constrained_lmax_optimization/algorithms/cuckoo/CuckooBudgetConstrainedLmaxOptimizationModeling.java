package problem_modelings.budget_constrained_lmax_optimization.algorithms.cuckoo;


import base_algorithms.Cost;
import base_algorithms.Cuckoo.CuckooModelingInterface;
import base_algorithms.Cuckoo.CuckooPlainOldData;
import base_algorithms.Cuckoo.model.Cuckoo;
import base_algorithms.Cuckoo.model.CuckooDataAndBehaviour;
import problem_modelings.budget_constrained_lmax_optimization.Utils;
import problem_modelings.budget_constrained_lmax_optimization.model_specifications.BudgetConstrainedLmaxOptimizationModelingAbstract;
import problem_modelings.budget_constrained_lmax_optimization.model_specifications.BudgetConstrainedLmaxOptimizationModelingPlainOldData;

import java.util.*;

public class CuckooBudgetConstrainedLmaxOptimizationModeling extends BudgetConstrainedLmaxOptimizationModelingAbstract implements CuckooModelingInterface {

    CuckooPlainOldData cuckooPlainOldData;

    public CuckooBudgetConstrainedLmaxOptimizationModeling(BudgetConstrainedLmaxOptimizationModelingPlainOldData modelPlainOldData, CuckooPlainOldData cuckooPlainOldData) {
        super(modelPlainOldData);
        this.cuckooPlainOldData = cuckooPlainOldData;
    }

    @Override
    public Cost calculateCost(CuckooDataAndBehaviour cuckooDataAndBehaviours) {
        CuckooBudgetConstrainedLmaxOptimizationModelingDataAndBehaviour castedCuckooDataAndBehaviours = (CuckooBudgetConstrainedLmaxOptimizationModelingDataAndBehaviour) cuckooDataAndBehaviours;
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
                modelPlainOldData.candidateControllers, controllerXSpinVariables,
                modelPlainOldData.maxControllerLoad, modelPlainOldData.maxControllerCoverage, maxL
        );

        double controllerSynchronizationCost = Utils.getControllerSynchronizationCost(
                modelPlainOldData.graph,
                modelPlainOldData.controllerY,
                modelPlainOldData.candidateControllers, controllerXSpinVariables,
                modelPlainOldData.sensorToSensorWorkload
        );

        return new Cost()
                .setReliabilityCost(reliabilityEnergy)
                .setLoadBalancingCost(loadBalancingEnergy)
                .setLmaxCost(maxL)
                .setSummationOfLMaxCost(summationOfLMax)
                .setSynchronizationCost(controllerSynchronizationCost);
    }

    // TODO: Revise generateEggs and generateEggsByElr

    @Override
    public List<Cuckoo> generateEggs(Cuckoo matureCuckoo) throws Exception {
        if (!matureCuckoo.isMature()) {
            throw new Exception("Using generateEggs is not valid for not mature cuckoos!");
        }
        List<Cuckoo> eggs = new ArrayList<>();
        CuckooBudgetConstrainedLmaxOptimizationModelingDataAndBehaviour dataAndBehaviour =
                (CuckooBudgetConstrainedLmaxOptimizationModelingDataAndBehaviour) matureCuckoo.getCuckooDataAndBehaviour();
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

        CuckooBudgetConstrainedLmaxOptimizationModelingDataAndBehaviour dataAndBehaviour = (CuckooBudgetConstrainedLmaxOptimizationModelingDataAndBehaviour) matureCuckoo.getCuckooDataAndBehaviour();

        boolean[] tempCandidateController = dataAndBehaviour.controllerXSpinVariables.clone();
        int maxElr = matureCuckoo.getMatureCuckoo().getELR();
        int candidateElr = random.nextInt(maxElr);
//        int reverseFromFalseToTrueCount = random.nextInt(Math.min(candidateElr, modelPlainOldData.totalBudget / modelPlainOldData.costController));
        int reverseFromFalseToTrueCount = Math.min(maxElr, tempCandidateController.length);

        List<Integer> trueIndices = new ArrayList<>();
        List<Integer> falseIndices = new ArrayList<>();

        for (int i = 0; i < tempCandidateController.length; i++) {
            if (!tempCandidateController[i]) {
                falseIndices.add(i);
            }
        }

        Set<Integer> controllerInversionIndicesFromFalseToTrue = new HashSet<>();
        Set<Integer> controllerInversionIndicesFromTrueToFalse = new HashSet<>();

        if (falseIndices.size() > 0) {
            while (controllerInversionIndicesFromFalseToTrue.size() < reverseFromFalseToTrueCount) {
                int index = random.nextInt(falseIndices.size());
                controllerInversionIndicesFromFalseToTrue.add(falseIndices.get(index));
            }
        }

        controllerInversionIndicesFromFalseToTrue.forEach(index -> tempCandidateController[index] = true);

        for (int i = 0; i < tempCandidateController.length; i++) {
            if (tempCandidateController[i]) {
                trueIndices.add(i);
            }
        }

        int extraTrueCount = Math.max(trueIndices.size() - modelPlainOldData.totalBudget / modelPlainOldData.costController, 0);

        while (controllerInversionIndicesFromTrueToFalse.size() < extraTrueCount) {
            int index = random.nextInt(trueIndices.size());
            controllerInversionIndicesFromTrueToFalse.add(trueIndices.get(index));
        }

        controllerInversionIndicesFromTrueToFalse.forEach(index -> tempCandidateController[index] = false);

        CuckooDataAndBehaviour cuckooDataAndBehaviour = new CuckooBudgetConstrainedLmaxOptimizationModelingDataAndBehaviour(tempCandidateController);

        return new Cuckoo(false, cuckooDataAndBehaviour);
    }

    @Override
    public Cuckoo generateInitialRandomCuckoos() throws Exception {
        boolean[] controllersSpinVariables = new boolean[modelPlainOldData.candidateControllers.size()];
        Set<Integer> trueIndices = new HashSet<>();

        while (trueIndices.size() < modelPlainOldData.totalBudget / modelPlainOldData.costController) {
            Random random = new Random();
            int randTrueIndex = random.nextInt(controllersSpinVariables.length);
            trueIndices.add(randTrueIndex);
        }

        trueIndices.forEach(trueIndex -> controllersSpinVariables[trueIndex] = true);

        CuckooDataAndBehaviour cuckooDataAndBehaviour = new CuckooBudgetConstrainedLmaxOptimizationModelingDataAndBehaviour(controllersSpinVariables);
        Cuckoo cuckoo = new Cuckoo(true, cuckooDataAndBehaviour);
        cuckoo.setCost(calculateCost(cuckooDataAndBehaviour));
        return cuckoo;
    }

    @Override
    public void printGeneratedSolution(CuckooDataAndBehaviour cuckooDataAndBehaviour) {
        CuckooBudgetConstrainedLmaxOptimizationModelingDataAndBehaviour dataAndBehaviour = (CuckooBudgetConstrainedLmaxOptimizationModelingDataAndBehaviour) cuckooDataAndBehaviour;
        super.printGeneratedSolution(dataAndBehaviour.controllerXSpinVariables);
    }

    @Override
    public CuckooPlainOldData getData() {
        return cuckooPlainOldData;
    }
}
