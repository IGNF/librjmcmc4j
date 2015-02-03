package fr.ign.rjmcmc.energy;

import java.util.Collection;



public interface CollectionEnergy<T> extends Energy {
  double getValue(final Collection<T> t);
}
