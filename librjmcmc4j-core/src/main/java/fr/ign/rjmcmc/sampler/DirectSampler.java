package fr.ign.rjmcmc.sampler;

import org.apache.commons.math3.random.RandomGenerator;

import fr.ign.rjmcmc.configuration.Configuration;
import fr.ign.rjmcmc.configuration.Modification;

public interface DirectSampler<C extends Configuration<C, M>, M extends Modification<C, M>> extends Density<C, M> {
  void init(RandomGenerator random, C conf);
}
