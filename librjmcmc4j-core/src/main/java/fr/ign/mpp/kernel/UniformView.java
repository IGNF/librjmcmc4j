package fr.ign.mpp.kernel;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.random.RandomGenerator;

import fr.ign.mpp.configuration.AbstractBirthDeathModification;
import fr.ign.mpp.configuration.AbstractGraphConfiguration;
import fr.ign.rjmcmc.kernel.SimpleObject;
import fr.ign.rjmcmc.kernel.View;

public class UniformView<T extends SimpleObject, C extends AbstractGraphConfiguration<T, C, M>, M extends AbstractBirthDeathModification<T, C, M>>
    implements View<C, M> {
  /**
   * Logger.
   */
  static Logger LOGGER = Logger.getLogger(UniformView.class.getName());

  int dimension = 0;
  ObjectBuilder<T> builder;
  int n = 1;

  public UniformView(ObjectBuilder<T> b) {
    this(b, 1);
  }

  public UniformView(ObjectBuilder<T> b, int n) {
    this.builder = b;
    this.dimension = b.size();
    this.n = n;
  }

  private int sample(RandomGenerator rng, int n) {
    return new UniformIntegerDistribution(rng, 0, n - 1).sample();
  }

  @Override
  public double select(boolean direct, RandomGenerator e, C conf, M modif, double[] val) {
    return direct ? selectDeath(e, conf, modif, val) : selectBirth(e, conf, modif, val);
  }

  private double selectDeath(RandomGenerator e, C conf, M modif, double[] out) {
    int size = conf.size();
    if (size < this.n) {// not enough objects to remove in the configuration
      return 0.;
    }
    int denom = 1;
    int d[] = new int[this.n];
    for (int i = 0; i < this.n; ++i, --size) {
      d[i] = (size == 1) ? 0 : sample(e, size);
      for (int j = 0; j < i; ++j)
        if (d[j] <= d[i])
          ++d[i]; // skip already selected indices
      for (int j = 0; j < i; ++j)
        if (d[j] == d[i]) {
          LOGGER.log(Level.SEVERE, "sampled " + d[i] + " twice");
        }
      Iterator<T> it = conf.iterator();
      for (int j = 0; j < d[i]; j++) {
        it.next();
      }
      T t = it.next();
      modif.insertDeath(t);
      double[] outTmp = new double[this.builder.size()];
      this.builder.setCoordinates(t, outTmp);
      for (int j = 0; j < this.builder.size(); j++) {
        out[i * this.builder.size() + j] = outTmp[j];
      }
      denom *= size;
    }
    return 1. / (double) denom;
  }

  private double selectBirth(RandomGenerator e, C conf, M modif, double[] in) {
    int beg = conf.size() - modif.getDeath().size() + 1;
    int end = beg + this.n;
    int denom = 1;
    int current = 0;
    for (int size = beg; size < end; ++size) {
      double[] v = new double[this.dimension];
      for (int i = 0; i < this.dimension; i++) {
        v[i] = in[i + current];
      }
      modif.insertBirth(this.builder.build(v));
      current += this.dimension;
      denom *= size;
    }
    return 1. / (double) denom;
  }

  @Override
  public int dimension() {
    return this.dimension;
  }

  // @Override
  // public void get(C conf, M modif, Vector<Double> val0) {
  // int index = 0;
  // for (T t : modif.getDeath()) {
  // this.builder.setCoordinates(t,
  // val0.subList(index, index + this.dimension));
  // index += this.dimension;
  // }
  // }
  //
  // @Override
  // public void set(C conf, M modif, Vector<Double> val1) {
  // int index = 0;
  // // System.out.println("set " + modif.getBirth().size() + " dim = "
  // // + this.dimension);
  // for (T t : modif.getBirth()) {
  // // System.out.println(val1.size() + " " + index + " "
  // // + (index + this.dimension));
  // t.set(val1.subList(index, index + this.dimension));
  // index += this.dimension;
  // }
  // }
}
