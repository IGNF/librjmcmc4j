package fr.ign.rjmcmc.kernel;

import org.apache.commons.math3.random.RandomGenerator;

public class RejectionVariate extends Variate {
  Variate m_variate;
  Predicate m_pred;
  Normalizer m_normalizer;

  public RejectionVariate(/* int d, */RandomGenerator rng, Variate variate, Predicate pred, Normalizer normalizer) {
    super(/* d */rng);
    m_variate = variate;
    m_pred = pred;
    m_normalizer = normalizer;
  }

  interface Normalizer {
    void fail();

    void pass();

    double inv_probability();

    void clear();
  }

  public static class constant_normalizer implements Normalizer {
    double m_inv_probability;

    public constant_normalizer(double p) {
      m_inv_probability = 1. / p;
    }

    public void fail() {
    }

    public void pass() {
    }

    public double inv_probability() {
      return m_inv_probability;
    }

    public void clear() {
    }
  };

  // Monte Carlo estimation of the success rate of the predicate (assuming it is constant)
  public static class monte_carlo implements Normalizer {
    long m_test, m_pass;
    double m_inv_probability;

    public monte_carlo() {
      clear();
    }

    public void fail() {
      ++m_test;
      m_inv_probability = (double) (m_test) / m_pass;
    }

    public void pass() {
      ++m_test;
      ++m_pass;
      m_inv_probability = (double) (m_test) / m_pass;
    }

    public double inv_probability() {
      return m_inv_probability;
    }

    public void clear() {
      m_pass = m_test = 0;
      m_inv_probability = 0.;
    }
  };

  // Monte Carlo estimation of the success rate of the predicate based on the last N tests
  public static class sliding_monte_carlo implements Normalizer {
    boolean[] m_test;
    int m_i;
    long m_pass;
    double m_inv_probability;

    public sliding_monte_carlo(int n) {
      m_test = new boolean[n];
      clear();
    }

    public void fail() {
      if (m_test[m_i]) {
        --m_pass;
        m_test[m_i] = false;
        m_inv_probability = (double) (m_test.length) / m_pass;
      }
      m_i = (m_i + 1) % m_test.length;
    }

    public void pass() {
      if (!m_test[m_i]) {
        ++m_pass;
        m_test[m_i] = true;
        m_inv_probability = (double) (m_test.length) / m_pass;
      }
      m_i = (m_i + 1) % m_test.length;
    }

    public double inv_probability() {
      return m_inv_probability;
    }

    public void clear() {
      for (int i = 0; i < m_test.length; ++i)
        m_test[i] = false;
      m_i = 0;
      m_pass = 0;
      m_inv_probability = 0.;
    }
  };

  // Monte Carlo estimation of the success rate of the predicate based with (decay^n) sample weights
  // where n is the age of the sample (in number of iterations)
  public static class decay_monte_carlo implements Normalizer {
    double m_decay; // 0<decay<1, usually close to 1
    double m_inv_probability;

    public decay_monte_carlo() {
      this(0.999);
    }

    decay_monte_carlo(double decay) {
      this.m_decay = decay;
      assert (decay > 0 && decay < 1);
      clear();
    }

    // p(i+1) = decay*p(i) + (1-decay)*(pass?1:0)
    // 1/p(i+1) = 1/(decay*p(i) + (1-decay)*(pass?1:0)) = (1/p(i)) / (decay +
    // (1-decay)*(pass?(1/p(i)):0))
    // fail -> (1/p(i)) / decay
    // pass -> (1/p(i)) / (decay + (1-decay)*(1/p(i)))
    public void fail() {
      m_inv_probability /= m_decay;
    }

    public void pass() {
      m_inv_probability /= m_decay + (1. - m_decay) * m_inv_probability;
    }

    public double inv_probability() {
      return this.m_inv_probability;
    }

    public void clear() {
      this.m_inv_probability = 1.;
    }
  };

  @Override
  public double compute(double[] var0, int d) {
    double res = this.m_variate.compute(var0, d);
    if (!this.m_pred.check(var0, d)) {
      this.m_normalizer.fail();
      return 0; // sampling failure code
    }
    this.m_normalizer.pass();
    // renormalization by the current estimate of the rejection probability
    return res * m_normalizer.inv_probability();
  }

  // template<typename ForwardIterator>
  // inline double pdf(ForwardIterator it) const {
  // public double pdf(double[] v) {
  // if (!m_pred.check(v))
  // return 0.; // it is outside the support
  // return m_variate.pdf(v) * m_normalizer.inv_probability(); // renormalization by the current
  // // estimate of the rejection
  // // probability
  // }

  Normalizer normalizer() {
    return m_normalizer;
  }
}
