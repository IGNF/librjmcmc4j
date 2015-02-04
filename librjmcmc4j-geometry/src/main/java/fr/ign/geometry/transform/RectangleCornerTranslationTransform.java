package fr.ign.geometry.transform;

import java.util.Vector;

import org.apache.log4j.Logger;

import fr.ign.rjmcmc.kernel.Transform;

public class RectangleCornerTranslationTransform implements Transform {
  /**
   * Logger.
   */
  static Logger LOGGER = Logger.getLogger(RectangleCornerTranslationTransform.class.getName());

//  @Override
//  public int dimension() {
//    return 7;
//  }

  public RectangleCornerTranslationTransform(int n) {
    this(n, 10);
  }

  int cornerNumber;
  double m_d;

  public RectangleCornerTranslationTransform(int n, double d) {
    this.cornerNumber = n;
    this.m_d = d;
  }

  // public double apply(double[] in, double[] out) {
  @Override
  public double apply(boolean direct, Vector<Double> val0, Vector<Double> var0,
      Vector<Double> val1, Vector<Double> var1) {
    // double x = in[0];
    // double y = in[1];
    // double u = in[2];
    // double v = in[3];
    // double r = in[4];
    // double s = in[5];
    // double t = in[6];
    double x = val0.get(0);
    double y = val0.get(1);
    double u = val0.get(2);
    double v = val0.get(3);
    double r = val0.get(4);
    double s = var0.get(0);
    double t = var0.get(1);

    double dx = m_d * (s - 0.5);
    double dy = m_d * (t - 0.5);
    switch (this.cornerNumber) {
      case 0:
        // out[0] = x + dx - r * dy;
        // out[1] = y + dy + r * dx;
        val1.set(0, x + dx - r * dy);
        val1.set(1, y + dy + r * dx);
        break;
      case 1:
        val1.set(0, x + dx + r * dy);
        val1.set(1, y + dy - r * dx);
        break;
      case 2:
        val1.set(0, x - dx - r * dy);
        val1.set(1, y - dy + r * dx);
        break;
      case 3:
        val1.set(0, x - dx + r * dy);
        val1.set(1, y - dy - r * dx);
        break;
    }
    val1.set(2, u + dx);
    val1.set(3, v + dy);
    val1.set(4, r);
    var1.set(0, 1 - s);
    var1.set(1, 1 - t);
    return 1.0;
  }

  // @Override
  // public double inverse(double[] in, double[] out) {
  // return this.apply(in, out);
  // }

  @Override
  public double getAbsJacobian(boolean d) {
    return 1;
  }

//  @Override
//  public double getInverseAbsJacobian(double[] d) {
//    return 1;
//  }

  @Override
  public int dimension(int n0, int n1) {
    return 7;
  }
}
