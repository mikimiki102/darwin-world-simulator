package agh.ics.oop.simulation;

public record FireSimulationConfig(
        SimulationConfig basicConfig,
        int fireChance,
        int fireDuration,
        int fireEnergyLoss
) {
    public static final FireSimulationConfig DEFAULT = new FireSimulationConfig(
            SimulationConfig.DEFAULT, 5, 3, 2
    );
}