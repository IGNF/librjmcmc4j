package fr.ign.mpp.kernel;

import java.util.Iterator;

import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;

import fr.ign.mpp.configuration.AbstractBirthDeathModification;
import fr.ign.mpp.configuration.AbstractGraphConfiguration;
import fr.ign.rjmcmc.kernel.SimpleObject;
import fr.ign.rjmcmc.kernel.View;

public class UniformTypeView<T extends SimpleObject, C extends AbstractGraphConfiguration<T, C, M>, M extends AbstractBirthDeathModification<T, C, M>>
    implements View<C, M> {
  /**
   * Logger.
   */
  static Logger LOGGER = Logger.getLogger(UniformTypeView.class.getName());

  int dimension = 0;
  ObjectBuilder<T> builder;
  int n = 1;
  private Class<? extends T> clazz;

  public UniformTypeView(Class<? extends T> clazz, ObjectBuilder<T> b) {
    this(clazz, b, 1);
  }

  public UniformTypeView(Class<? extends T> clazz, ObjectBuilder<T> b, int n) {
    this.clazz = clazz;
    this.builder = b;
    this.dimension = b.size();
    this.n = n;
  }

  private int sample(RandomGenerator rng, int n) {
    UniformIntegerDistribution distribution = new UniformIntegerDistribution(rng, 0, n - 1);
    return distribution.sample();
  }

  @Override
  public double select(boolean direct, RandomGenerator e, C conf, M modif, double[] v) {
    return direct ? selectDeath(e, conf, modif, v) : selectBirth(e, conf, modif, v);
  }

  private double selectDeath(RandomGenerator e, C conf, M modif, double[] out) {
    int size = conf.size(clazz);
    if (size < this.n) {
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
          LOGGER.error("sampled " + d[i] + " twice");
        }
      Iterator<T> it = conf.iterator(clazz);
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
    int beg = conf.size(clazz) - modif.getDeath().size() + 1;
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
  // this.builder.setCoordinates(t, val0.subList(index, index + this.dimension));
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
