package fr.ign.rjmcmc.kernel;

import java.util.Vector;

import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Assert;
import org.junit.Test;

import fr.ign.random.Random;

public class RejectionVariateTest {
  private void test(int iter, RejectionVariate.Normalizer normalizer) {
	RandomGenerator rng = Random.random();
    RejectionVariate rej = new RejectionVariate(rng, new Variate(rng), new Predicate() {
      @Override
      public boolean check(Vector<Double> val) {
        double x = val.get(0).doubleValue();
        double y = val.get(1).doubleValue();
        // System.out.println(x + "+" + y + " = " + (x+y) + " -> " + ((x + y) < 1.));
        return (x + y) < 1.;
      }
    }, normalizer);

    Vector<Double> val = new Vector<>();
    val.setSize(2);
    String s = "";
    // repeated sampling
    for (int i = 0; i < iter; ++i) {
      double pdf = rej.compute(val);
      if (pdf == 0)
        continue; // sampling failed
//      if (pdf != rej.pdf(val)) {
//        System.out.println("pdf mismatch : " + pdf + " = " + rej.pdf(val));
//      }
      if (i % (iter / 20) == 0) {
        s += ((double) ((int) (pdf * 1000)) / 1000) + ", ";
      }
    }
    System.out.println(s);
    double result = 1. / rej.normalizer().inv_probability();
    System.out.println("-> " + result);
    Assert.assertEquals("Wrong integration result", 0.5, result, 0.05);
  }

  @Test
  public void test() {
    int iter = 100000;
    test(iter, new RejectionVariate.constant_normalizer(0.5));
    test(iter, new RejectionVariate.monte_carlo());
    test(iter, new RejectionVariate.decay_monte_carlo(0.999));
    test(iter, new RejectionVariate.sliding_monte_carlo(1000));
  }
}
