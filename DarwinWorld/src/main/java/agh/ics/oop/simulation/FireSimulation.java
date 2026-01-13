package agh.ics.oop.simulation;

import agh.ics.oop.model.Animal;
import agh.ics.oop.model.MapDirection;
import agh.ics.oop.model.Plant;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.model.util.Pair;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class FireSimulation extends Simulation {
    public record Config(
            Simulation.Config basicConfig,
            int fireChance,
            int onFireDuration,
            int fireEnergyLoss
    ) {}

    private final FireSimulation.Config config;

    private final Map<Vector2d, Integer> burningPlants = new HashMap<>();
    private final Map<Animal, Integer> burningAnimals = new HashMap<>();

    public FireSimulation(FireSimulation.Config config) {
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
            ignitePlant(pos, config.onFireDuration());
            log("FIRE IGNITE at " + pos + " (by id=" + animals.get(0).getId() + ")");
        } else {
            super.singleConsume(cell);
        }
    }

    private void ignitePlant(Vector2d position, int duration) {
        worldMap.tryConsumePlant(position);
        worldMap.place(new Plant.OnFire(position));
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
                burningAnimals.merge(animal, config.onFireDuration(), Math::max);
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
                return  false;
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

        var newlyIgnited = new HashSet<Vector2d>();
        for (var pos : burningPlants.keySet()) {
            for (var nb : neighbors4(pos)) {
                if (worldMap.hasPlantAt(nb)) {
                    newlyIgnited.add(nb);
                }
            }
        }
        for (var nb : newlyIgnited) {
            ignitePlant(nb, config.onFireDuration());
            log("FIRE SPREAD to " + nb);
        }
    }

    private List<Vector2d> neighbors4(Vector2d pos) {
        var res = new ArrayList<Vector2d>(4);
        for (var dir : List.of(MapDirection.NORTH, MapDirection.EAST, MapDirection.SOUTH, MapDirection.WEST)) {
            var next = pos.add(dir.toUnitVector());
            var adjusted = worldMap.computePosition(next, dir).first();
            res.add(adjusted);
        }
        return res;
    }

    public int getBurningPlantsCount() { return burningPlants.size(); }
    public int getBurningAnimalsCount() { return burningAnimals.size(); }
}
