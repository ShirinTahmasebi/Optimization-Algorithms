package base_algorithms.quantum_annealing;

import javafx.util.Pair;

public class QAPlainOldData {

    public final int trotterReplicas;   // P
    public final float temperatureQuantum;    // TQ
    public float temperature;                        // T
    public final float temperatureInitial;           // T
    public final int monteCarloSteps;
    public float tunnelingField;
    public final float tunnelingFieldInitial;
    public final float tunnelingFiledFinal;
    public final float tunnelingFiledEvaporation;
    public final float coolingRate = .7f;

    public Pair<Double, Double> prevEnergyPair;

    public QAPlainOldData(
            int trotterReplicas,
            float temperature,
            int monteCarloSteps,
            float tunnelingFieldInitial,
            float tunnelingFieldFinal,
            float tunnelingFieldEvaporation
    ) {
        this.trotterReplicas = trotterReplicas;
        this.temperatureQuantum = temperature;
        this.temperature = temperature;
        this.temperatureInitial = temperature;
        this.monteCarloSteps = monteCarloSteps;
        this.tunnelingField = tunnelingFieldInitial;
        this.tunnelingFieldInitial = tunnelingFieldInitial;
        this.tunnelingFiledFinal = tunnelingFieldFinal;
        this.tunnelingFiledEvaporation = tunnelingFieldEvaporation;
    }
}
