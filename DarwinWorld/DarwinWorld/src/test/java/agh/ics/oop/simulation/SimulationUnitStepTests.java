package agh.ics.oop.simulation;

import agh.ics.oop.model.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SimulationUnitStepsTest {

    private static void call(Object target, String name) {
        try {
            Method m = target.getClass().getDeclaredMethod(name);
            m.setAccessible(true);
            m.invoke(target);
        } catch (Exception e) {
            throw new RuntimeException("Cannot call " + name + ": " + e, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<Vector2d, Plant> plantMap(WorldMap map) {
        try {
            Field f = WorldMap.class.getDeclaredField("plantMap");
            f.setAccessible(true);
            return (Map<Vector2d, Plant>) f.get(map);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Animal animal(Vector2d pos, int energy, int birthday) {
        return new Animal(pos, new Genome(8), energy, birthday);
    }

    private static Simulation simCfg(int w, int h, int plantsPerDay, int energyLossPerDay) {
        var cfg = new Simulation.Config(
                w, h,
                0, plantsPerDay,
                0, 0,
                20, energyLossPerDay, 10,
                0, 0,
                10,
                8
        );
        return new Simulation(cfg);
    }


    @Test
    void testInit() {
        var cfg = new Simulation.Config(
                20, 10,
                15, 0,
                8, 20,
                30, 1, 10,
                0, 0,
                10,
                8
        );
        var sim = new Simulation(cfg);
        sim.init();

        var s = sim.snapshot();
        assertEquals(8, s.animals());
        assertEquals(15, s.plants());
        assertEquals(0, s.day());
    }

    @Test
    void testRunIncrementsDayAndGrowsPlants() {
        var sim = simCfg(20, 10, 3, 0);
        sim.init();
        assertEquals(0, sim.snapshot().plants());

        sim.run();

        assertEquals(1, sim.snapshot().day());
        assertEquals(3, sim.snapshot().plants());
    }


    @Test
    void testRemoveDead() {
        var sim = simCfg(10, 10, 0, 0);
        var map = sim.getMap();
        var a = animal(new Vector2d(1, 1), 5, 0);
        var d = animal(new Vector2d(2, 2), 0, 0);
        map.place(a);
        map.place(d);

        call(sim, "removeDead");

        var list = map.getAnimalsFlat();
        assertEquals(1, list.size());
        assertEquals(a.getId(), list.get(0).getId());
    }

    @Test
    void testMoveEnergyLoss() {
        var sim = simCfg(10, 10, 0, 2);
        var map = sim.getMap();
        var a = animal(new Vector2d(3, 3), 10, 0);
        map.place(a);

        call(sim, "move");

        assertEquals(8, a.getEnergy());
    }

    @Test
    void testConsumeGivesEnergy() {
        var sim = simCfg(10, 10, 0, 0);
        var map = sim.getMap();
        var pos = new Vector2d(5, 5);

        var a1 = animal(pos, 50, 0);
        var a2 = animal(pos, 10, 0);
        map.place(a1);
        map.place(a2);

        plantMap(map).put(pos, new Plant(pos));

        call(sim, "consume");

        int plantEnergy = 10;
        boolean a1Ate = (a1.getEnergy() == 50 + plantEnergy) && (a2.getEnergy() == 10);
        boolean a2Ate = (a2.getEnergy() == 10 + plantEnergy) && (a1.getEnergy() == 50);
        assertTrue(a1Ate || a2Ate, "dokładnie jedno zwierzę powinno dostać +10 energii");

        assertFalse(plantMap(map).containsKey(pos), "roślina powinna zniknąć po konsumpcji");
    }

    @Test
    void testReproduceAddsChild() {
        var sim = simCfg(10, 10, 0, 0);
        var map = sim.getMap();
        var pos = new Vector2d(2, 2);
        var p1 = animal(pos, 40, 0);
        var p2 = animal(pos, 35, 0);
        map.place(p1);
        map.place(p2);
        int before = map.getAnimalsFlat().size();

        call(sim, "reproduce");

        var after = map.getAnimalsFlat();
        assertEquals(before + 1, after.size());

        assertEquals(35, p1.getEnergy());
        assertEquals(30, p2.getEnergy());
    }

    @Test
    void testGrowPlantsCap() {
        var sim = simCfg(5, 5, 7, 0);
        var map = sim.getMap();

        call(sim, "growPlants");
        assertEquals(7, map.getPlants().size());

        for (int i = 0; i < 10; i++) call(sim, "growPlants");
        assertTrue(map.getPlants().size() <= 25);
    }
}
