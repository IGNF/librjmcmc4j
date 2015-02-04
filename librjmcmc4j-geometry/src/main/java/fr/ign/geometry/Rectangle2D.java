package fr.ign.geometry;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.util.Assert;

import fr.ign.rjmcmc.kernel.SimpleObject;

public class Rectangle2D implements Primitive, SimpleObject {
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

  public Rectangle2D(List<Double> v) {
    Assert.equals(5, v.size());
    this.centerx = v.get(0).doubleValue();
    this.centery = v.get(1).doubleValue();
    this.normalx = v.get(2).doubleValue();
    this.normaly = v.get(3).doubleValue();
    this.ratio = v.get(4).doubleValue();
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
    return this.toGeometry().intersection(p.toGeometry()).getArea();
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
   * Translates edge(i) linearly from edge(i+2) with f : 0->degenerate, 1->identity...
   * // assert(!is_degenerate());
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
        return new Rectangle2D(this.centerx, this.centery, -this.ratio * this.normaly, this.ratio
            * this.normalx, 1 / this.ratio);
      }
      default: {
        return new Rectangle2D(this.centerx, this.centery, this.ratio * this.normaly, -this.ratio
            * this.normalx, 1 / this.ratio);
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
    return this.centerx == r.centerx && this.centery == r.centery && this.normalx == r.normalx
        && this.normaly == r.normaly && this.ratio == r.ratio;
  }

  public static double triangle_area(double n2, double m2, double tx, double ty, double rx,
      double ry) {
    if (tx >= n2 || ry >= m2)
      return 0; // no intersection, above
    if (tx < -n2) {
      ty = ty + (tx + n2) * (ry - ty) / (tx - rx);
      tx = -n2;
    } // crop left
    if (ry < -m2) {
      rx = rx + (ry + m2) * (rx - tx) / (ty - ry);
      ry = -m2;
    } // crop bottom
    if (ty < ry)
      return 0; // no intersection, underneath
    if (ty <= m2) {
      if (rx <= n2)
        return (ty - ry) * (rx - tx) / 2; // all inside
      return (1 + (rx - n2) / (rx - tx)) * (ty - ry) * (n2 - tx) / 2; // cut right
    }
    if (rx <= n2)
      return (1 + (ty - m2) / (ty - ry)) * (rx - tx) * (m2 - ry) / 2; // cut top
    double mx = tx + (m2 - ty) * (rx - tx) / (ry - ty);
    if (mx >= n2)
      return (n2 - tx) * (m2 - ry); // rectangle
    double ny = ty + (tx - n2) * (ry - ty) / (tx - rx);
    return (mx - tx) * (m2 - ry) + ((m2 + ny) / 2 - ry) * (n2 - mx); // rectangle with the upper
    // right corner cut
  }

