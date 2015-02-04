package fr.ign.rjmcmc.energy;


public class MinusBinaryEnergy<T, U> implements BinaryEnergyOperator<T, U> {
  BinaryEnergy<T,U> energy1;
  BinaryEnergy<T,U> energy2;
  public MinusBinaryEnergy(BinaryEnergy<T,U> e1, BinaryEnergy<T,U> e2) {
    this.energy1 = e1;
    this.energy2 = e2;
  }

  @Override
  public double getValue(T t, U u) {
    return this.energy1.getValue(t, u) - this.energy2.getValue(t, u);
  }

}
