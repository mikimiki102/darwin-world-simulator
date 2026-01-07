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

    public boolean precedes(Vector2d other) {
        return x <= other.x && y <= other.y;
    }

    public boolean follows(Vector2d other) {
        return other.precedes(this);
    }

    public Vector2d add(Vector2d other) {
        return new Vector2d(
                x + other.x(),
                y + other.y()
        );
    }

    public Vector2d subtract(Vector2d other) {
        return new Vector2d(
                x - other.x(),
                y - other.y()
        );
    }

    public Vector2d upperRight(Vector2d other) {
        return new Vector2d(
                Math.max(x, other.x()),
                Math.max(y, other.y())
        );
    }

    public Vector2d lowerLeft(Vector2d other) {
        return new Vector2d(
                Math.min(x, other.x()),
                Math.min(y, other.y())
        );
    }

    public Vector2d opposite() {
        return new Vector2d(-x, -y);
    }

//    public Vector2d toGridPosition(Boundary bounds) {
//        return new Vector2d(x - bounds.lowerLeft().x, bounds.upperRight().y - y);
//    }

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
