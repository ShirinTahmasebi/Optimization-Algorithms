package problem_modelings.cost_optimization.algorithms;

import base_algorithms.quantum_annealing.QAResultBaseInterface;
import javafx.util.Pair;
import main.Parameters;
import problem_modelings.cost_optimization.Utils;
import base_algorithms.quantum_annealing.QAModelingInterface;
import base_algorithms.quantum_annealing.QAPlainOldData;
import problem_modelings.cost_optimization.model_specifications.CostOptimizationModelingAbstract;
import problem_modelings.cost_optimization.model_specifications.CostOptimizationModelingPlainOldData;
import problem_modelings.cost_optimization.model_specifications.CostOptimizationModelingQAResult;

import java.util.Random;

public class QACostOptimizationModeling extends CostOptimizationModelingAbstract implements QAModelingInterface {

    private QAPlainOldData qaDataStructure;

    public QACostOptimizationModeling(CostOptimizationModelingPlainOldData costOptimizationModelingPlainOldData, QAPlainOldData qaDataStructure) {
        super(costOptimizationModelingPlainOldData);
        this.qaDataStructure = qaDataStructure;
    }

    @Override
    public void resetDynamicVariables() {
        qaDataStructure.temperature = qaDataStructure.temperatureInitial;
        qaDataStructure.tunnelingField = qaDataStructure.tunnelingFieldInitial;
        modelPlainOldData.tempControllerXSpinVariables = new boolean[modelPlainOldData.candidateControllers.size()];
        modelPlainOldData.tempSinkXSpinVariables = new boolean[modelPlainOldData.candidateSinks.size()];
        modelPlainOldData.sinkXSpinVariables = new boolean[modelPlainOldData.candidateSinks.size()];
        modelPlainOldData.controllerXSpinVariables = new boolean[modelPlainOldData.candidateControllers.size()];
        modelPlainOldData.replicasOfSinkXSpinVariables = new boolean[qaDataStructure.trotterReplicas][modelPlainOldData.candidateSinks.size()];
        modelPlainOldData.replicasOfControllerXSpinVariables = new boolean[qaDataStructure.trotterReplicas][modelPlainOldData.candidateControllers.size()];
    }

    @Override
    public void generateReplicasOfSolutions() {
        for (int i = 0; i < qaDataStructure.trotterReplicas; i++) {
            // --- Select random configuration for replicas
            for (int j = 0; j < modelPlainOldData.candidateSinks.size(); j++) {
                double probabilityOfOne = Math.random();
                modelPlainOldData.replicasOfSinkXSpinVariables[i][j] = probabilityOfOne < .5;
            }
            for (int j = 0; j < modelPlainOldData.candidateControllers.size(); j++) {
                double probabilityOfOne = Math.random();
                modelPlainOldData.replicasOfControllerXSpinVariables[i][j] = probabilityOfOne < .5;
            }
        }
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
        qaDataStructure.prevEnergyPair = calculateCost(-1);
    }

    @Override
    public void getAReplica(int replicaNumber) {
        modelPlainOldData.tempSinkXSpinVariables = modelPlainOldData.replicasOfSinkXSpinVariables[replicaNumber].clone();
        modelPlainOldData.tempControllerXSpinVariables = modelPlainOldData.replicasOfControllerXSpinVariables[replicaNumber].clone();
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
    public Pair<Double, Double> calculateCost(int currentReplicaNum) {
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

        double potentialEnergy = reliabilityEnergy + loadBalancingEnergy + costEnergy;
        double kineticEnergy = getKineticEnergy(currentReplicaNum);

        return new Pair<>(potentialEnergy, kineticEnergy);
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

        int sinkReplicaCoupling = 0;
        int controllerReplicaCoupling = 0;

        for (int i = 0; i < modelPlainOldData.candidateSinks.size(); i++) {
            boolean areSpinVariablesTheSame = (modelPlainOldData.replicasOfSinkXSpinVariables[currentReplicaNum][i] && modelPlainOldData.replicasOfSinkXSpinVariables[currentReplicaNum + 1][i]);
            sinkReplicaCoupling = areSpinVariablesTheSame ? 1 : -1;
        }

        for (int i = 0; i < modelPlainOldData.candidateControllers.size(); i++) {
            boolean areSpinVariablesTheSame
                    = (modelPlainOldData.replicasOfControllerXSpinVariables[currentReplicaNum][i]
                    && modelPlainOldData.replicasOfControllerXSpinVariables[currentReplicaNum + 1][i]);
            controllerReplicaCoupling = areSpinVariablesTheSame ? 1 : -1;
        }

        // Multiply sum of two final results with coupling
        return coupling * (sinkReplicaCoupling + controllerReplicaCoupling);
    }

    @Override
    public double calculateEnergyFromPair(Pair<Double, Double> energyPair) {
        return energyPair.getKey() + energyPair.getValue();
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
    public QAPlainOldData getData() {
        return qaDataStructure;
    }

    @Override
    public QAResultBaseInterface getResult() {
        return new CostOptimizationModelingQAResult();
    }

    @SuppressWarnings("unused")
    private double calculatePotentialEnergy(int currentReplicaNum) {
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
}
