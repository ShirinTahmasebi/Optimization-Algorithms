package main;

public class Parameters {

    public static class QuantumAnnealing {
        public static final int TROTTER_REPLICAS = 100;             // P
        public static final float TEMPERATURE = 200f;               // T
        public static final int MONTE_CARLO_STEP = 110;             // M
        public static final float TUNNELING_FIELD_INITIAL = 1f;
        public static final float TUNNELING_FIELD_FINAL = .5f;
        public static final float TUNNELING_FIELD_EVAPORATION = .9f;
    }

    public static class SimulatedAnnealing {
        public static final float TEMPERATURE_INITIAL = 100;                // T Initial
        public static final float TEMPERATURE_FINAL = 1;                    // T Final
        public static final float TEMPERATURE_COOLING_RATE = .75f;          // T Cooling Rate
        public static final int MONTE_CARLO_STEP = 50;                      // M
    }

    public static class Cuckoo {
        public static final int POPULATION = 200;      // Npop
        public static final int MONTE_CARLO_STEP = 5;
        public static final int MAX_EGG_NUMBER = 201;
        public static final int MIN_EGG_NUMBER = 150;
        public static final double EGG_KILLING_RATE = .001;
        public static final int MAX_CUCKOO_NUMBERS = 10000;
    }

    public static class SynchronizationOverheadModel {
        public static final double SYNC_OVERHEAD_WEIGHT = 0.1;
        public static final double SYNC_DELAY_WEIGHT = 1 - SYNC_OVERHEAD_WEIGHT;
    }

    public static class Common {
        public static final boolean DO_PRINT_INSTANCES = false;
        public static final boolean DO_PRINT_STEPS = false;
        public static final float COST_REDUCTION_FACTOR = 0.75f;
        public static final int SINK_LOAD = 10;                            // w
        public static final int CONTROLLER_LOAD = 10;                      // wPrime
        public static final int SENSOR_SINK_MAX_DISTANCE = 3;              // Lmax
        public static final int SENSOR_CONTROLLER_MAX_DISTANCE = 2;        // LPrimeMax
        public static final GraphSizeEnum GRAPH_SIZE = GraphSizeEnum.RANDOM_100_SPECIAL_104;
        public static final int SIMULATION_COUNT = 10;
        public static final int MAX_SINK_COVERAGE = 6;                      // k
        public static final int MAX_CONTROLLER_COVERAGE = 6;                // kPrime
        public static final int MAX_SINK_LOAD = 30;                         // W
        public static final int MAX_CONTROLLER_LOAD = 30;                   // WPrime
        public static final int COST_SINK = 1;
        public static final int COST_CONTROLLER = 3;
        public static final boolean USE_RANDOM_GRAPH = false;
        public static final ModelNoEnum GRAPH_SPEC_MODEL_NO = ModelNoEnum.BUDGET_CONSTRAINED_CONTROLLER_OVERHEAD;
        public static final ModelNoEnum MODEL_NO = ModelNoEnum.BUDGET_CONSTRAINED_CONTROLLER_OVERHEAD;
    }

    public static class ResultInfoConstants {
        public static final String START_TIME = "START_TIME";
        public static final String END_TIME = "END_TIME";
        public static final String ENERGY = "ENERGY";
        public static final String LMAX = "LMAX";
        public static final String SUMMATION_OF_LMAX = "SUMMATION_OF_LMAX";
    }
}
