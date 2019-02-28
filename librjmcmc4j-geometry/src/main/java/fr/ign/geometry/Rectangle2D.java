package fr.ign.geometry;

import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;

public class Rectangle2D implements Primitive {
  public double centerx;
  public double centery;
  public double normalx;
  public double normaly;
  public double ratio;

  public Rectangle2D(double cx, double cy, double nx, double ny, double r) {
    this.centerx = cx;
    this.centery = cy;
    this.normalx = nx;
    this.normaly = ny;
    this.ratio = r;
  }

  public Rectangle2D(double[] v) {
    this.centerx = v[0];
    this.centery = v[1];
    this.normalx = v[2];
    this.normaly = v[3];
    this.ratio = v[4];
  }

  public Point2D point(int i) {
    Vector2D m = new Vector2D(-this.ratio * this.normaly, this.ratio * this.normalx);
    switch (i % 4) {
    case 0: {
      Point2D p = new Point2D(this.centerx - m.x - this.normalx, this.centery - m.y - this.normaly);
      return p;
    }
    case 1: {
      Point2D p = new Point2D(this.centerx - m.x + this.normalx, this.centery - m.y + this.normaly);
      return p;
    }
    case 2: {
      Point2D p = new Point2D(this.centerx + m.x + this.normalx, this.centery + m.y + this.normaly);
      return p;
    }
    default: {
      Point2D p = new Point2D(this.centerx + m.x - this.normalx, this.centery + m.y - this.normaly);
      return p;
    }
    }
  }

  public Segment2D segment(int i) {
    Vector2D m = new Vector2D(-this.ratio * this.normaly, this.ratio * this.normalx);
    switch (i % 4) {
    case 0: {
      Point2D p = new Point2D(this.centerx - m.x, this.centery - m.y);
      Point2D p1 = p.minus(this.normalx, this.normaly);
      Point2D p2 = p.plus(this.normalx, this.normaly);
      return new Segment2D(p1, p2);
    }
    case 1: {
      Point2D p = new Point2D(this.centerx + this.normalx, this.centery + this.normaly);
      Point2D p1 = p.minus(m);
      Point2D p2 = p.plus(m);
      return new Segment2D(p1, p2);
    }
    case 2: {
      Point2D p = new Point2D(this.centerx + m.x, this.centery + m.y);
      Point2D p1 = p.plus(this.normalx, this.normaly);
      Point2D p2 = p.minus(this.normalx, this.normaly);
      return new Segment2D(p1, p2);
    }
    default: {
      Point2D p = new Point2D(this.centerx - this.normalx, this.centery - this.normaly);
      Point2D p1 = p.plus(m);
      Point2D p2 = p.minus(m);
      return new Segment2D(p1, p2);
    }
    }
  }

  @Override
  public int size() {
    return 5;
  }

  @Override
  public double intersectionArea(Primitive p) {
    IntersectionArea i = new IntersectionArea(this, p);
    return i.getArea();
  }

  Polygon geom = null;

  @Override
  public Polygon toGeometry() {
    if (geom == null) {
      GeometryFactory geomFact = new GeometryFactory();
      Coordinate[] pts = new Coordinate[5];
      double mx = -this.ratio * this.normaly;
      double my = this.ratio * this.normalx;
      pts[0] = new Coordinate(this.centerx + this.normalx + mx, this.centery + this.normaly + my);
      pts[1] = new Coordinate(this.centerx - this.normalx + mx, this.centery - this.normaly + my);
      pts[2] = new Coordinate(this.centerx - this.normalx - mx, this.centery - this.normaly - my);
      pts[3] = new Coordinate(this.centerx + this.normalx - mx, this.centery + this.normaly - my);
      pts[4] = new Coordinate(pts[0]);
      LinearRing ring = geomFact.createLinearRing(pts);
      Polygon poly = geomFact.createPolygon(ring, null);
      this.geom = poly;
    }
    return this.geom;
  }

  public static Rectangle2D fromGeometry(Polygon p) {
    double x1 = p.getCoordinates()[0].x;
    double y1 = p.getCoordinates()[0].y;
    // double x2 = p.getCoordinates()[1].x;
    // double y2 = p.getCoordinates()[1].y;
    double x3 = p.getCoordinates()[2].x;
    double y3 = p.getCoordinates()[2].y;
    double x4 = p.getCoordinates()[3].x;
    double y4 = p.getCoordinates()[3].y;
    double centerx = (x1 + x3) / 2;
    double normalx = (x1 + x4 - 2 * centerx) / 2;
    // double mx = x1 - centerx - normalx;
    double centery = (y1 + y3) / 2;
    double normaly = (y1 + y4 - 2 * centery) / 2;
    double my = y1 - centery - normaly;
    double ratio = my / normalx;
    return new Rectangle2D(centerx, centery, normalx, normaly, ratio);
  }

