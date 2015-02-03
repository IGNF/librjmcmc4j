package fr.ign.rjmcmc.distribution;

import org.apache.commons.math3.distribution.AbstractIntegerDistribution;
import org.apache.commons.math3.random.RandomGenerator;

public class PoissonDistribution implements Distribution {
  private AbstractIntegerDistribution mDistribution;

    public PoissonDistribution(RandomGenerator rng, double mean) {
	this.mDistribution = new org.apache.commons.math3.distribution.PoissonDistribution(
			rng, 
			mean, 
			org.apache.commons.math3.distribution.PoissonDistribution.DEFAULT_EPSILON, 
			org.apache.commons.math3.distribution.PoissonDistribution.DEFAULT_MAX_ITERATIONS);
  }

  // lambda ^(n1 - n0) * n0! / n1!
  @Override
  public double pdfRatio(int n0, int n1) {
    double res = 1.;
    for (; n1 > n0; --n1)
      res *= this.mDistribution.getNumericalMean() / n1;
    for (; n0 > n1; --n0)
      res *= n0 / this.mDistribution.getNumericalMean();
    return res;
  }

  @Override
  public double pdf(int n) {
    return this.mDistribution.probability(n);
  }

  @Override
  public int sample(RandomGenerator e) {
    return this.mDistribution.sample();//FIXME use e
  }
}
