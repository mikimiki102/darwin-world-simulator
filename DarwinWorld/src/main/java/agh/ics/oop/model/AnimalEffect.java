package agh.ics.oop.model;

import java.util.Objects;

public abstract class AnimalEffect {
    abstract void apply(Animal animal, SimulationConfig config);
    abstract boolean isExpired();

    @Override
    public boolean equals(Object o) {
        return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        // Hash oparty na typie klasy
        return Objects.hash(getClass());
    }
}