  /* Intersection area */
  public static double intersection_area_two_slabs(Rectangle2D a, Rectangle2D b) {
    // if(a.is_degenerate() || b.is_degenerate()) return 0;
    // List<Vector2D> v = new ArrayList<Vector2D>();
    // List<Line> l = new ArrayList<Line>();
    // for(int i=0; i<4; ++i) { v.add(b[i]-O); l.add(b.line(i)); }
    // Vector2d n = a.normal();
    // double r = a.ratio();
    // double n2 = n.squared_length();
    // double rn2 = r*r*n2;
    // Vector2Dvc(a.center()-O);
    // Vector2Dtrn(-r*n.y(),r*n.x());
    // double ctrn = vc*trn;
    // double cn = vc*n;
    //
    // Vector2D[] ln = { n, trn };
    // double lc[] = { cn+n2, ctrn+rn2, cn-n2, ctrn-rn2 };

    // basically it is the iterative intersection of the convex polygon initialized with b
    // with the 2 slabs line0/line2 and line1/line3.
    // for(int i=0; i<2; ++i) {
    // int begin0=-1, end0=-1;
    // int begin1=-1, end1=-1;
    // int n=v.size();
    // for(int j=0; j<n; ++j) {
    // double dot = ln[i]*v[j];
    // if( dot>lc[i]) {
    // if(end0==-1 && begin0!=-1) end0=j;
    // if(begin1<=end1) begin1=j;
    // } else {
    // if(begin0<=end0) begin0=j;
    // if(dot<lc[i+2]) {
    // if(end1==-1 && begin1!=-1) end1=j;
    // } else {
    // if(begin1<=end1) begin1=j;
    // }
    // }
    // }
    // if(begin0==-1 || begin1==-1 ) return 0; // outside the slab
    // if(end0 ==-1) {
    // if(begin0!=0) end0=0;
    // else begin0=begin1;
    // }
    // if(end1 ==-1) {
    // if(begin1!=0) end1=0;
    // else {
    // if(end0==-1) continue; // inside the slab
    // begin1=begin0;
    // }
    // }
    //
    // List<Vector2D> w = new ArrayList<Vector2D>();
    // List<Line> m = new ArrayList<Line>();
    // if(end0!=-1) { // cut outside line(i+1)
    // for(int j=begin1; j!=end0; j=(j+1)%n) {
    // w.add(v[j]);
    // m.add(l[j]);
    // }
    // Point2D inter = new Point2D(0,0);
    // Line li = new Line(ln[i].x(),ln[i].y(),-lc[i]);
    // intersection(l[(end0+n-1)%n],li).assign(inter);
    // w.push_back(inter-O);
    // m.push_back(li);
    // m.push_back(l[(begin0+n-1)%n]);
    // intersection(li,m.back()).assign(inter);
    // w.push_back(inter-O);
    // }
    // if(end1!=-1) { // cut outside line(i+3)
    // for(int j=begin0; j!=end1; j=(j+1)%n) {
    // w.push_back(v[j]);
    // m.push_back(l[j]);
    // }
    // Point2D inter = new Point2D(0,0);
    // Line li = new Line(ln[i].x(),ln[i].y(),-lc[i+2]);
    // intersection(l[(end1+n-1)%n],li).assign(inter);
    // w.push_back(inter-O);
    // m.push_back(li);
    // m.push_back(l[(begin1+n-1)%n]);
    // intersection(li,m.back()).assign(inter);
    // w.push_back(inter-O);
    // }
    // std::swap(v,w);
    // std::swap(l,m);
    // }
    // std::vector<Point_2> p;
    // for(unsigned int i=0; i<v.size(); ++i) { p.push_back(O+v[i]); }
    double area = 0;
    // area_2(p.begin(),p.end(),area);
    return Math.abs(area);
  }

  enum Sign {
    NEGATIVE, POSITIVE, ZERO;
  }

  public static Sign sign(double t) {
    return (t < 0) ? Sign.NEGATIVE : ((t > 0) ? Sign.POSITIVE : Sign.ZERO);
  }

