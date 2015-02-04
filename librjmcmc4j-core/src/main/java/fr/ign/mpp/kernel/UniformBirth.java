package fr.ign.mpp.kernel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;

import fr.ign.rjmcmc.kernel.DiagonalAffineTransform;
import fr.ign.rjmcmc.kernel.Predicate;
import fr.ign.rjmcmc.kernel.RejectionVariate;
import fr.ign.rjmcmc.kernel.SimpleObject;
import fr.ign.rjmcmc.kernel.Transform;
import fr.ign.rjmcmc.kernel.Variate;

/**
 * A uniform birth sampler.
 * <p>
 * \(J_{F{-1}}(F(p)) = J_{F}(p)^{-1}\).
 * @author Julien Perret
 * @param <T>
 */
public class UniformBirth<T extends SimpleObject> implements ObjectSampler<T> {
  /**
   * Logger.
   */
  static Logger LOGGER = Logger.getLogger(UniformBirth.class.getName());

  Transform transform;
  int dimension;
  ObjectBuilder<T> builder;
  Variate variate;
  T object;

  @Override
  public T getObject() {
    return this.object;
  }

  /**
   * Constructs a uniform birth.
   * @param a
   *        an object
   * @param b
   *        another object
   * @param builder
   *        an object builder
   */
  public UniformBirth(RandomGenerator rng, T a, T b, ObjectBuilder<T> builder) {
      this(rng, a, b, builder, DiagonalAffineTransform.class, (Object[]) null);
  }

  /**
   * Constructs a uniform birth.
   * <p>
   * The constructor takes a tranform class and a list of parameters to be given to the transform
   * constructor.
   * @param a
   *        an object
   * @param b
   *        another object
   * @param builder
   *        an object builder
   * @param trans
   *        a transform class
   * @param o
   *        a generic array of parameters to be given to the transform constructor
   */
  public <Trans extends Transform> UniformBirth(RandomGenerator rng, T a, T b, ObjectBuilder<T> builder,
      Class<Trans> trans, Object... o) {
      this(rng, a, b, builder, trans, null, o);
  }

  /**
   * Constructs a uniform birth.
   * <p>
   * The constructor takes a predicate if given (and thus builds a rejectionVariate to hold it) as
   * well as a tranform class and a list of parameters to be given to the transform constructor.
   * @param a
   *        an object
   * @param b
   *        another object
   * @param builder
   *        an object builder
   * @param trans
   *        a transform class
   * @param pred
   *        a predicate
   * @param o
   *        a generic array of parameters to be given to the transform constructor
   */
  public <Trans extends Transform> UniformBirth(RandomGenerator rng, T a, T b, ObjectBuilder<T> builder,
      Class<Trans> trans, Predicate pred, Object... o) {
    this.dimension = a.size();
    Vector<Double> d = new Vector<>();
    d.setSize(a.size());
    double[] arrayA = a.toArray();
    double[] arrayB = b.toArray();
    for (int i = 0; i < a.size(); i++) {
      d.set(i, arrayB[i] - arrayA[i]);
    }
    Vector<Double> coordinates = new Vector<>();
    coordinates.setSize(a.size());
    builder.setCoordinates(a, coordinates);
    Constructor<?> cons = trans.getConstructors()[0];
    try {
      int length = 2 + ((o == null) ? 0 : o.length);
      Object[] parameters = new Object[length];
      parameters[0] = d;
      parameters[1] = coordinates;
      if (o != null) {
        for (int i = 0; i < o.length; i++) {
          parameters[2 + i] = o[i];
        }
      }
      this.transform = (Transform) cons.newInstance(parameters);
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException e) {
      this.transform = new DiagonalAffineTransform(d, coordinates);
      e.printStackTrace();
    }
    this.builder = builder;
    Variate internalVariate = new Variate(/* this.builder.size() */rng);
    this.variate = (pred == null) ? internalVariate : new RejectionVariate(/* this.builder.size(), */
									   rng, internalVariate, pred, new RejectionVariate.monte_carlo());
  }

  @Override
  public double sample(RandomGenerator e) {
    Vector<Double> var0 = new Vector<>();
    var0.setSize(this.dimension);
    Vector<Double> val1 = new Vector<>();
    val1.setSize(this.dimension);
    double phi = this.variate.compute(var0);
    double jacob = this.transform.apply(true, new Vector<Double>(0), var0, val1,
        new Vector<Double>(0));
    this.object = this.builder.build(val1);
//    System.out.println("sample " + (phi / jacob));
    return phi / jacob;
  }

  @Override
  public double pdf(T t) {
    Vector<Double> val1 = new Vector<>();
    val1.setSize(this.dimension);
    this.builder.setCoordinates(t, val1);
    Vector<Double> val0 = new Vector<>();
    val0.setSize(this.dimension);
    double J10 = this.transform.apply(false, val1, new Vector<Double>(0), new Vector<Double>(0),
        val0);
    double pdf = this.variate.pdf(val0);
//    LOGGER.info(t);
//    LOGGER.info("pdf = " + (pdf * J10) + " = " + pdf + " * " + J10 + " [" + val1 + ", " + val0
//        + "]");
//    LOGGER.info(val0.get(0) + " " + val0.get(1));
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
