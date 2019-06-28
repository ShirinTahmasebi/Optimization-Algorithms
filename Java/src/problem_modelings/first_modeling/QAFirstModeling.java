package problem_modelings.first_modeling;

import javafx.util.Pair;
import main.Utils;
import main.model.Graph;
import main.model.Vertex;
import problem_modelings.algorithms_modeling.QA.QAModelingInterface;
import problem_modelings.algorithms_modeling.QA.QAPlainOldData;
import problem_modelings.modeling_types.FirstModelAbstract;

import java.util.List;
import java.util.Random;

public class QAFirstModeling extends FirstModelAbstract implements QAModelingInterface {

    private QAPlainOldData qaDataStructure;

    public QAFirstModeling(
            Graph graph,
            List<Vertex> candidateSinks,
            List<Vertex> candidateControllers,
            boolean[][] sinkYSpinVariables,
            boolean[][] controllerYSpinVariables,
            int sensorSinkMaxDistance,
            int sensorControllerMaxDistance,
            int maxSinkCoverage,
            int maxControllerCoverage,
            int maxSinkLoad,
            int maxControllerLoad,
            int costSink,
            int costController,
            float costReductionFactor,
            QAPlainOldData qaDataStructure
    ) {
        super(
                graph,
                candidateSinks,
                candidateControllers,
                sinkYSpinVariables,
                controllerYSpinVariables,
                sensorSinkMaxDistance,
                sensorControllerMaxDistance,
                maxSinkCoverage,
                maxControllerCoverage,
                maxSinkLoad,
                maxControllerLoad,
                costSink,
                costController,
                costReductionFactor
        );

        this.qaDataStructure = qaDataStructure;

    }

    @Override
    public void initializeVariables() {

    }

    @Override
    public void resetDynamicVariables() {
        qaDataStructure.temperature = qaDataStructure.temperatureInitial;
        qaDataStructure.tunnelingField = qaDataStructure.tunnelingFieldInitial;
        this.tempControllerXSpinVariables = new boolean[candidateControllers.size()];
        this.tempSinkXSpinVariables = new boolean[candidateSinks.size()];
        this.sinkXSpinVariables = new boolean[candidateSinks.size()];
        this.controllerXSpinVariables = new boolean[candidateControllers.size()];
        this.replicasOfSinkXSpinVariables = new boolean[qaDataStructure.trotterReplicas][candidateSinks.size()];
        this.replicasOfControllerXSpinVariables = new boolean[qaDataStructure.trotterReplicas][candidateControllers.size()];
    }

    @Override
    public void generateReplicasOfSolutions() {
        for (int i = 0; i < qaDataStructure.trotterReplicas; i++) {
            // --- Select random configuration for replicas
            for (int j = 0; j < candidateSinks.size(); j++) {
                double probabilityOfOne = Math.random();
                replicasOfSinkXSpinVariables[i][j] = probabilityOfOne < .5;
            }
            for (int j = 0; j < candidateControllers.size(); j++) {
                double probabilityOfOne = Math.random();
                replicasOfControllerXSpinVariables[i][j] = probabilityOfOne < .5;
            }
        }
    }

    @Override
    public void generateInitialSpinVariablesAndEnergy() {
        // --- Initialize temp lists to false
        for (int i = 0; i < candidateControllers.size(); i++) {
            controllerXSpinVariables[i] = false;
        }

        for (int i = 0; i < candidateSinks.size(); i++) {
            sinkXSpinVariables[i] = false;
        }

        tempControllerXSpinVariables = controllerXSpinVariables.clone();
        tempSinkXSpinVariables = sinkXSpinVariables.clone();
        qaDataStructure.prevEnergyPair = calculateCost(-1);
    }

    @Override
    public void getAReplica(int replicaNumber) {
        tempSinkXSpinVariables = replicasOfSinkXSpinVariables[replicaNumber].clone();
        tempControllerXSpinVariables = replicasOfControllerXSpinVariables[replicaNumber].clone();
    }

    @Override
    public void generateNeighbor() {
        Random random = new Random();
        int randInt = random.nextInt(tempSinkXSpinVariables.length + tempControllerXSpinVariables.length);

        if (randInt < tempSinkXSpinVariables.length) {
            // Change randInt-th item in sink array
            boolean prevValue = tempSinkXSpinVariables[randInt];
            tempSinkXSpinVariables[randInt] = !prevValue;
        } else {
            // Change index-th item in controller array
            int index = randInt - (tempSinkXSpinVariables.length - 1) - 1;
            boolean prevValue = tempControllerXSpinVariables[index];
            tempControllerXSpinVariables[index] = !prevValue;
        }
        if (main.Main.DO_PRINT_STEPS) {
            Utils.printGeneratedSolution(tempSinkXSpinVariables, tempControllerXSpinVariables);
        }
    }

