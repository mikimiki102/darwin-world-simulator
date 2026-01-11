package agh.ics.oop.simulation;

public final class ConsoleDebugListener implements SimulationChangeListener {
    @Override
    public void onSimulationChanged(Simulation sim) {
        for (var line : sim.drainEventLog()) {
            System.out.println(line);
        }
    }
}
