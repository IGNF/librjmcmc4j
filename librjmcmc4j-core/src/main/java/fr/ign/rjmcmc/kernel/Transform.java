package fr.ign.rjmcmc.kernel;

import java.util.Vector;

public interface Transform {
  // int dimension();
  // double apply(double[] in, double[] out);
  double getAbsJacobian(boolean direct);

  // double inverse(double[] in, double[] out);
  // double getInverseAbsJacobian(double [] d);
  int dimension(int n0, int n1);

  /**
   * Computes val1 from val0.
   * @param direct
   * @param val0
   * @param var0
   * @param val1
   * @param var1
   * @return
   */
  double apply(boolean direct, Vector<Double> val0, Vector<Double> var0, Vector<Double> val1,
      Vector<Double> var1);
}
