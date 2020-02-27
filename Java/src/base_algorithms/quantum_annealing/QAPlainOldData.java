package base_algorithms.quantum_annealing;

import base_algorithms.Cost;

public class QAPlainOldData {

    public final int trotterReplicas;                   // P
    public final float temperatureQuantum;              // TQ
    public float temperature;                           // T
    public final float temperatureInitial;              // T
    public final int monteCarloSteps;
    public float tunnelingField;
    public final float tunnelingFieldInitial;
    public final float tunnelingFiledFinal;
    public final float tunnelingFiledEvaporation;
    public final float coolingRate = .7f;

    public Cost prevEnergyPair;

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
        prevEnergyPair = new Cost()
                .setBudgetCostEnergy(Integer.MAX_VALUE)
                .setKineticEnergy(Integer.MAX_VALUE)
                .setLmaxCost(Integer.MAX_VALUE)
                .setSummationOfLMaxCost(Integer.MAX_VALUE)
                .setSynchronizationOverheadCost(Integer.MAX_VALUE)
                .setSynchronizationDelayCost(Integer.MAX_VALUE)
                .setLoadBalancingCost(Integer.MAX_VALUE)
                .setReliabilityCost(Integer.MAX_VALUE);
    }
}