  public static double intersection_area(Rectangle2D a, Rectangle2D b) {
    // if(a.is_degenerate() || b.is_degenerate()) return 0;
    Vector2D v = new Vector2D(b.centerx - a.centerx, b.centery - a.centery);
    Vector2D m = new Vector2D(-a.normaly, a.normalx);
    Vector2D na = new Vector2D(a.normalx, a.normaly);
    Vector2D nb = new Vector2D(b.normalx, b.normaly);
    double n2 = na.getNormSq();
    double m2 = Math.abs(a.ratio) * n2;
    double br = Math.abs(b.ratio);
    double cx = na.dotProduct(v);
    double cy = m.dotProduct(v);
    double nx = na.dotProduct(nb);
    double ny = m.dotProduct(nb);
    switch (sign(nx)) {
      case ZERO: { // m and b.normal() are collinear
        ny = Math.abs(ny);
        double mx = br * ny;
        double lx = cx - mx;
        double rx = cx + mx;
        double by = cy - ny;
        double ty = cy + ny;
        if (rx <= -n2 || n2 <= lx || ty <= -m2 || m2 <= by)
          return 0;
        return (Math.min(n2, rx) - Math.max(-n2, lx)) * (Math.min(m2, ty) - Math.max(-m2, by)) / n2;
      }
      case NEGATIVE: // b.normal() =rotate180(b.normal())
      {
        nx = -nx;
        ny = -ny;
      }
      default:
        ;
    }
    double mx = -br * ny;
    double my = br * nx;
    switch (sign(ny)) {
      case ZERO: { // n and b.normal() are collinear
        double lx = cx - nx;
        double rx = cx + nx;
        double by = cy - my;
        double ty = cy + my;
        if (rx <= -n2 || n2 <= lx || ty <= -m2 || m2 <= by)
          return 0;
        return (Math.min(n2, rx) - Math.max(-n2, lx)) * (Math.min(m2, ty) - Math.max(-m2, by)) / n2;
      }
      case NEGATIVE: // b.normal() =rotate90(b.normal())
        nx = -nx;
        // std::swap(nx,mx);
        double tmp = nx;
        nx = mx;
        mx = tmp;
        ny = -ny;
        // std::swap(ny,my);
        tmp = ny;
        ny = my;
        my = tmp;
      default: { // nx>0, ny>0, mx<0 and my>0 case
        double sumx = (nx + mx);
        double sumy = ny + my;
        double difx = nx - mx;
        double dify = ny - my;
        double x[] = { cx - sumx, cx + difx, cx + sumx, cx - difx }; // bottom, right, top, left
        double y[] = { cy - sumy, cy + dify, cy + sumy, cy - dify };
        if (y[0] >= m2 || y[2] <= -m2 || x[3] >= n2 || x[1] <= -n2)
          return 0; // one edge of "this" separates the 2 rectangles
        double area = triangle_area(n2, m2, x[2], y[2], x[1], y[1])
            + triangle_area(m2, n2, y[3], -x[3], y[2], -x[2])
            + triangle_area(n2, m2, -x[0], -y[0], -x[3], -y[3])
            + triangle_area(m2, n2, -y[1], x[1], -y[0], x[0]);
        // iso-rectangle area
        double lx = x[0], by = y[1];
        double rx = x[2], ty = y[3];
        double s = 1;
        if (lx > rx) {
          // std::swap(lx,rx);
          tmp = lx;
          lx = rx;
          rx = tmp;
          s = -s;
        }
        if (by > ty) {
          // std::swap(by,ty);
          tmp = by;
          by = ty;
          ty = tmp;
          s = -s;
        }
        if (by >= m2 || ty <= -m2 || lx >= n2 || rx <= -n2) {
          return area / n2;
        }
        return (area + s * (Math.min(n2, rx) - Math.max(-n2, lx))
            * (Math.min(m2, ty) - Math.max(-m2, by)))
            / n2;
      }
    }
  }

  public static boolean do_intersect(Rectangle2D a, Rectangle2D b) {
    Vector2D an = new Vector2D(a.normalx, a.normaly);
    Vector2D bn = new Vector2D(b.normalx, b.normaly);
    Vector2D v = new Vector2D(b.centerx - a.centerx, b.centery - a.centery);

    double det = Math.abs(an.x() * bn.y() - an.y() * bn.x());
    double dot = Math.abs(an.dotProduct(bn));

    double an2 = an.getNormSq();
    double br = Math.abs(b.ratio);
    double ax0 = an.dotProduct(v);
    double dax = dot + br * det + an2;
    if (ax0 * ax0 >= dax * dax)
      return false;

    double ar = Math.abs(a.ratio);
    double ay0 = an.x() * v.y() - an.y() * v.x();
    double day = det + br * dot + ar * an2;
    if (ay0 * ay0 >= day * day)
      return false;

    double bn2 = bn.getNormSq();
    double bx0 = bn.dotProduct(v);
    double dbx = dot + ar * det + bn2;
    if (bx0 * bx0 >= dbx * dbx)
      return false;

    double by0 = v.x() * bn.y() - v.y() * bn.x();
    double dby = det + ar * dot + br * bn2;
    return (by0 * by0 < dby * dby);
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
}
