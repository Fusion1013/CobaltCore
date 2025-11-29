package se.fusion1013.cobaltCore.shape;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.util.Vector;

public class BezierUtil {
  public static List<Vector> bezierCurve(
      Vector start, // P0
      Vector dirA, // direction *offset* from start
      Vector dirB, // direction *offset* from end
      Vector end, // P3
      int points) {
    // Convert direction vectors into control points
    Vector p0 = start.clone();
    Vector p1 = start.clone().add(dirA); // control point 1
    Vector p2 = end.clone().add(dirB); // control point 2
    Vector p3 = end.clone();

    List<Vector> list = new ArrayList<>();

    for (int i = 0; i <= points; i++) {
      double t = (double) i / points;
      list.add(cubicBezierPoint(p0, p1, p2, p3, t));
    }

    return list;
  }

  public static Vector cubicBezierPoint(Vector p0, Vector p1, Vector p2, Vector p3, double t) {
    double u = 1 - t;
    double tt = t * t;
    double uu = u * u;
    double uuu = uu * u;
    double ttt = tt * t;

    Vector p = p0.clone().multiply(uuu); // u³ * P0
    p.add(p1.clone().multiply(3 * uu * t)); // 3u²t * P1
    p.add(p2.clone().multiply(3 * u * tt)); // 3ut² * P2
    p.add(p3.clone().multiply(ttt)); // t³ * P3

    return p;
  }

  public static Vector cubicBezierTangent(Vector p0, Vector p1, Vector p2, Vector p3, double t) {
    double u = 1 - t;
    return p1.clone()
        .subtract(p0)
        .multiply(3 * u * u)
        .add(p2.clone().subtract(p1).multiply(6 * u * t))
        .add(p3.clone().subtract(p2).multiply(3 * t * t));
  }
}
