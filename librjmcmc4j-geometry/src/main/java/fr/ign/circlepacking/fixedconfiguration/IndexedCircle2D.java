package fr.ign.circlepacking.fixedconfiguration;

import java.util.List;

import fr.ign.geometry.Circle2D;

public class IndexedCircle2D extends Circle2D {
  public int index;

  public IndexedCircle2D(int i, double cx, double cy, double r) {
    super(cx, cy, r);
    this.index = i;
  }

  @Override
  public double[] toArray() {
    return new double[] { this.index, this.center_x, this.center_y, this.radius };
  }

  @Override
  public int size() {
    return 4;
  }

  @Override
  public void set(List<Double> list) {
    this.index = list.get(0).intValue();
    this.center_x = list.get(1);
    this.center_y = list.get(2);
    this.radius = list.get(3);
  }

  @Override
  public Object[] getArray() {
    return new Object[] { this.toGeometry(), this.getArea() };
  }
}
