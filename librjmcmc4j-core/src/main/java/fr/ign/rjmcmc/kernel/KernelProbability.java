package fr.ign.rjmcmc.kernel;

import fr.ign.rjmcmc.configuration.Configuration;
import fr.ign.rjmcmc.configuration.Modification;

public interface KernelProbability<C extends Configuration<C, M>, M extends Modification<C, M>> {
  double probability(C c);
}
