package fr.ign.rjmcmc.kernel;

import java.util.Vector;

public class IdentityTransform implements Transform {

  @Override
  public double getAbsJacobian(boolean direct) {
    return 1;
  }

  @Override
  public int dimension(int n0, int n1) {
    return Math.max(n0, n1);
  }

  @Override
  public double apply(boolean direct, Vector<Double> val0, Vector<Double> var0,
      Vector<Double> val1, Vector<Double> var1) {
    for (int index = 0; index < val0.size() + var0.size(); index++) {
      double val = (index < val0.size()) ? val0.get(index) : var0.get(index - val0.size());
      if (index < val1.size()) {
        val1.set(index, val);
      } else {
        var1.set(index - val1.size(), val);
      }
    }
    return 1;
  }

}
