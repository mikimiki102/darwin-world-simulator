package agh.ics.oop.model;

import agh.ics.oop.Simulation;

public class FireSimulation extends Simulation {
    public FireSimulation(WorldMap worldMap, SimulationConfig config) {
        super(worldMap, config);
    }

    @Override
    public void run() {
        super.run();
        spreadFire();
    }

    @Override
    protected void consume() {
        worldMap.getAnimalsGroupedNSorted().forEach(cell -> {
            if (worldMap.tryConsumePlant(cell.first())) {
                cell.second().getFirst().consume();
            }
        });
    }

    private void spreadFire() {

    }
}
