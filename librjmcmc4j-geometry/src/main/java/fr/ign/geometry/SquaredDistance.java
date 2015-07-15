package fr.ign.geometry;

public class SquaredDistance {
  Rectangle2D a;
  Rectangle2D b;

  public SquaredDistance(Rectangle2D a, Rectangle2D b) {
    this.a = a;
    this.b = b;
  }
  public double getSquaredDistance() {
    return squared_distance(a, b);
  }

  static double squared_distance(Rectangle2D r, Point2D q) {
    Vector2D v = new Vector2D(q.minus(r.center()));
    double n2 = r.normal().squared_length();
    double x = Math.max(0., Math.abs(r.normal().dotProduct(v)) - n2);
    double y = Math.max(0., Math.abs((r.normal().x() * v.y() - r.normal().y() * v.x())) - Math.abs(r.ratio()) * n2);
    return (x * x + y * y) / n2;
  }

  static double squared_distance(Point2D q, Rectangle2D r) {
    return squared_distance(r, q);
  }

  /*
   * equivalent, but at least twice as fast as return min(min( min(squared_distance(b.point(0)),squared_distance(b.point(1))),
   * min(squared_distance(b.point(2)),squared_distance(b.point(3))) ),min( min(b.squared_distance(point(0)),b.squared_distance(point(1))),
   * min(b.squared_distance(point(2)),b.squared_distance(point(3))) ));
   */
  static double squared_distance(Rectangle2D a, Rectangle2D b) {
    Vector2D v = new Vector2D(a.center(), b.center());
    Vector2D an = new Vector2D(a.normal());
    Vector2D bn = new Vector2D(b.normal());
    double dot = an.dotProduct(bn);
    double det = bn.x() * an.y() - bn.y() * an.x();
    double an2 = an.squared_length();
    double bn2 = bn.squared_length();
    double ar = Math.abs(a.ratio());
    double arn2 = ar * an2;
    double ax = an.dotProduct(v);
    double ay = an.x() * v.y() - an.y() * v.x();
    double br = Math.abs(b.ratio());
    double brn2 = br * bn2;
    double bx = bn.dotProduct(v);
    double by = bn.x() * v.y() - bn.y() * v.x();
    double ardot = ar * dot;
    double ardet = ar * det;
    double brdot = br * dot;
    double brdet = br * det;
    double ax0 = dot + brdet;
    double ax1 = dot - brdet;
    double bx0 = dot - ardet;
    double bx1 = dot + ardet;
    double ay0 = det - brdot;
    double ay1 = det + brdot;
    double by0 = det + ardot;
    double by1 = det - ardot;
    double x[] = {
        Math.max(0., Math.abs(ax - ax0) - an2), Math.max(0., Math.abs(ax + ax0) - an2),
        Math.max(0., Math.abs(ax - ax1) - an2), Math.max(0., Math.abs(ax + ax1) - an2),
        Math.max(0., Math.abs(bx - bx0) - bn2), Math.max(0., Math.abs(bx + bx0) - bn2),
        Math.max(0., Math.abs(bx - bx1) - bn2), Math.max(0., Math.abs(bx + bx1) - bn2)
    };
    double y[] = {
        Math.max(0., Math.abs(ay + ay0) - arn2), Math.max(0., Math.abs(ay - ay0) - arn2),
        Math.max(0., Math.abs(ay + ay1) - arn2), Math.max(0., Math.abs(ay - ay1) - arn2),
        Math.max(0., Math.abs(by - by0) - brn2), Math.max(0., Math.abs(by + by0) - brn2),
        Math.max(0., Math.abs(by - by1) - brn2), Math.max(0., Math.abs(by + by1) - brn2)
    };
    double[] d2 = new double[8];
    for (int i = 0; i < 8; ++i)
      d2[i] = x[i] * x[i] + y[i] * y[i];
    return Math.min(
        Math.min(Math.min(d2[0], d2[1]), Math.min(d2[2], d2[3])) / an2,
        Math.min(Math.min(d2[4], d2[5]), Math.min(d2[6], d2[7])) / bn2);
  }
}
