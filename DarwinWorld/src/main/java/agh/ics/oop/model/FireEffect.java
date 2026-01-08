package agh.ics.oop.model;

public class FireEffect extends AnimalEffect {
    private int duration;
    private final int energyPenalty;

    public FireEffect(int duration, int energyPenalty) {
        this.duration = duration;
        this.energyPenalty = energyPenalty;
    }

    @Override
    public void apply(Animal animal, SimulationConfig config) {
        if (duration > 0) {
            animal.addEnergy(-energyPenalty);
            duration--;
        }
    }

    @Override
    public boolean isExpired() {
        return duration <= 0;
    }
}
