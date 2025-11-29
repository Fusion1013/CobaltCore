package se.fusion1013.cobaltCore.shape.tesseract;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.util.Vector;

public class Tesseract {

  public static List<Vector> generateTesseract(
      double width, double wGap, int density, double rx, double ry, double rz, double rw) {

    // 1. Generate 16 vertices of a tesseract
    Vertex4D[] verts = new Vertex4D[16];
    int index = 0;
    for (int x = -1; x <= 1; x += 2) {
      for (int y = -1; y <= 1; y += 2) {
        for (int z = -1; z <= 1; z += 2) {
          for (int w = -1; w <= 1; w += 2) {
            verts[index++] =
                new Vertex4D(
                    x * (width / 2.0), y * (width / 2.0), z * (width / 2.0), w * (wGap / 2.0));
          }
        }
      }
    }

    // 2. Rotate every point in 4D (XY, XZ, YZ, and W-mixing)
    for (Vertex4D v : verts) {
      v.rotate4D(rx, ry, rz, rw);
    }

    // 3. Project into 3D
    for (Vertex4D v : verts) {
      v.projectTo3D(120);
    }

    // 4. Generate list of edges (two vertices differ by only one coordinate)
    List<int[]> edges = new ArrayList<>();
    for (int i = 0; i < verts.length; i++) {
      for (int j = i + 1; j < verts.length; j++) {
        if (hypercubeEdge(i, j)) {
          edges.add(new int[] {i, j});
        }
      }
    }

    // 5. Sample points along every edge
    List<Vector> points = new ArrayList<>();
    for (int[] e : edges) {
      Vertex4D a = verts[e[0]];
      Vertex4D b = verts[e[1]];
      points.addAll(lineSample(a, b, density));
    }

    return points;
  }

  private static boolean hypercubeEdge(int i, int j) {
    // Two vertices of a tesseract share an edge if they differ in exactly 1 bit
    return Integer.bitCount(i ^ j) == 1;
  }

  private static List<Vector> lineSample(Vertex4D a, Vertex4D b, int density) {
    List<Vector> pts = new ArrayList<>();
    for (int i = 0; i <= density; i++) {
      double t = (double) i / density;
      pts.add(new Vector(lerp(a.x, b.x, t), lerp(a.y, b.y, t), lerp(a.z, b.z, t)));
    }
    return pts;
  }

  private static double lerp(double a, double b, double t) {
    return a + (b - a) * t;
  }

  public static class Vertex4D {
    double x, y, z, w;

    public Vertex4D(double x, double y, double z, double w) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.w = w;
    }

    public void rotate4D(double rx, double ry, double rz, double rw) {
      // XY rotation
      rotXY(rx);
      // XZ rotation
      rotXZ(ry);
      // YZ rotation
      rotYZ(rz);
      // XW, YW, ZW mix rotation
      rotXW(rw);
      rotYW(rw);
      rotZW(rw);
    }

    private void rotXY(double a) {
      double cx = Math.cos(a), sx = Math.sin(a);
      double nx = cx * x - sx * y;
      double ny = sx * x + cx * y;
      x = nx;
      y = ny;
    }

    private void rotXZ(double a) {
      double cx = Math.cos(a), sx = Math.sin(a);
      double nx = cx * x - sx * z;
      double nz = sx * x + cx * z;
      x = nx;
      z = nz;
    }

    private void rotYZ(double a) {
      double cx = Math.cos(a), sx = Math.sin(a);
      double ny = cx * y - sx * z;
      double nz = sx * y + cx * z;
      y = ny;
      z = nz;
    }

    private void rotXW(double a) {
      double c = Math.cos(a), s = Math.sin(a);
      double nx = c * x - s * w;
      double nw = s * x + c * w;
      x = nx;
      w = nw;
    }

    private void rotYW(double a) {
      double c = Math.cos(a), s = Math.sin(a);
      double ny = c * y - s * w;
      double nw = s * y + c * w;
      y = ny;
      w = nw;
    }

    private void rotZW(double a) {
      double c = Math.cos(a), s = Math.sin(a);
      double nz = c * z - s * w;
      double nw = s * z + c * w;
      z = nz;
      w = nw;
    }

    public void projectTo3D(double scale) {
      double denom = w + scale;
      if (denom == 0) denom = 0.0001;
      x = (x * scale) / denom;
      y = (y * scale) / denom;
      z = (z * scale) / denom;
    }
  }
}
