package fr.ign.mpp.kernel;


/**
 * Object builder
 * @param <T> type
 */
public interface ObjectBuilder<T> {
  T build(double[] val);
  void setCoordinates(T t, double[] val);
  int size();
}
