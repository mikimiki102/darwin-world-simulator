package agh.ics.oop.simulation;

import agh.ics.oop.model.*;
import agh.ics.oop.model.util.Pair;
import agh.ics.oop.model.util.RandomPositionGenerator;
import agh.ics.oop.stats.WorldStats;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Simulation implements Runnable {
    protected final WorldMap worldMap;
    private final SimulationConfig config;
    private final Reproducer reproducer;
    private final List<SimulationChangeListener> listeners = new ArrayList<>();
    private final WorldStats stats;
    private final List<String> eventLog = new ArrayList<>();
    private ScheduledExecutorService executor;
    private int day = 0;

    public Simulation(SimulationConfig config) {
        this.config = config;
        this.worldMap = new WorldMap(config.width(), config.height());
        reproducer = new Reproducer(
                config.energyToReproduce(),
                config.energyToChild(),
                config.minMutations(),
                config.maxMutations()
        );
        this.stats = new WorldStats(config.width(), config.height());
    }

    protected void log(String s) {
        eventLog.add(s);
    }

    public List<String> drainEventLog() {
        var copy = new ArrayList<>(eventLog);
        eventLog.clear();
        return copy;
    }

    @Override
    public void run() {
        coreDay();
        recomputeAndNotify();
    }

    protected void coreDay() {
        day += 1;
        removeDead();
        move();
        consume();
        reproduce();
        growPlants();
    }

    protected void recomputeAndNotify() {
        stats.recompute(worldMap, day);
        notifyListeners();
    }

    public void start() {
        if (isRunning())
            throw new IllegalStateException("Cannot start already started Simulation");
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(this, 100, 1000, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        try {
            executor.shutdown();
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public boolean isRunning() {
        return executor != null && !executor.isShutdown();
    }

    private void populate() {
        var rng = ThreadLocalRandom.current();
        var positions = new RandomPositionGenerator(
                config.startAnimalCount(),
                worldMap.getWidth(),
                worldMap.getHeight(),
                rng
        );
        for (var pos : positions) {
            var animal = makeAnimal(pos, rng);
            worldMap.place(animal);
            log("SPAWN id=" + animal.getId() + " at " + pos + " energy=" + animal.getEnergy());
        }
    }

    private void removeDead() {
        var dead = worldMap.getAnimalsFlat().stream()
                .filter(Animal::isDead)
                .toList();
        dead.forEach(a -> log("DEAD  id=" + a.getId() + " at " + a.getPosition() + " day=" + day));
        stats.onAnimalsDied(dead, day);
        dead.forEach(worldMap::remove);
    }

    private void move() {
        worldMap.getAnimalsFlat().forEach(animal -> {
            var before = animal.getPosition();
            var eBefore = animal.getEnergy();
            var dirBefore = animal.getOrientation();
            worldMap.move(animal);
            animal.loseEnergy(config.energyLossPreDay());
            var dirAfter = animal.getOrientation();
            var after = animal.getPosition();
            if (!before.equals(after)) {
                log("MOVE  id=" + animal.getId() +
                        " " + before + " -> " + after +
                        " dir=" + dirBefore + " -> " + dirAfter +
                        " energy: " + eBefore + " -> " + animal.getEnergy());
            } else {
                log("STAY  id=" + animal.getId() +
                        " at " + before +
                        " energy: " + eBefore + " -> " + animal.getEnergy());
            }
        });
    }

    protected void singleConsume(Pair<Vector2d, List<Animal>> cell) {
        final var animals = cell.second();
        if (!animals.isEmpty() && worldMap.tryConsumePlant(cell.first())) {
            final var strongest = animals.get(0);
            final int before = strongest.getEnergy();
            strongest.increaseEnergy(config.plantEnergy());
            log("EAT   id=" + strongest.getId() +
                    " at " + cell.first() +
                    " +" + config.plantEnergy() +
                    " energy: " + before + " -> " + strongest.getEnergy());
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
                var p1 = animals.get(i - 1);
                var p2 = animals.get(i);
                final var child = reproducer.tryReproduce(p1, p2, day);
                if (child.isEmpty())
                    break;
                var c = child.get();
                newborns.add(c);
                log("BORN  id=" + c.getId() +
                        " at " + c.getPosition() +
                        " parents=(" + p1.getId() + "," + p2.getId() + ")" +
                        " energy=" + c.getEnergy());
            }
        });
        newborns.forEach(worldMap::place);
    }

    private void growPlants() {
        worldMap.growPlants(config.plantsPerDay());
    }

    public void init() {
        if (isRunning()) throw new IllegalStateException("Init after start");
        worldMap.growPlants(config.startPlantCount());
        populate();
        stats.recompute(worldMap, day);
        notifyListeners();
    }

    private Animal makeAnimal(Vector2d pos, java.util.Random rng) {
        Genome genome = new Genome(config.genomeLength());
        return new Animal(pos, genome, config.startAnimalEnergy(), day);
    }

    public WorldMap getMap() {
        return worldMap;
    }

    public WorldStats.Snapshot snapshot() {
        return stats.snapshot();
    }

    public List<AnimalState> getAnimalsState() {
        return worldMap.getAnimalsFlat().stream()
                .map(a -> new AnimalState(a.getId(), a.getPosition(), a.getOrientation(), a.getEnergy()))
                .toList();
    }

    public void addListener(SimulationChangeListener listener) {
        listeners.add(listener);
    }

    protected void notifyListeners() {
        listeners.forEach(l -> l.onSimulationChanged(this));
    }
}
