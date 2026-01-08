package agh.ics.oop.model;

import agh.ics.oop.model.util.Pair;

public interface MoveValidator {

    /**
     * Validates and calculates the new position in the world;
     *
     * @param position The position checked for the movement validity.
     * @param orientation The orientation checked for the movement validity.
     * @return A pair of world position and world orientation.
     */
    Pair<Vector2d, MapDirection> computePosition(Vector2d position, MapDirection orientation);
}
