package fr.ign.mpp.kernel;

import java.util.List;
import java.util.Vector;


public interface ObjectBuilder<T> {
  T build(Vector<Double> val1);
  void setCoordinates(T t, List<Double> val1);
  int size();
}
