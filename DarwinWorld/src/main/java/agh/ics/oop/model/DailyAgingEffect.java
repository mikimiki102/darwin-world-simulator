package agh.ics.oop.model;

public class DailyAgingEffect extends AnimalEffect {
    @Override
    public void apply(Animal animal, SimulationConfig config) {
        animal.increaseAge(1);
    }
    @Override
    public boolean isExpired() { return false; }
}
