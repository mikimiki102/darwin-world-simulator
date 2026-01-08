package agh.ics.oop.model;

import agh.ics.oop.Simulation;
import agh.ics.oop.model.util.Pair;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FireSimulation extends Simulation {
    public record Config(
            Simulation.Config basicConfig,
            int fireChance,
            int onFireDuration,
            int fireEnergyLoss
    ) {}

    private final FireSimulation.Config config;

    public FireSimulation(FireSimulation.Config config, WorldMap worldMap) {
        super(config.basicConfig(), worldMap);
        this.config = config;
    }

    @Override
    public void run() {
        super.run();
        spreadFire();
    }

    @Override
    protected void singleConsume(Pair<Vector2d, List<Animal>> cell) {
        if (ThreadLocalRandom.current().nextInt(100) < config.fireChance()) {
            // TODO: ignite cell
        } else {
            super.singleConsume(cell);
        }
    }

    private void spreadFire() {

    }
}
