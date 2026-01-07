package agh.ics.oop;

import agh.ics.oop.model.*;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Simulation implements Runnable {
    private WorldMap worldMap;
    private int startingGrassAmount;
    private int dailyGrassAmount;
    private int grassEnergy;

    private void simulateDay() {
        // Remove dead animals
        worldMap.getAnimalsFlat()
                .filter(Animal::isDead)
                .forEach(worldMap::remove);

        // Move
        worldMap.getAnimalsFlat().forEach(worldMap::move);
        // Consume
        worldMap.getAnimalsGrouped().forEach(entry -> {
            if (worldMap.tryConsumeGrass(entry.first())) {
                entry.second().getFirst().consumeGrass(grassEnergy);
            }
        });

        // Reproduce
        worldMap.growGrass(dailyGrassAmount); // Place grass
    }


    public void run() {
        while (true) {
            simulateDay();
        }
    }
}
