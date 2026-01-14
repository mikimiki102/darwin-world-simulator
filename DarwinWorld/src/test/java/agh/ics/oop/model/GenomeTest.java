package agh.ics.oop.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class GenomeTest {

    private static void setGenes(Genome g, int[] newGenes) {
        try {
            var f = Genome.class.getDeclaredField("genes");
            f.setAccessible(true);
            f.set(g, newGenes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static int[] getGenes(Genome g) {
        return g.toArray();
    }

    @Test
    void sortByStronger_returnsFirstWithGreaterOrEqualEnergy() throws Exception {
        var g = new Genome(4);

        var a = new Animal(new Vector2d(0, 0), g, 10, 0);
        var b = new Animal(new Vector2d(0, 0), g, 7, 0);

        Method m = Genome.class.getDeclaredMethod("sortByStronger", Animal.class, Animal.class);
        m.setAccessible(true);

        @SuppressWarnings("unchecked")
        var pair = (agh.ics.oop.model.util.Pair<Animal, Animal>) m.invoke(new Genome(1), a, b);

        assertSame(a, pair.first());
        assertSame(b, pair.second());

        var c = new Animal(new Vector2d(0, 0), g, 5, 0);
        var d = new Animal(new Vector2d(0, 0), g, 5, 0);

        @SuppressWarnings("unchecked")
        var pair2 = (agh.ics.oop.model.util.Pair<Animal, Animal>) m.invoke(new Genome(1), c, d);

        assertSame(c, pair2.first(), "przy remisie pierwszy argument powinien zostać uznany za 'stronger'");
        assertSame(d, pair2.second());
    }

    @Test
    void applyMutations_changesBetweenMinAndMaxPositions_butKeepsGeneRange() throws Exception {
        int len = 30;
        int dirs = MapDirection.values().length;

        var genome = new Genome(len);

        int[] base = new int[len];
        Arrays.fill(base, 0);
        setGenes(genome, base);

        int min = 5, max = 10;

        Method m = Genome.class.getDeclaredMethod("applyMutations", int.class, int.class);
        m.setAccessible(true);

        m.invoke(genome, min, max);

        int[] after = getGenes(genome);

        int changed = 0;
        for (int x : after) if (x != 0) changed++;

        assertTrue(changed <= max, "widocznie zmienionych pozycji nie może być więcej niż max");

        for (int x : after) {
            assertTrue(x >= 0 && x < dirs, "gen musi być w zakresie 0..MapDirection.values().length-1");
        }
    }

    @Test
    void applyMutations_withMinEqualsMax_neverChangesMoreThanThatManyVisiblePositions() throws Exception {
        int len = 40;
        var genome = new Genome(len);

        int[] base = new int[len];
        Arrays.fill(base, 0);
        setGenes(genome, base);

        int k = 8;

        Method m = Genome.class.getDeclaredMethod("applyMutations", int.class, int.class);
        m.setAccessible(true);

        m.invoke(genome, k, k);

        int[] after = getGenes(genome);

        int changed = 0;
        for (int x : after) if (x != 0) changed++;

        assertTrue(changed <= k);
    }

    @Test
    void constructorWithParents_setsParentsPresent_andGenomeLengthMatchesStronger() {
        var g1 = new Genome(12);
        var g2 = new Genome(12);

        var stronger = new Animal(new Vector2d(0,0), g1, 20, 0);
        var weaker   = new Animal(new Vector2d(0,0), g2, 10, 0);

        var child = new Genome(stronger, weaker, 0, 0);

        assertTrue(child.getParents().isPresent());
        assertSame(stronger, child.getParents().get().first());
        assertSame(weaker, child.getParents().get().second());

        assertEquals(12, child.toArray().length);
    }
}

