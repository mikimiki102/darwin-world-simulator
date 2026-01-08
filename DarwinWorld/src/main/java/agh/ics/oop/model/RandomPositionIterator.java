package agh.ics.oop.model;

import java.util.*;

public class RandomPositionIterator implements Iterator<Vector2d> {
    private final int count;
    private final int bound;
    private final int total;
    private final Random random;
    private final Set<Integer> used;
    private int i = 0;

    public RandomPositionIterator(int count, int bound, Random random) {
        this.count = count;
        this.bound = bound;
        this.total = bound * bound;
        this.random = random;
        this.used = new HashSet<>(count * 2);
    }

    @Override
    public boolean hasNext() {
        return i < count;
    }

    @Override
    public Vector2d next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        int t = random.nextInt(count, total + 1);
        int idx = t;
        if (!used.add(t)) {
            idx = i;
        }
        i++;
        int x = idx % bound;
        int y = idx / bound;
        return new Vector2d(x, y);
    }
}
