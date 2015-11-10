package fr.ign.simulatedannealing;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.math3.random.RandomGenerator;

import fr.ign.rjmcmc.configuration.Configuration;
import fr.ign.rjmcmc.configuration.Modification;
import fr.ign.rjmcmc.sampler.DirectSampler;

public class SalamonInitialSchedule {
  static Logger LOGGER = Logger.getLogger(SalamonInitialSchedule.class.getName());

  public static <C extends Configuration<C, M>, M extends Modification<C, M>> double getTemperature(RandomGenerator rng, DirectSampler<C, M> s, C c, int iter) {
    double e1 = 0;
    double e2 = 0;
    double inv = 1. / iter;
    for (int i = 0; i < iter; ++i) {
      if (i % 100 == 0) {
        LOGGER.log(Level.FINE, i + " / " + iter);
      }
      s.init(rng, c);
      double e = c.getEnergy();
      double inv_e = inv * e;
      e1 += inv_e;
      e2 += inv_e * e;
    }
    double std_dev = Math.sqrt(e2 - e1 * e1);
    return 2 * std_dev;
  }
}
