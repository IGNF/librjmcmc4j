package fr.ign.rjmcmc.distribution;

import org.apache.commons.math3.distribution.AbstractIntegerDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.random.RandomGenerator;

public class UniformDistribution implements Distribution {
  private AbstractIntegerDistribution mVariate;
  private double mPdf;

  public UniformDistribution(RandomGenerator rng, int a, int b) {
      this.mVariate = new UniformIntegerDistribution(rng, a, b);
      //this.mVariate.reseedRandomGenerator(System.currentTimeMillis());
    this.mPdf = 1. / (b - a + 1.);
  }

  @Override
  public double pdfRatio(int n0, int n1) {
    assert (pdf(n0) > 0);
    return this.mVariate.getSupportLowerBound() <= n1 && n1 <= this.mVariate.getSupportUpperBound() ? 1. : 0.;
  }

  @Override
  public double pdf(int n) {
    return this.mPdf
        * ((this.mVariate.getSupportLowerBound() <= n && n <= this.mVariate.getSupportUpperBound()) ? 1. : 0.);
  }

  @Override
  public int sample(RandomGenerator e) {
    return this.mVariate.sample(); //FIXME use e
  }
}
