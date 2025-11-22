package se.fusion1013.cobaltCore.particle;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltCore.util.VectorUtil;

import java.util.ArrayList;
import java.util.List;

public class ParticleShapeUtils {

    public static List<Vector> generateSphere(double radius, int count) {
        List<Vector> points = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            double theta = Math.random() * 2 * Math.PI;      // azimuth
            double phi = Math.acos(2 * Math.random() - 1);   // polar

            double x = radius * Math.sin(phi) * Math.cos(theta);
            double y = radius * Math.sin(phi) * Math.sin(theta);
            double z = radius * Math.cos(phi);

            points.add(new Vector(x, y, z));
        }

        return points;
    }

    public static List<Vector> generateCube(double size, int count) {
        int particlesPerSide = Math.floorDiv(count, 12) - 1;
        List<Vector> points = new ArrayList<>(count);

        double a = size / 2;
        double angleX, angleY;
        Vector v = new Vector();
        for (int i = 0; i < 4; i++) {
            angleY = i * Math.PI / 2;
            for (int j = 0; j < 2; j++) {
                angleX = j * Math.PI;
                for (int p = 0; p <= particlesPerSide; p++) {
                    v.setX(a).setY(a);
                    v.setZ(size * p / particlesPerSide - a);
                    VectorUtil.rotateAroundAxisX(v, angleX);
                    VectorUtil.rotateAroundAxisY(v, angleY);
                    points.add(v.clone());
                }
            }
            for (int p = 0; p <= particlesPerSide; p++) {
                v.setX(a).setZ(a);
                v.setY(size * p / particlesPerSide - a);
                VectorUtil.rotateAroundAxisY(v, angleY);
                points.add(v.clone());
            }
        }

        return points;
    }

    public static List<Vector> generateTorus(double majorRadius, double minorRadius, int count) {
        List<Vector> points = new ArrayList<>(count);

        // Choose grid resolution (approximate square root split)
        int uSteps = (int) Math.sqrt(count);
        int vSteps = (int) Math.ceil((double) count / uSteps);

        for (int i = 0; i < uSteps; i++) {
            double u = 2 * Math.PI * i / uSteps;
            for (int j = 0; j < vSteps; j++) {
                if (points.size() >= count) break;

                double v = 2 * Math.PI * j / vSteps;

                double x = (majorRadius + minorRadius * Math.cos(v)) * Math.cos(u);
                double y = (majorRadius + minorRadius * Math.cos(v)) * Math.sin(u);
                double z = minorRadius * Math.sin(v);

                points.add(new Vector(x, y, z));
            }
        }

        return points;
    }

    public static List<Vector> generateFibonacciSphere(double radius, int count) {
        List<Vector> points = new ArrayList<>(count);
        double goldenAngle = Math.PI * (3 - Math.sqrt(5)); // ~2.399963...

        for (int i = 0; i < count; i++) {
            double y = 1 - (2.0 * i) / (count - 1); // y goes from 1 to -1
            double r = Math.sqrt(1 - y * y);        // radius at this y

            double theta = goldenAngle * i;

            double x = Math.cos(theta) * r;
            double z = Math.sin(theta) * r;

            points.add(new Vector(x * radius, y * radius, z * radius));
        }

        return points;
    }

    /**
     * Generates animated vector points in a spiral pattern that moves up and down
     * smoothly over time.
     *
     * @param radius        Radius of the spiral
     * @param height        Maximum vertical height
     * @param turns         Number of spiral turns
     * @param density       Number of points along the spiral
     * @return List of Vectors representing animated spiral points
     */
    public static List<Vector> getAnimatedSpiral(
            double radius,
            double height,
            int turns,
            int density
    ) {
        long time = System.currentTimeMillis();

        List<Vector> points = new ArrayList<>(density);

        // Controls how fast the vertical wave moves
        double timeSpeed = time * 0.002;

        for (int i = 0; i < density; i++) {

            // Progress along spiral [0..1]
            double progress = (double) i / (double) density;

            // Angle around the spiral (more turns = tighter coil)
            double angle = progress * turns * Math.PI * 2;

            // Base spiral position before animation
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            double y = progress * height;

            // Vertical animation using a time-based sine wave
            double wave = Math.sin(angle + timeSpeed) * (height * 0.1);

            // Apply wave motion
            y += wave;

            // Convert relative position to world position
            Vector spiralPoint = new Vector(x, y, z);

            points.add(spiralPoint);
        }

        return points;
    }

    /**
     * Generates N animated particle points arranged in a rotating circle,
     * where each particle also moves up and down smoothly over time.
     *
     * @param radius     Radius of the circle
     * @param particles  Number of particles in the ring
     * @param rotationSpeed      Rotation speed (bigger = faster rotation)
     * @param waveHeight Maximum vertical displacement from the sine wave
     * @return List of animated particle locations as vectors
     */
    public static List<Vector> getAnimatedCircle(
            double radius,
            int particles,
            double rotationSpeed,
            double heightSpeed,
            double waveHeight
    ) {
        long time = System.currentTimeMillis();

        // Convert time to rotational phase
        double rotation = time * rotationSpeed * 0.001;

        List<Vector> points = new ArrayList<>(particles);

        for (int i = 0; i < particles; i++) {

            // Normalized position around the circle [0..1]
            double progress = (double) i / (double) particles;

            // Base angle for evenly spaced placement
            double angle = progress * Math.PI * 2;

            // Add global rotation
            double animatedAngle = angle + rotation;

            // Compute horizontal coordinates
            double x = Math.cos(animatedAngle) * radius;
            double z = Math.sin(animatedAngle) * radius;

            // Vertical wave based on time + particle position
            double yWave = Math.sin(animatedAngle + time * heightSpeed) * waveHeight;

            // Create final position (world-relative)
            Vector point = new Vector(x,yWave,z
            );

            points.add(point);
        }

        return points;
    }

    // -- SHAPE MODIFIERS

    public static List<Vector> applyWiggle(List<Vector> points, double amplitude, double frequency) {
        long time = System.currentTimeMillis();

        List<Vector> wiggled = new ArrayList<>(points.size());

        for (int i = 0; i < points.size(); i++) {
            Vector original = points.get(i).clone();

            // Time-based offset using sin wave
            double offset = Math.sin((i * frequency) + time * 0.002) * amplitude;

            // Apply the wiggle on X and Z (you can change this)
            original.setX(original.getX() + offset);
            original.setZ(original.getZ() + offset * 0.5); // smaller on Z for asymmetry

            wiggled.add(original);
        }

        return wiggled;
    }

    public static List<Vector> applyPositionWiggle(List<Vector> points, double amplitude, double frequency) {
        long time = System.currentTimeMillis();

        List<Vector> result = new ArrayList<>(points.size());

        for (Vector original : points) {
            Vector v = applyPositionWiggle(amplitude, frequency, original, time);
            result.add(v);
        }

        return result;
    }

    public static @NotNull Vector applyPositionWiggle(double amplitude, double frequency, Vector original, long time) {
        Vector v = original.clone();

        // Use the point's spatial position to drive the wiggle
        double x = v.getX();
        double y = v.getY();
        double z = v.getZ();

        // Compute a position-based wiggle using a smooth sine function
        double wiggle =
                Math.sin((x + time * 0.001) * frequency) +
                        Math.sin((y + time * 0.001) * frequency * 1.3) +
                        Math.sin((z + time * 0.001) * frequency * 0.7);

        wiggle /= 3.0; // normalize to [-1, 1]

        // Apply wiggle in a direction of your choice
        v.add(new Vector(
                wiggle * amplitude,
                wiggle * amplitude * 0.7,
                wiggle * amplitude * 0.5
        ));
        return v;
    }

}
