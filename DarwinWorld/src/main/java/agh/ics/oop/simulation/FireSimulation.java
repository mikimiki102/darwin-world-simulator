package agh.ics.oop.simulation;

import agh.ics.oop.model.Animal;
import agh.ics.oop.model.MapDirection;
import agh.ics.oop.model.PlantOnFire;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.model.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class FireSimulation extends Simulation {
    private final FireSimulationConfig config;

    private final Map<Vector2d, Integer> burningPlants = new HashMap<>();
    private final Map<Animal, Integer> burningAnimals = new HashMap<>();

    public FireSimulation(FireSimulationConfig config) {
        super(config.basicConfig());
        this.config = config;
    }

    @Override
    public void run() {
        coreDay();
        fireTick();
        recomputeAndNotify();
    }

    @Override
    protected void singleConsume(Pair<Vector2d, List<Animal>> cell) {
        var pos = cell.first();
        var animals = cell.second();
        if (!animals.isEmpty()
                && worldMap.hasPlantAt(pos)
                && ThreadLocalRandom.current().nextInt(100) < config.fireChance()) {
            ignitePlant(pos, config.fireDuration());
            log("FIRE IGNITE at " + pos + " (by id=" + animals.get(0).getId() + ")");
        } else {
            super.singleConsume(cell);
        }
    }

    private void ignitePlant(Vector2d position, int duration) {
        worldMap.tryConsumePlant(position);
        worldMap.place(new PlantOnFire(position));
        burningPlants.putIfAbsent(position, duration);
    }

    private void fireTick() {
        effectBurningAnimals();
        removeBurnedPlants();
        spreadFire();
    }

    private void effectBurningAnimals() {
        worldMap.getAnimalsFlat().forEach(animal -> {
            if (burningPlants.containsKey(animal.getPosition())) {
                burningAnimals.merge(animal, config.fireDuration(), Math::max);
                log("FIRE ANIMAL IGNITE id=" + animal.getId() + " at " + animal.getPosition());
            }
        });

        burningAnimals.entrySet().removeIf(entry -> {
            final var animal = entry.getKey();
            final int fireLeft = entry.getValue() - 1;
            animal.loseEnergy(config.fireEnergyLoss());
            if (fireLeft <= 0 || animal.isDead()) {
                log("FIRE ANIMAL OUT id=" + animal.getId());
                return true;
            } else {
                entry.setValue(fireLeft);
                return false;
            }
        });
    }

    private void removeBurnedPlants() {
        burningPlants.entrySet().removeIf(entry -> {
            final var position = entry.getKey();
            final int daysLeft = entry.getValue() - 1;
            if (daysLeft <= 0) {
                worldMap.tryConsumePlant(position);
                log("FIRE BURNOUT at " + position);
                return true;
            } else {
                entry.setValue(daysLeft);
                return false;
            }
        });
    }

    private void spreadFire() {
        if (burningPlants.isEmpty()) return;

        final var burningPositions = new ArrayList<>(burningPlants.keySet());

        for (final var position : burningPositions) {
            for (final var cellPosition : neighborCells(position)) {
                if (worldMap.hasPlantAt(cellPosition)) {
                    ignitePlant(cellPosition, config.fireDuration());
                    log("FIRE SPREAD to " + cellPosition);
                }
            }
        }
    }

    private List<Vector2d> neighborCells(Vector2d pos) {
        var res = new ArrayList<Vector2d>(4);
        for (var dir : List.of(MapDirection.NORTH, MapDirection.EAST, MapDirection.SOUTH, MapDirection.WEST)) {
            var next = pos.add(dir.toUnitVector());
            var adjusted = worldMap.computePosition(next, dir).first();
            res.add(adjusted);
        }
        return res;
    }

    public int getBurningPlantsCount() {
        return burningPlants.size();
    }

    public int getBurningAnimalsCount() {
        return burningAnimals.size();
    }
}
