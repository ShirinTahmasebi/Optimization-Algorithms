package main;

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
import problem_modelings.budget_constrained_modeling.algorithms.QABudgetConstrainedModeling;
import problem_modelings.budget_constrained_modeling.algorithms.SABudgetConstrainedModeling;
import problem_modelings.budget_constrained_modeling.algorithms.cuckoo.CuckooBudgetConstrainedModeling;
import problem_modelings.budget_constrained_modeling.model_specifications.BudgetConstrainedModelPlainOldData;
import problem_modelings.first_modeling.algorithms.QAFirstModeling;
import problem_modelings.first_modeling.algorithms.SAFirstModeling;
import problem_modelings.first_modeling.algorithms.cuckoo.CuckooFirstModeling;
import problem_modelings.first_modeling.model_specifications.FirstModelPlainOldData;

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

//        client.executeAlgorithmsOnFirstModel();
        client.executeAlgorithmsOnBudgetConstrainedModel();

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

        retrieveVariablesFromFile(2);

        BudgetConstrainedModelPlainOldData budgetConstrainedModelPlainOldData = new BudgetConstrainedModelPlainOldData(
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

        CuckooModelingInterface cuckooModelingInterface = new CuckooBudgetConstrainedModeling(budgetConstrainedModelPlainOldData, cuckooPlainOldData);

        CuckooAlgorithm cuckooAlgorithm = new CuckooAlgorithm(cuckooModelingInterface);

        for (int i = 0; i < main.Parameters.Common.SIMULATION_COUNT; i++) {
            double cuckooPotentialEnergy = cuckooAlgorithm.execute();
            double lmax = ((CuckooBudgetConstrainedModeling) cuckooModelingInterface).calculateMaxL(cuckooAlgorithm.getSelectedCuckooDataAndBehavior());
            double summationOfLMax = ((CuckooBudgetConstrainedModeling) cuckooModelingInterface).calculateDistanceToNearestControllerEnergy(cuckooAlgorithm.getSelectedCuckooDataAndBehavior());

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

        QAModelingInterface qaModelingInterface = new QABudgetConstrainedModeling(
                budgetConstrainedModelPlainOldData,
                qaPlainOldData
        );

        QAAlgorithm qaAlgorithm = new QAAlgorithm(qaModelingInterface);

        for (int i = 0; i < main.Parameters.Common.SIMULATION_COUNT; i++) {

            double qaPotentialEnergy = qaAlgorithm.execute();
            double lmax = ((QABudgetConstrainedModeling) qaModelingInterface).calculateMaxL();
            double summationOfLMAx = ((QABudgetConstrainedModeling) qaModelingInterface).calculateDistanceToNearestControllerEnergy();

            chartEx.addToQASeries(i + 1, qaPotentialEnergy);

            qaEnergySum += qaPotentialEnergy;
            qaLMaxSum += lmax;
            qaSummationOfLMaxSum += summationOfLMAx;

            System.out.println("QA Energy: " + qaPotentialEnergy);
            System.out.println("QA L Max: " + lmax);
            System.out.println("QA Summation of L Max: " + summationOfLMAx);
        }

        Date quantumTimeB = new Date();

        Date simulatedTimeA = new Date();

        SAPlainOldData saPlainOldData = new SAPlainOldData(
                Parameters.SimulatedAnnealing.TEMPERATURE_INITIAL,
                Parameters.SimulatedAnnealing.TEMPERATURE_FINAL,
                Parameters.SimulatedAnnealing.TEMPERATURE_COOLING_RATE,
                Parameters.SimulatedAnnealing.MONTE_CARLO_STEP
        );

        SAModelingInterface saModelingInterface = new SABudgetConstrainedModeling(
                budgetConstrainedModelPlainOldData,
                saPlainOldData
        );

        SAAlgorithm saAlgorithm = new SAAlgorithm(saModelingInterface);

        for (int i = 0; i < main.Parameters.Common.SIMULATION_COUNT; i++) {

            double saPotentialEnergy = saAlgorithm.execute();
            double lmax = ((SABudgetConstrainedModeling) saModelingInterface).calculateMaxL();
            double summationOfLMax = ((SABudgetConstrainedModeling) saModelingInterface).calculateDistanceToNearestControllerEnergy();

            chartEx.addToSASeries(i + 1, saPotentialEnergy);

            saEnergySum += saPotentialEnergy;
            saLMaxSum += lmax;
            saSummationOfLMaxSum += summationOfLMax;

            System.out.println("SA Energy: " + saPotentialEnergy);
            System.out.println("SA L Max: " + lmax);
            System.out.println("SA Summation of L Max: " + summationOfLMax);
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

        retrieveVariablesFromFile(1);

        FirstModelPlainOldData firstModelPlainOldData = new FirstModelPlainOldData(
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

        CuckooModelingInterface cuckooModelingInterface = new CuckooFirstModeling(firstModelPlainOldData, cuckooPlainOldData);

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

        QAModelingInterface qaModelingInterface = new QAFirstModeling(
                firstModelPlainOldData,
                qaPlainOldData
        );

        QAAlgorithm qaAlgorithm = new QAAlgorithm(qaModelingInterface);

        for (int i = 0; i < main.Parameters.Common.SIMULATION_COUNT; i++) {
            double qaPotentialEnergy = qaAlgorithm.execute();
            chartEx.addToQASeries(i + 1, qaPotentialEnergy);
            qaEnergySum += qaPotentialEnergy;
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

        SAModelingInterface saModelingInterface = new SAFirstModeling(
                firstModelPlainOldData,
                saPlainOldData
        );


        SAAlgorithm saAlgorithm = new SAAlgorithm(saModelingInterface);

        for (int i = 0; i < main.Parameters.Common.SIMULATION_COUNT; i++) {
            double saPotentialEnergy = saAlgorithm.execute();
            chartEx.addToSASeries(i + 1, saPotentialEnergy);
            saEnergySum += saPotentialEnergy;
            System.out.println("SA Energy: " + saPotentialEnergy);
        }


        Date simulatedTimeB = new Date();

        // TODO: Print Results Using printResults Method
    }

    private void retrieveVariablesFromFile(int modelNo) {
        if (modelNo == 1) {
            graph = (Graph) readObjectFromFile(Utils.FILE_NAME_GRAPH + main.Parameters.Common.GRAPH_SIZE);
            candidateSinks = (List<Vertex>) readObjectFromFile(Utils.FILE_NAME_CANDIDATE_SINKS + main.Parameters.Common.GRAPH_SIZE);
            candidateControllers = (List<Vertex>) readObjectFromFile(Utils.FILE_NAME_CANDIDATE_CONTROLLERS + main.Parameters.Common.GRAPH_SIZE);
            sinkYSpinVariables = (boolean[][]) readObjectFromFile(Utils.FILE_NAME_SINK_Y_SPIN_VARIABLES + main.Parameters.Common.GRAPH_SIZE);
            controllerYSpinVariables = (boolean[][]) readObjectFromFile(Utils.FILE_NAME_CONTROLLER_Y_SPIN_VARIABLES + main.Parameters.Common.GRAPH_SIZE);
            distances = (int[][]) readObjectFromFile(Utils.FILE_NAME_DISTANCES + main.Parameters.Common.GRAPH_SIZE);
        } else if (modelNo == 2) {
            graph = (Graph) readObjectFromFile(Utils.FILE_NAME_GRAPH + main.Parameters.Common.GRAPH_SIZE);
            candidateControllers = (List<Vertex>) readObjectFromFile(Utils.FILE_NAME_CANDIDATE_CONTROLLERS + main.Parameters.Common.GRAPH_SIZE);
            controllerY = (int[][]) readObjectFromFile(Utils.FILE_NAME_CONTROLLER_Y + main.Parameters.Common.GRAPH_SIZE);
            controllerY = (int[][]) readObjectFromFile(Utils.FILE_NAME_CONTROLLER_Y + main.Parameters.Common.GRAPH_SIZE);
            distances = (int[][]) readObjectFromFile(Utils.FILE_NAME_DISTANCES + main.Parameters.Common.GRAPH_SIZE);
        }
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