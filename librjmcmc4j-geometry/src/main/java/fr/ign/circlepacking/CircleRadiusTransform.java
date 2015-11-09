package fr.ign.circlepacking;

import fr.ign.rjmcmc.kernel.Transform;

public class CircleRadiusTransform implements Transform {
  private double d;

  public CircleRadiusTransform(double d) {
    this.d = d;
  }

  @Override
  public int dimension() {
    return 4;
  }

  @Override
  public double apply(boolean direct, double[] in, double[] out) {
    double x = in[0];
    double y = in[1];
    double radius = in[2];
    double s = in[3];
    out[0] = x;
    out[1] = y;
    out[2] = (s - 0.5) * d + radius;
    out[3] = 1 - s;
    // abs(determinant(jacobian([(s - 0.5) * d + radius, 1-s],[radius, s]))) = 1
    return 1.0;
  }
}
