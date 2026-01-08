package agh.ics.oop;

import agh.ics.oop.model.*;
import agh.ics.oop.model.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Simulation implements Runnable {
    public record Config(
            int width,
            int height,

            int startPlantCount,
            int plantsPerDay,

            int startAnimalCount,
            int startAnimalEnergy,

            int energyToReproduce,
            int energyLossPreDay,
            int energyToChild,
            int minMutations,
            int maxMutations,
            int plantEnergy,
            int genomeLength
    ) { }

    private final Simulation.Config config;
    protected final WorldMap worldMap;
    private ScheduledExecutorService executor;
    private int day = 0;
    private final Animal.Reproducer reproducer;

    public Simulation(Simulation.Config config, WorldMap worldMap) {
        this.config = config;
        this.worldMap = worldMap;
        worldMap.growPlants(config.plantsPerDay());
        reproducer = new Animal.Reproducer(
                config.energyToReproduce(),
                config.energyToChild(),
                config.minMutations(),
                config.maxMutations()
        );
    }

    @Override
    public void run() {
        day += 1;
        removeDead();
        move();
        consume();
        reproduce();
        growPlants();
    }

    public void start() {
        if (isRunning())
            throw new IllegalStateException("Cannot start already started Simulation");
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(this, 1, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        try {
            executor.shutdown();
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt(); // Pass interrupt to parent thread
        }
    }

    private boolean isRunning() {
        return executor != null && !executor.isShutdown();
    }

    private void populate() {
        new RandomPositionGenerator(config.startAnimalCount, ThreadLocalRandom.current());
    }

    private void removeDead() {
        worldMap.getAnimalsFlat()
                .stream()
                .filter(Animal::isDead)
                .forEach(worldMap::remove);
    }

    private void move() {
        worldMap.getAnimalsFlat().forEach(animal -> {
            worldMap.move(animal);
            animal.loseEnergy(config.energyLossPreDay());
        });
    }

    protected void singleConsume(Pair<Vector2d, List<Animal>> cell) {
        if (worldMap.tryConsumePlant(cell.first())) {
            cell.second().getFirst().increaseEnergy(config.plantEnergy());
        }
    }

    private void consume() {
        worldMap.getAnimalsGroupedNSorted().forEach(this::singleConsume);
    }

    private void reproduce() {
        final var newborns = new ArrayList<Animal>();
        worldMap.getAnimalsGroupedNSorted().forEach(cell -> {
            final var animals = cell.second();
            for (int i = 1; i < animals.size(); i += 2) {
                final var child = reproducer.tryReproduce(animals.get(i - 1), animals.get(i), day);
                if (child.isEmpty())
                    break;
                newborns.add(child.get());
            }
        });
        newborns.forEach(worldMap::place);
    }

    private void growPlants() {
        worldMap.growPlants(config.plantsPerDay());
    }
}
