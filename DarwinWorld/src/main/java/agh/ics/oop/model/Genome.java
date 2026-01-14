package agh.ics.oop.model;

import agh.ics.oop.model.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class Genome {
    private final Optional<Pair<Animal, Animal>> parents;
    final int[] genes;
    private int currentIndex;

    public Genome(int length) {
        parents = Optional.empty();
        genes = new int[length];
        final var random = ThreadLocalRandom.current();
        for (int i = 0; i < length; i++) genes[i] = random.nextInt(MapDirection.values().length);
        currentIndex = random.nextInt(length);
    }

    public Genome(int[] genes, int currentIndex) {
        this.parents = Optional.empty();
        this.genes = Arrays.copyOf(genes, genes.length);
        this.currentIndex = Math.floorMod(currentIndex, genes.length);
    }

    public Genome(Animal animal1, Animal animal2, int minMutations, int maxMutations) {
        final var sorted = sortByStronger(animal1, animal2);
        final var stronger = sorted.first();
        final var weaker = sorted.second();
        parents = Optional.of(sorted);

        int length = stronger.getGenome().genes.length;
        genes = new int[length];

        final var random = ThreadLocalRandom.current();
        double energySum = stronger.getEnergy() + weaker.getEnergy();
        int splitPoint = (int) Math.round((double) stronger.getEnergy() / energySum * length);
        boolean strongerOnLeft = random.nextBoolean();

        if (strongerOnLeft) {
            System.arraycopy(stronger.getGenome().genes, 0, this.genes, 0, splitPoint);
            System.arraycopy(weaker.getGenome().genes, splitPoint, this.genes, splitPoint, length - splitPoint);
        } else {
            System.arraycopy(weaker.getGenome().genes, 0, this.genes, 0, length - splitPoint);
            System.arraycopy(stronger.getGenome().genes, length - splitPoint, this.genes, length - splitPoint, splitPoint);
        }

        applyMutations(minMutations, maxMutations);
        currentIndex = random.nextInt(length);
    }

    private Pair<Animal, Animal> sortByStronger(Animal animal1, Animal animal2) {
        if (animal1.getEnergy() >= animal2.getEnergy()) return new Pair<>(animal1, animal2);
        return new Pair<>(animal2, animal1);
    }

    private void applyMutations(int min, int max) {
        final var random = ThreadLocalRandom.current();
        int numberOfMutations = random.nextInt(max - min + 1) + min;

        final var indices = new ArrayList<Integer>();
        for (int i = 0; i < genes.length; i++) indices.add(i);
        Collections.shuffle(indices);

        for (int i = 0; i < numberOfMutations; i++) {
            int idxToMutate = indices.get(i);
            genes[idxToMutate] = random.nextInt(MapDirection.values().length);
        }
    }

    public int getNext() {
        final int gen = genes[currentIndex];
        currentIndex = (currentIndex + 1) % genes.length;
        return gen;
    }

    public int[] toArray() {
        return Arrays.copyOf(genes, genes.length);
    }

    public Optional<Pair<Animal, Animal>> getParents() {
        return parents;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }
}
