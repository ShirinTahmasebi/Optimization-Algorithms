package main;

import main.model.Graph;
import main.model.Vertex;
import problem_modelings.algorithms_modeling.QA.QAAlgorithm;
import problem_modelings.algorithms_modeling.QA.QAModelingInterface;
import problem_modelings.algorithms_modeling.QA.QAPlainOldData;
import problem_modelings.first_modeling.QAFirstModeling;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static main.Utils.readObjectFromFile;

public class FactoryClient {

    private List<Vertex> candidateSinks = new ArrayList<>();            // AS
    private List<Vertex> candidateControllers = new ArrayList<>();      // AC
    private Graph graph;

    private boolean[][] sinkYSpinVariables;           // SY (Y Spin Variable)
    private boolean[][] controllerYSpinVariables;     // SYPrime (Y Spin Variable)

    public static void main(String[] args) {
        FactoryClient client = new FactoryClient();

        client.retrieveVariablesFromFile(client);

        LineChartEx chartEx = new LineChartEx();
        double cuckooEnergySum = 0;
        double qaEnergySum = 0;
        double saEnergySum = 0;

        Date cuckooTimeA = new Date();

        // TODO: Execute Cuckoo Algorithm

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
                client.graph,
                client.candidateSinks,
                client.candidateControllers,
                client.sinkYSpinVariables,
                client.controllerYSpinVariables,
                main.Parameters.Common.SENSOR_SINK_MAX_DISTANCE,
                main.Parameters.Common.SENSOR_CONTROLLER_MAX_DISTANCE,
                main.Parameters.Common.MAX_SINK_COVERAGE,
                main.Parameters.Common.MAX_CONTROLLER_COVERAGE,
                main.Parameters.Common.MAX_SINK_LOAD,
                main.Parameters.Common.MAX_CONTROLLER_LOAD,
                main.Parameters.Common.COST_SINK,
                main.Parameters.Common.COST_CONTROLLER,
                main.Parameters.Common.COST_REDUCTION_FACTOR,
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

        // TODO: Execute SA Algorithm

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

    private void retrieveVariablesFromFile(FactoryClient m) {
        m.graph = (Graph) readObjectFromFile(Utils.FILE_NAME_GRAPH + main.Parameters.Common.GRAPH_SIZE);
        m.candidateSinks = (List<Vertex>) readObjectFromFile(Utils.FILE_NAME_CANDIDATE_SINKS + main.Parameters.Common.GRAPH_SIZE);
        m.candidateControllers = (List<Vertex>) readObjectFromFile(Utils.FILE_NAME_CANDIDATE_CONTROLLERS + main.Parameters.Common.GRAPH_SIZE);
        m.sinkYSpinVariables = (boolean[][]) readObjectFromFile(Utils.FILE_NAME_SINK_Y_SPIN_VARIABLES + main.Parameters.Common.GRAPH_SIZE);
        m.controllerYSpinVariables = (boolean[][]) readObjectFromFile(Utils.FILE_NAME_CONTROLLER_Y_SPIN_VARIABLES + main.Parameters.Common.GRAPH_SIZE);
    }
}
