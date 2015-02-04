package fr.ign.random;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

public class Random {
  private static RandomGenerator generator = null;

  /**
   * Simple function which return an initialised random number generator.
   * @return  an initialised random number generator
   */
  public static RandomGenerator random() {
    if (generator == null) {
      synchronized (Random.class) {
        if (generator == null) {
          generator = new MersenneTwister(System.currentTimeMillis());
        }
      }
    }
    return generator;
  }
}
