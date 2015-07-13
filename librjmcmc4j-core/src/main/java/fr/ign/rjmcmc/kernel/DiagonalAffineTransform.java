package fr.ign.rjmcmc.kernel;


public class DiagonalAffineTransform implements Transform {
  private double delta[][];
  private double mat[][];
  private double absJacobian[];

  // @Override
  public double getAbsJacobian(boolean direct) {
    return this.absJacobian[direct ? 0 : 1];
  }

  public double[] getDelta() {
    return this.delta[0];
  }

  public double[] getDeltaInv() {
    return this.delta[1];
  }

  public double[] getMat() {
    return this.mat[0];
  }

  public double[] getMatInv() {
    return this.mat[1];
  }

  public DiagonalAffineTransform(double[] d, double[] coordinates) {
    this.mat = new double[2][d.length];
    this.delta = new double[2][d.length];
    double determinant = 1.;
    for (int i = 0; i < d.length; ++i) {
      double dvalue = d[i];
      determinant *= dvalue;
      mat[0][i] = dvalue;
      mat[1][i] = 1 / dvalue;
      delta[0][i] = coordinates[i];
      delta[1][i] = -coordinates[i] / dvalue;
    }
    this.absJacobian = new double[2];
    this.absJacobian[0] = Math.abs(determinant);
    this.absJacobian[1] = Math.abs(1 / determinant);
  }

  @Override
  public int dimension() {
    return this.mat[0].length;
  }

  @Override
  public double apply(boolean direct, double[] in, double[] out) {
    return apply(direct ? 0 : 1, in, out);
  }

  private double apply(int index, double[] in, double[] out) {
    for (int i = 0; i < in.length; i++) {
      out[i] = in[i] * mat[index][i] + delta[index][i];
    }
    return this.absJacobian[index];
  }
}
