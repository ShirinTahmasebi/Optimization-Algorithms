package main;

import base_algorithms.Cuckoo.CuckooAlgorithm;
import base_algorithms.Cuckoo.CuckooModelingInterface;
import base_algorithms.Cuckoo.CuckooPlainOldData;
import base_algorithms.quantum_annealing.QAAlgorithm;
import base_algorithms.quantum_annealing.QAModelingInterface;
import base_algorithms.quantum_annealing.QAPlainOldData;
import base_algorithms.quantum_annealing.QAResultBase;
import base_algorithms.simulated_annealing.SAAlgorithm;
import base_algorithms.simulated_annealing.SAModelingInterface;
import base_algorithms.simulated_annealing.SAPlainOldData;
import base_algorithms.simulated_annealing.SAResultBase;
import javafx.util.Pair;
import main.model.Graph;
import main.model.Vertex;
import problem_modelings.budget_constrained_lmax_optimization.algorithms.QABudgetConstrainedLmaxOptimizationModeling;
import problem_modelings.budget_constrained_lmax_optimization.algorithms.SABudgetConstrainedLmaxOptimizationModeling;
import problem_modelings.budget_constrained_lmax_optimization.algorithms.cuckoo.CuckooBudgetConstrainedLmaxOptimizationModeling;
import problem_modelings.budget_constrained_lmax_optimization.model_specifications.BudgetConstrainedLmaxOptimizationModelignSAResult;
import problem_modelings.budget_constrained_lmax_optimization.model_specifications.BudgetConstrainedLmaxOptimizationModelingPlainOldData;
import problem_modelings.budget_constrained_lmax_optimization.model_specifications.BudgetConstrainedLmaxOptimizationModelignQAResult;
import problem_modelings.cost_optimization.algorithms.QACostOptimizationModeling;
import problem_modelings.cost_optimization.algorithms.SACostOptimizationModeling;
import problem_modelings.cost_optimization.algorithms.cuckoo.CuckooCostOptimizationModeling;
import problem_modelings.cost_optimization.model_specifications.CostOptimizationModelingPlainOldData;

import java.util.*;

import static main.Utils.readObjectFromFile;

public class FactoryClient {

    private List<Vertex> candidateSinks = new ArrayList<>();            // AS
    private List<Vertex> candidateControllers = new ArrayList<>();      // AC
    private Graph graph;

    private int[][] controllerY;                    // YPrime (Y Number of Hops)
    private boolean[][] sinkYSpinVariables;         // SY (Y Spin Variable)
    private boolean[][] controllerYSpinVariables;   // SYPrime (Y Spin Variable)
    private int[][] distances;

    public static void main(String[] args) {
        FactoryClient client = new FactoryClient();

        if (Parameters.Common.MODEL_NO == ModelNoEnum.COST_OPTIMIZATION) {
            client.executeAlgorithmsOnFirstModel();
        } else if (Parameters.Common.MODEL_NO == ModelNoEnum.BUDGET_CONSTRAINED_LMAX_OPTIMIZATION) {
            client.executeAlgorithmsOnBudgetConstrainedModel();
        } else if (Parameters.Common.MODEL_NO == ModelNoEnum.BUDGET_CONSTRAINED_CONTROLLER_OVERHEAD) {
            // TODO: Add problem modeling
        }
    }

