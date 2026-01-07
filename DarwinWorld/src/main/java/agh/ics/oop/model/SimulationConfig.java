package agh.ics.oop.model;

import agh.ics.oop.model.util.Pair;

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
    Pair<Integer, Integer> parentsEnergyLoss() {
        int half = energyToChild / 2;
        return new Pair(half, half + energyToChild % 2);
    }
}