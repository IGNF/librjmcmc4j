package fr.ign.geometry;

public class PointInRectangle {
  Rectangle2D a;
  Point2D p;

  public PointInRectangle(Rectangle2D a, Point2D p) {
    this.a = a;
    this.p = p;
  }

  public boolean isPointInRectangle() {
    return isPointInRectangle(a, p);
  }

  static boolean isPointInRectangle(Rectangle2D a, Point2D p) {
    return isPointInRectangleTriangleAreas(a, p);
  }

  public static boolean isPointInRectangleTriangleAreas(Rectangle2D a, Point2D p) {
    Point2D p1 = a.point(0);
    Point2D p2 = a.point(1);
    Point2D p3 = a.point(2);
    Point2D p4 = a.point(3);
    double halfArea = a.getArea() / 2;
    return triangleArea(p, p1, p2) <= halfArea && triangleArea(p, p2, p3) <= halfArea && triangleArea(p, p3, p4) <= halfArea
        && triangleArea(p, p4, p1) <= halfArea;
  }

  public static boolean isPointInRectangleTriangleAreasSum(Rectangle2D a, Point2D p) {
    Point2D p1 = a.point(0);
    Point2D p2 = a.point(1);
    Point2D p3 = a.point(2);
    Point2D p4 = a.point(3);
    double a1 = triangleArea(p, p1, p2);
    double a2 = triangleArea(p, p2, p3);
    double a3 = triangleArea(p, p3, p4);
    double a4 = triangleArea(p, p4, p1);
    System.out.println("" + a1 + " " + a2 + " " + a3 + " " + a4 + " = " + (a1 + a2 + a3 + a4));
    return a1 + a2 + a3 + a4 <= a.getArea();// + 0.0000001;
  }

  public static double triangleAreaHeron(Point2D p, Point2D p1, Point2D p2) {
    double a = Math.sqrt(p.squaredDistance(p1));
    double b = Math.sqrt(p.squaredDistance(p2));
    double c = Math.sqrt(p1.squaredDistance(p2));
    double s = (a + b + c) / 2;
    return Math.sqrt(s * (s - a) * (s - b) * (s - c));
  }

  public static double triangleArea(Point2D p, Point2D p1, Point2D p2) {
    Vector2D u = new Vector2D(p, p1);
    Vector2D v = new Vector2D(p, p2);
    return Math.abs(u.crossProduct(v)) / 2;
  }
}
