package agh.ics.oop.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

public class Genome {
    private final int[] genes;
    private int currentIndex;

    public Genome(int length) {
        this.genes = new int[length];
        final var random = new ThreadLocalRandom.current();
        for (int i = 0; i < length; i++) {
            this.genes[i] = random.nextInt(MapDirection.values().length);
        }
        this.currentIndex = random.nextInt(length);
    }

    public Genome(Animal stronger, Animal weaker, int minMutations, int maxMutations) {
        int length = stronger.getGenome().genes.length;
        this.genes = new int[length];
        final var random = new ThreadLocalRandom.current();

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
        this.currentIndex = random.nextInt(length);
    }

    private void applyMutations(int min, int max) {
        final var random = new ThreadLocalRandom.current();
        int numberOfMutations = random.nextInt(max - min + 1) + min;

        final var indices = new ArrayList<Integer>();
        for (int i = 0; i < genes.length; i++) indices.add(i);
        Collections.shuffle(indices);

        for (int i = 0; i < numberOfMutations; i++) {
            int idxToMutate = indices.get(i);
            this.genes[idxToMutate] = random.nextInt(MapDirection.values().length);
        }
    }

    public int getNext() {
        final int gen = genes[currentIndex];
        currentIndex = (currentIndex + 1) % genes.length;
        return gen;
    }
}