    private void executeAlgorithmsOnBudgetConstrainedModel() {
        LineChartEx chartEx = new LineChartEx();

        double cuckooEnergySum = 0;
        double cuckooLMaxSum = 0;
        double cuckooSummationOfLMaxSum = 0;

        double qaEnergySum = 0;
        double qaLMaxSum = 0;
        double qaSummationOfLMaxSum = 0;

        double saEnergySum = 0;
        double saLMaxSum = 0;
        double saSummationOfLMaxSum = 0;

        retrieveVariablesFromFile();

        BudgetConstrainedLmaxOptimizationModelingPlainOldData budgetConstrainedLmaxOptimizationModelingPlainOldData = new BudgetConstrainedLmaxOptimizationModelingPlainOldData(
                graph,
                candidateControllers,
                controllerY,
                main.Parameters.Common.SENSOR_CONTROLLER_MAX_DISTANCE,
                main.Parameters.Common.MAX_CONTROLLER_COVERAGE,
                main.Parameters.Common.MAX_CONTROLLER_LOAD,
                main.Parameters.Common.COST_CONTROLLER,
                (candidateControllers.size() / 3) * Parameters.Common.COST_CONTROLLER,
                distances
        );

        Date cuckooTimeA = new Date();

        CuckooPlainOldData cuckooPlainOldData = new CuckooPlainOldData();

        CuckooModelingInterface cuckooModelingInterface = new CuckooBudgetConstrainedLmaxOptimizationModeling(budgetConstrainedLmaxOptimizationModelingPlainOldData, cuckooPlainOldData);

        CuckooAlgorithm cuckooAlgorithm = new CuckooAlgorithm(cuckooModelingInterface);

        for (int i = 0; i < main.Parameters.Common.SIMULATION_COUNT; i++) {
            double cuckooPotentialEnergy = cuckooAlgorithm.execute();
            double lmax = ((CuckooBudgetConstrainedLmaxOptimizationModeling) cuckooModelingInterface).calculateMaxL(cuckooAlgorithm.getSelectedCuckooDataAndBehavior());
            double summationOfLMax = ((CuckooBudgetConstrainedLmaxOptimizationModeling) cuckooModelingInterface).calculateDistanceToNearestControllerEnergy(cuckooAlgorithm.getSelectedCuckooDataAndBehavior());

            chartEx.addToCuckooSeries(i + 1, cuckooPotentialEnergy);

            cuckooEnergySum += cuckooPotentialEnergy;
            cuckooLMaxSum += lmax;
            cuckooSummationOfLMaxSum += summationOfLMax;

            System.out.println("Cuckoo Energy: " + cuckooPotentialEnergy);
            System.out.println("Cuckoo L Max: " + lmax);
            System.out.println("Cuckoo Summation of L Max: " + summationOfLMax);
        }

        Date cuckooTimeB = new Date();

        Date quantumTimeA = new Date();

        QAPlainOldData qaPlainOldData = new QAPlainOldData(
                main.Parameters.QuantumAnnealing.TROTTER_REPLICAS,
                main.Parameters.QuantumAnnealing.TEMPERATURE,
                main.Parameters.QuantumAnnealing.MONTE_CARLO_STEP,
                main.Parameters.QuantumAnnealing.TUNNELING_FIELD_INITIAL,
                main.Parameters.QuantumAnnealing.TUNNELING_FIELD_FINAL,
                main.Parameters.QuantumAnnealing.TUNNELING_FIELD_EVAPORATION
        );

        QAModelingInterface qaModelingInterface = new QABudgetConstrainedLmaxOptimizationModeling(
                budgetConstrainedLmaxOptimizationModelingPlainOldData,
                qaPlainOldData
        );

        QAAlgorithm qaAlgorithm = new QAAlgorithm(qaModelingInterface);

        for (int i = 0; i < main.Parameters.Common.SIMULATION_COUNT; i++) {

            Pair<Double, QAResultBase> qaResultPair = qaAlgorithm.execute();

            chartEx.addToQASeries(i + 1, qaResultPair.getKey());

            qaEnergySum += qaResultPair.getKey();
            qaLMaxSum += ((BudgetConstrainedLmaxOptimizationModelignQAResult) qaResultPair.getValue()).lMax;
            qaSummationOfLMaxSum += ((BudgetConstrainedLmaxOptimizationModelignQAResult) qaResultPair.getValue()).summationOfDistanceToNearestControllers;

            System.out.println("QA Energy: " + qaResultPair.getKey());
            System.out.println("QA L Max: " + ((BudgetConstrainedLmaxOptimizationModelignQAResult) qaResultPair.getValue()).lMax);
            System.out.println("QA Summation of L Max: " + ((BudgetConstrainedLmaxOptimizationModelignQAResult) qaResultPair.getValue()).summationOfDistanceToNearestControllers);
        }

        Date quantumTimeB = new Date();

        Date simulatedTimeA = new Date();

        SAPlainOldData saPlainOldData = new SAPlainOldData(
                Parameters.SimulatedAnnealing.TEMPERATURE_INITIAL,
                Parameters.SimulatedAnnealing.TEMPERATURE_FINAL,
                Parameters.SimulatedAnnealing.TEMPERATURE_COOLING_RATE,
                Parameters.SimulatedAnnealing.MONTE_CARLO_STEP
        );

        SAModelingInterface saModelingInterface = new SABudgetConstrainedLmaxOptimizationModeling(
                budgetConstrainedLmaxOptimizationModelingPlainOldData,
                saPlainOldData
        );

        SAAlgorithm saAlgorithm = new SAAlgorithm(saModelingInterface);

        for (int i = 0; i < main.Parameters.Common.SIMULATION_COUNT; i++) {

            Pair<Double, SAResultBase> saResultPair = saAlgorithm.execute();

            chartEx.addToSASeries(i + 1, saResultPair.getKey());

            long lMax = ((BudgetConstrainedLmaxOptimizationModelignSAResult) saResultPair.getValue()).lMax;
            double summationOfDistanceToNearestControllers = ((BudgetConstrainedLmaxOptimizationModelignSAResult) saResultPair.getValue()).summationOfDistanceToNearestControllers;

            saEnergySum += saResultPair.getKey();
            saLMaxSum += lMax;
            saSummationOfLMaxSum += summationOfDistanceToNearestControllers;

            System.out.println("SA Energy: " + saResultPair.getKey());
            System.out.println("SA L Max: " + lMax);
            System.out.println("SA Summation of L Max: " + summationOfDistanceToNearestControllers);
        }

        Date simulatedTimeB = new Date();

        Map<String, Map<String, Double>> algorithmResultsMap = new HashMap<>();

        Map<String, Double> cuckooMap = new HashMap<>();
        cuckooMap.put(Parameters.ResultInfoConstants.ENERGY, cuckooEnergySum);
        cuckooMap.put(Parameters.ResultInfoConstants.START_TIME, (double) cuckooTimeA.getTime());
        cuckooMap.put(Parameters.ResultInfoConstants.END_TIME, (double) cuckooTimeB.getTime());
        cuckooMap.put(Parameters.ResultInfoConstants.LMAX, cuckooLMaxSum);
        cuckooMap.put(Parameters.ResultInfoConstants.SUMMATION_OF_LMAX, cuckooSummationOfLMaxSum);

        algorithmResultsMap.put(OptimizationAlgorithmsEnum.CUCKOO.name(), cuckooMap);

        Map<String, Double> qaMap = new HashMap<>();
        qaMap.put(Parameters.ResultInfoConstants.ENERGY, qaEnergySum);
        qaMap.put(Parameters.ResultInfoConstants.START_TIME, (double) quantumTimeA.getTime());
        qaMap.put(Parameters.ResultInfoConstants.END_TIME, (double) quantumTimeB.getTime());
        qaMap.put(Parameters.ResultInfoConstants.LMAX, qaLMaxSum);
        qaMap.put(Parameters.ResultInfoConstants.SUMMATION_OF_LMAX, qaSummationOfLMaxSum);

        algorithmResultsMap.put(OptimizationAlgorithmsEnum.QUANTUM_ANNEALING.name(), qaMap);

        Map<String, Double> saMap = new HashMap<>();
        saMap.put(Parameters.ResultInfoConstants.ENERGY, saEnergySum);
        saMap.put(Parameters.ResultInfoConstants.START_TIME, (double) simulatedTimeA.getTime());
        saMap.put(Parameters.ResultInfoConstants.END_TIME, (double) simulatedTimeB.getTime());
        saMap.put(Parameters.ResultInfoConstants.LMAX, saLMaxSum);
        saMap.put(Parameters.ResultInfoConstants.SUMMATION_OF_LMAX, saSummationOfLMaxSum);

        algorithmResultsMap.put(OptimizationAlgorithmsEnum.SIMULATED_ANNEALING.name(), saMap);

        printResults(chartEx, algorithmResultsMap);
    }

