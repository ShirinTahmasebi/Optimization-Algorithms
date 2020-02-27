package problem_modelings.cost_optimization.algorithms;

import base_algorithms.Cost;
import base_algorithms.simulated_annealing.SAModelingInterface;
import base_algorithms.simulated_annealing.SAPlainOldData;
import main.Parameters;
import problem_modelings.cost_optimization.Utils;
import problem_modelings.cost_optimization.model_specifications.CostOptimizationModelingAbstract;
import problem_modelings.cost_optimization.model_specifications.CostOptimizationModelingPlainOldData;

import java.util.Random;

public class SACostOptimizationModeling extends CostOptimizationModelingAbstract implements SAModelingInterface {

    private SAPlainOldData saPlainOldData;

    public SACostOptimizationModeling(CostOptimizationModelingPlainOldData costOptimizationModelingPlainOldData, SAPlainOldData saPlainOldData) {
        super(costOptimizationModelingPlainOldData);
        this.saPlainOldData = saPlainOldData;
    }

    @Override
    public void resetDynamicVariables() {
        saPlainOldData.temperature = saPlainOldData.temperatureInitial;
        this.modelPlainOldData.tempControllerXSpinVariables = new boolean[modelPlainOldData.candidateControllers.size()];
        this.modelPlainOldData.tempSinkXSpinVariables = new boolean[modelPlainOldData.candidateSinks.size()];
        this.modelPlainOldData.sinkXSpinVariables = new boolean[modelPlainOldData.candidateSinks.size()];
        this.modelPlainOldData.controllerXSpinVariables = new boolean[modelPlainOldData.candidateControllers.size()];
    }

    @Override
    public void generateInitialSpinVariablesAndEnergy() throws Exception {
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
    public Cost calculateCost() {
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

        return new Cost()
                .setReliabilityCost(reliabilityEnergy)
                .setLoadBalancingCost(loadBalancingEnergy)
                .setBudgetCostEnergy(costEnergy);
    }

    @Override
    public void acceptSolution() {
        modelPlainOldData.sinkXSpinVariables = modelPlainOldData.tempSinkXSpinVariables.clone();
        modelPlainOldData.controllerXSpinVariables = modelPlainOldData.tempControllerXSpinVariables.clone();
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
