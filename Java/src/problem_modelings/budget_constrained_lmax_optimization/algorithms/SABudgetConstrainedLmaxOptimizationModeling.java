package problem_modelings.budget_constrained_lmax_optimization.algorithms;

import base_algorithms.simulated_annealing.SAModelingInterface;
import base_algorithms.simulated_annealing.SAPlainOldData;
import base_algorithms.simulated_annealing.SAResultBase;
import main.Parameters;
import problem_modelings.budget_constrained_lmax_optimization.Utils;
import problem_modelings.budget_constrained_lmax_optimization.model_specifications.BudgetConstrainedLmaxOptimizationModelignSAResult;
import problem_modelings.budget_constrained_lmax_optimization.model_specifications.BudgetConstrainedLmaxOptimizationModelingAbstract;
import problem_modelings.budget_constrained_lmax_optimization.model_specifications.BudgetConstrainedLmaxOptimizationModelingPlainOldData;

import java.util.*;

public class SABudgetConstrainedLmaxOptimizationModeling extends BudgetConstrainedLmaxOptimizationModelingAbstract implements SAModelingInterface {

    private SAPlainOldData saPlainOldData;

    public SABudgetConstrainedLmaxOptimizationModeling(BudgetConstrainedLmaxOptimizationModelingPlainOldData modelPlainOldData, SAPlainOldData saPlainOldData) {
        super(modelPlainOldData);
        this.saPlainOldData = saPlainOldData;
    }

    @Override
    public void resetDynamicVariables() {
        saPlainOldData.temperature = saPlainOldData.temperatureInitial;
        saPlainOldData.prevEnergy = 0;
        this.modelPlainOldData.tempControllerXSpinVariables = new boolean[modelPlainOldData.candidateControllers.size()];
        this.modelPlainOldData.controllerXSpinVariables = new boolean[modelPlainOldData.candidateControllers.size()];
    }

    @Override
    public void generateInitialSpinVariablesAndEnergy() {
        // --- Initialize temp lists to false
        for (int i = 0; i < modelPlainOldData.candidateControllers.size(); i++) {
            modelPlainOldData.controllerXSpinVariables[i] = false;
        }

        modelPlainOldData.tempControllerXSpinVariables = modelPlainOldData.controllerXSpinVariables.clone();
        saPlainOldData.prevEnergy = calculateCost();
    }

    @Override
    public void generateNeighbor() {
        List<Integer> trueIndices = new ArrayList<>();
        List<Integer> falseIndices = new ArrayList<>();

        for (int i = 0; i < modelPlainOldData.tempControllerXSpinVariables.length; i++) {
            if (modelPlainOldData.tempControllerXSpinVariables[i]) {
                trueIndices.add(i);
            } else {
                falseIndices.add(i);
            }
        }

        if (trueIndices.size() == 0 && falseIndices.size() == modelPlainOldData.candidateControllers.size()) {
            // Generate Initial Solution

            Set<Integer> indices = new HashSet<>();
            while (indices.size() < modelPlainOldData.totalBudget / modelPlainOldData.costController) {
                Random random = new Random();
                int randIndex = random.nextInt(modelPlainOldData.candidateControllers.size());
                indices.add(randIndex);
            }

            for (Integer index : indices) {
                modelPlainOldData.tempControllerXSpinVariables[index] = true;
            }

        } else {
            Random random = new Random();
            int randTrueIndex = random.nextInt(trueIndices.size());
            int randFalseIndex = random.nextInt(falseIndices.size());

            // Change index-th item in controller array
            modelPlainOldData.tempControllerXSpinVariables[falseIndices.get(randFalseIndex)] = true;
            modelPlainOldData.tempControllerXSpinVariables[trueIndices.get(randTrueIndex)] = false;
        }

        if (Parameters.Common.DO_PRINT_STEPS) {
            super.printGeneratedSolution(modelPlainOldData.tempControllerXSpinVariables);
        }
    }

    @Override
    public double calculateCost() {
        int maxL = super.calculateMaxL(modelPlainOldData.tempControllerXSpinVariables);

        int reliabilityEnergy = Utils.getReliabilityEnergy(
                modelPlainOldData.graph,
                modelPlainOldData.controllerY,
                modelPlainOldData.candidateControllers, modelPlainOldData.tempControllerXSpinVariables,
                modelPlainOldData.maxControllerCoverage, 0
        );

        double loadBalancingEnergy = Utils.getLoadBalancingEnergy(
                modelPlainOldData.graph,
                modelPlainOldData.controllerY,
                modelPlainOldData.candidateControllers, modelPlainOldData.tempControllerXSpinVariables,
                modelPlainOldData.maxControllerLoad, modelPlainOldData.maxControllerCoverage, 0
        );

        double lMaxEnergy = Utils.getMaxLEnergy(maxL);

        double distanceToNearestControllerEnergy = super.calculateDistanceToNearestControllerEnergy(modelPlainOldData.tempControllerXSpinVariables);
        return reliabilityEnergy + loadBalancingEnergy + lMaxEnergy + distanceToNearestControllerEnergy;
    }

    @Override
    public void acceptSolution() {
        modelPlainOldData.controllerXSpinVariables = modelPlainOldData.tempControllerXSpinVariables.clone();
    }

    @Override
    public void printGeneratedSolution() {
        super.printGeneratedSolution(modelPlainOldData.tempControllerXSpinVariables);
    }

    @Override
    public SAPlainOldData getData() {
        return saPlainOldData;
    }

    @Override
    public SAResultBase getResult() {
        int maxL = super.calculateMaxL(modelPlainOldData.controllerXSpinVariables);
        int toNearestControllerEnergy = super.calculateDistanceToNearestControllerEnergy(modelPlainOldData.controllerXSpinVariables);
        return new BudgetConstrainedLmaxOptimizationModelignSAResult(maxL, toNearestControllerEnergy);
    }
}
