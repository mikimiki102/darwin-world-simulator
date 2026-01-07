package agh.ics.oop.model;

public record Boundary(Vector2d lowerLeft, Vector2d upperRight) {
    public static Boundary zero() {
        return new Boundary(Vector2d.zero(), Vector2d.zero());
    }

    public int width() {
        return upperRight.x() - lowerLeft.x() + 1;
    }

    public int height() {
        return upperRight.y() - lowerLeft.y() + 1;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Boundary) {
            final var b = (Boundary)o;
            return lowerLeft.equals(b.lowerLeft) && upperRight.equals(b.upperRight);
        }
        return false;
    }
}
