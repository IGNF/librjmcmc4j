package fr.ign.circlepacking.fixedconfiguration;

import java.util.List;

import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.random.RandomGenerator;

import fr.ign.mpp.kernel.ObjectBuilder;
import fr.ign.rjmcmc.kernel.View;

public class CirclePackingFixedView implements View<CirclePackingFixedConfiguration, CirclePackingFixedModification> {
  ObjectBuilder<IndexedCircle2D> builder;

  public CirclePackingFixedView(ObjectBuilder<IndexedCircle2D> b) {
    this.builder = b;
  }

  private int sample(RandomGenerator rng, int n) {
    return new UniformIntegerDistribution(rng, 0, n - 1).sample();
  }

  @Override
  public double select(boolean direct, RandomGenerator rng, CirclePackingFixedConfiguration conf, CirclePackingFixedModification modif, double[] val) {
    if (direct) {// DEATH
//      System.out.println("death");
      int size = conf.size();
      if (size == 0) {
        return 0;
      }
      int index = sample(rng, size);
      IndexedCircle2D c = conf.circles.get(index);
      modif.setDeath(c);
      this.builder.setCoordinates(c, val);
      val[0] = 0.0;
      return 1. / (double) size;
    }
    // BIRTH
//    System.out.println("birth");
    boolean change = (modif.type == 2);/// if there is already a type death in the modif, it is a change
    int newsize = conf.size() + (change ? 0 : 1);
    double[] v = new double[this.builder.size()];
    for (int i = 0; i < v.length; i++) {
      v[i] = val[i];
    }
    if (change) {
//      System.out.println("change");
      v[0] = modif.circle.index;
      IndexedCircle2D c = this.builder.build(v);
      modif.setChange(c, c.radius);
    } else {
      List<Integer> ids = conf.getAvailableIds();
      int index = sample(rng, ids.size());
      int id = ids.get(index);
      v[0] = id;
      modif.setBirth(this.builder.build(v));
    }
    return 1. / (double) newsize;
  }

  @Override
  public int dimension() {
    return 2;
  }
}
