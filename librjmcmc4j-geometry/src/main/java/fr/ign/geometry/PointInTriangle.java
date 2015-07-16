package fr.ign.geometry;

public class PointInTriangle {
  Point2D a;
  Point2D b;
  Point2D c;
  Point2D p;

  public PointInTriangle(Point2D a, Point2D b, Point2D c, Point2D p) {
    this.a = a;
    this.b = b;
    this.c = c;
    this.p = p;
  }

  public boolean isPointInTriangle() {
    return isPointInTriangle(a, b, c, p);
  }

  static boolean isPointInTriangle(Point2D a, Point2D b, Point2D c, Point2D p) {
    return isPointInTriangle(a.x, a.y, b.x, b.y, c.x, c.y, p.x, p.y);
  }

  public static boolean isPointInTriangle(double ax, double ay, double bx, double by, double cx, double cy, double px, double py) {
    // vectors
    double v0x = cx - ax;
    double v0y = cy - ay;
    double v1x = bx - ax;
    double v1y = by - ay;
    double v2x = px - ax;
    double v2y = py - ay;
    // Compute dot products
    double dot00 = v0x * v0x + v0y * v0y;
    double dot01 = v0x * v1x + v0y * v1y;
    double dot02 = v0x * v2x + v0y * v2y;
    double dot11 = v1x * v1x + v1y * v1y;
    double dot12 = v1x * v2x + v1y * v2y;
    // Compute barycentric coordinates
    double invDenom = 1. / (dot00 * dot11 - dot01 * dot01);
    double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
    double v = (dot00 * dot12 - dot01 * dot02) * invDenom;
    // Check if point is in triangle
    return (u >= 0) && (v >= 0) && (u + v < 1);
  }
}
