package problem_modelings.budget_constrained_lmax_optimization.algorithms;

import base_algorithms.quantum_annealing.QAResultBaseInterface;
import base_algorithms.quantum_annealing.QAModelingInterface;
import base_algorithms.quantum_annealing.QAPlainOldData;
import javafx.util.Pair;
import main.Parameters;
import problem_modelings.budget_constrained_lmax_optimization.Utils;
import problem_modelings.budget_constrained_lmax_optimization.model_specifications.BudgetConstrainedLmaxOptimizationModelingAbstract;
import problem_modelings.budget_constrained_lmax_optimization.model_specifications.BudgetConstrainedLmaxOptimizationModelingPlainOldData;
import problem_modelings.budget_constrained_lmax_optimization.model_specifications.BudgetConstrainedLmaxOptimizationModelignQAResult;

import java.util.*;

public class QABudgetConstrainedLmaxOptimizationModeling extends BudgetConstrainedLmaxOptimizationModelingAbstract implements QAModelingInterface {

    private QAPlainOldData qaDataStructure;

    public QABudgetConstrainedLmaxOptimizationModeling(BudgetConstrainedLmaxOptimizationModelingPlainOldData modelPlainOldData, QAPlainOldData qaDataStructure) {
        super(modelPlainOldData);
        this.qaDataStructure = qaDataStructure;
    }

    @Override
    public void resetDynamicVariables() {
        qaDataStructure.temperature = qaDataStructure.temperatureInitial;
        qaDataStructure.tunnelingField = qaDataStructure.tunnelingFieldInitial;
        modelPlainOldData.controllerYSpinVariable = new boolean[modelPlainOldData.graph.getVertexes().size()][modelPlainOldData.candidateControllers.size()];
        modelPlainOldData.tempControllerXSpinVariables = new boolean[modelPlainOldData.candidateControllers.size()];
        modelPlainOldData.controllerXSpinVariables = new boolean[modelPlainOldData.candidateControllers.size()];
        modelPlainOldData.replicasOfControllerXSpinVariables = new boolean[qaDataStructure.trotterReplicas][modelPlainOldData.candidateControllers.size()];
    }

    @Override
    public void generateReplicasOfSolutions() {
        Random random = new Random();

        for (int i = 0; i < qaDataStructure.trotterReplicas; i++) {
            // --- Select random configuration for replicas
            Set<Integer> selectedControllerIndices = new HashSet<>();

            while (selectedControllerIndices.size() < modelPlainOldData.totalBudget / modelPlainOldData.costController) {
                selectedControllerIndices.add(random.nextInt(modelPlainOldData.candidateControllers.size()));
            }

            for (int j : selectedControllerIndices) {
                modelPlainOldData.replicasOfControllerXSpinVariables[i][j] = true;
            }
        }
    }

    @Override
    public void generateInitialSpinVariablesAndEnergy() {
        // --- Initialize temp lists to false
        for (int i = 0; i < modelPlainOldData.candidateControllers.size(); i++) {
            modelPlainOldData.controllerXSpinVariables[i] = false;
        }

        modelPlainOldData.tempControllerXSpinVariables = modelPlainOldData.controllerXSpinVariables.clone();
        // TODO: Check This Line - calculateCost(-1)
        qaDataStructure.prevEnergyPair = new Pair(Double.MAX_VALUE, Double.MAX_VALUE);
    }

    @Override
    public void getAReplica(int replicaNumber) {
        modelPlainOldData.tempControllerXSpinVariables = modelPlainOldData.replicasOfControllerXSpinVariables[replicaNumber].clone();
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

        Random random = new Random();
        int randTrueIndex = random.nextInt(trueIndices.size());
        int randFalseIndex = random.nextInt(falseIndices.size());

        // Change index-th item in controller array
        modelPlainOldData.tempControllerXSpinVariables[falseIndices.get(randFalseIndex)] = true;
        modelPlainOldData.tempControllerXSpinVariables[trueIndices.get(randTrueIndex)] = false;

        if (Parameters.Common.DO_PRINT_STEPS) {
            super.printGeneratedSolution(modelPlainOldData.tempControllerXSpinVariables);
        }
    }

