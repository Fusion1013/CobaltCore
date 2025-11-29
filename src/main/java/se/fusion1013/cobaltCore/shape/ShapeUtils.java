package se.fusion1013.cobaltCore.shape;

import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import se.fusion1013.cobaltCore.util.VectorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static se.fusion1013.cobaltCore.shape.BezierUtil.cubicBezierPoint;
import static se.fusion1013.cobaltCore.shape.BezierUtil.cubicBezierTangent;

/**
 * Utility class for generating lists of points.
 */
public class ShapeUtils {

    private static final Random random = new Random();

    public static List<Vector> spiralAroundPath(
            List<Vector> path,
            double radius,
            double turns,
            double time,
            int detailPerSegment,
            double wiggleAmplitude) {
        List<Vector> out = new ArrayList<>();
        if (path.size() < 2) return out;

        // ---- 1. Compute tangents for each segment ----
        List<Vector> tangents = new ArrayList<>();
        for (int i = 0; i < path.size() - 1; i++) {
            tangents.add(path.get(i + 1).clone().subtract(path.get(i)).normalize());
        }

        // ---- 2. Initial frame (normal + binormal) ----
        Vector initialTangent = tangents.get(0).clone();
        Vector arbitrary = new Vector(0, 1, 0);

        // Avoid near-parallel vectors
        if (Math.abs(initialTangent.dot(arbitrary)) > 0.9) {
            arbitrary = new Vector(1, 0, 0);
        }

        Vector normal = initialTangent.clone().crossProduct(arbitrary).normalize();
        Vector binormal = initialTangent.clone().crossProduct(normal).normalize();

        double totalLength = 0;
        List<Double> segmentLengths = new ArrayList<>();

        for (int i = 0; i < path.size() - 1; i++) {
            double len = path.get(i + 1).distance(path.get(i));
            segmentLengths.add(len);
            totalLength += len;
        }

        double accumulated = 0;
        double wiggleTime = 0;

        // ---- 3. Build spiral with parallel transport ----
        for (int i = 0; i < path.size() - 1; i++) {

            Vector tangent = tangents.get(i).clone();

            for (int s = 0; s < detailPerSegment; s++) {
                double tSeg = (double) s / detailPerSegment;
                wiggleTime += 0.1;

                Vector base =
                        path.get(i).clone().add(path.get(i + 1).clone().subtract(path.get(i)).multiply(tSeg));

                double t = (accumulated + segmentLengths.get(i) * tSeg) / totalLength;
                double angle = turns * Math.PI * 2 * t + time;

                // Create offset using current frame
                double calculatedRadius = radius + Math.sin(wiggleTime) * wiggleAmplitude;
                double x = Math.cos(angle) * calculatedRadius;
                double y = Math.sin(angle) * calculatedRadius;

                Vector point =
                        base.clone().add(normal.clone().multiply(x)).add(binormal.clone().multiply(y));

                out.add(point);
            }

            // ---- 4. Parallel transport the frame ----
            // Compute rotation needed to align old tangent to the next tangent
            if (i < tangents.size() - 1) {
                Vector newTangent = tangents.get(i + 1).clone();

                // Rotation axis
                Vector axis = tangent.clone().crossProduct(newTangent);
                double axisLength = axis.length();

                if (axisLength > 1e-6) {
                    axis.normalize();
                    double angle = Math.acos(tangent.dot(newTangent));

                    // Rotate normal and binormal around axis
                    normal = rotateAroundAxis(normal, axis, angle);
                    binormal = rotateAroundAxis(binormal, axis, angle);
                }
            }

            accumulated += segmentLengths.get(i);
        }

        return out;
    }

    private static Vector rotateAroundAxis(Vector v, Vector axis, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        return v.clone()
                .multiply(cos)
                .add(axis.clone().crossProduct(v).multiply(sin))
                .add(axis.clone().multiply(axis.dot(v) * (1 - cos)));
    }

