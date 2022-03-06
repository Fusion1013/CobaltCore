package se.fusion1013.plugin.cobaltcore.util;

import org.bukkit.util.Vector;

import java.util.Random;

public class GeometryUtil {

    /**
     * Gets a random point on a unit shape.
     *
     * @param shape the shape to get the point on.
     * @return a random point.
     */
    public static Vector getPointOnUnit(Shape shape){
        return switch (shape) {
            case SPHERE -> getPointOnSphere(1);
            case CUBE -> getPointOnCube(1);
        };

    }

    /**
     * Gets a random point in a unit shape.
     *
     * @param shape the shape to get the point in.
     * @return a random point.
     */
    public static Vector getPointInUnit(Shape shape){
        return switch (shape) {
            case SPHERE -> getPointInSphere(1);
            case CUBE -> getPointInCube(1);
        };
    }

    /**
     * Gets a point on a cube with a radius.
     *
     * @param radius the radius of the cube.
     * @return a point on the cube.
     */
    public static Vector getPointOnCube(double radius){
        float[] result = new float[3];

        Random r = new Random();
        int side = r.nextInt(0, 6);
        int c = side%3;

        result[c] = side > 2 ? 1f : 0f;
        result[(c+1)%3] = (float)Math.random();
        result[(c+2)%3] = (float)Math.random();

        return new Vector(result[0]*radius, result[1]*radius, result[2]*radius);
    }

    /**
     * Gets a random point in a cube with a radius.
     *
     * @param radius the radius of the cube.
     * @return a point in the cube.
     */
    public static Vector getPointInCube(double radius){
        return new Vector(Math.random()*radius, Math.random()*radius, Math.random()*radius);
    }

    /**
     * Gets a random point on the surface of a sphere.
     * @param radius the radius of the sphere.
     * @return a random point on the surface of the sphere.
     */
    public static Vector getPointOnSphere(double radius){
        double u = Math.random();
        double v = Math.random();
        double theta = 2 * Math.PI * u;
        double phi = Math.acos(2 * v - 1);
        double dx = radius * Math.sin(phi) * Math.cos(theta);
        double dy = radius * Math.sin(phi) * Math.sin(theta);
        double dz = radius * Math.cos(phi);

        return new Vector(dx, dy, dz);
    }

    /**
     * Gets a random point in a sphere.
     *
     * @param radius the radius of the sphere.
     * @return a random point inside the sphere.
     */
    public static Vector getPointInSphere(double radius){
        double u = Math.random();
        double v = Math.random();
        double theta = u * 2.0 * Math.PI;
        double phi = Math.acos(2.0 * v - 1.0);
        double r = Math.cbrt(Math.random());
        double sinTheta = Math.sin(theta);
        double cosTheta = Math.cos(theta);
        double sinPhi = Math.sin(phi);
        double cosPhi = Math.cos(phi);
        double x = r * sinPhi * cosTheta * radius;
        double y = r * sinPhi * sinTheta * radius;
        double z = r * cosPhi * radius;

        return new Vector(x, y, z);
    }

    public enum Shape{
        SPHERE,
        CUBE
    }

}