    private void executeAlgorithmsOnFirstModel() {
        LineChartEx chartEx = new LineChartEx();
        double cuckooEnergySum = 0;
        double qaEnergySum = 0;
        double saEnergySum = 0;

        retrieveVariablesFromFile();

        CostOptimizationModelingPlainOldData costOptimizationModelingPlainOldData = new CostOptimizationModelingPlainOldData(
                graph,
                candidateSinks,
                candidateControllers,
                sinkYSpinVariables,
                controllerYSpinVariables,
                main.Parameters.Common.SENSOR_SINK_MAX_DISTANCE,
                main.Parameters.Common.SENSOR_CONTROLLER_MAX_DISTANCE,
                main.Parameters.Common.MAX_SINK_COVERAGE,
                main.Parameters.Common.MAX_CONTROLLER_COVERAGE,
                main.Parameters.Common.MAX_SINK_LOAD,
                main.Parameters.Common.MAX_CONTROLLER_LOAD,
                main.Parameters.Common.COST_SINK,
                main.Parameters.Common.COST_CONTROLLER,
                main.Parameters.Common.COST_REDUCTION_FACTOR
        );

        Date cuckooTimeA = new Date();

        CuckooPlainOldData cuckooPlainOldData = new CuckooPlainOldData();

        CuckooModelingInterface cuckooModelingInterface = new CuckooCostOptimizationModeling(costOptimizationModelingPlainOldData, cuckooPlainOldData);

        CuckooAlgorithm cuckooAlgorithm = new CuckooAlgorithm(cuckooModelingInterface);

        for (int i = 0; i < main.Parameters.Common.SIMULATION_COUNT; i++) {
            double cuckooPotentialEnergy = cuckooAlgorithm.execute();
            chartEx.addToCuckooSeries(i + 1, cuckooPotentialEnergy);
            cuckooEnergySum += cuckooPotentialEnergy;
            System.out.println("Cuckoo Energy: " + cuckooPotentialEnergy);
        }

        Date cuckooTimeB = new Date();

        Date quantumTimeA = new Date();

        QAPlainOldData qaPlainOldData = new QAPlainOldData(
                main.Parameters.QuantumAnnealing.TROTTER_REPLICAS,
                main.Parameters.QuantumAnnealing.TEMPERATURE,
                main.Parameters.QuantumAnnealing.MONTE_CARLO_STEP,
                main.Parameters.QuantumAnnealing.TUNNELING_FIELD_INITIAL,
                main.Parameters.QuantumAnnealing.TUNNELING_FIELD_FINAL,
                main.Parameters.QuantumAnnealing.TUNNELING_FIELD_EVAPORATION
        );

        QAModelingInterface qaModelingInterface = new QACostOptimizationModeling(
                costOptimizationModelingPlainOldData,
                qaPlainOldData
        );

        QAAlgorithm qaAlgorithm = new QAAlgorithm(qaModelingInterface);

        for (int i = 0; i < main.Parameters.Common.SIMULATION_COUNT; i++) {
            Pair<Double, QAResultBase> qaPotentialEnergy = qaAlgorithm.execute();
            chartEx.addToQASeries(i + 1, qaPotentialEnergy.getKey());
            qaEnergySum += qaPotentialEnergy.getKey();
            System.out.println("QA Energy: " + qaPotentialEnergy);
        }

        Date quantumTimeB = new Date();

        Date simulatedTimeA = new Date();

        SAPlainOldData saPlainOldData = new SAPlainOldData(
                Parameters.SimulatedAnnealing.TEMPERATURE_INITIAL,
                Parameters.SimulatedAnnealing.TEMPERATURE_FINAL,
                Parameters.SimulatedAnnealing.TEMPERATURE_COOLING_RATE,
                Parameters.SimulatedAnnealing.MONTE_CARLO_STEP
        );

        SAModelingInterface saModelingInterface = new SACostOptimizationModeling(
                costOptimizationModelingPlainOldData,
                saPlainOldData
        );


        SAAlgorithm saAlgorithm = new SAAlgorithm(saModelingInterface);

        for (int i = 0; i < main.Parameters.Common.SIMULATION_COUNT; i++) {
            Pair<Double, SAResultBase> saExecuteResult = saAlgorithm.execute();
            chartEx.addToSASeries(i + 1, saExecuteResult.getKey());
            saEnergySum += saExecuteResult.getKey();
            System.out.println("SA Energy: " + saExecuteResult);
        }


        Date simulatedTimeB = new Date();

        // TODO: Print Results Using printResults Method
    }

