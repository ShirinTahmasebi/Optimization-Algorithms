package main;

public class Parameters {

    public static class QuantumAnnealing {
        public static final float COST_REDUCTION_FACTOR = 0.75f;
        public static final int TROTTER_REPLICAS = 50;     // P
        public static final float TEMPERATURE = 100f;         // T
        public static final int MONTE_CARLO_STEP = 100;   // M
        public static final float TUNNLING_FIELD_INITIAL = 1f;
        public static final float TUNNLING_FIELD_FINAL = .5f;
        public static final float TUNNLING_FIELD_EVAPORATION = .95f;
    }

    public static class SimulatedAnnealing {
        public static final float COST_REDUCTION_FACTOR = 0.75f;
        public static final float TEMPERATURE_INITIAL = 100;              // T Initial
        public static final float TEMPERATURE_FINAL = 1;                // T Final
        public static final float TEMPERATURE_COOLING_RATE = .75f;         // T Cooling Rate
        public static final int MONTE_CARLO_STEP = 50;   // M
    }

    public static class Cuckoo {

    }

    public static class Common {
        public static final int SINK_LOAD = 10;            // w
        public static final int CONTROLLER_LOAD = 10;      // wPrime
        public static final int SENSOR_SINK_MAX_DISTANCE = 3;              // Lmax
        public static final int SENSOR_CONTROLLER_MAX_DISTANCE = 2;        // LPrimeMax
        public static final int GRAPH_SIZE = 3;
    }
}
