package agh.ics.oop.model;

import agh.ics.oop.model.util.Pair;

import java.util.Optional;

public class Reproducer {
    private final int neededEnergy;
    private final int energyToChild;
    private final Pair<Integer, Integer> parentEnergyLoss;
    private final int minMutations;
    private final int maxMutations;

    public Reproducer(int neededEnergy, int energyToChild, int minMutations, int maxMutations) {
        this.neededEnergy = neededEnergy;
        this.energyToChild = energyToChild;
        this.parentEnergyLoss = new Pair<>(energyToChild - energyToChild / 2, energyToChild / 2);
        this.minMutations = minMutations;
        this.maxMutations = maxMutations;
    }

    public Optional<Animal> tryReproduce(Animal animal1, Animal animal2, int day) {
        if (!animal1.canReproduce(neededEnergy) || !animal2.canReproduce(neededEnergy)) return Optional.empty();
        final var childGenome = new Genome(animal1, animal2, minMutations, maxMutations);
        final var child = new Animal(animal1.getPosition(), childGenome, energyToChild, day);
        animal1.loseEnergy(parentEnergyLoss.first());
        animal2.loseEnergy(parentEnergyLoss.second());
        animal1.addChild(child);
        animal2.addChild(child);
        return Optional.of(child);
    }
}
