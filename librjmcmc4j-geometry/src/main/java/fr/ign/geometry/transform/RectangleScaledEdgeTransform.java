package fr.ign.geometry.transform;

import fr.ign.rjmcmc.kernel.Transform;

public class RectangleScaledEdgeTransform implements Transform {
  public RectangleScaledEdgeTransform() {
  }

  @Override
  public double apply(boolean direct, double[] in, double[] out) {
    double x = in[0];
    double y = in[1];
    double u = in[2];
    double v = in[3];
    double r = in[4];
    double s = in[5];
    double f = Math.exp(2.0 * (s - 0.5));
    double g = 1 - f;
    // res = Rectangle_2(c+m*(1-f), n,f*r);
    out[0] = x - g * r * v;
    out[1] = y + g * r * u;
    out[2] = u;
    out[3] = v;
    out[4] = f * r;
    out[5] = 1.0 - s;
    return 1;
  }

  @Override
  public int dimension() {
    return 6;
  }
}
