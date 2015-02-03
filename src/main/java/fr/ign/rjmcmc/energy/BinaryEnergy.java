package fr.ign.rjmcmc.energy;



public interface BinaryEnergy<T,U> extends Energy {
  double getValue(T t, U u);
}
