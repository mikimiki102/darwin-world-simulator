package agh.ics.oop.stats;

import agh.ics.oop.model.*;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorldStatsTest {

    static class FixedGenome extends Genome {
        private final int[] genes;
        public FixedGenome(int... genes) {
            super(1);
            this.genes = genes.clone();
        }
        @Override public int getNext() { return 0; }
        @Override public int[] toArray() { return genes.clone(); }
    }

    private static Animal animal(Vector2d pos, int energy, int birthday, int kids, int... genes) {
        Animal a = new Animal(pos, new FixedGenome(genes), energy, birthday);
        for (int i = 0; i < kids; i++) {
            a.addChild(new Animal(pos, new FixedGenome(genes), 1, birthday + 1));
        }
        return a;
    }

    @Test
    void onAnimalsDied_countsDeadAndNonNegativeLifespan() {
        WorldStats stats = new WorldStats(5, 5);

        Animal d1 = animal(new Vector2d(0, 0), 0, 3, 0, 1);
        Animal d2 = animal(new Vector2d(1, 0), 0, 10, 0, 2);

        stats.onAnimalsDied(List.of(d1, d2), 8);

        assertEquals(2.5, stats.snapshot().avgLifespanOfDead(), 1e-9);
    }

    @Test
    void recompute_setsCountsAveragesAndFreeFields() {
        int W = 4, H = 3;
        var map = new WorldMap(W, H);

        var a = animal(new Vector2d(0, 0), 10, 0, 2, 1);
        var b = animal(new Vector2d(1, 0), 20, 0, 0, 2);

        assertTrue(map.place(a));
        assertTrue(map.place(b));

        map.growPlants(3);

        var stats = new WorldStats(W, H);
        stats.recompute(map, 7);
        var snap = stats.snapshot();

        assertEquals(7, snap.day());
        assertEquals(2, snap.animals());
        assertEquals(3, snap.plants());

        assertEquals(7, snap.freeFields());

        assertEquals(15.0, snap.avgEnergyOfLiving(), 1e-9);
        assertEquals(1.0, snap.avgChildrenOfLiving(), 1e-9);
    }

    @Test
    void recompute_whenNoAnimals_averagesAreZero_andPopularGenotypesEmpty() {
        int W = 2, H = 2;
        var map = new WorldMap(W, H);
        map.growPlants(1);

        var stats = new WorldStats(W, H);
        stats.recompute(map, 1);
        var snap = stats.snapshot();

        assertEquals(0, snap.animals());
        assertEquals(0.0, snap.avgEnergyOfLiving(), 1e-9);
        assertEquals(0.0, snap.avgChildrenOfLiving(), 1e-9);
        assertTrue(snap.mostPopularGenotypes().isEmpty());
    }

    @Test
    void recompute_popularGenotypes_returnsTop3SortedByFrequency() {
        int W = 20, H = 20;
        var map = new WorldMap(W, H);

        assertTrue(map.place(animal(new Vector2d(0, 0), 1, 0, 0, 2)));
        assertTrue(map.place(animal(new Vector2d(1, 0), 1, 0, 0, 2)));
        assertTrue(map.place(animal(new Vector2d(2, 0), 1, 0, 0, 2)));

        assertTrue(map.place(animal(new Vector2d(0, 1), 1, 0, 0, 1)));
        assertTrue(map.place(animal(new Vector2d(1, 1), 1, 0, 0, 1)));

        assertTrue(map.place(animal(new Vector2d(0, 2), 1, 0, 0, 3)));

        assertTrue(map.place(animal(new Vector2d(1, 2), 1, 0, 0, 4)));

        var stats = new WorldStats(W, H);
        stats.recompute(map, 0);

        var top = stats.snapshot().mostPopularGenotypes();
        assertEquals(3, top.size());

        assertArrayEquals(new int[]{2}, top.get(0));
        assertArrayEquals(new int[]{1}, top.get(1));

        boolean thirdIs3 = java.util.Arrays.equals(new int[]{3}, top.get(2));
        boolean thirdIs4 = java.util.Arrays.equals(new int[]{4}, top.get(2));
        assertTrue(thirdIs3 || thirdIs4);
    }

    @Test
    void snapshot_avgLifeOfDead_isComputedFromDeaths() {
        var stats = new WorldStats(5, 5);

        var d1 = animal(new Vector2d(0, 0), 0, 0, 0, 1);
        var d2 = animal(new Vector2d(1, 0), 0, 5, 0, 1);

        stats.onAnimalsDied(List.of(d1, d2), 10);

        assertEquals(7.5, stats.snapshot().avgLifespanOfDead(), 1e-9);
    }
}
