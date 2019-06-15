package main;

import cuckoo.TestCuckooAlgorithm;
import main.model.Graph;
import main.model.Vertex;
import quantum.anealing.TestQuantumAnnealingAlgorithm;
import simulated.anealing.TestSimulatedAnnealingAlgorithm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static main.Utils.readObjectFromFile;

public class Main {

    public static final boolean DO_PRINT_INSTANCES = false;
    public static final boolean DO_PRINT_STEPS = false;

    private static final int SIMULATION_COUNT = 5;
    private static final int SENSOR_SINK_MAX_DISTANCE = 3;              // Lmax
    private static final int SENSOR_CONTROLLER_MAX_DISTANCE = 2;        // LPrimeMax
    private static final int MAX_SINK_COVERAGE = 6;             // k
    private static final int MAX_CONTROLLER_COVERAGE = 6;       // kPrime
    private static final int MAX_SINK_LOAD = 30;        // W
    private static final int MAX_CONTROLLER_LOAD = 30;  // WPrime
    private static final int COST_SINK = 1;
    private static final int COST_CONTROLLER = 3;

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
                SENSOR_SINK_MAX_DISTANCE,
                SENSOR_CONTROLLER_MAX_DISTANCE,
                MAX_SINK_COVERAGE,
                MAX_CONTROLLER_COVERAGE,
                MAX_SINK_LOAD,
                MAX_CONTROLLER_LOAD,
                COST_SINK,
                COST_CONTROLLER
        );

        for (int i = 0; i < SIMULATION_COUNT; i++) {
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
                SENSOR_SINK_MAX_DISTANCE,
                SENSOR_CONTROLLER_MAX_DISTANCE,
                MAX_SINK_COVERAGE,
                MAX_CONTROLLER_COVERAGE,
                MAX_SINK_LOAD,
                MAX_CONTROLLER_LOAD,
                COST_SINK,
                COST_CONTROLLER
        );

        for (int i = 0; i < SIMULATION_COUNT; i++) {
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
                SENSOR_SINK_MAX_DISTANCE,
                SENSOR_CONTROLLER_MAX_DISTANCE,
                MAX_SINK_COVERAGE,
                MAX_CONTROLLER_COVERAGE,
                MAX_SINK_LOAD,
                MAX_CONTROLLER_LOAD,
                COST_SINK,
                COST_CONTROLLER
        );

        for (int i = 0; i < SIMULATION_COUNT; i++) {
            double saPotentialEnergy = saTest.execute();
            chartEx.addToSASeries(i + 1, saPotentialEnergy);
            saEnergySum += saPotentialEnergy;
            System.out.println("SA Energy: " + saPotentialEnergy);
        }

        Date simulatedTimeB = new Date();

        chartEx.drawChart();
        System.out.println("Cuckoo average potential energy is: " + cuckooEnergySum / SIMULATION_COUNT);
        System.out.println("Cuckoo average time is: " + (double) (cuckooTimeB.getTime() - cuckooTimeA.getTime()) / SIMULATION_COUNT);
        System.out.println();
        System.out.println("QA average potential energy is: " + qaEnergySum / SIMULATION_COUNT);
        System.out.println("QA average time is: " + (double) (quantumTimeB.getTime() - quantumTimeA.getTime()) / SIMULATION_COUNT);
        System.out.println();
        System.out.println("SA average potential energy is: " + saEnergySum / SIMULATION_COUNT);
        System.out.println("SA average time is: " + (double) (simulatedTimeB.getTime() - simulatedTimeA.getTime()) / SIMULATION_COUNT);
    }

    private void retrieveVariablesFromFile(Main m) {
        m.graph = (Graph) readObjectFromFile(Utils.FILE_NAME_GRAPH + main.Parameters.Common.GRAPH_SIZE);
        m.candidateSinks = (List<Vertex>) readObjectFromFile(Utils.FILE_NAME_CANDIDATE_SINKS + main.Parameters.Common.GRAPH_SIZE);
        m.candidateControllers = (List<Vertex>) readObjectFromFile(Utils.FILE_NAME_CANDIDATE_CONTROLLERS + main.Parameters.Common.GRAPH_SIZE);
        m.sinkYSpinVariables = (boolean[][]) readObjectFromFile(Utils.FILE_NAME_SINK_Y_SPIN_VARIABLES + main.Parameters.Common.GRAPH_SIZE);
        m.controllerYSpinVariables = (boolean[][]) readObjectFromFile(Utils.FILE_NAME_CONTROLLER_Y_SPIN_VARIABLES + main.Parameters.Common.GRAPH_SIZE);

    }
}