    public static List<Vector> generateSpiralConnector(
            Vector start,
            Vector dirStart,
            Vector dirEnd,
            Vector end,
            int points,
            double radius,
            double spiralTurns,
            double time) {
        List<Vector> out = new ArrayList<>();

        // Control points are offsets from endpoints
        Vector p0 = start.clone();
        Vector p1 = start.clone().add(dirStart);
        Vector p2 = end.clone().add(dirEnd);
        Vector p3 = end.clone();

        for (int i = 0; i <= points; i++) {
            double t = (double) i / points;

            // 1) Base point on cubic Bézier curve
            Vector base = cubicBezierPoint(p0, p1, p2, p3, t);

            // 2) Approximate tangent along curve (for orientation)
            Vector ahead = cubicBezierPoint(p0, p1, p2, p3, Math.min(1, t + 0.01));
            Vector tangent = ahead.clone().subtract(base).normalize();

            // 3) Create a perpendicular frame (normal + binormal)
            Vector arbitrary = new Vector(0, 1, 0);
            if (Math.abs(tangent.dot(arbitrary)) > 0.9) arbitrary = new Vector(1, 0, 0);

            Vector normal = tangent.clone().crossProduct(arbitrary).normalize();
            Vector binormal = tangent.clone().crossProduct(normal).normalize();

            // 4) Spiral angle
            double angle = (t * spiralTurns * Math.PI * 2) + time;

            // 5) Offset around Bézier curve
            double x = Math.cos(angle) * radius;
            double y = Math.sin(angle) * radius;

            Vector spiraled =
                    base.clone().add(normal.clone().multiply(x)).add(binormal.clone().multiply(y));

            out.add(spiraled);
        }

        return out;
    }

    public static List<Vector> generateSpiralConnector2(
            Vector start, // point A
            Vector end, // point B
            Vector dirA, // outgoing direction at A
            Vector dirB, // incoming direction at B
            double radius, // spiral radius
            double turns, // number of turns
            double wiggleAmp, // animation "organic" amplitude
            double time, // time variable
            int points // resolution
    ) {
        List<Vector> list = new ArrayList<>();

        List<Vector> bezierPoints = BezierUtil.bezierCurve(start, dirA, dirB, end, points);
        for (int i = 0; i < points; i++) {
            double t = (double) i / points;

            Vector p = bezierPoints.get(i);

            // ---- 2) Curve tangent (for spiral frame) ----
            Vector tangent = cubicBezierTangent(start, dirA, dirB, end, t).normalize();

            // ---- 3) Find a perpendicular frame (normal & binormal) ----
            Vector arbitrary =
                    Math.abs(tangent.getY()) < 0.99 ? new Vector(0, 1, 0) : new Vector(1, 0, 0);

            Vector normal = tangent.clone().crossProduct(arbitrary).normalize();
            Vector binormal = tangent.clone().crossProduct(normal).normalize();

            // ---- 4) Spiral angle with animation wiggle ----
            double angle = (t * turns * Math.PI * 2) + time * 2.0;

            double dynamicRadius = radius + Math.sin(time * 3 + t * 12) * wiggleAmp;

            // ---- 5) Offset perpendicular to the curve ----
            double nx = Math.cos(angle) * normal.getX() + Math.sin(angle) * binormal.getX();
            double ny = Math.cos(angle) * normal.getY() + Math.sin(angle) * binormal.getY();
            double nz = Math.cos(angle) * normal.getZ() + Math.sin(angle) * binormal.getZ();

            Vector offset = new Vector(nx, ny, nz).multiply(dynamicRadius);

            list.add(p.add(offset));
        }

        return list;
    }

    public static List<Vector> generate3DSpirograph(
            double majorRadius, // major radius of torus
            double minorRadius, // minor radius of torus
            double amplitude, // amplitude in the tube
            double verticalTwistAmplitude, // vertical twist amplitude
            double freq1, // frequency around the torus
            double freq2, // frequency inside the torus tube
            double freq3, // vertical frequency
            int points // number of points
    ) {
        List<Vector> list = new ArrayList<>();

        for (int i = 0; i < points; i++) {
            double t = (Math.PI * 2) * (i / (double) points);

            // Spirograph-like torus sampling
            double theta = t * freq1; // around torus
            double phi = t * freq2; // inside tube
            double psi = t * freq3; // vertical twist

            // Torus base coordinates
            double x = (majorRadius + minorRadius * Math.cos(phi)) * Math.cos(theta);
            double y = (majorRadius + minorRadius * Math.cos(phi)) * Math.sin(theta);
            double z = minorRadius * Math.sin(phi);

            // Apply Spirograph distortions
            x += Math.cos(psi) * amplitude;
            y += Math.sin(psi) * amplitude;
            z += Math.sin(psi * 1.2) * verticalTwistAmplitude;

            list.add(new Vector(x, y, z));
        }

        return list;
    }

