package agh.ics.oop.model;

import agh.ics.oop.model.util.Pair;
import agh.ics.oop.model.util.RandomSet;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorldMap implements MoveValidator {
    private final int width;
    private final int height;
    private final Map<Vector2d, Set<Animal>> animals = new HashMap<>();
    private final Map<Vector2d, Plant> plantMap = new HashMap<>();
    private final PlantGenerator plantGenerator;

    public WorldMap(int width, int height) {
        this.width = width;
        this.height = height;
        plantGenerator = new PlantGenerator(width, height);
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

    public boolean tryConsumePlant(Vector2d position) {
        boolean wasConsumed = plantMap.remove(position) != null;
        if (wasConsumed) {
            plantGenerator.freePosition(position);
        }
        return wasConsumed;
    }

    public void growPlants(int count) {
        count = Math.min(count, plantGenerator.getFreeCount());
        for (int i = 0; i < count; i++) {
            final var position = plantGenerator.getPosition();
            plantMap.put(position, new Plant(position));
        }
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
