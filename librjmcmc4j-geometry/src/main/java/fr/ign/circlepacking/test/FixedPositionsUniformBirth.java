package fr.ign.circlepacking.test;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.math3.random.RandomGenerator;

import fr.ign.mpp.kernel.ObjectBuilder;
import fr.ign.mpp.kernel.ObjectSampler;
import fr.ign.rjmcmc.kernel.DiagonalAffineTransform;
import fr.ign.rjmcmc.kernel.SimpleObject;
import fr.ign.rjmcmc.kernel.Transform;
import fr.ign.rjmcmc.kernel.Variate;

/**
 * A uniform birth sampler.
 * <p>
 * \(J_{F{-1}}(F(p)) = J_{F}(p)^{-1}\).
 * 
 * @author Julien Perret
 * @param <T>
 */
public class FixedPositionsUniformBirth<T extends SimpleObject> implements ObjectSampler<T> {
  /**
   * Logger.
   */
  static Logger LOGGER = Logger.getLogger(FixedPositionsUniformBirth.class.getName());

  Transform transform;
  int dimension;
  ObjectBuilder<T> builder;
  Variate variate;
  T object;
  Integer[] ids;

  @Override
  public T getObject() {
    return this.object;
  }

  /**
   * Constructs a uniform birth.
   * <p>
   * The constructor takes a predicate if given (and thus builds a rejectionVariate to hold it) as well as a tranform
   * class and a list of parameters to be given to the transform constructor.
   * 
   * @param a
   *          an object
   * @param b
   *          another object
   * @param builder
   *          an object builder
   * @param trans
   *          a transform class
   * @param pred
   *          a predicate
   * @param o
   *          a generic array of parameters to be given to the transform constructor
   */
  public FixedPositionsUniformBirth(RandomGenerator rng, double[] a, double[] b, ObjectBuilder<T> builder, Map<Integer, List<Double>> fixedPositionsMap) {
    this.dimension = a.length;
    double[] d = new double[this.dimension];
    for (int i = 0; i < this.dimension; i++) {
      d[i] = b[i] - a[i];
    }
    this.transform = new DiagonalAffineTransform(d, a);
    this.builder = builder;
    this.variate = new Variate(rng);
    this.ids = fixedPositionsMap.keySet().toArray(new Integer[fixedPositionsMap.size()]);
  }

  @Override
  public double sample(RandomGenerator e) {
    double[] val0 = new double[this.dimension];
    double[] val1 = new double[this.dimension];
    double phi = this.variate.compute(val0, 0);
    double jacob = this.transform.apply(true, val0, val1);
    val1[0] = this.ids[(int) (val1[0] * this.ids.length)];
    this.object = this.builder.build(val1);
    return phi / jacob;
  }

  @Override
  public double pdf(T t) {
    double[] val1 = new double[this.dimension];
    this.builder.setCoordinates(t, val1);
    double[] val0 = new double[this.dimension];
    double J10 = this.transform.apply(false, val1, val0);
    double pdf = this.variate.pdf(val0, 1);// ignore id
    return pdf * J10;
  }

  /**
   * @return the transform used to build objects
   */
  public Transform getTransform() {
    return this.transform;
  }

  /**
   * @return the variate
   */
  public Variate getVariate() {
    return this.variate;
  }
}
