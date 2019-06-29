package algorithms.simulated_annealing;

public class SAPlainOldData {
    public float temperature;                    // T0
    public final float temperatureInitial;         // T1
    public final float temperatureFinal;         // T1
    public final float temperatureCoolingRate;
    public final int monteCarloSteps;
    public double prevEnergy;

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
    }
}
