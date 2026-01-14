package agh.ics.oop.model;

import agh.ics.oop.model.util.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorldMap implements MoveValidator {
    private final int width;
    private final int height;
    private final Map<Vector2d, Set<Animal>> animals = new HashMap<>();
    private final Map<Vector2d, Plant> plantMap = new HashMap<>();
    private PlantGenerator plantGenerator;

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
        if (!animals.containsKey(position)) return;

        final var cell = animals.get(position);
        if (!cell.contains(animal)) return;

        animal.move(this);
        if (position.equals(animal.getPosition())) return;

        cell.remove(animal);
        if (cell.isEmpty()) animals.remove(position);
        place(animal);
    }

    public boolean place(Animal animal) {
        final var position = animal.getPosition();
        animals.computeIfAbsent(position, k -> new HashSet<>(4));
        return animals.get(position).add(animal);
    }

    public boolean remove(Animal animal) {
        final var position = animal.getPosition();
        final var cell = animals.get(position);
        if (cell == null) return false;
        final var result = cell.remove(animal);
        if (cell.isEmpty()) animals.remove(position);
        return result;
    }

    public boolean place(Plant plant) {
        final var position = plant.getPosition();
        if (plantMap.containsKey(position)) return false;
        plantMap.put(position, plant);
        plantGenerator.occupyPosition(position);
        return true;
    }

    public boolean tryConsumePlant(Vector2d position) {
        boolean wasConsumed = plantMap.remove(position) != null;
        if (wasConsumed) plantGenerator.freePosition(position);
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
        return animals.entrySet().stream()
                .map(entry -> new Pair<>(
                        entry.getKey(),
                        entry.getValue().stream()
                                .sorted(Animal.getComparator())
                                .collect(Collectors.toUnmodifiableList())
                )).toList();
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

    public record Snapshot(int width, int height, List<AnimalSnap> animals, List<PlantSnap> plants) {
        public static Snapshot from(WorldMap map) {
            var a = map.getAnimalsFlat().stream().map(AnimalSnap::from).toList();
            var p = map.plantMap.values().stream().map(PlantSnap::from).toList();
            return new Snapshot(map.width, map.height, a, p);
        }

        public static WorldMap toWorldMap(Snapshot s) {
            var m = new WorldMap(s.width, s.height);
            m.animals.clear();
            m.plantMap.clear();
            m.plantGenerator = new PlantGenerator(s.width, s.height);

            int maxId = 0;

            for (var ps : s.plants) {
                Plant pl = ps.onFire ? new PlantOnFire(ps.pos) : new Plant(ps.pos);
                m.plantMap.put(ps.pos, pl);
                m.plantGenerator.occupyPosition(ps.pos);
            }

            for (var as : s.animals) {
                maxId = Math.max(maxId, as.id);
                var g = new Genome(as.genes, as.genomeIndex);
                var an = new Animal(as.id, as.pos, g, as.energy, as.birthday, as.orientation, as.childrenCount);
                m.place(an);
            }

            Animal.ensureNextIdAtLeast(maxId + 1);
            return m;
        }
    }

    public record AnimalSnap(
            int id,
            Vector2d pos,
            MapDirection orientation,
            int energy,
            int birthday,
            int childrenCount,
            int[] genes,
            int genomeIndex
    ) {
        static AnimalSnap from(Animal a) {
            return new AnimalSnap(
                    a.getId(),
                    a.getPosition(),
                    a.getOrientation(),
                    a.getEnergy(),
                    a.getBirthday(),
                    a.getChildrenCount(),
                    a.getGenome().toArray(),
                    a.getGenome().getCurrentIndex()
            );
        }
    }

    public record PlantSnap(Vector2d pos, boolean onFire) {
        static PlantSnap from(Plant p) {
            return new PlantSnap(p.getPosition(), p instanceof PlantOnFire);
        }
    }
}
