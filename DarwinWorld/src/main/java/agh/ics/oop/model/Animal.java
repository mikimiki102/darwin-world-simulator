package agh.ics.oop.model;

import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

public class Animal implements WorldElement {
    private static int NEXT_ID = 1;

    private final int id;
    private final Genome genome;
    private int childrenCount;
    private Vector2d position;
    private MapDirection orientation;
    private int energy;
    private final int birthday;

    public Animal(Vector2d position, Genome genome, int energy, int birthday) {
        this(NEXT_ID++, position, genome, energy, birthday, MapDirection.NORTH, 0);
    }

    public Animal(int id, Vector2d position, Genome genome, int energy, int birthday, MapDirection orientation, int childrenCount) {
        this.id = id;
        this.position = position;
        this.genome = genome;
        this.energy = energy;
        this.birthday = birthday;
        this.orientation = orientation;
        this.childrenCount = childrenCount;
    }

    public static void ensureNextIdAtLeast(int value) {
        if (NEXT_ID < value) NEXT_ID = value;
    }

    public static Comparator<Animal> getComparator() {
        return Comparator
                .comparingInt(Animal::getEnergy).reversed()
                .thenComparingInt(Animal::getBirthday)
                .thenComparingInt(Animal::getChildrenCount).reversed()
                .thenComparingInt(Animal::getRandom);
    }

    public int getId() {
        return id;
    }

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
        return childrenCount;
    }

    public void addChild(Animal child) {
        childrenCount++;
    }
}