    @Override
    public double getKineticEnergy(int currentReplicaNum) {
        if (currentReplicaNum + 1 >= qaDataStructure.trotterReplicas || currentReplicaNum < 0) {
            return 0;
        }

        // Calculate coupling among replicas
        float halfTemperatureQuantum = qaDataStructure.temperatureQuantum / 2;
        float angle = qaDataStructure.tunnelingField / (qaDataStructure.trotterReplicas * qaDataStructure.temperatureQuantum);
        double coupling = -halfTemperatureQuantum * Math.log(Math.tanh(angle));
        int controllerReplicaCoupling = 0;

        for (int i = 0; i < modelPlainOldData.candidateControllers.size(); i++) {
            boolean areSpinVariablesTheSame
                    = (modelPlainOldData.replicasOfControllerXSpinVariables[currentReplicaNum][i]
                    && modelPlainOldData.replicasOfControllerXSpinVariables[currentReplicaNum + 1][i]);
            controllerReplicaCoupling = areSpinVariablesTheSame ? 1 : -1;
        }

        // Multiply sum of two final results with coupling
        return coupling * controllerReplicaCoupling;
    }

    @Override
    public Pair<Double, Double> calculateCost(int currentReplicaNum) {
        int maxL = super.calculateMaxL(modelPlainOldData.tempControllerXSpinVariables);
        int summationOfLMax = super.calculateDistanceToNearestControllerEnergy(modelPlainOldData.tempControllerXSpinVariables);

        int reliabilityEnergy = Utils.getReliabilityEnergy(
                modelPlainOldData.graph,
                modelPlainOldData.controllerY,
                modelPlainOldData.candidateControllers, modelPlainOldData.tempControllerXSpinVariables,
                modelPlainOldData.maxControllerCoverage, maxL
        );

        double loadBalancingEnergy = Utils.getLoadBalancingEnergy(
                modelPlainOldData.graph,
                modelPlainOldData.controllerY,
                modelPlainOldData.candidateControllers, modelPlainOldData.tempControllerXSpinVariables,
                modelPlainOldData.maxControllerLoad, modelPlainOldData.maxControllerCoverage, maxL
        );

        double controllerSynchronizationDelayAndOverheadCost = Utils.getControllerSynchronizationDelayAndOverheadCost(
                modelPlainOldData.graph,
                modelPlainOldData.controllerY,
                modelPlainOldData.candidateControllers, modelPlainOldData.tempControllerXSpinVariables,
                modelPlainOldData.sensorsLoadToControllers
        );

        double lMaxEnergy = Utils.getMaxLEnergy(maxL);
        double distanceToNearestControllerEnergy = Utils.getSummationOfMaxLEnergy(summationOfLMax);
        double controllerSynchronizationOverheadEnergy = Utils.getControllerSynchronizationOverheadEnergy(controllerSynchronizationDelayAndOverheadCost);

        double potentialEnergy = reliabilityEnergy + loadBalancingEnergy + lMaxEnergy + distanceToNearestControllerEnergy + controllerSynchronizationOverheadEnergy;
        double kineticEnergy = getKineticEnergy(currentReplicaNum);

        return new Pair<>(potentialEnergy, kineticEnergy);
    }

    @Override
    public double calculateEnergyFromPair(Pair<Double, Double> energyPair) {
        return energyPair.getKey() + energyPair.getValue();
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
    public QAPlainOldData getData() {
        return qaDataStructure;
    }

    @Override
    public QAResultBaseInterface getResult() {
        int maxL = super.calculateMaxL(modelPlainOldData.controllerXSpinVariables);
        int toNearestControllerEnergy = super.calculateDistanceToNearestControllerEnergy(modelPlainOldData.controllerXSpinVariables);
        return new BudgetConstrainedLmaxOptimizationModelignQAResult(maxL, toNearestControllerEnergy);
    }
}
