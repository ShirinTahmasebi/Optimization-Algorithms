package main;

import base_algorithms.Cost;
import base_algorithms.Cuckoo.CuckooAlgorithm;
import base_algorithms.Cuckoo.CuckooModelingInterface;
import base_algorithms.Cuckoo.CuckooPlainOldData;
import base_algorithms.quantum_annealing.QAAlgorithm;
import base_algorithms.quantum_annealing.QAModelingInterface;
import base_algorithms.quantum_annealing.QAPlainOldData;
import base_algorithms.simulated_annealing.SAAlgorithm;
import base_algorithms.simulated_annealing.SAModelingInterface;
import base_algorithms.simulated_annealing.SAPlainOldData;
import main.model.Graph;
import main.model.Vertex;
import problem_modelings.budget_constrained_lmax_optimization.algorithms.QABudgetConstrainedLmaxOptimizationModeling;
import problem_modelings.budget_constrained_lmax_optimization.algorithms.SABudgetConstrainedLmaxOptimizationModeling;
import problem_modelings.budget_constrained_lmax_optimization.algorithms.cuckoo.CuckooBudgetConstrainedLmaxOptimizationModeling;
import problem_modelings.budget_constrained_lmax_optimization.model_specifications.BudgetConstrainedLmaxOptimizationModelingPlainOldData;
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
    private int[][] sensorToSensorWorkload;         // w[sensors][sensors]

    public static void main(String[] args) throws Exception {
        FactoryClient client = new FactoryClient();

        if (Parameters.Common.MODEL_NO == ModelNoEnum.COST_OPTIMIZATION) {
            client.executeAlgorithmsOnFirstModel();
        } else if (Parameters.Common.MODEL_NO == ModelNoEnum.BUDGET_CONSTRAINED_LMAX_OPTIMIZATION) {
            client.executeAlgorithmsOnBudgetConstrainedModel();
        } else if (Parameters.Common.MODEL_NO == ModelNoEnum.BUDGET_CONSTRAINED_CONTROLLER_OVERHEAD) {
            client.executeAlgorithmsOnBudgetConstrainedModel();
        }
    }

    private void executeAlgorithmsOnBudgetConstrainedModel() throws Exception {
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
                distances,
                sensorToSensorWorkload
        );

        // Cuckoo algorithm initialization
        CuckooPlainOldData cuckooPlainOldData = new CuckooPlainOldData();
        CuckooModelingInterface cuckooModelingInterface = new CuckooBudgetConstrainedLmaxOptimizationModeling(budgetConstrainedLmaxOptimizationModelingPlainOldData, cuckooPlainOldData);
        CuckooAlgorithm cuckooAlgorithm = new CuckooAlgorithm(cuckooModelingInterface);

        // QA algorithm initialization
        QAPlainOldData qaPlainOldData = new QAPlainOldData(
                main.Parameters.QuantumAnnealing.TROTTER_REPLICAS,
                main.Parameters.QuantumAnnealing.TEMPERATURE,
                main.Parameters.QuantumAnnealing.MONTE_CARLO_STEP,
                main.Parameters.QuantumAnnealing.TUNNELING_FIELD_INITIAL,
                main.Parameters.QuantumAnnealing.TUNNELING_FIELD_FINAL,
                main.Parameters.QuantumAnnealing.TUNNELING_FIELD_EVAPORATION
        );
        QAModelingInterface qaModelingInterface = new QABudgetConstrainedLmaxOptimizationModeling(budgetConstrainedLmaxOptimizationModelingPlainOldData, qaPlainOldData);
        QAAlgorithm qaAlgorithm = new QAAlgorithm(qaModelingInterface);

        // SA algorithm initialization
        SAPlainOldData saPlainOldData = new SAPlainOldData(
                Parameters.SimulatedAnnealing.TEMPERATURE_INITIAL,
                Parameters.SimulatedAnnealing.TEMPERATURE_FINAL,
                Parameters.SimulatedAnnealing.TEMPERATURE_COOLING_RATE,
                Parameters.SimulatedAnnealing.MONTE_CARLO_STEP
        );
        SAModelingInterface saModelingInterface = new SABudgetConstrainedLmaxOptimizationModeling(budgetConstrainedLmaxOptimizationModelingPlainOldData, saPlainOldData);
        SAAlgorithm saAlgorithm = new SAAlgorithm(saModelingInterface);

        // Cuckoo execution
        Date cuckooTimeA = new Date();
        for (int i = 0; i < main.Parameters.Common.SIMULATION_COUNT; i++) {
            Cost cuckooCost = cuckooAlgorithm.execute();
            double lmax = ((CuckooBudgetConstrainedLmaxOptimizationModeling) cuckooModelingInterface).calculateMaxL(cuckooAlgorithm.getSelectedCuckooDataAndBehavior());
            double summationOfLMax = ((CuckooBudgetConstrainedLmaxOptimizationModeling) cuckooModelingInterface).calculateDistanceToNearestControllerEnergy(cuckooAlgorithm.getSelectedCuckooDataAndBehavior());

            cuckooEnergySum += cuckooCost.getPotentialEnergy();
            cuckooLMaxSum += lmax;
            cuckooSummationOfLMaxSum += summationOfLMax;

            chartEx.addToCuckooSeries(i + 1, cuckooCost.getPotentialEnergy());
            printResults(cuckooCost, OptimizationAlgorithmsEnum.CUCKOO);
        }
        Date cuckooTimeB = new Date();

        // QA execution
        Date quantumTimeA = new Date();
        for (int i = 0; i < main.Parameters.Common.SIMULATION_COUNT; i++) {
            Cost qaCost = qaAlgorithm.execute();

            qaEnergySum += qaCost.getPotentialEnergy();
            qaLMaxSum += qaCost.getlMaxCost();
            qaSummationOfLMaxSum += qaCost.getSummationOfLMaxCost();

            chartEx.addToQASeries(i + 1, qaCost.getPotentialEnergy());
            printResults(qaCost, OptimizationAlgorithmsEnum.QUANTUM_ANNEALING);
        }
        Date quantumTimeB = new Date();

        // SA execution
        Date simulatedTimeA = new Date();
        for (int i = 0; i < main.Parameters.Common.SIMULATION_COUNT; i++) {
            Cost saCost = saAlgorithm.execute();

            chartEx.addToSASeries(i + 1, saCost.getPotentialEnergy());

            // TODO: Revise these two lines
            double lMax = saCost.getlMaxCost();
            double summationOfDistanceToNearestControllers = saCost.getSummationOfLMaxCost();

            saEnergySum += saCost.getPotentialEnergy();
            saLMaxSum += lMax;
            saSummationOfLMaxSum += summationOfDistanceToNearestControllers;

            printResults(saCost, OptimizationAlgorithmsEnum.SIMULATED_ANNEALING);
        }
        Date simulatedTimeB = new Date();

        // Create results map
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

        printResultsSummary(chartEx, algorithmResultsMap);
    }

    private void executeAlgorithmsOnFirstModel() throws Exception {
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
            Cost cuckooPotentialEnergy = cuckooAlgorithm.execute();
            chartEx.addToCuckooSeries(i + 1, cuckooPotentialEnergy.getPotentialEnergy());
            cuckooEnergySum += cuckooPotentialEnergy.getPotentialEnergy();
            System.out.println("Cuckoo Energy: " + cuckooPotentialEnergy.getPotentialEnergy());
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
            Cost qaCost = qaAlgorithm.execute();
            chartEx.addToQASeries(i + 1, qaCost.getPotentialEnergy());
            qaEnergySum += qaCost.getPotentialEnergy();
            System.out.println("QA Energy: " + qaCost.getPotentialEnergy());
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
            Cost saExecuteResult = saAlgorithm.execute();
            chartEx.addToSASeries(i + 1, saExecuteResult.getPotentialEnergy());
            saEnergySum += saExecuteResult.getPotentialEnergy();
            System.out.println("SA Energy: " + saExecuteResult.getPotentialEnergy());
        }


        Date simulatedTimeB = new Date();

        // TODO: Print Results Using printResultsSummary Method
    }

    private void retrieveVariablesFromFile() {
        if (Parameters.Common.MODEL_NO == ModelNoEnum.COST_OPTIMIZATION) {
            graph = (Graph) readObjectFromFile(Utils.FILE_NAME_GRAPH);
            candidateSinks = (List<Vertex>) readObjectFromFile(Utils.FILE_NAME_CANDIDATE_SINKS);
            candidateControllers = (List<Vertex>) readObjectFromFile(Utils.FILE_NAME_CANDIDATE_CONTROLLERS);
            sinkYSpinVariables = (boolean[][]) readObjectFromFile(Utils.FILE_NAME_SINK_Y_SPIN_VARIABLES);
            controllerYSpinVariables = (boolean[][]) readObjectFromFile(Utils.FILE_NAME_CONTROLLER_Y_SPIN_VARIABLES);
            distances = (int[][]) readObjectFromFile(Utils.FILE_NAME_DISTANCES);
        } else if (Parameters.Common.MODEL_NO == ModelNoEnum.BUDGET_CONSTRAINED_LMAX_OPTIMIZATION || Parameters.Common.MODEL_NO == ModelNoEnum.BUDGET_CONSTRAINED_CONTROLLER_OVERHEAD) {
            graph = (Graph) readObjectFromFile(Utils.FILE_NAME_GRAPH);
            candidateControllers = (List<Vertex>) readObjectFromFile(Utils.FILE_NAME_CANDIDATE_CONTROLLERS);
            controllerY = (int[][]) readObjectFromFile(Utils.FILE_NAME_CONTROLLER_Y);
            controllerY = (int[][]) readObjectFromFile(Utils.FILE_NAME_CONTROLLER_Y);
            distances = (int[][]) readObjectFromFile(Utils.FILE_NAME_DISTANCES);
            sensorToSensorWorkload = (int[][]) readObjectFromFile(Utils.FILE_NAME_SENSOR_TO_SENSOR_WORKLOAD);
        }
    }

    private void printResultsSummary(LineChartEx chartEx, Map<String, Map<String, Double>> algorithmResultInfo) {
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

    private void printResults(Cost cost, OptimizationAlgorithmsEnum optimizationAlgorithm) throws Exception {
        System.out.println(optimizationAlgorithm.name() + " Total Cost: " + cost.getPotentialEnergy());
        System.out.println(optimizationAlgorithm.name() + " LMax: " + cost.getlMaxCost());
        System.out.println(optimizationAlgorithm.name() + " Summation of LMax Cost: " + cost.getSummationOfLMaxCost());
        System.out.println(optimizationAlgorithm.name() + " Sync Overhead Cost: " + cost.getSynchronizationOverheadCost());
        System.out.println(optimizationAlgorithm.name() + " Sync Delay Cost: " + cost.getSynchronizationDelayCost());
        System.out.println(optimizationAlgorithm.name() + " Reliability Cost: " + cost.getReliabilityCost());
        System.out.println(optimizationAlgorithm.name() + " Load Cost: " + cost.getLoadBalancingCost());
        System.out.println(optimizationAlgorithm.name() + " Kinetic Cost: " + cost.getKineticEnergy());
        System.out.println(optimizationAlgorithm.name() + " Budget Cost: " + cost.getBudgetCostEnergy());
    }
}