package agh.ics.oop.model;

public enum MapDirection {
    NORTH(new Vector2d(0, 1), "Północ", "N"),
    NORTH_EAST(new Vector2d(1, 1), "Północny wschód", "NE"),
    EAST(new Vector2d(1, 0), "Wschód", "E"),
    SOUTH_EAST(new Vector2d(1, -1), "Południowy wschód", "SE"),
    SOUTH(new Vector2d(0, -1), "Południe", "S"),
    SOUTH_WEST(new Vector2d(-1, -1), "Południowy zachód", "SW"),
    WEST(new Vector2d(-1, 0), "Zachód", "W"),
    NORTH_WEST(new Vector2d(-1, 1), "Północny zachód", "NW");

    private final Vector2d unitVector;
    private final String fullName;
    private final String simpleName;

    MapDirection(Vector2d unitVector, String fullName, String simpleName) {
        this.unitVector = unitVector;
        this.fullName = fullName;
        this.simpleName = simpleName;
    }

    @Override
    public String toString() {
        return this.simpleName;
    }

    public String toFullName() {
        return this.fullName;
    }

    public MapDirection next() {
        final var enumValues = MapDirection.values();
        return enumValues[(this.ordinal() + 1) % enumValues.length];
    }

    public MapDirection previous() {
        final var enumValues = MapDirection.values();
        final int index = (enumValues.length + this.ordinal() - 1) % enumValues.length;
        return enumValues[index];
    }

    public MapDirection rotate(int gen) {
        final var enumValues = MapDirection.values();
        gen = (gen % enumValues.length) + enumValues.length;
        final int index = (this.ordinal() + gen) % enumValues.length;
        return enumValues[index];
    }

    public Vector2d toUnitVector() {
        return this.unitVector;
    }
}
