package agh.ics.oop.model;

public record Vector2d(int x, int y) {
    public static Vector2d zero() {
        return new Vector2d(0, 0);
    }

    public static Vector2d max() {
        return new Vector2d(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public static Vector2d min() {
        return new Vector2d(Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }

    public Vector2d add(Vector2d other) {
        return new Vector2d(
                x + other.x(),
                y + other.y()
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vector2d)) {
            return false;
        }
        return equals((Vector2d) obj);
    }

    public boolean equals(Vector2d other) {
        return other != null && x == other.x() && y == other.y();
    }
}