    /**
     * Generates a spirograph.
     *
     * @param fixedCircleRadius   radius of the fixed circle.
     * @param rollingCircleRadius radius of the rolling circle.
     * @param drawingPointOffset  offset of the drawing point.
     * @param points              number of sampled points.
     * @param epitrochoid         true = epitrochoid, false = hypotrochoid
     * @return a list of points along a spirograph.
     */
    public static List<Vector> generateSpirograph(
            double fixedCircleRadius,
            double rollingCircleRadius,
            double drawingPointOffset,
            int points,
            boolean epitrochoid,
            double time,
            double height) {
        List<Vector> list = new ArrayList<>();

        for (int i = 0; i < points; i++) {
            double t = (Math.PI * 2) * (i / (double) points);

            double k =
                    fixedCircleRadius + (epitrochoid ? rollingCircleRadius : -rollingCircleRadius); // R ± r
            double ratio = k / rollingCircleRadius; // frequency ratio

            double x = k * Math.cos(t) - drawingPointOffset * Math.cos(ratio * t);
            double y = k * Math.sin(t) - drawingPointOffset * Math.sin(ratio * t);
            double z = Math.sin(time * 2) * height * i;
            list.add(new Vector(x, y, z));
        }

        return list;
    }

    /**
     * Generates a line from one point to another.
     *
     * @param startPoint the starting point.
     * @param endPoint   the end point.
     * @param density    the density of the line.
     * @return a list of points in the shape of a line.
     */
    public static List<Vector> generateLine(Vector startPoint, Vector endPoint, int density) {
        List<Vector> points = new ArrayList<>();
        double distance = startPoint.distance(endPoint);
        int steps = (int) Math.round(density * distance);
        Vector direction = endPoint.clone().subtract(startPoint).normalize();
        for (int i = 0; i < steps; i++) {
            // double r = randomRange <= 0 ? 0 : random.nextDouble(-randomRange, randomRange);
            // TODO: Random offset
            Vector location = direction.clone().multiply((double) i / (double) density);
            points.add(location);
        }
        return points;
    }

    public static List<Vector> generateCircle(double radius, int iterations, boolean randomPlacement) {
        if (randomPlacement) return generateCircleRandomPlacement(radius, iterations);
        else return generateCircleEvenPlacement(radius, iterations);
    }

    private static List<Vector> generateCircleRandomPlacement(double radius, int iterations) {
        List<Vector> points = new ArrayList<>();
        for (int i = 0; i < iterations; i++) {
            double angle = ((Math.PI * 2) / iterations) * random.nextDouble(0, 1) * iterations;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            points.add(new Vector(x, 0, z));
        }
        return points;
    }

    /**
     * Generates a circle.
     *
     * @param radius     the radius of the circle.
     * @param iterations the number of points along the circle.
     * @return a list of points in the shape of a circle.
     */
    public static List<Vector> generateCircleEvenPlacement(double radius, int iterations) {
        List<Vector> points = new ArrayList<>();

        for (int i = 0; i < iterations; i++) {
            double angle = ((Math.PI * 2) / iterations) * i;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            points.add(new Vector(x, 0, z));
        }

        return points;
    }

    /**
     * Generates a sphere with points on the surface.
     *
     * @param radius the radius of the sphere.
     * @param count  the number of points on the surface of the sphere.
     * @return a list of points in the shape of a sphere.
     */
    public static List<Vector> generateSphere(double radius, int count) {
        List<Vector> points = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            double theta = Math.random() * 2 * Math.PI; // azimuth
            double phi = Math.acos(2 * Math.random() - 1); // polar

            double x = radius * Math.sin(phi) * Math.cos(theta);
            double y = radius * Math.sin(phi) * Math.sin(theta);
            double z = radius * Math.cos(phi);

            points.add(new Vector(x, y, z));
        }

