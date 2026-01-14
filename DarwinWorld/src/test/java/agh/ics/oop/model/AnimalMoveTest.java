package agh.ics.oop.model;

import agh.ics.oop.model.util.Pair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnimalMoveTest {

    @Test
    void move_rotates_and_moves_then_validator_applied() {
        Genome genome = new GenomeStub(1);
        Animal animal = new Animal(
                new Vector2d(2, 2),
                genome,
                10,
                0
        );

        MoveValidator validator = (pos, dir) ->
                new Pair<>(new Vector2d(0, 0), MapDirection.NORTH);

        animal.move(validator);

        assertEquals(new Vector2d(0, 0), animal.getPosition());
        assertEquals(MapDirection.NORTH, animal.getOrientation());
    }

    static class GenomeStub extends Genome {
        private final int value;

        GenomeStub(int value) {
            super(null, null, 0, 0);
            this.value = value;
        }

        @Override
        public int getNext() {
            return value;
        }
    }
}