    private void retrieveVariablesFromFile() {
        if (Parameters.Common.MODEL_NO == ModelNoEnum.COST_OPTIMIZATION) {
            graph = (Graph) readObjectFromFile(Utils.FILE_NAME_GRAPH + Parameters.Common.GRAPH_SIZE.number);
            candidateSinks = (List<Vertex>) readObjectFromFile(Utils.FILE_NAME_CANDIDATE_SINKS + Parameters.Common.GRAPH_SIZE.number);
            candidateControllers = (List<Vertex>) readObjectFromFile(Utils.FILE_NAME_CANDIDATE_CONTROLLERS + Parameters.Common.GRAPH_SIZE.number);
            sinkYSpinVariables = (boolean[][]) readObjectFromFile(Utils.FILE_NAME_SINK_Y_SPIN_VARIABLES + Parameters.Common.GRAPH_SIZE.number);
            controllerYSpinVariables = (boolean[][]) readObjectFromFile(Utils.FILE_NAME_CONTROLLER_Y_SPIN_VARIABLES + Parameters.Common.GRAPH_SIZE.number);
            distances = (int[][]) readObjectFromFile(Utils.FILE_NAME_DISTANCES + Parameters.Common.GRAPH_SIZE.number);
        } else if (Parameters.Common.MODEL_NO == ModelNoEnum.BUDGET_CONSTRAINED_LMAX_OPTIMIZATION) {
            graph = (Graph) readObjectFromFile(Utils.FILE_NAME_GRAPH + Parameters.Common.GRAPH_SIZE.number);
            candidateControllers = (List<Vertex>) readObjectFromFile(Utils.FILE_NAME_CANDIDATE_CONTROLLERS + Parameters.Common.GRAPH_SIZE.number);
            controllerY = (int[][]) readObjectFromFile(Utils.FILE_NAME_CONTROLLER_Y + Parameters.Common.GRAPH_SIZE.number);
            controllerY = (int[][]) readObjectFromFile(Utils.FILE_NAME_CONTROLLER_Y + Parameters.Common.GRAPH_SIZE.number);
            distances = (int[][]) readObjectFromFile(Utils.FILE_NAME_DISTANCES + Parameters.Common.GRAPH_SIZE.number);
        }
        // TODO: Add problem modeling
    }