        return points;
    }

    /**
     * Generates a cube with particles at the edges.
     *
     * @param size  the size of the cube.
     * @param count the number of points along the edges of the cube.
     * @return a list of points in the shape of a cube.
     */
    public static List<Vector> generateCube(double size, int count) {
        int particlesPerSide = Math.floorDiv(count, 12) - 1;
        List<Vector> points = new ArrayList<>(count);

        double a = size / 2;
        double angleX;
        double angleY;
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

    /**
     * Generates a torus.
     *
     * @param majorRadius the major radius of the torus.
     * @param minorRadius the minor radius of the torus.
     * @param count       the number of points on the surface of the torus.
     * @return a list of points in the shape of a torus.
     */
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

    /**
     * Generates a fibonacci sphere.
     *
     * @param radius the radius of the sphere.
     * @param count  the number of points on the surface of the sphere.
     * @return a list of points in the shape of a fibonacci sphere.
     */
    public static List<Vector> generateFibonacciSphere(double radius, int count) {
        List<Vector> points = new ArrayList<>(count);
        double goldenAngle = Math.PI * (3 - Math.sqrt(5)); // ~2.399963...

        for (int i = 0; i < count; i++) {
            double y = 1 - (2.0 * i) / (count - 1); // y goes from 1 to -1
            double r = Math.sqrt(1 - y * y); // radius at this y

            double theta = goldenAngle * i;

            double x = Math.cos(theta) * r;
            double z = Math.sin(theta) * r;

            points.add(new Vector(x * radius, y * radius, z * radius));
        }

        return points;
    }

    /**
     * Generates animated vector points in a spiral pattern that moves up and down smoothly over time.
     *
     * @param radius  Radius of the spiral
     * @param height  Maximum vertical height
     * @param turns   Number of spiral turns
     * @param density Number of points along the spiral
     * @return List of Vectors representing animated spiral points
     */
    public static List<Vector> getAnimatedSpiral(
            double radius, double height, int turns, int density) {
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
     * Generates N animated particle points arranged in a rotating circle, where each particle also
     * moves up and down smoothly over time.
     *
     * @param radius        Radius of the circle
     * @param particles     Number of particles in the ring
     * @param rotationSpeed Rotation speed (bigger = faster rotation)
     * @param waveHeight    Maximum vertical displacement from the sine wave
     * @return List of animated particle locations as vectors
     */
    public static List<Vector> getAnimatedCircle(
            double radius, int particles, double rotationSpeed, double heightSpeed, double waveHeight) {
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
            Vector point = new Vector(x, yWave, z);

            points.add(point);
        }

        return points;
    }

    // -- SHAPE MODIFIERS

    /**
     * Applies a wiggle to the points in the order that they are passed to the method.
     *
     * @param points    the points to apply the wiggle to.
     * @param amplitude the amplitude of the wiggle.
     * @param frequency the frequency of the wiggle.
     * @return a list of points that have the wiggle function applied.
     */
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

    /**
     * Applies a position-based wiggle to the points.
     *
     * @param points    the points to apply the wiggle function to.
     * @param amplitude the amplitude of the wiggle.
     * @param frequency the frequency of the wiggle.
     * @return a list of points that have the wiggle function applied.
     */
    public static List<Vector> applyPositionWiggle(
            List<Vector> points, double amplitude, double frequency) {
        long time = System.currentTimeMillis();

        List<Vector> result = new ArrayList<>(points.size());

        for (Vector original : points) {
            Vector v = applyPositionWiggle(amplitude, frequency, original, time);
            result.add(v);
        }

        return result;
    }

    /**
     * Applies a position-based wiggle to the points.
     *
     * @param amplitude the amplitude of the wiggle.
     * @param frequency the frequency of the wiggle.
     * @param original  the point to apply the wiggle to.
     * @param time      the time to base the wiggle of.
     * @return a list of points that have the wiggle function applied.
     */
    public static @NotNull Vector applyPositionWiggle(
            double amplitude, double frequency, Vector original, long time) {
        Vector v = original.clone();

        // Use the point's spatial position to drive the wiggle
        double x = v.getX();
        double y = v.getY();
        double z = v.getZ();

        // Compute a position-based wiggle using a smooth sine function
        double wiggle =
                Math.sin((x + time * 0.001) * frequency)
                        + Math.sin((y + time * 0.001) * frequency * 1.3)
                        + Math.sin((z + time * 0.001) * frequency * 0.7);

        wiggle /= 3.0; // normalize to [-1, 1]

        // Apply wiggle in a direction of your choice
        v.add(new Vector(wiggle * amplitude, wiggle * amplitude * 0.7, wiggle * amplitude * 0.5));
        return v;
    }
}
