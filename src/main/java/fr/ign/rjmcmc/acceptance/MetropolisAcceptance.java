package fr.ign.rjmcmc.acceptance;

import fr.ign.simulatedannealing.temperature.Temperature;

public class MetropolisAcceptance<T extends Temperature> implements Acceptance<T> {
  @Override
  public double compute(double delta, Temperature temperature, double greenRatio) {
    double result = greenRatio;
    for (int i = 0; i < temperature.size(); i++) {
      result *= Math.exp(-delta / temperature.getTemperature(i));
    }
    return result;
  }
}
