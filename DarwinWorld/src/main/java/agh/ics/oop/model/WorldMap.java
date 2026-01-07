package agh.ics.oop.model;

import agh.ics.oop.model.util.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorldMap implements MoveValidator {
    private final int width;
    private final int height;
    private final Map<Vector2d, Set<Animal>> animals = new HashMap<>();
    private final Map<Vector2d, Grass> grassMap = new HashMap<>();

    public WorldMap(int width, int height, int numOfGrass) {
        this.width = width;
        this.height = height;
    }

    @Override
    public Pair<Vector2d, MapDirection> toWorld(Vector2d position, MapDirection direction) {
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
            animals.put(position, Set.of(animal));
            return true;
        } else {
            return animals.get(position).add(animal);
        }
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

    public Stream<Animal> getAnimalsFlat() {
        return animals.values().stream().flatMap(Set::stream);
    }

    public Stream<Pair<Vector2d, List<Animal>>> getAnimalsGrouped() {
        return animals
                .entrySet()
                .stream()
                .map(entry -> new Pair<>(
                        entry.getKey(),
                        entry.getValue()
                                .stream()
                                .sorted(Animal.getComparator())
                                .collect(Collectors.toUnmodifiableList()))
                );
    }

    public void growGrass(int amount) {

    }

    public boolean tryConsumeGrass(Vector2d position) {
        return grassMap.remove(position) != null;
    }


//    protected final MapVisualizer visualizer = new MapVisualizer(this);
//
//    private final Id id = Id.generateUUID();
//    private final Map<Vector2d, Animal> animals = new HashMap<>();
//    private final HashSet<MapChangeListener> listeners = new HashSet<>();
//
//    public AbstractWorldMap() {
//        boundingBox = new Boundary(
//            new Vector2d(Integer.MIN_VALUE, Integer.MIN_VALUE),
//            new Vector2d(Integer.MAX_VALUE, Integer.MAX_VALUE)
//        );
//    }
//
//    public AbstractWorldMap(Vector2d lowerLeft, Vector2d upperRight) {
//        boundingBox = new Boundary(lowerLeft, upperRight);
//    }
//
//    @Override
//    public void place(WorldElement element) throws IncorrectPositionException {
//        if (!(element instanceof Animal)) {
//            throw new IllegalArgumentException(element + " is not an instance of Animal class");
//        }
//        final var animal = (Animal)element;
//        final var position = animal.getPosition();
//        final var canMove = canMoveTo(position);
//        if (canMove) {
//            animals.put(position, animal);
//            mapChanged("Animal has been placed on " + position);
//        } else {
//            throw new IncorrectPositionException(position);
//        }
//    }
//
//    @Override
//    public void move(Animal animal, MoveDirection direction) {
//        if (animal.equals(objectAt(animal.getPosition()))) {
//            var oldPosition = animal.getPosition();
//            var oldOrientation = animal.getOrientation();
//
//            animal.move(direction, this);
//
//            var newPosition = animal.getPosition();
//            var newOrientation = animal.getOrientation();
//            if (!newPosition.equals(oldPosition)) {
//                animals.remove(oldPosition, animal);
//                animals.put(newPosition, animal);
//                mapChanged("Animal has moved from " + oldPosition +  " to " + newPosition);
//            } else if (!newOrientation.equals(oldOrientation)) {
//                mapChanged("Animal has changed its orientation from " + oldOrientation + " to " + newOrientation);
//            }
//        }
//    }
//
//    @Override
//    public boolean isOccupied(Vector2d position) {
//        return animals.containsKey(position);
//    }
//
//    @Override
//    public WorldElement objectAt(Vector2d position) {
//        return animals.get(position);
//    }
//
//    @Override
//    public boolean canMoveTo(Vector2d position) {
//        return position.follows(boundingBox.lowerLeft()) && position.precedes(boundingBox.upperRight()) && !isOccupied(position);
//    }
//
//    @Override
//    public String toString() {
//        final var boundingBox = getCurrentBounds();
//        return visualizer.draw(boundingBox.lowerLeft(), boundingBox.upperRight());
//    }
//
//    public List<WorldElement> getElements() {
//        return List.copyOf(animals.values());
//    }
//
//    public abstract Boundary getCurrentBounds();
//
//    public boolean registerListener(MapChangeListener listener) {
//        return listeners.add(listener);
//    }
//
//    public boolean unregisterListener(MapChangeListener listener) {
//        return listeners.remove(listener);
//    }
//
//    protected void mapChanged(String message) {
//        listeners.forEach(listener -> listener.mapChanged(this, message));
//    }
//
//    @Override
//    public Id getId() {
//        return id;
//    }
}
