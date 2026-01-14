package agh.ics.oop.model;

import agh.ics.oop.model.util.Pair;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class GenomeTest {

    @Test
    void sortByStronger_returns_stronger_first() throws Exception {
        Animal weak = new Animal(new Vector2d(0, 0), new Genome(1), 5, 0);
        Animal strong = new Animal(new Vector2d(0, 0), new Genome(1), 20, 0);

        Genome g = new Genome(1);

        Method m = Genome.class.getDeclaredMethod("sortByStronger", Animal.class, Animal.class);
        m.setAccessible(true);

        @SuppressWarnings("unchecked")
        Pair<Animal, Animal> res = (Pair<Animal, Animal>) m.invoke(g, weak, strong);

        assertSame(strong, res.first());
        assertSame(weak, res.second());
    }

    @Test
    void applyMutations_keeps_length_and_gene_range() throws Exception {
        Animal a1 = new Animal(new Vector2d(0, 0), new Genome(20), 100, 0);
        Animal a2 = new Animal(new Vector2d(0, 0), new Genome(20), 50, 0);

        Genome child = new Genome(a1, a2, 0, 0);
        int[] before = child.toArray();
        int len = before.length;

        Method m = Genome.class.getDeclaredMethod("applyMutations", int.class, int.class);
        m.setAccessible(true);
        m.invoke(child, 5, 10);

        int[] after = child.toArray();

        assertEquals(len, after.length);

        for (int gene : after) {
            assertTrue(gene >= 0 && gene < MapDirection.values().length);
        }
    }
}
