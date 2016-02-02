package fr.ign.rjmcmc.kernel;

import org.apache.commons.math3.random.RandomGenerator;

public class TransformedVariate extends Variate {
  Transform transform;
  Variate variate;
  public TransformedVariate(RandomGenerator rng, Transform t, Variate v) {
    super(rng);
    this.transform = t;
    this.variate = v;
  }
  @Override
  public double compute(double[] var0, int d) {
    double[] var = new double[transform.dimension()];
    double res = variate.compute(var, d);
    double pdf = transform.apply(true, var, var0);
    // this is a variable substitution (https://en.wikipedia.org/wiki/Probability_density_function#Multiple_variables)
    // q(it) = q(t(val)), t being the transform
    // q(it) = q(val) * |J_(t^-1)(t(val))| = q(val) / |J_t(val)|
    return res/pdf;
  }
  @Override
  public double pdf(double[] var1, int d) {
    double[] var = new double[transform.dimension()];
    double res = transform.apply(false, var1, var);
    return res * variate.pdf(var, d);
  }
}
