package fr.ign.mpp.kernel;

import org.apache.commons.math3.random.RandomGenerator;

import fr.ign.rjmcmc.kernel.IdentityTransform;
import fr.ign.rjmcmc.kernel.SimpleObject;
import fr.ign.rjmcmc.kernel.Transform;
import fr.ign.rjmcmc.kernel.Variate;

/**
 * Object Birth.
 * @param <T> Simple Object Type
 */
public class ObjectBirth<T extends SimpleObject> implements ObjectSampler<T> {

  protected Transform transform;
  protected int dimension;
  protected ObjectBuilder<T> builder;
  protected Variate variate;
  protected T object;

  public ObjectBirth(ObjectBuilder<T> b, Variate v) {
    this.builder = b;
    this.variate = v;
    this.dimension = builder.size();
    this.transform = new IdentityTransform(this.dimension);
  }

  @Override
  public double sample(RandomGenerator e) {
    double[] val0 = new double[this.dimension];
    double[] val1 = new double[this.dimension];
    double phi = this.variate.compute(val0, 0);
    double jacob = this.transform.apply(true, val0, val1);
    this.object = this.builder.build(val1);
    return phi / jacob;
  }

  @Override
  public double pdf(T t) {
    double[] val1 = new double[this.dimension];
    this.builder.setCoordinates(t, val1);
    double[] val0 = new double[this.dimension];
    double J10 = this.transform.apply(false, val1, val0);
    double pdf = this.variate.pdf(val0, 0);
    return pdf * J10;
  }

  @Override
  public T getObject() {
    return this.object;
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

  public ObjectBuilder<T> getBuilder() {
    return builder;
  }
}
