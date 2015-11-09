package fr.ign.circlepacking.fixedconfiguration;

import fr.ign.rjmcmc.configuration.Modification;

public class CirclePackingFixedModification implements Modification<CirclePackingFixedConfiguration, CirclePackingFixedModification> {
  int type = -1;
  IndexedCircle2D circle = null;
  double radius;

  public void setBirth(IndexedCircle2D c) {
    this.circle = c;
    this.type = 0;
  }

  public void setDeath(IndexedCircle2D c) {
    this.circle = c;
    this.type = 1;
  }

  public void setChange(IndexedCircle2D c, double r) {
    this.circle = c;
    this.type = 2;
    this.radius = r;
  }

  @Override
  public void apply(CirclePackingFixedConfiguration c) {
    switch (this.type) {
    case 0:
      c.circles.add(this.circle);
      return;
    case 1:
      c.circles.remove(this.circle);
      return;
    default:
      for (IndexedCircle2D circle : c.circles) {
        if (circle.index == this.circle.index) {
          circle.radius = this.radius;
          return;
        }
      }
    }
  }
}
