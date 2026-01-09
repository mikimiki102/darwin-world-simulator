package agh.ics.oop.model.util;

import agh.ics.oop.model.Vector2d;

import java.util.Iterator;
import java.util.Random;

public class RandomPositionGenerator implements Iterable<Vector2d> {
    private int count = 0;
    private int bound = 0;
    private final Random random;

    public RandomPositionGenerator(int n, Random random) {
        // TODO: rework - needs to generate position based on width and height
        final var densityFactor = 10;
        if (n > 0) {
            this.count = n;
            bound = (int) Math.ceil(Math.sqrt((long) n * densityFactor));
        }
        this.random = random;
    }

    public RandomPositionGenerator(int n) {
        this(n, new Random(0));
    }

    @Override
    public Iterator<Vector2d> iterator() {
        return new RandomPositionIterator(count, bound, random);
    }
}
