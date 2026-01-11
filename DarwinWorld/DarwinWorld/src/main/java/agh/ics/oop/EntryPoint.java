package agh.ics.oop;

import agh.ics.oop.simulation.SimulationApp;
import javafx.application.Application;

public class EntryPoint {
    public static void main(String[] args) {
        System.out.println("system wystartował");
        Application.launch(SimulationApp.class, args);
        System.out.println("system zakończył działanie");
    }
}
