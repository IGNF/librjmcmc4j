package fr.ign.simulatedannealing.temperature;

public interface Temperature {
  void cool(double alpha);
  double getTemperature(int i);
  int size();
}
