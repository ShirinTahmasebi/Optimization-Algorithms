package main;

public class Parameters {

    static {
        if (Common.MODEL_NO == ModelNoEnum.BUDGET_CONSTRAINED_CONTROLLER_OVERHEAD && Common.USE_RANDOM_GRAPH) {
            switch (Common.GRAPH_SIZE) {
                case RANDOM_100_SPECIAL_104:
                    SynchronizationOverheadModel.SUMMATION_OFL_MAX_BALANCE = 50;
                    break;
                case RANDOM_150_SPECIAL_169:
                    break;
                case RANDOM_200_SPECIAL_195:
                    break;
                case RANDOM_NONE_SPECIAL_143:
                    break;
                case RANDOM_80_SPECIAL_NONE:
                    break;
            }
        }
    }

    public static class QuantumAnnealing {
        public static final int TROTTER_REPLICAS = 100;             // P
        public static final float TEMPERATURE = 200f;               // T
        public static final int MONTE_CARLO_STEP = 80;              // M
        public static final float TUNNELING_FIELD_INITIAL = 1f;
        public static final float TUNNELING_FIELD_FINAL = .5f;
        public static final float TUNNELING_FIELD_EVAPORATION = .7f;
    }

    public static class SimulatedAnnealing {
        public static final float TEMPERATURE_INITIAL = 100;                // T Initial
        public static final float TEMPERATURE_FINAL = .05f;                    // T Final
        public static final float TEMPERATURE_COOLING_RATE = .7f;          // T Cooling Rate
        public static final int MONTE_CARLO_STEP = 50;                      // M
    }

    public static class Cuckoo {
        public static final int POPULATION = 200;      // Npop
        public static final int MONTE_CARLO_STEP = 5;
        public static final int MAX_EGG_NUMBER = 20;
        public static final int MIN_EGG_NUMBER = 2;
        public static final double EGG_KILLING_RATE = .001;
        public static final int MAX_CUCKOO_NUMBERS = 1000;
    }

    public static class SynchronizationOverheadModel {
        public static int SUMMATION_OFL_MAX_BALANCE = 10;
        public static int SYNCHRONIZATION_COST_BALANCE = 50;
        // --
        public static final double INTER_CONTROLLER_SYNC_COEFFICIENT = 0.1;                         // alpha
        public static final double LMAX_COEFFICIENT = 1 - INTER_CONTROLLER_SYNC_COEFFICIENT;        // beta
    }

    public static class Common {
        public static final int PENALTY_COEFFICIENT = 1;                    // gamma
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
        public static final boolean USE_RANDOM_GRAPH = true;
        public static final ModelNoEnum GRAPH_SPEC_MODEL_NO = ModelNoEnum.BUDGET_CONSTRAINED_CONTROLLER_OVERHEAD;
        public static final ModelNoEnum MODEL_NO = ModelNoEnum.BUDGET_CONSTRAINED_CONTROLLER_OVERHEAD;
    }

    public static class ResultInfoConstants {
        public static final String START_TIME = "START_TIME";
        public static final String END_TIME = "END_TIME";
        public static final String POTENTIAL_ENERGY = "POTENTIAL_ENERGY";
        public static final String KINETIC_ENERGY = "KINETIC_ENERGY";
        public static final String LMAX = "LMAX";
        public static final String SUMMATION_OF_LMAX = "SUMMATION_OF_LMAX";
        public static final String SYNC_OVERHEAD_COST = "SYNC_OVERHEAD_COST";
        public static final String SYNC_DELAY_COST = "SYNC_DELAY_COST";
        public static final String RELIABILITY_COST = "RELIABILITY_COST";
        public static final String LOAD_BALANCING_COST = "LOAD_BALANCING_COST";
        public static final String BUDGET_COST_ENERGY = "BUDGET_COST_ENERGY";
    }
}
