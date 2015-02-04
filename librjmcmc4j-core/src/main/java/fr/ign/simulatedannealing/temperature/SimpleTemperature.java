package fr.ign.simulatedannealing.temperature;

public class SimpleTemperature implements Temperature {
  double[] temperature;

  public SimpleTemperature(double... temps) {
    this.temperature = temps;
  }

  @Override
  public void cool(double alpha) {
    for (int i = 0; i < this.temperature.length; i++) {
      this.temperature[i] *= alpha;
    }
  }

  @Override
  public double getTemperature(int i) {
    return this.temperature[i % this.temperature.length];
  }

  @Override
  public int size() {
    return this.temperature.length;
  }

}
