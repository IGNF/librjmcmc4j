package fr.ign.rjmcmc.energy;


public class MinusUnaryEnergy<T> implements UnaryEnergyOperator<T> {
  UnaryEnergy<T> energy1;
  UnaryEnergy<T> energy2;
  public MinusUnaryEnergy(UnaryEnergy<T> e1, UnaryEnergy<T> e2) {
    this.energy1 = e1;
    this.energy2 = e2;
  }

  @Override
  public double getValue(T t) {
    return this.energy1.getValue(t) - this.energy2.getValue(t);
  }

}
