package fr.ign.mpp.kernel;

import java.lang.reflect.Constructor;
import java.util.logging.Logger;

import org.apache.commons.math3.random.RandomGenerator;

import fr.ign.rjmcmc.kernel.DiagonalAffineTransform;
import fr.ign.rjmcmc.kernel.Predicate;
import fr.ign.rjmcmc.kernel.RejectionVariate;
import fr.ign.rjmcmc.kernel.SimpleObject;
import fr.ign.rjmcmc.kernel.Transform;
import fr.ign.rjmcmc.kernel.TransformedVariate;
import fr.ign.rjmcmc.kernel.Variate;

/**
 * A uniform birth sampler.
 * <p>
 * \(J_{F{-1}}(F(p)) = J_{F}(p)^{-1}\).
 * 
 * @author Julien Perret
 * @param <T> Simple Object Type
 */
public class UniformBirth<T extends SimpleObject> extends ObjectBirth<T> {
  /**
   * Logger.
   */
  static Logger LOGGER = Logger.getLogger(UniformBirth.class.getName());

  /**
   * Constructs a uniform birth.
   * 
   * @param a
   *          an object
   * @param b
   *          another object
   * @param builder
   *          an object builder
   */
  public UniformBirth(RandomGenerator rng, T a, T b, ObjectBuilder<T> builder) {
    this(rng, a, b, builder, DiagonalAffineTransform.class, (Object[]) null);
  }

  /**
   * Constructs a uniform birth.
   * 
   * @param a
   *          an object
   * @param b
   *          another object
   * @param builder
   *          an object builder
   */
  public UniformBirth(RandomGenerator rng, T a, T b, ObjectBuilder<T> builder, Variate v) {
    this(rng, a, b, builder, v, DiagonalAffineTransform.class, null, (Object[]) null);
  }

  /**
   * Constructs a uniform birth.
   * <p>
   * The constructor takes a tranform class and a list of parameters to be given to the transform constructor.
   * 
   * @param a
   *          an object
   * @param b
   *          another object
   * @param builder
   *          an object builder
   * @param trans
   *          a transform class
   * @param o
   *          a generic array of parameters to be given to the transform constructor
   */
  public <Trans extends Transform> UniformBirth(RandomGenerator rng, T a, T b, ObjectBuilder<T> builder, Class<Trans> trans, Object... o) {
    this(rng, a, b, builder, new Variate(rng), trans, null, o);
  }

  /**
   * Constructs a uniform birth.
   * <p>
   * The constructor takes a predicate if given (and thus builds a rejectionVariate to hold it) as well as a tranform class and a list of parameters to be given
   * to the transform constructor.
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
  public <Trans extends Transform> UniformBirth(RandomGenerator rng, T a, T b, ObjectBuilder<T> builder, Variate v, Class<Trans> trans, Predicate pred,
      Object... o) {
    super(builder, getVariate(rng, a, b, builder, v, trans, pred, o));
  }

  private static <T extends SimpleObject, Trans extends Transform> Variate getVariate(RandomGenerator rng, T a, T b, ObjectBuilder<T> builder, Variate v,
      Class<Trans> trans, Predicate pred, Object... o) {
    int dimension = builder.size();
    double[] d = new double[dimension];
    double[] arrayA = a.toArray();
    double[] arrayB = b.toArray();
    for (int i = 0; i < dimension; i++) {
      d[i] = arrayB[i] - arrayA[i];
    }
    double[] coordinates = new double[dimension];
    builder.setCoordinates(a, coordinates);
    Constructor<?> cons = trans.getConstructors()[0];
    Transform transform;
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
      transform = (Transform) cons.newInstance(parameters);
    } catch (Exception e) {
      transform = new DiagonalAffineTransform(d, coordinates);
      e.printStackTrace();
    }
    Variate internalVariate = new TransformedVariate(rng, transform, v);
    return (pred == null) ? internalVariate : new RejectionVariate(rng, internalVariate, pred, new RejectionVariate.monte_carlo());
  }
}
