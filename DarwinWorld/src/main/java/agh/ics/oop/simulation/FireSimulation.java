package agh.ics.oop.simulation;

import agh.ics.oop.model.Animal;
import agh.ics.oop.model.MapDirection;
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
        fireEndOfDayTick();
        spreadFire();
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
            return;
        }
        super.singleConsume(cell);
    }

    private void fireEndOfDayTick() {
        var burningPositions = new HashSet<>(burningPlants.keySet());
        for (var a : worldMap.getAnimalsFlat()) {
            if (burningPositions.contains(a.getPosition())) {
                burningAnimals.merge(a, config.onFireDuration(), Math::max);
                log("FIRE ANIMAL IGNITE id=" + a.getId() + " at " + a.getPosition());
            }
        }

        var itA = burningAnimals.entrySet().iterator();
        while (itA.hasNext()) {
            var e = itA.next();
            var a = e.getKey();
            int left = e.getValue() - 1;
            a.loseEnergy(config.fireEnergyLoss());
            if (left <= 0 || a.isDead()) {
                itA.remove();
                log("FIRE ANIMAL OUT id=" + a.getId());
            } else {
                e.setValue(left);
            }
        }

        var toRemove = new ArrayList<Vector2d>();
        for (var entry : burningPlants.entrySet()) {
            var pos = entry.getKey();
            int daysLeft = entry.getValue() - 1;
            if (daysLeft <= 0) {
                worldMap.tryConsumePlant(pos);
                toRemove.add(pos);
                log("FIRE BURNOUT at " + pos);
            } else {
                entry.setValue(daysLeft);
            }
        }
        toRemove.forEach(burningPlants::remove);
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

    private void ignitePlant(Vector2d pos, int duration) {
        burningPlants.merge(pos, Math.max(1, duration), Math::max);
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
