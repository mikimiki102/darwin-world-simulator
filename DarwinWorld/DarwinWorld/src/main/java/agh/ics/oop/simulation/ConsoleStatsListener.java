package agh.ics.oop.simulation;


public final class ConsoleStatsListener implements SimulationChangeListener {
    @Override
    public void onSimulationChanged(Simulation sim) {
        var s = sim.snapshot();
        System.out.println(
                "[day=" + s.day() + "] " +
                        "animals=" + s.animals() +
                        ", plants=" + s.plants() +
                        ", free=" + s.freeFields() +
                        ", avgEnergy(living)=" + String.format("%.2f", s.avgEnergyOfLiving()) +
                        ", avgLife(dead)=" + String.format("%.2f", s.avgLifespanOfDead()) +
                        ", avgKids(living)=" + String.format("%.2f", s.avgChildrenOfLiving())
        );
    }
}
