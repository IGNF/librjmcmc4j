package fr.ign.mpp.kernel;



public interface ObjectBuilder<T> {
  T build(double[] val);
  void setCoordinates(T t, double[] val);
  int size();
}
