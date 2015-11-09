package fr.ign.lamps;

import fr.ign.rjmcmc.kernel.Transform;

public class CircleCenterTransform implements Transform {
  private double d;

  public CircleCenterTransform(double d) {
    this.d = d;
  }

  @Override
  public int dimension() {
    return 41;
  }

  @Override
  public double apply(boolean direct, double[] in, double[] out) {
    double x = in[0];
    double y = in[1];
    double s = in[2];
    double t = in[3];
    out[0] = (s - 0.5) * d + x;
    out[1] = (t - 0.5) * d + y;
    out[2] = 1 - s;
    out[3] = 1 - t;
    return 1.0;
  }
}
