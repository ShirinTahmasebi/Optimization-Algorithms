package problem_modelings.first_modeling;

import algorithms.simulated_annealing.SAModelingInterface;
import algorithms.simulated_annealing.SAPlainOldData;
import main.Parameters;
import main.Utils;
import problem_modelings.modeling_types.first_modeling.FirstModelAbstract;
import problem_modelings.modeling_types.first_modeling.FirstModelPlainOldData;

import java.util.Random;

public class SAFirstModeling extends FirstModelAbstract implements SAModelingInterface {

    private SAPlainOldData saPlainOldData;

    public SAFirstModeling(FirstModelPlainOldData firstModelPlainOldData, SAPlainOldData saPlainOldData) {
        super(firstModelPlainOldData);
        this.saPlainOldData = saPlainOldData;
    }

    @Override
    public void resetDynamicVariables() {
        saPlainOldData.temperature = saPlainOldData.temperatureInitial;
        saPlainOldData.prevEnergy = 0;
        this.modelPlainOldData.tempControllerXSpinVariables = new boolean[modelPlainOldData.candidateControllers.size()];
        this.modelPlainOldData.tempSinkXSpinVariables = new boolean[modelPlainOldData.candidateSinks.size()];
        this.modelPlainOldData.sinkXSpinVariables = new boolean[modelPlainOldData.candidateSinks.size()];
        this.modelPlainOldData.controllerXSpinVariables = new boolean[modelPlainOldData.candidateControllers.size()];
    }

    @Override
    public void generateInitialSpinVariablesAndEnergy() {
        // --- Initialize temp lists to false
        for (int i = 0; i < modelPlainOldData.candidateControllers.size(); i++) {
            modelPlainOldData.controllerXSpinVariables[i] = false;
        }

        for (int i = 0; i < modelPlainOldData.candidateSinks.size(); i++) {
            modelPlainOldData.sinkXSpinVariables[i] = false;
        }

        modelPlainOldData.tempControllerXSpinVariables = modelPlainOldData.controllerXSpinVariables.clone();
        modelPlainOldData.tempSinkXSpinVariables = modelPlainOldData.sinkXSpinVariables.clone();
        saPlainOldData.prevEnergy = calculateCost();
    }

    @Override
    public void generateNeighbor() {
        Random random = new Random();
        int randInt = random.nextInt(modelPlainOldData.tempSinkXSpinVariables.length + modelPlainOldData.tempControllerXSpinVariables.length);

        if (randInt < modelPlainOldData.tempSinkXSpinVariables.length) {
            // Change randInt-th item in sink array
            boolean prevValue = modelPlainOldData.tempSinkXSpinVariables[randInt];
            modelPlainOldData.tempSinkXSpinVariables[randInt] = !prevValue;
        } else {
            // Change index-th item in controller array
            int index = randInt - (modelPlainOldData.tempSinkXSpinVariables.length - 1) - 1;
            boolean prevValue = modelPlainOldData.tempControllerXSpinVariables[index];
            modelPlainOldData.tempControllerXSpinVariables[index] = !prevValue;
        }
        if (Parameters.Common.DO_PRINT_STEPS) {
            super.printGeneratedSolution(modelPlainOldData.tempSinkXSpinVariables, modelPlainOldData.tempControllerXSpinVariables);
        }
    }

    @Override
    public double calculateCost() {
        int reliabilityEnergy = Utils.getReliabilityEnergy(
                modelPlainOldData.graph,
                modelPlainOldData.sinkYSpinVariables, modelPlainOldData.controllerYSpinVariables,
                modelPlainOldData.candidateSinks, modelPlainOldData.tempSinkXSpinVariables,
                modelPlainOldData.candidateControllers, modelPlainOldData.tempControllerXSpinVariables,
                modelPlainOldData.maxSinkCoverage, modelPlainOldData.maxControllerCoverage
        );

        double loadBalancingEnergy = Utils.getLoadBalancingEnergy(
                modelPlainOldData.graph,
                modelPlainOldData.sinkYSpinVariables, modelPlainOldData.controllerYSpinVariables,
                modelPlainOldData.candidateSinks, modelPlainOldData.tempSinkXSpinVariables,
                modelPlainOldData.candidateControllers, modelPlainOldData.tempControllerXSpinVariables,
                modelPlainOldData.maxSinkLoad, modelPlainOldData.maxSinkCoverage,
                modelPlainOldData.maxControllerLoad, modelPlainOldData.maxControllerCoverage
        );

        double costEnergy = Utils.getCostEnergy(
                modelPlainOldData.candidateSinks, modelPlainOldData.tempSinkXSpinVariables,
                modelPlainOldData.candidateControllers, modelPlainOldData.tempControllerXSpinVariables,
                modelPlainOldData.costSink, modelPlainOldData.costController, modelPlainOldData.costReductionFactor
        );

        return reliabilityEnergy + loadBalancingEnergy + costEnergy;
    }

    @Override
    public void acceptSolution() {

    }

    @Override
    public void printGeneratedSolution() {
        super.printGeneratedSolution(modelPlainOldData.tempSinkXSpinVariables, modelPlainOldData.tempControllerXSpinVariables);
    }

    @Override
    public SAPlainOldData getData() {
        return saPlainOldData;
    }
}
