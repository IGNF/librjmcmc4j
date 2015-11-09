package fr.ign.circlepacking;

import fr.ign.rjmcmc.kernel.Transform;

public class CircleCenterTransform implements Transform {
  private double d;

  public CircleCenterTransform(double d) {
    this.d = d;
  }

  @Override
  public int dimension() {
    return 5;
  }

  @Override
  public double apply(boolean direct, double[] in, double[] out) {
    double x = in[0];
    double y = in[1];
    double radius = in[2];
    double s = in[3];
    double t = in[4];
    out[0] = (s - 0.5) * d + x;
    out[1] = (t - 0.5) * d + y;
    out[2] = radius;
    out[3] = 1 - s;
    out[4] = 1 - t;
    return 1.0;
  }
}
