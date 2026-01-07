package agh.ics.oop;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SimulationEngine {
    private final List<Simulation> simulations;
    private ArrayList<Thread> threads;
    private ExecutorService executorService;
    private boolean running = false;
    private int timeoutValue = 10;
    private TimeUnit timeoutUnit = TimeUnit.SECONDS;

    private static SimulationEngine singleton;

    public SimulationEngine() {
        simulations = new ArrayList<>();
    }

    public SimulationEngine(List<Simulation> simulations) {
        this.simulations = simulations;
    }

    public void setTimeout(int value, TimeUnit unit) {
        timeoutValue = value;
        timeoutUnit = unit;
    }

    public void runSync() {
        if (!running) {
            running = true;
            for (final var sim : simulations) {
                sim.run();
            }
            running = false;
        }
    }
    public void runAsync() {
        if (!running) {
            threads = simulations.stream()
                    .map(Thread::new)
                    .collect(Collectors.toCollection(ArrayList::new));
            threads.forEach(thread -> thread.start());
            running = true;
        }
    }

    public void runAsyncInThreadPool() {
        if (!running) {
            executorService = Executors.newFixedThreadPool(4);
            for (final var sim : simulations) {
                executorService.submit(sim);
            }
            running = true;
        }
    }

    public void awaitSimulationEnd() {
        if (threads != null) {
            for (final var thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    System.err.println("Thread " + thread.getName() + " has been interrupted!\n" + e);
                }
            }
        }
        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(timeoutValue, timeoutUnit)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                System.err.println("Thread pool has been interrupted!\n" + e);
            }

        }
        running = false;
    }

    public void addSimulation(Simulation simulation) {
        simulations.add(simulation);
        if (running) {
            if (threads != null) {
                final var thread = new Thread(simulation);
                threads.add(thread);
                thread.start();
            } else if (executorService != null) {
                executorService.submit(simulation);
            }
        }
    }

    public static SimulationEngine getGlobalEngine() {
        if (singleton == null) {
            singleton = new SimulationEngine();
        }
        return singleton;
    }
}
