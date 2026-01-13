package agh.ics.oop.model;

import agh.ics.oop.model.util.Pair;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Animal implements WorldElement {
    private static int NEXT_ID = 1;
    private final int id;

    private Vector2d position;
    private MapDirection orientation = MapDirection.NORTH;
    private final Genome genome;
    private int energy;
    private int birthday;
    private final List<Animal> children = new ArrayList<>();

    public static class Reproducer {
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
            if (!animal1.canReproduce(neededEnergy) || !animal2.canReproduce(neededEnergy))
                return Optional.empty();
            final var childGenome = new Genome(animal1, animal2, minMutations, maxMutations);
            final var child = new Animal(animal1.getPosition(), childGenome, energyToChild, day);
            animal1.loseEnergy(parentEnergyLoss.first());
            animal2.loseEnergy(parentEnergyLoss.second());
            animal1.addChild(child);
            animal2.addChild(child);
            return Optional.of(child);
        }
    }

    public Animal(Vector2d position, Genome genome, int energy, int birthday) {
        this.id = NEXT_ID++;
        this.position = position;
        this.genome = genome;
        this.energy = energy;
        this.birthday = birthday;
    }

    public int getId() { return id; }

    @Override
    public Vector2d getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return orientation.toString();
    }

    public boolean isDead() {
        return energy <= 0;
    }

    public void move(MoveValidator validator) {
        orientation = orientation.rotate(genome.getNext());
        position = position.add(orientation.toUnitVector());
        final var pair = validator.computePosition(position, orientation);
        position = pair.first();
        orientation = pair.second();
    }

    public MapDirection getOrientation() {
        return orientation;
    }

    public static Comparator<Animal> getComparator() {
        return Comparator
                .comparingInt(Animal::getEnergy).reversed()
                .thenComparingInt(Animal::getBirthday)
                .thenComparingInt(Animal::getChildrenCount).reversed()
                .thenComparingInt(Animal::getRandom);
    }

    public int getEnergy() {
        return energy;
    }

    public void loseEnergy(int amount) {
        energy -= amount;
    }

    public int getBirthday() {
        return birthday;
    }
    
    private int getRandom() {
        return ThreadLocalRandom.current().nextInt();
    }

    public void increaseEnergy(int amount) {
        energy += amount;
    }

    public boolean canReproduce(int energyNeeded) {
        return energy >= energyNeeded;
    }

    public Genome getGenome() {
        return genome;
    }

    public int getBirthDay() {
        return getBirthday();
    }

    public int getChildrenCount() {
        return children.size();
    }

    public void addChild(Animal child) {
        children.add(child);
    }
}
