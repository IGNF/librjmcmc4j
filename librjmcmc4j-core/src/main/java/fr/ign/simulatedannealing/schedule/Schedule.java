package fr.ign.simulatedannealing.schedule;

import fr.ign.simulatedannealing.temperature.Temperature;

public interface Schedule<T extends Temperature> {
  public T getTemperature();
  public Schedule<T> next();
}
