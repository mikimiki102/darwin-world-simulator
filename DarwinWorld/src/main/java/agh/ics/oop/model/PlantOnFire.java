package agh.ics.oop.model;

public final class PlantOnFire extends Plant {
    public PlantOnFire(Vector2d position) {
        super(position);
    }

    @Override
    public String toString() {
        return "!";
    }
}