package agh.ics.oop.simulation;

import agh.ics.oop.model.Animal;
import agh.ics.oop.model.Vector2d;
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

    public FireSimulation(FireSimulation.Config config) {
        super(config.basicConfig());
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
