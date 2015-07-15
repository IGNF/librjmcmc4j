package fr.ign.mpp.kernel;

import org.apache.commons.math3.random.RandomGenerator;

import fr.ign.mpp.configuration.AbstractBirthDeathModification;
import fr.ign.mpp.configuration.AbstractGraphConfiguration;
import fr.ign.rjmcmc.kernel.Kernel;
import fr.ign.rjmcmc.kernel.NullView;
import fr.ign.rjmcmc.kernel.SimpleObject;
import fr.ign.rjmcmc.kernel.Transform;
import fr.ign.rjmcmc.kernel.Variate;
import fr.ign.rjmcmc.kernel.View;

public class KernelFactory<T extends SimpleObject, C extends AbstractGraphConfiguration<T, C, M>, M extends AbstractBirthDeathModification<T, C, M>> {

  public KernelFactory() {
  }

  /**
   * Make a new uniform birth/death kernel.
   * 
   * @param builder
   *          an object builder
   * @param b
   *          a uniform birth
   * @param p
   *          the probability of the kernel
   * @param q
   *          the probability to choose the direct transform of the kernel
   * @return the new kernel
   */
  public Kernel<C, M> make_uniform_birth_death_kernel(RandomGenerator rng, ObjectBuilder<T> builder, UniformBirth<T> b, double p, double q, String n) {
    return new Kernel<C, M>(new NullView<C, M>(), new UniformView<T, C, M>(builder), b.getVariate(), new Variate(rng), b.getTransform(), p, q, n);
  }

  /**
   * Make a uniform modification kernel.
   * 
   * @param builder
   *          an object builder
   * @param t
   *          a transform
   * @param p
   *          probability of the kernel
   * @param name
   *          name of the kernel
   * @return the new kernel
   */
  public Kernel<C, M> make_uniform_modification_kernel(RandomGenerator rng, ObjectBuilder<T> builder, Transform t, double p, String name) {
    return make_uniform_modification_kernel(rng, builder, t, p, 1.0, name);
  }

  /**
   * Make a uniform modification kernel.
   * 
   * @param builder
   *          an object builder
   * @param t
   *          a transform
   * @param p
   *          probability of the kernel
   * @param q
   *          probability to choose the direct transform
   * @param name
   *          name of the kernel
   * @return the new kernel
   */
  public Kernel<C, M> make_uniform_modification_kernel(RandomGenerator rng, ObjectBuilder<T> builder, Transform t, double p, double q, String name) {
    return make_uniform_modification_kernel(rng, builder, t, p, q, 1, 1, name);
  }

  /**
   * Make a uniform modification kernel.
   * 
   * @param builder
   *          an object builder
   * @param t
   *          a transform
   * @param p
   *          probability of the kernel
   * @param q
   *          probability to choose the direct transform
   * @param name
   *          name of the kernel
   * @param n0
   *          number of objects in view0
   * @param n1
   *          number of objects in view1
   * @return the new kernel
   */
  public Kernel<C, M> make_uniform_modification_kernel(RandomGenerator rng, ObjectBuilder<T> builder, Transform t, double p, double q, int n0, int n1, String name) {
    View<C, M> view0 = new UniformView<T, C, M>(builder, n0);
    View<C, M> view1 = new UniformView<T, C, M>(builder, n1);
    Variate variate0 = new Variate(/* t.dimension() - n0 * builder.size() */rng);
    Variate variate1 = new Variate(/* t.dimension() - n1 * builder.size() */rng);
    return new Kernel<>(view0, view1, variate0, variate1, t, p, q, name);
  }
}
