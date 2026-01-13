package agh.ics.oop.simulation;

public record SimulationConfig(
        int width,
        int height,
        int startPlantCount,
        int plantsPerDay,
        int startAnimalCount,
        int startAnimalEnergy,
        int energyToReproduce,
        int energyLossPreDay,
        int energyToChild,
        int minMutations,
        int maxMutations,
        int plantEnergy,
        int genomeLength
) {
    public static final SimulationConfig DEFAULT = new SimulationConfig(
            50, 30, 120, 10, 20, 20, 30, 1, 20, 0, 0, 10, 8
    );
}
