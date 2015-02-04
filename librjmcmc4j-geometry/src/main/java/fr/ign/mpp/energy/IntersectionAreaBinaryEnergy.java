package fr.ign.mpp.energy;

import fr.ign.geometry.Primitive;
import fr.ign.geometry.Rectangle2D;
import fr.ign.rjmcmc.energy.BinaryEnergy;

public class IntersectionAreaBinaryEnergy<T extends Primitive> implements BinaryEnergy<T, T> {
  public IntersectionAreaBinaryEnergy() {
  }

  @Override
  public double getValue(T t, T u) {
    Rectangle2D a = (Rectangle2D) t;
    Rectangle2D b = (Rectangle2D) u;
    if (Rectangle2D.do_intersect(a, b)) {
      return Rectangle2D.intersection_area(a, b);
    }
    return 0;
    // return t.intersectionArea(u);
  }

}
