package se.fusion1013.cobaltCore.particle;

import org.bukkit.util.Vector;
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

}
