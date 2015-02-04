package fr.ign.rjmcmc.kernel;

import java.util.List;

public interface SimpleObject {
  double[] toArray();
  int size();
  void set(List<Double> list);
  Object[] getArray();
}
