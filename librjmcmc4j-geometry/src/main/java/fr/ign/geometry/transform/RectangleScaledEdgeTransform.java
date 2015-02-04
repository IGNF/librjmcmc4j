package fr.ign.geometry.transform;

import java.util.Vector;

import org.apache.log4j.Logger;

import fr.ign.rjmcmc.kernel.Transform;

public class RectangleScaledEdgeTransform implements Transform {
  /**
   * Logger.
   */
  static Logger LOGGER = Logger.getLogger(RectangleScaledEdgeTransform.class.getName());

  // @Override
  // public int dimension() {
  // return 6;
  // }

  public RectangleScaledEdgeTransform() {
  }

  @Override
  public double apply(boolean direct, Vector<Double> val0, Vector<Double> var0,
      Vector<Double> val1, Vector<Double> var1) {
    double x = val0.get(0);
    double y = val0.get(1);
    double u = val0.get(2);
    double v = val0.get(3);
    double r = val0.get(4);
    double s = var0.get(0);
    double f = Math.exp(2.0 * (s - 0.5));
    double g = 1 - f;
    // res = Rectangle_2(c+m*(1-f), n,f*r);
    val1.set(0, x - g * r * v);
    val1.set(1, y + g * r * u);
    val1.set(2, u);
    val1.set(3, v);
    val1.set(4, f * r);
    var1.set(0, 1.0 - s);
    return 1;
  }

  @Override
  public int dimension(int n0, int n1) {
    return 6;
  }

  // @Override
  // public double inverse(double[] in, double[] out) {
  // return this.apply(in, out);
  // }
  //
  @Override
  public double getAbsJacobian(boolean d) {
    return 1;
  }
  // @Override
  // public double getInverseAbsJacobian(double[] d) {
  // return 1;
  // }
}
