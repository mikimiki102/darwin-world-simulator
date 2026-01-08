package agh.ics.oop.model;

import java.util.HashSet;
import java.util.Set;

public abstract class WorldElement {
    protected Vector2d position;
    protected final Set<AnimalEffect> effects;


    public WorldElement(Vector2d position, Set<AnimalEffect> effects) {
        this.position = position;
        this.effects = effects;
    }

    Vector2d getPosition() {
        return position;
    }
    abstract String toDisplay();
}
