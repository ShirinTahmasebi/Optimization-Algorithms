package main;

import base_algorithms.Cuckoo.CuckooAlgorithm;
import base_algorithms.Cuckoo.CuckooModelingInterface;
import base_algorithms.Cuckoo.CuckooPlainOldData;
import base_algorithms.simulated_annealing.SAAlgorithm;
import base_algorithms.simulated_annealing.SAModelingInterface;
import base_algorithms.simulated_annealing.SAPlainOldData;
import main.model.Graph;
import main.model.Vertex;
import base_algorithms.quantum_annealing.QAAlgorithm;
import base_algorithms.quantum_annealing.QAModelingInterface;
import base_algorithms.quantum_annealing.QAPlainOldData;
import problem_modelings.budget_constrained_modeling.algorithms.QABudgetConstrainedModeling;
import problem_modelings.budget_constrained_modeling.algorithms.SABudgetConstrainedModeling;
import problem_modelings.budget_constrained_modeling.algorithms.cuckoo.CuckooBudgetConstrainedModeling;
import problem_modelings.budget_constrained_modeling.model_specifications.BudgetConstrainedModelPlainOldData;
import problem_modelings.first_modeling.algorithms.QAFirstModeling;
import problem_modelings.first_modeling.algorithms.SAFirstModeling;
import problem_modelings.first_modeling.algorithms.cuckoo.CuckooFirstModeling;
import problem_modelings.first_modeling.model_specifications.FirstModelPlainOldData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        double qaEnergySum = 0;
        double saEnergySum = 0;

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
            chartEx.addToCuckooSeries(i + 1, cuckooPotentialEnergy);
            cuckooEnergySum += cuckooPotentialEnergy;
            System.out.println("Cuckoo Energy: " + cuckooPotentialEnergy);
            System.out.println("Cuckoo L Max: " + ((CuckooBudgetConstrainedModeling) cuckooModelingInterface).calculateMaxL());
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
            chartEx.addToQASeries(i + 1, qaPotentialEnergy);
            qaEnergySum += qaPotentialEnergy;
            System.out.println("QA Energy: " + qaPotentialEnergy);
            System.out.println("QA L Max: " + ((QABudgetConstrainedModeling) qaModelingInterface).calculateMaxL());
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
            chartEx.addToSASeries(i + 1, saPotentialEnergy);
            saEnergySum += saPotentialEnergy;
            System.out.println("SA Energy: " + saPotentialEnergy);
            System.out.println("SA L Max: " + ((SABudgetConstrainedModeling) saModelingInterface).calculateMaxL());
        }

        Date simulatedTimeB = new Date();
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

        chartEx.drawChart();
        System.out.println();
        System.out.println("Cuckoo average potential energy is: " + cuckooEnergySum / main.Parameters.Common.SIMULATION_COUNT);
        System.out.println("Cuckoo average time is: " + (double) (cuckooTimeB.getTime() - cuckooTimeA.getTime()) / main.Parameters.Common.SIMULATION_COUNT);
        System.out.println();
        System.out.println("QA average potential energy is: " + qaEnergySum / main.Parameters.Common.SIMULATION_COUNT);
        System.out.println("QA average time is: " + (double) (quantumTimeB.getTime() - quantumTimeA.getTime()) / main.Parameters.Common.SIMULATION_COUNT);
        System.out.println();
        System.out.println("SA average potential energy is: " + saEnergySum / main.Parameters.Common.SIMULATION_COUNT);
        System.out.println("SA average time is: " + (double) (simulatedTimeB.getTime() - simulatedTimeA.getTime()) / main.Parameters.Common.SIMULATION_COUNT);

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
}