package agh.ics.oop.model;

import agh.ics.oop.model.util.RandomSet;

import java.util.concurrent.ThreadLocalRandom;

public class PlantGenerator {
    private final int width;
    private final int height;
    private final int jungleMinY;
    private final int jungleMaxY;
    private final RandomSet<Vector2d> freeJungleFields;
    private final RandomSet<Vector2d> freeStepFields;

    public PlantGenerator(int width, int height) {
        this.width = width;
        this.height = height;
        int jungleHeight = (int) Math.max(1, height * 0.2);
        int middle = height / 2;
        jungleMinY = middle - (jungleHeight / 2);
        jungleMaxY = jungleMinY + jungleHeight - 1;

        int mapArea = width * height;
        freeJungleFields = new RandomSet<>((int) Math.max(1, mapArea * 0.4));
        freeStepFields = new RandomSet<>((int) Math.max(1, mapArea * 1.6));
        freeAll();
    }

    private void freeAll() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < jungleMinY; y++) freeStepFields.add(new Vector2d(x, y));
            for (int y = jungleMinY; y <= jungleMaxY; y++) freeJungleFields.add(new Vector2d(x, y));
            for (int y = jungleMaxY + 1; y < height; y++) freeStepFields.add(new Vector2d(x, y));
        }
    }

    public Vector2d getPosition() {
        final var random = ThreadLocalRandom.current();
        if (freeJungleFields.isEmpty()) return freeStepFields.removeRandom(random);
        if (freeStepFields.isEmpty()) return freeJungleFields.removeRandom(random);
        if (random.nextInt(100) < 80) return freeJungleFields.removeRandom(random);
        return freeStepFields.removeRandom(random);
    }

    public void freePosition(Vector2d position) {
        if (isJungle(position)) freeJungleFields.add(position);
        else freeStepFields.add(position);
    }

    public void occupyPosition(Vector2d position) {
        if (isJungle(position)) freeJungleFields.remove(position);
        else freeStepFields.remove(position);
    }

    public boolean isJungle(Vector2d position) {
        return position.y() >= jungleMinY && position.y() <= jungleMaxY;
    }

    public int getFreeCount() {
        return freeJungleFields.size() + freeStepFields.size();
    }
}
