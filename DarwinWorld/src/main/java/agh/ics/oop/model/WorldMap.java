package agh.ics.oop.model;

import agh.ics.oop.model.util.Pair;

import java.util.List;

public interface WorldMap {
    boolean place(Animal animal);
    boolean remove(Animal animal);
    void move(Animal animal);
    List<Animal> getAnimalsFlat();
    List<Pair<Vector2d, List<Animal>>> getAnimalsGroupedNSorted();
    void processPlants();
    boolean tryConsumePlant(Vector2d position);
}
