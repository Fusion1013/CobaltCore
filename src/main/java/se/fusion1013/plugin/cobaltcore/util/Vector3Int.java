package se.fusion1013.plugin.cobaltcore.util;

public class Vector3Int {
    public int x;
    public int y;
    public int z;

    public Vector3Int(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "Vector3Int{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vector3Int v) {
            return v.x == x && v.y == y && v.z == z;
        }

        return false;
    }
}
