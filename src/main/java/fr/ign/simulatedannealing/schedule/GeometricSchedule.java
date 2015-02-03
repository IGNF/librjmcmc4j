package fr.ign.simulatedannealing.schedule;

import fr.ign.simulatedannealing.temperature.Temperature;

public class GeometricSchedule<T extends Temperature> implements Schedule<T> {
  T temperature;
  double alpha;

  public GeometricSchedule(T temp, double alpha) {
    this.temperature = temp;
    this.alpha = alpha;
  }

  @Override
  public T getTemperature() {
    return this.temperature;
  }

  @Override
  public Schedule<T> next() {
    this.temperature.cool(alpha);
    return this;
  }

}