  /**
   * Translates edge(i) linearly from edge(i+2) with f : 0->degenerate, 1->identity... // assert(!is_degenerate());
   * 
   * @param i
   * @param f
   * @return
   */
  public Rectangle2D scaledEdge(int i, double f) {
    Vector2D m = new Vector2D(-this.ratio * this.normaly, this.ratio * this.normalx);
    Point2D p = new Point2D(this.centerx, this.centery);
    Point2D c = null;
    switch (i % 4) {
    case 0:
      c = p.plus(m.scalarMultiply(1 - f));
      return new Rectangle2D(c.x, c.y, this.normalx, this.normaly, f * this.ratio);// c+(1-f)*m
    case 1:
      c = p.minus(this.normalx * (1 - f), this.normaly * (1 - f));
      return new Rectangle2D(c.x, c.y, m.x, m.y, f / this.ratio);// c-(1-f)*n
    case 2:
      c = p.minus(m.scalarMultiply(1 - f));
      return new Rectangle2D(c.y, c.y, -this.normalx, -this.normaly, f * this.ratio);// c-(1-f)*m
    default:
      c = p.plus(this.normalx * (1 - f), this.normaly * (1 - f));
      return new Rectangle2D(c.x, c.y, -m.x, -m.y, f / this.ratio);// c+(1-f)*n
    }
  }

  public Rectangle2D rotate(int i) {
    switch (i % 4) {
    case 0:
      return new Rectangle2D(this.centerx, this.centery, this.normalx, this.normaly, this.ratio);
    case 2:
      return new Rectangle2D(this.centerx, this.centery, -this.normalx, -this.normaly, this.ratio);
    case 1: {
      return new Rectangle2D(this.centerx, this.centery, -this.ratio * this.normaly, this.ratio * this.normalx, 1 / this.ratio);
    }
    default: {
      return new Rectangle2D(this.centerx, this.centery, this.ratio * this.normaly, -this.ratio * this.normalx, 1 / this.ratio);
    }
    }
  }

  @Override
  public String toString() {
    // return
    // "R2D("+this.center.x+","+this.center.y+"),("+this.normal.getX()+","+this.normal.getY()+"),("+this.ratio+") = "
    // + this.toGeometry().toString();
    return this.toGeometry().toString();
  }

  @Override
  public int hashCode() {
    int hashCode = 1;
    double[] array = { this.centerx, this.centery, this.normalx, this.normaly, this.ratio };
    for (double e : array)
      hashCode = 31 * hashCode + hashCode(e);
    return hashCode;
  }

  public int hashCode(double value) {
    long bits = Double.doubleToLongBits(value);
    return (int) (bits ^ (bits >>> 32));
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Rectangle2D)) {
      return false;
    }
    Rectangle2D r = (Rectangle2D) o;
    return this.centerx == r.centerx && this.centery == r.centery && this.normalx == r.normalx && this.normaly == r.normaly
        && this.ratio == r.ratio;
  }

  @Override
  public double[] toArray() {
    return new double[] { this.centerx, this.centery, this.normalx, this.normaly, this.ratio };
  }

  @Override
  public void set(List<Double> v) {
    for (int i = 0; i < v.size(); i++) {
      double value = v.get(i);
      switch (i) {
      case 0:
        this.centerx = value;
        break;
      case 1:
        this.centery = value;
        break;
      case 2:
        this.normalx = value;
        break;
      case 3:
        this.normaly = value;
        break;
      case 4:
        this.ratio = value;
        break;
      }
    }
  }

  @Override
  public Object[] getArray() {
    return new Object[] { this.toGeometry() };
  }

  boolean is_degenerate() {
    return this.ratio * this.normal_squared_length() == 0.;
  }

  double normal_squared_length() {
    return this.normalx * this.normalx + this.normaly * this.normaly;
  }

  public Point2D center() {
    return new Point2D(this.centerx, this.centery);
  }

  public Vector2D normal() {
    return new Vector2D(this.normalx, this.normaly);
  }

  public double ratio() {
    return this.ratio;
  }

  public double getArea() {
    return 4 * ratio * normal_squared_length();
  }
}
