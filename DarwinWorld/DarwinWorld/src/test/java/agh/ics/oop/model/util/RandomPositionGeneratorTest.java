package agh.ics.oop.model.util;

import agh.ics.oop.model.Vector2d;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class RandomPositionGeneratorTest {

    @Test
    void iterator_ProducesNPositionsWithinBounds() {
        int n = 50, w = 10, h = 6;
        var gen = new RandomPositionGenerator(n, w, h, new Random(123));

        int cnt = 0;
        for (Vector2d p : gen) {
            assertTrue(p.x() >= 0 && p.x() < w, "x out of bounds: " + p);
            assertTrue(p.y() >= 0 && p.y() < h, "y out of bounds: " + p);
            cnt++;
        }
        assertEquals(n, cnt, "generator should produce exactly n positions");
    }
}
