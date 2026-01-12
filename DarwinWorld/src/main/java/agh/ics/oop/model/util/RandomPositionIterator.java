package agh.ics.oop.model.util;

import agh.ics.oop.model.Vector2d;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

public class RandomPositionIterator implements Iterator<Vector2d> {
    private final int count;
    private final int width;
    private final int height;
    private final Random random;
    private int produced = 0;

    public RandomPositionIterator(int count, int width, int height, Random random) {
        if (width <= 0 || height <= 0) throw new IllegalArgumentException("width/height <= 0");
        this.count = Math.max(0, count);
        this.width = width;
        this.height = height;
        this.random = (random == null) ? new Random(0) : random;
    }

    @Override
    public boolean hasNext() {
        return produced < count;
    }

    @Override
    public Vector2d next() {
        if (!hasNext()) throw new NoSuchElementException();
        produced++;
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        return new Vector2d(x, y);
    }
}
