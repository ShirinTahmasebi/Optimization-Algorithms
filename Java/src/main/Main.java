package main;

import algorithms.cuckoo.TestCuckooAlgorithm;
import main.model.Graph;
import main.model.Vertex;
import algorithms.quantum_annealing.TestQuantumAnnealingAlgorithm;
import algorithms.simulated_annealing.TestSimulatedAnnealingAlgorithm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static main.Utils.readObjectFromFile;

public class Main {

    public static final boolean DO_PRINT_INSTANCES = false;
    public static final boolean DO_PRINT_STEPS = false;

    private List<Vertex> candidateSinks = new ArrayList<>();            // AS
    private List<Vertex> candidateControllers = new ArrayList<>();      // AC
    private Graph graph;

    private boolean[][] sinkYSpinVariables;           // SY (Y Spin Variable)
    private boolean[][] controllerYSpinVariables;     // SYPrime (Y Spin Variable)

    public static void main(String[] args) {
        Main m = new Main();

        m.retrieveVariablesFromFile(m);

        LineChartEx chartEx = new LineChartEx();
        double cuckooEnergySum = 0;
        double qaEnergySum = 0;
        double saEnergySum = 0;

        Date cuckooTimeA = new Date();

        TestCuckooAlgorithm cuckooTest = new TestCuckooAlgorithm(
                m.graph,
                m.candidateSinks,
                m.candidateControllers,
                m.sinkYSpinVariables,
                m.controllerYSpinVariables,
                main.Parameters.Common.SENSOR_SINK_MAX_DISTANCE,
                main.Parameters.Common.SENSOR_CONTROLLER_MAX_DISTANCE,
                main.Parameters.Common.MAX_SINK_COVERAGE,
                main.Parameters.Common.MAX_CONTROLLER_COVERAGE,
                main.Parameters.Common.MAX_SINK_LOAD,
                main.Parameters.Common.MAX_CONTROLLER_LOAD,
                main.Parameters.Common.COST_SINK,
                main.Parameters.Common.COST_CONTROLLER
        );

        for (int i = 0; i < main.Parameters.Common.SIMULATION_COUNT; i++) {
            double cuckooPotentialEnergy = cuckooTest.execute();
            chartEx.addToCuckooSeries(i + 1, cuckooPotentialEnergy);
            cuckooEnergySum += cuckooPotentialEnergy;
            System.out.println("Cuckoo Energy: " + cuckooPotentialEnergy);
        }

        Date cuckooTimeB = new Date();

        Date quantumTimeA = new Date();

        TestQuantumAnnealingAlgorithm qaTest = new TestQuantumAnnealingAlgorithm(
                m.graph,
                m.candidateSinks,
                m.candidateControllers,
                m.sinkYSpinVariables,
                m.controllerYSpinVariables,
                main.Parameters.Common.SENSOR_SINK_MAX_DISTANCE,
                main.Parameters.Common.SENSOR_CONTROLLER_MAX_DISTANCE,
                main.Parameters.Common.MAX_SINK_COVERAGE,
                main.Parameters.Common.MAX_CONTROLLER_COVERAGE,
                main.Parameters.Common.MAX_SINK_LOAD,
                main.Parameters.Common.MAX_CONTROLLER_LOAD,
                main.Parameters.Common.COST_SINK,
                main.Parameters.Common.COST_CONTROLLER
        );

        for (int i = 0; i < main.Parameters.Common.SIMULATION_COUNT; i++) {
            double qaPotentialEnergy = qaTest.execute();
            chartEx.addToQASeries(i + 1, qaPotentialEnergy);
            qaEnergySum += qaPotentialEnergy;
            System.out.println("QA Energy: " + qaPotentialEnergy);
        }

        Date quantumTimeB = new Date();

        Date simulatedTimeA = new Date();

        TestSimulatedAnnealingAlgorithm saTest = new TestSimulatedAnnealingAlgorithm(
                m.graph,
                m.candidateSinks,
                m.candidateControllers,
                m.sinkYSpinVariables,
                m.controllerYSpinVariables,
                main.Parameters.Common.SENSOR_SINK_MAX_DISTANCE,
                main.Parameters.Common.SENSOR_CONTROLLER_MAX_DISTANCE,
                main.Parameters.Common.MAX_SINK_COVERAGE,
                main.Parameters.Common.MAX_CONTROLLER_COVERAGE,
                main.Parameters.Common.MAX_SINK_LOAD,
                main.Parameters.Common.MAX_CONTROLLER_LOAD,
                main.Parameters.Common.COST_SINK,
                main.Parameters.Common.COST_CONTROLLER
        );

        for (int i = 0; i < main.Parameters.Common.SIMULATION_COUNT; i++) {
            double saPotentialEnergy = saTest.execute();
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

    private void retrieveVariablesFromFile(Main m) {
        m.graph = (Graph) readObjectFromFile(Utils.FILE_NAME_GRAPH + main.Parameters.Common.GRAPH_SIZE);
        m.candidateSinks = (List<Vertex>) readObjectFromFile(Utils.FILE_NAME_CANDIDATE_SINKS + main.Parameters.Common.GRAPH_SIZE);
        m.candidateControllers = (List<Vertex>) readObjectFromFile(Utils.FILE_NAME_CANDIDATE_CONTROLLERS + main.Parameters.Common.GRAPH_SIZE);
        m.sinkYSpinVariables = (boolean[][]) readObjectFromFile(Utils.FILE_NAME_SINK_Y_SPIN_VARIABLES + main.Parameters.Common.GRAPH_SIZE);
        m.controllerYSpinVariables = (boolean[][]) readObjectFromFile(Utils.FILE_NAME_CONTROLLER_Y_SPIN_VARIABLES + main.Parameters.Common.GRAPH_SIZE);

    }
}
