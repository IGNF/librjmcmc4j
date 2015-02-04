package fr.ign.geometry;

public class Segment2D {
  Point2D start;

  public Point2D getStart() {
    return this.start;
  }

  Point2D end;

  public Point2D getEnd() {
    return this.end;
  }

  public Segment2D(Point2D a, Point2D b) {
    this.start = a;
    this.end = b;
  }

  public Segment2D(Segment2D s) {
    this.start = s.start;
    this.end = s.end;
  }

  public double length() {
    double dx = this.end.x - this.start.x;
    double dy = this.end.y - this.start.y;
    return Math.sqrt(dx * dx + dy * dy);
  }
}
