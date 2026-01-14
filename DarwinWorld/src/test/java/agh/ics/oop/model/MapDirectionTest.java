package agh.ics.oop.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MapDirectionTest {
    @Test
    void testToString() {
        assertEquals("N", MapDirection.NORTH.toString());
        assertEquals("NE", MapDirection.NORTH_EAST.toString());
        assertEquals("E", MapDirection.EAST.toString());
        assertEquals("SE", MapDirection.SOUTH_EAST.toString());
        assertEquals("S", MapDirection.SOUTH.toString());
        assertEquals("SW", MapDirection.SOUTH_WEST.toString());
        assertEquals("W", MapDirection.WEST.toString());
        assertEquals("NW", MapDirection.NORTH_WEST.toString());
    }

    @Test
    void testToFullName() {
        assertEquals("Północ", MapDirection.NORTH.toFullName());
        assertEquals("Północny wschód", MapDirection.NORTH_EAST.toFullName());
        assertEquals("Wschód", MapDirection.EAST.toFullName());
        assertEquals("Południowy wschód", MapDirection.SOUTH_EAST.toFullName());
        assertEquals("Południe", MapDirection.SOUTH.toFullName());
        assertEquals("Południowy zachód", MapDirection.SOUTH_WEST.toFullName());
        assertEquals("Zachód", MapDirection.WEST.toFullName());
        assertEquals("Północny zachód", MapDirection.NORTH_WEST.toFullName());
    }

    @Test
    void next() {
        assertEquals(MapDirection.NORTH_EAST, MapDirection.NORTH.next());
        assertEquals(MapDirection.EAST, MapDirection.NORTH_EAST.next());
        assertEquals(MapDirection.SOUTH_EAST, MapDirection.EAST.next());
        assertEquals(MapDirection.SOUTH, MapDirection.SOUTH_EAST.next());
        assertEquals(MapDirection.SOUTH_WEST, MapDirection.SOUTH.next());
        assertEquals(MapDirection.WEST, MapDirection.SOUTH_WEST.next());
        assertEquals(MapDirection.NORTH_WEST, MapDirection.WEST.next());
        assertEquals(MapDirection.NORTH, MapDirection.NORTH_WEST.next());
    }

    @Test
    void previous() {
        assertEquals(MapDirection.NORTH, MapDirection.NORTH_EAST.previous());
        assertEquals(MapDirection.NORTH_WEST, MapDirection.NORTH.previous());
        assertEquals(MapDirection.WEST, MapDirection.NORTH_WEST.previous());
        assertEquals(MapDirection.SOUTH_WEST, MapDirection.WEST.previous());
        assertEquals(MapDirection.SOUTH, MapDirection.SOUTH_WEST.previous());
        assertEquals(MapDirection.SOUTH_EAST, MapDirection.SOUTH.previous());
        assertEquals(MapDirection.EAST, MapDirection.SOUTH_EAST.previous());
        assertEquals(MapDirection.NORTH_EAST, MapDirection.EAST.previous());
    }

    @Test
    void rotate() {
        assertEquals(MapDirection.NORTH_EAST, MapDirection.NORTH.rotate(1));
        assertEquals(MapDirection.EAST, MapDirection.NORTH.rotate(2));
        assertEquals(MapDirection.SOUTH, MapDirection.NORTH.rotate(4));
        assertEquals(MapDirection.NORTH, MapDirection.NORTH.rotate(8));
        assertEquals(MapDirection.NORTH_EAST, MapDirection.NORTH.rotate(9)); // 8 + 1

        assertEquals(MapDirection.NORTH_WEST, MapDirection.NORTH.rotate(-1));
        assertEquals(MapDirection.WEST, MapDirection.NORTH.rotate(-2));
        assertEquals(MapDirection.NORTH, MapDirection.NORTH.rotate(-8));
        assertEquals(MapDirection.NORTH_WEST, MapDirection.NORTH.rotate(-9));
    }

    @Test
    void toUnitVector() {
        assertEquals(new Vector2d(0, -1), MapDirection.NORTH.toUnitVector());
        assertEquals(new Vector2d(1, -1), MapDirection.NORTH_EAST.toUnitVector());
        assertEquals(new Vector2d(1, 0), MapDirection.EAST.toUnitVector());
        assertEquals(new Vector2d(1, 1), MapDirection.SOUTH_EAST.toUnitVector());
        assertEquals(new Vector2d(0, 1), MapDirection.SOUTH.toUnitVector());
        assertEquals(new Vector2d(-1, 1), MapDirection.SOUTH_WEST.toUnitVector());
        assertEquals(new Vector2d(-1, 0), MapDirection.WEST.toUnitVector());
        assertEquals(new Vector2d(-1, -1), MapDirection.NORTH_WEST.toUnitVector());
    }
}