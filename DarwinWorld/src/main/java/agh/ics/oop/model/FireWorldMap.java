package agh.ics.oop.model;

import java.util.concurrent.ThreadLocalRandom;

public class FireWorldMap extends BasicWorldMap {
    public FireWorldMap(int width, int height, SimulationConfig config) {
        super(width, height, config);
    }

    @Override
    public boolean tryConsumePlant(Vector2d position) {
        final var random = ThreadLocalRandom.current();
        final int diceRoll = random.nextInt(100);
        if (diceRoll < config.fireChance()) {
            final var fireEffect = new FireEffect(config.onFireDuration(), config.fireEnergyLoss());
            animals.get(position).forEach(animal -> animal.addEffect(fireEffect));
            return false;
        } else{
            return super.tryConsumePlant(position);
        }
    }
}
