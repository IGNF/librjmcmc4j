package fr.ign.circlepacking.fixedconfiguration;

import fr.ign.rjmcmc.kernel.Transform;

public class IndexedCircleRadiusTransform implements Transform {
  private double d;
  public IndexedCircleRadiusTransform(double d) {
    this.d = d;
  }

  @Override
  public int dimension() {
    return 3;
  }

  @Override
  public double apply(boolean direct, double[] in, double[] out) {
    int id = (int) in[0];
    double radius = in[1];
    double s = in[2];
    out[0] = id;
    out[1] = (s - 0.5) * d + radius;
    out[2] = 1 - s;
    // abs(determinant(jacobian([(s - 0.5) * d + radius, 1-s],[radius, s]))) = 1
    return 1.0;
  }
}
