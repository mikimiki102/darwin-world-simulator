package agh.ics.oop.stats;

import agh.ics.oop.model.Animal;
import agh.ics.oop.model.WorldMap;

import java.util.*;
import java.util.stream.Collectors;

public final class WorldStats {

    private final int width;
    private final int height;
    private long deadCount = 0;
    private long deadLifespanSum = 0;
    private int day = 0;
    private int animals = 0;
    private int plants = 0;
    private int freeFields = 0;
    private double avgEnergyOfLiving = 0.0;
    private double avgChildrenOfLiving = 0.0;
    private List<int[]> mostPopularGenotypes = List.of();
    public WorldStats(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void onAnimalsDied(Collection<Animal> dead, int currentDay) {
        for (var a : dead) {
            deadCount++;
            deadLifespanSum += Math.max(0, currentDay - a.getBirthDay());
        }
    }

    public void recompute(WorldMap map, int currentDay) {
        this.day = currentDay;

        var living = map.getAnimalsFlat();
        animals = living.size();
        plants = map.getPlants().size();

        int animalCells = map.getAnimalsGroupedNSorted().size();
        int occupied = animalCells + plants;
        freeFields = Math.max(0, width * height - occupied);

        long sumE = 0, sumKids = 0;
        for (var a : living) {
            sumE += a.getEnergy();
            sumKids += a.getChildrenCount();
        }
        avgEnergyOfLiving = animals == 0 ? 0.0 : (double) sumE / animals;
        avgChildrenOfLiving = animals == 0 ? 0.0 : (double) sumKids / animals;

        Map<String, Integer> counts = new HashMap<>();
        Map<String, int[]> repr = new HashMap<>();
        for (var a : living) {
            int[] genes = a.getGenome().toArray();
            String key = Arrays.toString(genes);
            counts.merge(key, 1, Integer::sum);
            repr.putIfAbsent(key, genes);
        }
        mostPopularGenotypes = counts.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .limit(3)
                .map(e -> repr.get(e.getKey()))
                .collect(Collectors.toList());
    }

    public Snapshot snapshot() {
        double avgLife = deadCount == 0 ? 0.0 : (double) deadLifespanSum / deadCount;
        return new Snapshot(day, animals, plants, freeFields, mostPopularGenotypes,
                avgEnergyOfLiving, avgLife, avgChildrenOfLiving);
    }

    public record Snapshot(
            int day,
            int animals,
            int plants,
            int freeFields,
            List<int[]> mostPopularGenotypes,
            double avgEnergyOfLiving,
            double avgLifespanOfDead,
            double avgChildrenOfLiving
    ) {
    }
}
