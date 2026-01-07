package agh.ics.oop.model;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;

public class Animal implements Entity {
    private Vector2d position;
    private int energy;
    private final int generation;
    private int numOfChildren = 0;
    private MapDirection orientation = MapDirection.NORTH;
    private Iterator<Integer> gens;

    public Animal(int generation) {
        position = new Vector2d(2, 2);
        this.generation = generation;
    }

    public Animal(Vector2d position, int generation) {
        this.position = position;
        this.generation = generation;
    }

    @Override
    public String toString() {
        return orientation.toString();
    }

    public boolean isDead() {
        return energy <= 0;
    }

    public void move(MoveValidator validator) {
        orientation = orientation.rotate(gens.next());
        position = position.add(orientation.toUnitVector());
        final var pair = validator.toWorld(position, orientation);
        position = pair.first();
        orientation = pair.second();
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
                .thenComparing(Animal::getGeneration)
                .thenComparing(Animal::getNumOfChildren).reversed()
                .thenComparing(Animal::getRandom);
    }

    public int getEnergy() {
        return energy;
    }

    public int getGeneration() {
        return generation;
    }

    public int getNumOfChildren() {
        return numOfChildren;
    }

    public int getRandom() {
        return new Random().nextInt();
    }

    public void consumeGrass(int grassEnergy) {
        energy += grassEnergy;
    }
}
