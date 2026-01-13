package agh.ics.oop.model;

public class Plant implements WorldElement {
    private final Vector2d position;

    public static class OnFire extends Plant {
        public OnFire(Vector2d position) {
            super(position);
        }

        @Override
        public String toString() {
            return "!";
        }
    }

    public Plant(Vector2d position) {
        this.position = position;
    }

    @Override
    public Vector2d getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "*";
    }
}

