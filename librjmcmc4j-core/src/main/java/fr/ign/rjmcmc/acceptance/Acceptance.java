package fr.ign.rjmcmc.acceptance;

import fr.ign.simulatedannealing.temperature.Temperature;

public interface Acceptance<T extends Temperature> {
  double compute(double delta, Temperature temperature, double green_ratio);
}
