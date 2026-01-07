package agh.ics.oop.model;

import java.util.Comparator;
import java.util.Random;

public class Animal {
    private Vector2d position;
    private int energy;
    private int age = 0;
    private int numOfChildren = 0;
    private MapDirection orientation = MapDirection.NORTH;
    private final Genome genome;
    private final SimulationConfig config;

    public Animal(Vector2d position, Genome genome, SimulationConfig config) {
        this.position = position;
        this.genome = genome;
        this.config = config;
        energy = config.energyToChild();
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
        final var pair = validator.toWorld(position, orientation);
        position = pair.first();
        orientation = pair.second();
        age += 1;
        energy -= config.energyLossPreDay();
    }

    public Vector2d getPosition() {
        return position;
    }

    public MapDirection getOrientation() {
        return orientation;
    }

    public static Comparator<Animal> getComparator() {
        return Comparator
                .comparing(Animal::getEnergy).reversed()
                .thenComparing(Animal::getAge)
                .thenComparing(Animal::getNumOfChildren).reversed()
                .thenComparing(Animal::getRandom);
    }

    public int getEnergy() {
        return energy;
    }

    public int getAge() {
        return age;
    }

    public int getNumOfChildren() {
        return numOfChildren;
    }

    public int getRandom() {
        return new Random().nextInt();
    }

    public void consume() {
        energy += config.plantEnergy();
    }

    public Animal tryReproduce(Animal partner) {
        final int needed_energy = config.energyToReproduce() ;
        if (energy < needed_energy || partner.energy < needed_energy)
            return null;

        Animal stronger, weaker;
        if (energy >= partner.energy) {
            stronger = this;
            weaker = partner;
        } else {
            stronger = partner;
            weaker = this;
        }

        final var energyLosses = config.parentsEnergyLoss();
        weaker.energy -= energyLosses.first();
        stronger.energy -= energyLosses.second();

        final var childGenome = new Genome(stronger, weaker, config.minMutations(), config.maxMutations());
        return new Animal(position, childGenome, config);
    }

    public Genome getGenome() {
        return genome;
    }
}
