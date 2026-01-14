package agh.ics.oop.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class WorldMapTest {


    static class FixedGenome extends Genome {
        public FixedGenome() { super(1); }
        @Override public int getNext() { return 0; }
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

    private static Animal animalAt(Vector2d pos, int energy, int birthday, MapDirection dir) {
        Animal a = new Animal(pos, new FixedGenome(), energy, birthday);
        setOrientation(a, dir);
        return a;
    }


    @Test
    void placeAndRemove_WorkOnCells() {
        var map = new WorldMap(6, 4);
        var pos = new Vector2d(2, 3);
        var a = animalAt(pos, 10, 0, MapDirection.NORTH);

        assertTrue(map.place(a), "pierwsze dodanie powinno zwrócić true");
        assertEquals(1, map.getAnimalsFlat().size(), "powinien być 1 zwierzak na mapie");

        assertFalse(map.place(a), "ponowne dodanie tego samego obiektu do Set powinno zwrócić false");
        assertEquals(1, map.getAnimalsFlat().size());

        assertTrue(map.remove(a), "usunięcie istniejącego zwierzaka powinno zwrócić true");
        assertEquals(0, map.getAnimalsFlat().size(), "po usunięciu nie powinno być zwierząt");
    }

    @Test
    void move_WrapsAroundX() {
        int W = 7, H = 5;
        var map = new WorldMap(W, H);

        var start = new Vector2d(0, 2);
        var a = animalAt(start, 10, 0, MapDirection.WEST);
        map.place(a);

        map.move(a);

        assertEquals(new Vector2d(W - 1, 2), a.getPosition(), "powinno zawinąć do prawej krawędzi");
        assertEquals(MapDirection.WEST, a.getOrientation(), "orientacja pozioma nie powinna się zmienić");
    }

    @Test
    void move_BouncesOnPolesY() {
        int W = 6, H = 4;
        var map = new WorldMap(W, H);

        var start = new Vector2d(3, 0);
        var a = animalAt(start, 10, 0, MapDirection.NORTH); // <- NORTH, bo y=-1 próbuje wyjść poza mapę
        map.place(a);

        map.move(a);

        assertEquals(start, a.getPosition(), "po odbiciu na biegunie pozycja powinna zostać bez zmiany");
        assertEquals(MapDirection.SOUTH, a.getOrientation(), "kierunek powinien się odwrócić");
    }

    @Test
    void growPlants_IncreasesCount_UptoCapacity() {
        int W = 5, H = 5;
        var map = new WorldMap(W, H);

        map.growPlants(7);
        assertEquals(7, map.getPlants().size(), "powinno urosnąć dokładnie 7 roślin (puste pole każdorazowo)");

        map.growPlants(1000);
        assertEquals(W * H, map.getPlants().size(), "nie może urosnąć więcej roślin niż pól na mapie");
    }
}