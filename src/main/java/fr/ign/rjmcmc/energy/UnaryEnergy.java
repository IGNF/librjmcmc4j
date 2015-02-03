package fr.ign.rjmcmc.energy;



public interface UnaryEnergy<T> extends Energy {
  double getValue(T t);
}
