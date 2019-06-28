package problem_modelings.budget_constrained_modeling;

import javafx.util.Pair;
import problem_modelings.algorithms_modeling.QA.QAModelingInterface;
import problem_modelings.algorithms_modeling.QA.QAPlainOldData;
import problem_modelings.modeling_types.BudgetConstrainedModelAbstract;

public class QABudgetConstrainedModeling extends BudgetConstrainedModelAbstract implements QAModelingInterface {
    @Override
    public void initializeVariables() {

    }

    @Override
    public void resetDynamicVariables() {

    }

    @Override
    public void generateReplicasOfSolutions() {

    }

    @Override
    public void generateInitialSpinVariablesAndEnergy() {

    }

    @Override
    public void getAReplica(int replicaNumber) {

    }


    @Override
    public void generateNeighbor() {

    }

    @Override
    public Pair<Double, Double> calculateCost(int currentReplicaNum) {
        return null;
    }

    @Override
    public double calculateEnergyFromPair(Pair<Double, Double> energyPair) {
        return 0;
    }

    @Override
    public void acceptSolution() {

    }

    @Override
    public void printGeneratedSolution() {

    }

    @Override
    public QAPlainOldData getData() {
        return null;
    }
}
