package agh.ics.oop.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Vector2dTest {

    @Test
    void equals_SameCoordinates_True() {
        var a = new Vector2d(3, 5);
        var b = new Vector2d(3, 5);
        assertTrue(a.equals(b));
        assertTrue(a.equals((Object) b));
        assertTrue(a.equals(b));
    }

    @Test
    void equals_DifferentCoordinates_False() {
        var a = new Vector2d(3, 5);
        var b = new Vector2d(3, 6);
        var c = new Vector2d(4, 5);
        assertFalse(a.equals(b));
        assertFalse(a.equals(c));
    }

    @Test
    void add_ReturnsComponentWiseSum() {
        var a = new Vector2d(2, 3);
        var b = new Vector2d(5, -1);
        assertEquals(new Vector2d(7, 2), a.add(b));
    }
}
