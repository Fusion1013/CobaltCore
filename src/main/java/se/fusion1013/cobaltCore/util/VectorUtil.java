package se.fusion1013.cobaltCore.util;

import org.bukkit.util.Vector;

import java.util.Random;

public class VectorUtil {
    /**
     * Rotates a vector around the X axis at an angle
     *
     * @param v     Starting vector
     * @param angle How much to rotate
     * @return The starting vector rotated
     */
    public static Vector rotateAroundAxisX(Vector v, double angle) {
        double y, z, cos, sin;
        cos = Math.cos(angle);
        sin = Math.sin(angle);
        y = v.getY() * cos - v.getZ() * sin;
        z = v.getY() * sin + v.getZ() * cos;
        return v.setY(y).setZ(z);
    }

    /**
     * Rotates a vector around the Y axis at an angle
     *
     * @param v     Starting vector
     * @param angle How much to rotate
     * @return The starting vector rotated
     */
    public static Vector rotateAroundAxisY(Vector v, double angle) {
        double x, z, cos, sin;
        cos = Math.cos(angle);
        sin = Math.sin(angle);
        x = v.getX() * cos + v.getZ() * sin;
        z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }

    /**
     * Generates a deterministic normalized vector based on (x, y, z).
     * The same input coordinates will always produce the same output direction.
     *
     * @param x input coordinate
     * @param y input coordinate
     * @param z input coordinate
     * @return deterministic normalized vector
     */
    public static Vector deterministicRandomDirection(double x, double y, double z) {
        // Combine coordinates into a deterministic seed
        long seed = Double.doubleToLongBits(x * 31 + y * 131 + z * 997);
        Random random = new Random(seed);

        // Generate a random direction using spherical coordinates
        double theta = random.nextDouble() * 2 * Math.PI;
        double phi = Math.acos(2 * random.nextDouble() - 1);

        // Convert to Cartesian unit vector
        double nx = Math.sin(phi) * Math.cos(theta);
        double ny = Math.sin(phi) * Math.sin(theta);
        double nz = Math.cos(phi);

        return new Vector(nx, ny, nz);
    }
}