    private void printResults(LineChartEx chartEx, Map<String, Map<String, Double>> algorithmResultInfo) {
        chartEx.drawChart();
        System.out.println();

        List<OptimizationAlgorithmsEnum> optimizationAlgorithmsEnums = Arrays.asList(OptimizationAlgorithmsEnum.values());

        for (OptimizationAlgorithmsEnum optimizationAlgorithm : optimizationAlgorithmsEnums) {
            Map<String, Double> algorithmInfoPair = algorithmResultInfo.get(optimizationAlgorithm.name());
            System.out.println(optimizationAlgorithm.name() + " potential energy is: " + algorithmInfoPair.get(Parameters.ResultInfoConstants.ENERGY) / main.Parameters.Common.SIMULATION_COUNT);
            System.out.println(optimizationAlgorithm.name() + " lmax is: " + algorithmInfoPair.get(Parameters.ResultInfoConstants.LMAX) / main.Parameters.Common.SIMULATION_COUNT);
            System.out.println(optimizationAlgorithm.name() + " summation of lamx is: " + algorithmInfoPair.get(Parameters.ResultInfoConstants.SUMMATION_OF_LMAX) / main.Parameters.Common.SIMULATION_COUNT);
            System.out.println(optimizationAlgorithm.name() + " average time is: " +
                            (algorithmInfoPair.get(Parameters.ResultInfoConstants.END_TIME) - algorithmInfoPair.get(Parameters.ResultInfoConstants.START_TIME))
                                    / main.Parameters.Common.SIMULATION_COUNT
            );
            System.out.println();
        }
    }
}