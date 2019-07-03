package problem_modelings.first_modeling.algorithms.cuckoo;

import base_algorithms.Cuckoo.CuckooModelingInterface;
import base_algorithms.Cuckoo.CuckooPlainOldData;
import base_algorithms.Cuckoo.model.Cuckoo;
import base_algorithms.Cuckoo.model.CuckooDataAndBehaviour;
import problem_modelings.first_modeling.Utils;
import problem_modelings.first_modeling.model_specifications.FirstModelAbstract;
import problem_modelings.first_modeling.model_specifications.FirstModelPlainOldData;

import java.util.*;

public class CuckooFirstModeling extends FirstModelAbstract implements CuckooModelingInterface {

    private CuckooPlainOldData cuckooPlainOldData;

    public CuckooFirstModeling(FirstModelPlainOldData firstModelPlainOldData, CuckooPlainOldData cuckooPlainOldData) {
        super(firstModelPlainOldData);
        this.cuckooPlainOldData = cuckooPlainOldData;
    }

    @Override
    public double calculateCost(CuckooDataAndBehaviour cuckooDataAndBehaviours) {
        boolean[] sinkXSpinVariables = ((CuckooFirstModelingDataAndBehaviour) cuckooDataAndBehaviours).sinkXSpinVariables;
        boolean[] controllerXSpinVariables = ((CuckooFirstModelingDataAndBehaviour) cuckooDataAndBehaviours).controllerXSpinVariables;
        int reliabilityEnergy = Utils.getReliabilityEnergy(
                modelPlainOldData.graph,
                modelPlainOldData.sinkYSpinVariables, modelPlainOldData.controllerYSpinVariables,
                modelPlainOldData.candidateSinks, sinkXSpinVariables,
                modelPlainOldData.candidateControllers, controllerXSpinVariables,
                modelPlainOldData.maxSinkCoverage, modelPlainOldData.maxControllerCoverage
        );

        double loadBalancingEnergy = Utils.getLoadBalancingEnergy(
                modelPlainOldData.graph,
                modelPlainOldData.sinkYSpinVariables, modelPlainOldData.controllerYSpinVariables,
                modelPlainOldData.candidateSinks, sinkXSpinVariables,
                modelPlainOldData.candidateControllers, controllerXSpinVariables,
                modelPlainOldData.maxSinkLoad, modelPlainOldData.maxSinkCoverage,
                modelPlainOldData.maxControllerLoad, modelPlainOldData.maxControllerCoverage
        );

        double costEnergy = Utils.getCostEnergy(
                modelPlainOldData.candidateSinks, sinkXSpinVariables,
                modelPlainOldData.candidateControllers, controllerXSpinVariables,
                modelPlainOldData.costSink, modelPlainOldData.costController, modelPlainOldData.costReductionFactor
        );

        return reliabilityEnergy + loadBalancingEnergy + costEnergy;
    }

    @Override
    public List<Cuckoo> generateEggs(Cuckoo matureCuckoo) throws Exception {
        if (!matureCuckoo.isMature()) {
            throw new Exception("Using generateEggs is not valid for not mature cuckoos!");
        }
        List<Cuckoo> eggs = new ArrayList<>();
        CuckooFirstModelingDataAndBehaviour dataAndBehaviour = (CuckooFirstModelingDataAndBehaviour) matureCuckoo.getCuckooDataAndBehaviour();
        matureCuckoo.getMatureCuckoo().setELR(
                Math.min(dataAndBehaviour.controllerXSpinVariables.length, dataAndBehaviour.sinkXSpinVariables.length) / 2
        );
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

        CuckooFirstModelingDataAndBehaviour dataAndBehaviour = (CuckooFirstModelingDataAndBehaviour) matureCuckoo.getCuckooDataAndBehaviour();

        boolean[] tempCandidateSink = dataAndBehaviour.sinkXSpinVariables.clone();
        boolean[] tempCandidateController = dataAndBehaviour.controllerXSpinVariables.clone();

        int maxElr = matureCuckoo.getMatureCuckoo().getELR();

        int sinkElr = random.nextInt(Math.min(maxElr, tempCandidateSink.length));
        int candidateElr = random.nextInt(Math.min(maxElr, tempCandidateController.length));

        Set<Integer> sinkInversionIndices = new HashSet<>();
        Set<Integer> controllerInversionIndices = new HashSet<>();

        while (sinkInversionIndices.size() < sinkElr) {
            int index = random.nextInt(tempCandidateSink.length);
            sinkInversionIndices.add(index);

            boolean prevValue = tempCandidateSink[index];
            tempCandidateSink[index] = !prevValue;
        }

        while (controllerInversionIndices.size() < candidateElr) {
            int index = random.nextInt(tempCandidateController.length);
            controllerInversionIndices.add(index);

            boolean prevValue = tempCandidateController[index];
            tempCandidateController[index] = !prevValue;
        }

        CuckooDataAndBehaviour cuckooDataAndBehaviour = new CuckooFirstModelingDataAndBehaviour(tempCandidateSink, tempCandidateController);

        return new Cuckoo(false, cuckooDataAndBehaviour);
    }

    @Override
    public Cuckoo generateInitialRandomCuckoos() {
        boolean[] controllersSpinVariables = new boolean[modelPlainOldData.candidateControllers.size()];
        for (int i = 0; i < modelPlainOldData.candidateControllers.size(); i++) {
            double probability = Math.random();
            controllersSpinVariables[i] = (probability < .5);
        }
        boolean[] sinkSpinVariables = new boolean[modelPlainOldData.candidateSinks.size()];
        for (int i = 0; i < modelPlainOldData.candidateSinks.size(); i++) {
            double probability = Math.random();
            sinkSpinVariables[i] = (probability < .5);
        }
        CuckooDataAndBehaviour cuckooDataAndBehaviour = new CuckooFirstModelingDataAndBehaviour(sinkSpinVariables, controllersSpinVariables);
        Cuckoo cuckoo = new Cuckoo(true, cuckooDataAndBehaviour);
        cuckoo.setCost(calculateCost(cuckooDataAndBehaviour));
        return cuckoo;
    }

    @Override
    public void printGeneratedSolution() {
        super.printGeneratedSolution(modelPlainOldData.tempSinkXSpinVariables, modelPlainOldData.tempControllerXSpinVariables);
    }

    @Override
    public CuckooPlainOldData getData() {
        return cuckooPlainOldData;
    }
}
