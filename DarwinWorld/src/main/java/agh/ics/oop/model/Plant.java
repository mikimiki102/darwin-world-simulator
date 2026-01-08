package agh.ics.oop.model;

import java.util.HashSet;

public class Plant extends WorldElement {
    public Plant(Vector2d position) {
        super(position, new HashSet<>());
    }

    @Override
    public String toDisplay() {
        return "*";
    }
}
