package fr.ign.rjmcmc.kernel;

import java.util.Vector;

public class Util {
  public static double[] toArray(Vector<Double> v) {
    double[] result = new double[v.size()];
    for (int i = 0; i < v.size(); i++) {
      result[i] = v.get(i).doubleValue();
    }
    return result;
  }
}
