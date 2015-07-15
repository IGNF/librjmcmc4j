package fr.ign.rjmcmc.kernel;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;

import fr.ign.rjmcmc.configuration.Configuration;
import fr.ign.rjmcmc.configuration.Modification;

public class Kernel<C extends Configuration<C, M>, M extends Modification<C, M>> {
  /**
   * Logger.
   */
  static Logger logger = Logger.getLogger(Kernel.class.getName());
  View<C, M> view0;
  View<C, M> view1;
  Variate variate0;
  Variate variate1;
  Transform transform;
  int kernelId;
  KernelProbability<C, M> prob;
  KernelProposalRatio<C, M> ratio;

  /**
   * Construct a new Kernel.
   * 
   * @param v0
   *          a view
   * @param v1
   *          another view
   * @param x0
   *          a variate
   * @param x1
   *          another variate
   * @param t
   *          a transform
   * @param p
   *          probability to apply the kernel
   * @param q
   *          probability to choose the direct transform
   */
  public Kernel(View<C, M> v0, View<C, M> v1, Variate x0, Variate x1, Transform t, KernelProbability<C, M> p, KernelProposalRatio<C, M> ratio, String name) {
    this.view0 = v0;
    this.view1 = v1;
    this.variate0 = x0;
    this.variate1 = x1;
    this.transform = t;
    this.prob = p;
    this.ratio = ratio;
    this.name = name;
  }

  /**
   * Construct a new Kernel.
   * 
   * @param v0
   *          a view
   * @param v1
   *          another view
   * @param x0
   *          a variate
   * @param x1
   *          another variate
   * @param t
   *          a transform
   * @param p
   *          probability to apply the kernel
   * @param q
   *          probability to choose the direct transform
   */
  public Kernel(View<C, M> v0, View<C, M> v1, Variate x0, Variate x1, Transform t, double p, double q, String name) {
    this.view0 = v0;
    this.view1 = v1;
    this.variate0 = x0;
    this.variate1 = x1;
    this.transform = t;
    final double prob = p;
    this.prob = new KernelProbability<C, M>() {
      @Override
      public double probability(C c) {
        return prob;
      }
    };
    final double r = q;
    this.ratio = new KernelProposalRatio<C, M>() {
      @Override
      public double probability(boolean direct, C c) {
        return r;
      }
    };
    this.name = name;
  }

  public int getKernelId() {
    return this.kernelId;
  }

  /**
   * Name of the kernel (mostly useful for logging and debug).
   */
  String name = "kernel";

  /**
   * @return the name of the kernel.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Change the name of the kernel.
   * 
   * @param n
   *          the new name
   */
  public void setName(String n) {
    this.name = n;
  }

  /**
   * @return the probability of the kernel.
   */
  public double probability(C c) {
    return this.prob.probability(c);
  }

  /**
   * Apply the kernel to propose a new modification.
   * 
   * @param e
   *          a random generator
   * @param probability
   *          a probability (uniform between 0 and p)
   * @param c
   *          the current configuration
   * @param modif
   *          the proposed configuration (modified by the function)
   * @return the inverse variate sampling
   */
  public double operator(RandomGenerator e, double probability, C c, M modif) {
    double[] val0 = new double[this.transform.dimension()];
    double[] val1 = new double[this.transform.dimension()];
    double ratio01 = this.ratio.probability(true, c);
    double ratio10 = this.ratio.probability(false, c);
    double p01 = ratio01 / (ratio01 + ratio10);
//    System.out.println(this.name + " " + probability + " (" + p01 + ", " + p10 + ")");
    if (probability < p01) { // branch probability : m_p01
      this.kernelId = 0;
      // returns the discrete probability that samples the portion of the configuration that is being modified (stored in the modif input)
      double J01 = view0.select(true, e, c, modif, val0);
      if (J01 == 0) {
        return 0; // abort: view sampling failed
      }
      // returns the continuous probability that samples the completion variates
      double phi01 = variate0.compute(val0, view0.dimension());
      if (phi01 == 0) {
        return 0; // abort : variate sampling failed
      }
      double jacob = transform.apply(true, val0, val1); // computes val1 from val0
      if (jacob == 0) {
        return 0;
      }
      // returns the continuous probability of the variate sampling, arguments are constant
      double phi10 = variate1.pdf(val1, view1.dimension());
      // returns the discrete probability of the inverse view sampling, arguments are constant except val1 that is encoded in modif
      double J10 = view1.select(false, e, c, modif, val1);
//      System.out.println(J01 + ", "+phi01+", "+jacob+", "+phi10+", "+J10);
//      System.out.println("jacob = " + (jacob * (p10 * J10 * phi10) / (p01 * J01 * phi01)));
//      return jacob * (p10 * J10 * phi10) / (p01 * J01 * phi01);
      return jacob * ratio01 * (J10 * phi10) / (J01 * phi01);
    } else { // branch probability : m_p10
      this.kernelId = 1;
      // returns the discrete probability of the inverse view sampling, arguments are constant except val1 that is encoded in modif
      double J10 = view1.select(true, e, c, modif, val1);
      // returns the discrete probability that samples the portion of the configuration that is being modified (stored in the modif input)
      if (J10 == 0) {
        return 0; // abort : view sampling failed
      }
      // returns the continuous probability that samples the completion variates
      double phi10 = variate1.compute(val1, view1.dimension());
      if (phi10 == 0) {
        return 0; // abort : variate sampling failed
      }
      double jacob = transform.apply(false, val1, val0); // computes val0 from val1
      if (jacob == 0) {
        return 0;
      }
      // returns the continuous probability of the variate sampling, arguments are constant
      double phi01 = variate0.pdf(val0, view0.dimension());
      double J01 = view0.select(false, e, c, modif, val0);
//      return jacob * (p01 * J01 * phi01) / (p10 * J10 * phi10);
      return jacob * ratio10 * (J01 * phi01) / (J10 * phi10);
    }
  }
}
