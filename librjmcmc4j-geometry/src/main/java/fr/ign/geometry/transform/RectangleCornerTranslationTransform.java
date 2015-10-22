package fr.ign.geometry.transform;

import fr.ign.rjmcmc.kernel.Transform;

public class RectangleCornerTranslationTransform implements Transform {
  public RectangleCornerTranslationTransform(int n) {
    this(n, 10);
  }

  int cornerNumber;
  double m_d;

  public RectangleCornerTranslationTransform(int n, double d) {
    this.cornerNumber = n;
    this.m_d = d;
  }

  @Override
  public double apply(boolean direct, double[] in, double[] out) {
    double x = in[0];
    double y = in[1];
    double u = in[2];
    double v = in[3];
    double r = in[4];
    double s = in[5];
    double t = in[6];

    double dx = m_d * (s - 0.5);
    double dy = m_d * (t - 0.5);
    switch (this.cornerNumber) {
    case 0:
      out[0] = x + dx - r * dy;
      out[1] = y + dy + r * dx;
      break;
    case 1:
      out[0] = x + dx + r * dy;
      out[1] = y + dy - r * dx;
      break;
    case 2:
      out[0] = x - dx - r * dy;
      out[1] = y - dy + r * dx;
      break;
    case 3:
      out[0] = x - dx + r * dy;
      out[1] = y - dy - r * dx;
      break;
    }
    out[2] = u + dx;
    out[3] = v + dy;
    out[4] = r;
    out[5] = 1 - s;
    out[6] = 1 - t;
    return 1.0;
  }

  // @Override
  public double getAbsJacobian(boolean d) {
    return 1;
  }

  @Override
  public int dimension() {
    return 7;
  }
}
