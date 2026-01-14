package agh.ics.oop.model;

import agh.ics.oop.model.util.Pair;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class AnimalMoveTest {

    static class FixedGenome extends Genome {
        private final int turn;
        public FixedGenome(int turn) { super(1); this.turn = turn; }
        @Override public int getNext() { return turn; }
    }

    static class IdentityValidator implements MoveValidator {
        @Override
        public Pair<Vector2d, MapDirection> computePosition(Vector2d position, MapDirection orientation) {
            return new Pair<>(position, orientation);
        }
    }

    static class ForcingValidator implements MoveValidator {
        private final Vector2d pos;
        private final MapDirection dir;
        ForcingValidator(Vector2d pos, MapDirection dir) { this.pos = pos; this.dir = dir; }

        @Override
        public Pair<Vector2d, MapDirection> computePosition(Vector2d position, MapDirection orientation) {
            return new Pair<>(pos, dir);
        }
    }

    private static void setOrientation(Animal a, MapDirection dir) {
        try {
            Field f = Animal.class.getDeclaredField("orientation");
            f.setAccessible(true);
            f.set(a, dir);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void move_identityValidator_updatesOrientationAndMovesOneStep() {
        var start = new Vector2d(2, 2);
        var a = new Animal(start, new FixedGenome(0), 10, 0);
        setOrientation(a, MapDirection.NORTH);

        MapDirection expectedDir = MapDirection.NORTH.rotate(0);
        Vector2d expectedPos = start.add(expectedDir.toUnitVector());

        a.move(new IdentityValidator());

        assertEquals(expectedDir, a.getOrientation());
        assertEquals(expectedPos, a.getPosition());
    }

    @Test
    void move_identityValidator_turnsBeforeMoving() {
        var a = new Animal(new Vector2d(5, 5), new FixedGenome(2), 10, 0);
        setOrientation(a, MapDirection.NORTH);

        MapDirection expectedDir = MapDirection.NORTH.rotate(2);
        Vector2d expectedPos = new Vector2d(5, 5).add(expectedDir.toUnitVector());

        a.move(new IdentityValidator());

        assertEquals(expectedDir, a.getOrientation());
        assertEquals(expectedPos, a.getPosition());
    }

    @Test
    void move_forcingValidator_canOverridePositionAndOrientation() {
        var a = new Animal(new Vector2d(0, 0), new FixedGenome(0), 10, 0);
        setOrientation(a, MapDirection.NORTH);

        a.move(new ForcingValidator(new Vector2d(9, 9), MapDirection.SOUTH));

        assertEquals(new Vector2d(9, 9), a.getPosition());
        assertEquals(MapDirection.SOUTH, a.getOrientation());
    }

    @Test
    void move_callsValidatorWithPositionAfterStepAndOrientationAfterRotation() {
        class CapturingValidator implements MoveValidator {
            Vector2d seenPos;
            MapDirection seenDir;

            @Override
            public Pair<Vector2d, MapDirection> computePosition(Vector2d position, MapDirection orientation) {
                this.seenPos = position;
                this.seenDir = orientation;
                return new Pair<>(position, orientation);
            }
        }

        var validator = new CapturingValidator();

        var a = new Animal(new Vector2d(1, 1), new FixedGenome(1), 10, 0);
        setOrientation(a, MapDirection.WEST);

        MapDirection expectedDir = MapDirection.WEST.rotate(1);
        Vector2d expectedPosAfterStep = new Vector2d(1, 1).add(expectedDir.toUnitVector());

        a.move(validator);

        assertEquals(expectedDir, validator.seenDir);
        assertEquals(expectedPosAfterStep, validator.seenPos);
    }
}
