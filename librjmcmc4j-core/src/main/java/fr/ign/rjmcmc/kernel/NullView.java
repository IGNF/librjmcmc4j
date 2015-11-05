package fr.ign.rjmcmc.kernel;

import org.apache.commons.math3.random.RandomGenerator;

import fr.ign.rjmcmc.configuration.Configuration;
import fr.ign.rjmcmc.configuration.Modification;

public class NullView<C extends Configuration<C, M>, M extends Modification<C, M>> implements View<C, M> {
  @Override
  public double select(boolean direct, RandomGenerator e, C c, M modif, double[] val) {
    return 1;
  }

  @Override
  public int dimension() {
    return 0;
  }
}
