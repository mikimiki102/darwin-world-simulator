package agh.ics.oop;

import agh.ics.oop.model.*;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Simulation implements Runnable {
    protected final WorldMap worldMap;
    protected final SimulationConfig config;
    private ScheduledExecutorService executor;

    public Simulation(WorldMap worldMap, SimulationConfig config) {
        this.worldMap = worldMap;
        this.config = config;
    }

    @Override
    public void run() {
        removeDead();
        move();
        consume();
        reproduce();
        processPlants();
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

    private void removeDead() {
        worldMap.getAnimalsFlat()
                .stream()
                .filter(Animal::isDead)
                .forEach(worldMap::remove);
    }

    private void move() {
        worldMap.getAnimalsFlat().forEach(worldMap::move);
    }

    protected void consume() {
        worldMap.getAnimalsGroupedNSorted().forEach(cell -> {
            if (worldMap.tryConsumePlant(cell.first())) {
                cell.second().getFirst().consume();
            }
        });
    }

    private void reproduce() {
        final var newborns = new ArrayList<Animal>();
        worldMap.getAnimalsGroupedNSorted().forEach(cell -> {
            final var animals = cell.second();
            for (int i = 1; i < animals.size(); i += 2) {
                final var animal1 = animals.get(i - 1);
                final var animal2 = animals.get(i);
                final var child = animal1.tryReproduce(animal2);
                if (child == null) break;
                newborns.add(child);
            }
        });
        newborns.forEach(worldMap::place);
    }

    private void processPlants() {
        worldMap.processPlants();
    }
}
