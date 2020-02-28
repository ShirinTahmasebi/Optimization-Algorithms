package base_algorithms.simulated_annealing;

import base_algorithms.Cost;

public class SAPlainOldData {
    public float temperature;                    // T0
    public final float temperatureInitial;         // T1
    public final float temperatureFinal;         // T1
    public final float temperatureCoolingRate;
    public final int monteCarloSteps;
    public Cost prevEnergy;

    public SAPlainOldData(
            float temperatureInitial,
            float temperatureFinal,
            float temperatureCoolingRate,
            int monteCarloSteps) {
        this.temperature = temperatureInitial;
        this.temperatureInitial = temperatureInitial;
        this.temperatureFinal = temperatureFinal;
        this.temperatureCoolingRate = temperatureCoolingRate;
        this.monteCarloSteps = monteCarloSteps;
        prevEnergy = new Cost()
                .setBudgetCostEnergy(Integer.MAX_VALUE)
                .setKineticEnergy(Integer.MAX_VALUE)
                .setLmaxCost(Integer.MAX_VALUE)
                .setSummationOfLMaxCost(Integer.MAX_VALUE)
                .setSynchronizationCost(Integer.MAX_VALUE)
                .setLoadBalancingCost(Integer.MAX_VALUE)
                .setReliabilityCost(Integer.MAX_VALUE);
    }
}
