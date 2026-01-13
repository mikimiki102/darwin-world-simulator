package agh.ics.oop.model;

import agh.ics.oop.model.util.Pair;
import agh.ics.oop.model.util.RandomSet;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorldMap implements MoveValidator {
    protected final int width;
    protected final int height;
    protected final int jungleMinY;
    protected final int jungleMaxY;
    protected final Map<Vector2d, Set<Animal>> animals = new HashMap<>();
    protected final Map<Vector2d, Plant> plantMap = new HashMap<>();
    protected final RandomSet<Vector2d> emptyJungleFields;
    protected final RandomSet<Vector2d> emptyStepFields;

    public WorldMap(int width, int height) {
        this.width = width;
        this.height = height;

        int jungleHeight = (int) Math.max(1, height * 0.2);
        int middle = height / 2;
        jungleMinY = middle - (jungleHeight / 2);
        jungleMaxY = jungleMinY + jungleHeight - 1;

        int mapArea = width * height;
        emptyJungleFields  = new RandomSet<>((int)Math.max(1, mapArea * 0.4));
        emptyStepFields = new RandomSet<>((int)Math.max(1, mapArea * 1.6));
        fillEmptyPlantFields();
    }

    @Override
    public Pair<Vector2d, MapDirection> computePosition(Vector2d position, MapDirection direction) {
        if (position.y() < 0 || position.y() >= height) {
            direction = direction.rotate(4);
            return new Pair<>(position.add(direction.toUnitVector()), direction);
        }
        final int x = (width + position.x()) % width;
        return new Pair<>(new Vector2d(x, position.y()), direction);
    }

    public void move(Animal animal) {
        final var position = animal.getPosition();
        if (!animals.containsKey(position))
            return;

        final var cell = animals.get(position);
        if (!cell.contains(animal))
            return;

        animal.move(this);
        if (position.equals(animal.getPosition()))
            return;

        cell.remove(animal);
        if (cell.isEmpty()) {
            animals.remove(position);
        }
        place(animal);
    }

    public boolean place(Animal animal) {
        final var position = animal.getPosition();
        if (!animals.containsKey(position)) {
            final var set = new HashSet<Animal>(4);
            animals.put(position, set);
        }
        return animals.get(position).add(animal);

    }

    public boolean remove(Animal animal) {
        final var position = animal.getPosition();
        final var cell = animals.get(position);
        final var result = cell.remove(animal);
        if (cell.isEmpty()) {
            animals.remove(position);
        }
        return result;
    }

    public boolean place(Plant plant) {
        final var position = plant.getPosition();
        if (plantMap.containsKey(position))
            return false;
        plantMap.put(position, plant);
        return true;
    }

    public List<Animal> getAnimalsFlat() {
        return animals.values().stream().flatMap(Set::stream).toList();
    }

    public List<Pair<Vector2d, List<Animal>>> getAnimalsGroupedNSorted() {
        return animals
                .entrySet()
                .stream()
                .map(entry -> new Pair<>(
                        entry.getKey(),
                        entry.getValue()
                                .stream()
                                .sorted(Animal.getComparator())
                                .collect(Collectors.toUnmodifiableList()))
                ).toList();
    }

    public List<WorldElement> getWorldElements() {
        return Stream.concat(
                animals.values().stream().flatMap(Set::stream),
                plantMap.values().stream())
                .toList();
    }

    private void fillEmptyPlantFields() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < jungleMinY; y++) {
                emptyStepFields.add(new Vector2d(x, y));
            }
            for (int y = jungleMinY; y <= jungleMaxY; y++) {
                emptyJungleFields.add(new Vector2d(x, y));
            }
            for (int y = jungleMaxY + 1; y < height; y++) {
                emptyStepFields.add(new Vector2d(x, y));
            }
        }
    }

    private Vector2d getRandomPlantPosition() {
        final var random = ThreadLocalRandom.current();
        if (emptyJungleFields.isEmpty()) {
            return emptyStepFields.removeRandom(random);
        }
        if (emptyStepFields.isEmpty()) {
            return emptyJungleFields.removeRandom(random);
        }

        int diceRoll = random.nextInt(100);
        if (diceRoll < 80) {
            return emptyJungleFields.removeRandom(random);
        }
        return emptyStepFields.removeRandom(random);
    }

    public void growPlants(int count) {
        final int availableCount = emptyJungleFields.size() + emptyStepFields.size();
        for (int i = 0; i < Math.min(count, availableCount); i++) {
            final var position = getRandomPlantPosition();
            plantMap.put(position, new Plant(position));
        }
    }

    private boolean isJungle(Vector2d position) {
        return position.y() >= jungleMinY && position.y() <= jungleMaxY;
    }

    public boolean tryConsumePlant(Vector2d position) {
        boolean wasConsumed = plantMap.remove(position) != null;
        if (wasConsumed) {
            if (isJungle(position)) {
                emptyJungleFields.add(position);
            } else {
                emptyStepFields.add(position);
            }
        }
        return wasConsumed;
    }

    public boolean hasPlantAt(Vector2d position) {
        return plantMap.containsKey(position);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isAnimalAt(Vector2d position) {
        return animals.containsKey(position) && !animals.get(position).isEmpty();
    }

    public Collection<Plant> getPlants() {
        return plantMap.values();
    }
}
