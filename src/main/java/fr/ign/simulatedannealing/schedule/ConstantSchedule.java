package fr.ign.simulatedannealing.schedule;

import fr.ign.simulatedannealing.temperature.Temperature;

public class ConstantSchedule<T extends Temperature> implements Schedule<T> {
  T temperature;

  public ConstantSchedule(T temp) {
    this.temperature = temp;
  }

  @Override
  public T getTemperature() {
    return this.temperature;
  }

  @Override
  public Schedule<T> next() {
    return this;
  }
}
