package se.fusion1013.cobaltCore.shape.tesseract;

public class Vertex {

  private double x;
  private double y;
  private double z;
  private double w;

  public Vertex(double x, double y, double z, double w) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
  }

  public void rotate(double xr, double yr, double zr, double wr) {
    // 4D rotation on YW axis
    double yy = y;
    y = yy * Math.cos(wr) - w * Math.sin(wr);
    w = yy * Math.sin(wr) + w * Math.cos(wr);
    // Constants
    double x = this.x;
    double y = this.y;
    double z = this.z;
    // Rotation Data
    double sx = Math.sin(xr);
    double sy = Math.sin(yr);
    double sz = Math.sin(zr);
    double cx = Math.cos(xr);
    double cy = Math.cos(yr);
    double cz = Math.cos(zr);
    // Repeating parts of equation
    double eq1 = sz * y + cz * x;
    double eq2 = cz * y - sz * x;
    double eq3 = cy * z + sy * eq1;
    // Applying Transformations
    this.x = cy * eq1 - sy * z;
    this.y = sx * eq3 + cx * eq2;
    this.z = cx * eq3 - sx * eq2;
  }

  public void project() {
    // Projects 4D to 3D
    double focalLength = 35.0;
    double cw = focalLength * focalLength;
    w -= (cw) / (focalLength);
    x = -x / w * focalLength;
    y = -y / w * focalLength;
    z = -z / w * focalLength;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getZ() {
    return z;
  }

  public double getW() {
    return w;
  }
}