    @Override
    public Pair<Double, Double> calculateCost(int currentReplicaNum) {
        int reliabilityEnergy = Utils.getReliabilityEnergy(
                graph,
                sinkYSpinVariables, controllerYSpinVariables,
                candidateSinks, tempSinkXSpinVariables,
                candidateControllers, tempControllerXSpinVariables,
                maxSinkCoverage, maxControllerCoverage
        );

        double loadBalancingEnergy = Utils.getLoadBalancingEnergy(
                graph,
                sinkYSpinVariables, controllerYSpinVariables,
                candidateSinks, tempSinkXSpinVariables,
                candidateControllers, tempControllerXSpinVariables,
                maxSinkLoad, maxSinkCoverage,
                maxControllerLoad, maxControllerCoverage
        );

        double costEnergy = Utils.getCostEnergy(
                candidateSinks, tempSinkXSpinVariables,
                candidateControllers, tempControllerXSpinVariables,
                costSink, costController, costReductionFactor
        );

        double potentialEnergy = reliabilityEnergy + loadBalancingEnergy + costEnergy;
        double kineticEnergy = getKineticEnergy(currentReplicaNum);

        return new Pair<>(potentialEnergy, kineticEnergy);
    }

    @Override
    public double calculateEnergyFromPair(Pair<Double, Double> energyPair) {
        return energyPair.getKey() + energyPair.getValue();
    }

    private double getKineticEnergy(int currentReplicaNum) {
        if (currentReplicaNum + 1 >= qaDataStructure.trotterReplicas || currentReplicaNum < 0) {
            return 0;
        }

        // Calculate coupling among replicas
        float halfTemperatureQuantum = qaDataStructure.temperatureQuantum / 2;
        float angle = qaDataStructure.tunnelingField / (qaDataStructure.trotterReplicas * qaDataStructure.temperatureQuantum);

        double coupling = -halfTemperatureQuantum * Math.log(Math.tanh(angle));

        int sinkReplicaCoupling = 0;
        int controllerReplicaCoupling = 0;

        for (int i = 0; i < candidateSinks.size(); i++) {
            boolean areSpinVariablesTheSame = (replicasOfSinkXSpinVariables[currentReplicaNum][i] && replicasOfSinkXSpinVariables[currentReplicaNum + 1][i]);
            sinkReplicaCoupling = areSpinVariablesTheSame ? 1 : -1;
        }

        for (int i = 0; i < candidateControllers.size(); i++) {
            boolean areSpinVariablesTheSame
                    = (replicasOfControllerXSpinVariables[currentReplicaNum][i]
                    && replicasOfControllerXSpinVariables[currentReplicaNum + 1][i]);
            controllerReplicaCoupling = areSpinVariablesTheSame ? 1 : -1;
        }

        // Multiply sum of two final results with coupling
        return coupling * (sinkReplicaCoupling + controllerReplicaCoupling);
    }

    @Override
    public void acceptSolution() {
        sinkXSpinVariables = tempSinkXSpinVariables.clone();
        controllerXSpinVariables = tempControllerXSpinVariables.clone();
    }

    @Override
    public void printGeneratedSolution() {
        super.printGeneratedSolution(tempSinkXSpinVariables, tempControllerXSpinVariables);
    }

    @Override
    public QAPlainOldData getData() {
        return qaDataStructure;
    }

    @SuppressWarnings("unused")
    private double calculatePotentialEnergy(int currentReplicaNum) {
        int reliabilityEnergy = Utils.getReliabilityEnergy(
                graph,
                sinkYSpinVariables, controllerYSpinVariables,
                candidateSinks, tempSinkXSpinVariables,
                candidateControllers, tempControllerXSpinVariables,
                maxSinkCoverage, maxControllerCoverage
        );

        double loadBalancingEnergy = Utils.getLoadBalancingEnergy(
                graph,
                sinkYSpinVariables, controllerYSpinVariables,
                candidateSinks, tempSinkXSpinVariables,
                candidateControllers, tempControllerXSpinVariables,
                maxSinkLoad, maxSinkCoverage,
                maxControllerLoad, maxControllerCoverage
        );

        double costEnergy = Utils.getCostEnergy(
                candidateSinks, tempSinkXSpinVariables,
                candidateControllers, tempControllerXSpinVariables,
                costSink, costController, costReductionFactor
        );

        return reliabilityEnergy + loadBalancingEnergy + costEnergy;
    }
}
