package fr.ign.geometry;

import java.util.Collection;
import java.util.Iterator;

public class IsoRectangle2D implements Collection<Double> {
  int minX;

  public int getMinX() {
    return minX;
  }

  public void setMinX(int minX) {
    this.minX = minX;
  }

  public int getMinY() {
    return minY;
  }

  public void setMinY(int minY) {
    this.minY = minY;
  }

  public int getMaxX() {
    return maxX;
  }

  public void setMaxX(int maxX) {
    this.maxX = maxX;
  }

  public int getMaxY() {
    return maxY;
  }

  public void setMaxY(int maxY) {
    this.maxY = maxY;
  }

  int minY;
  int maxX;
  int maxY;

  public IsoRectangle2D(int x0, int y0, int x1, int y1) {
    this.minX = x0;
    this.minY = y0;
    this.maxX = x1;
    this.maxY = y1;
  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public boolean isEmpty() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean contains(Object o) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Iterator<Double> iterator() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object[] toArray() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <T> T[] toArray(T[] a) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean add(Double e) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean remove(Object o) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean addAll(Collection<? extends Double> c) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void clear() {
    // TODO Auto-generated method stub

  }

  public Point2D min() {
    return new Point2D(this.minX, this.minY);
  }

  public Point2D max() {
    return new Point2D(this.maxX, this.maxY);
  }

  public Segment2D clip(Segment2D s) {
    Point2D p = s.getStart();
    Point2D q = s.getEnd();
    Vector2D d = q.subtract(p);// q = p+d
    double[] a = { p.x(), p.y() }; // a = p
    double[] b = { q.x(), q.y() }; // b = q
    for (int i = 0; i < 2; ++i) {
      double di = d.getOrdinate(i);
      if (di == 0 && (p.getOrdinate(i) < this.min().getOrdinate(i) || p.getOrdinate(i) > this.max().getOrdinate(i))) {
        // both points are outside the rectangle
        return null;
      } else {
        if (di == 0) {
          continue;
        }
        double tmin = 0, tmax = 1;
        int j = 1 - i, k = (di > 0) ? 1 : 0;
        int l = 1 - k; // l = 0 if d[i] > 0
        double[] m = { this.min().getOrdinate(i), this.max().getOrdinate(i) };
        // m[l] is the possibly unrespected bound : if d[i] is positive, look at the max, otherwise,
        // look at the min
        double t = (m[l] - p.getOrdinate(i)) / d.getOrdinate(i);
        if (t > tmin) {
          tmin = t;
          a[i] = m[l];
          a[j] = p.getOrdinate(j) + t * d.getOrdinate(j);
        } // a=p+td
        t = (m[k] - p.getOrdinate(i)) / d.getOrdinate(i);
        if (t < tmax) {
          tmax = t;
          b[i] = m[k];
          b[j] = p.getOrdinate(j) + t * d.getOrdinate(j);
        } // b=p+td
        if (tmax <= tmin) {
          return null;
        }
      }
    }
    Point2D pa = new Point2D(a[0], a[1]);
    Point2D pb = new Point2D(b[0], b[1]);
    Segment2D segment = new Segment2D(pa, pb);
    return segment;
  }
}
