package agh.ics.oop.model;

public class DailyEnergyLossEffect extends AnimalEffect {
    @Override
    public void apply(Animal animal, SimulationConfig config) {
        animal.addEnergy(-config.energyLossPreDay());
    }
    @Override
    public boolean isExpired() { return false; }
}
