package agh.ics.oop.model.util;

import java.util.HashSet;
import java.util.Random;

public class RandomSet<T> extends HashSet<T> {
    public RandomSet(int initialCapacity) {
        super(initialCapacity);
    }

    public T removeRandom(Random random) {
        final var arr = stream().toList();
        final int i = random.nextInt(arr.size());
        final T item = arr.get(i);
        remove(item);
        return item;
    }
}
