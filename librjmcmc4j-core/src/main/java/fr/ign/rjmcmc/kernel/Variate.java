package fr.ign.rjmcmc.kernel;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * @author Julien Perret
 */
public class Variate {
  private AbstractRealDistribution rand;

  public Variate(RandomGenerator rng) {
    this.rand = new UniformRealDistribution(rng, 0, 1);
  }

  /**
   * Returns the continuous probability that samples the completion variates.
   * 
   * @param var0
   *          the variate
   * @return the continuous probability that samples the completion variates.
   */
  public double compute(double[] var0, int d) {
    for (int i = d; i < var0.length; ++i) {
      var0[i] = rand.sample();
    }
    return 1.;
  }

  /**
   * Returns the continuous probability of the variate sampling, arguments are constant.
   * 
   * @param var1
   *          a variate
   * @return the continuous probability of the variate sampling, arguments are constant.
   */
  public double pdf(double[] var1, int d) {
    for (int i = d; i < var1.length; i++) {
      double v = var1[i];
      if (v < 0 || v > 1) {
        return 0;
      }
    }
    return 1.;
  }
}
