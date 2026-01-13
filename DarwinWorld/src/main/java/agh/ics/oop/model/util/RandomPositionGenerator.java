package agh.ics.oop.model.util;

import agh.ics.oop.model.Vector2d;

import java.util.Iterator;
import java.util.Random;

public class RandomPositionGenerator implements Iterable<Vector2d> {
    private final int count;
    private final int width;
    private final int height;
    private final Random random;

    public RandomPositionGenerator(int n, int width, int height, Random random) {
        this.count = Math.max(0, n);
        this.width = width;
        this.height = height;
        this.random = (random == null) ? new Random(0) : random;
    }

    @Override
    public Iterator<Vector2d> iterator() {
        return new RandomPositionIterator(count, width, height, random);
    }
}
