package fr.ign.rjmcmc.kernel;

import java.util.Vector;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;

/**
 * @author Julien Perret
 */
public class Variate {
  /**
   * Logger.
   */
  static Logger LOGGER = Logger.getLogger(Variate.class.getName());

  private AbstractRealDistribution rand;

  // private int dimension;
  //
  // public int getDimension() {
  // return dimension;
  // }

  public Variate(/* int d */RandomGenerator rng) {
      this.rand = new UniformRealDistribution(rng, 0, 1);
    // this.dimension = d;
  }

  /**
   * Returns the continuous probability that samples the completion variates.
   * @param var0
   *        the variate
   * @return the continuous probability that samples the completion variates.
   */
  public double compute(Vector<Double> var0) {
    for (int i = 0; i < var0.size(); ++i) {
      var0.set(i, new Double(rand.sample()));
    }
    return 1.;
  }

  /**
   * Returns the continuous probability of the variate sampling, arguments are constant.
   * @param var1
   *        a variate
   * @return the continuous probability of the variate sampling, arguments are constant.
   */
  public double pdf(Vector<Double> var1) {
    for (int i = 0; i < var1.size(); i++) {
      double v = var1.get(i);
      if (v < 0 || v > 1) {
        // LOGGER.info(i + " out of range " + v);
        return 0;
      }
    }
    return 1.;
  }
}
