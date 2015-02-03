package fr.ign.rjmcmc.kernel;

import java.util.Vector;

import org.apache.log4j.Logger;

public class DiagonalAffineTransform implements Transform {
  /**
   * Logger.
   */
  static Logger LOGGER = Logger.getLogger(DiagonalAffineTransform.class.getName());

  private double delta[][];
  // private double delta_inv[];
  private double mat[][];
  // private double mat_inv[];
  private double absJacobian[];

  // private double absJacobian_inv;

  @Override
  public double getAbsJacobian(boolean direct) {
    return this.absJacobian[direct ? 0 : 1];
  }

  //
  // @Override
  // public double getInverseAbsJacobian(double[] d) {
  // return this.absJacobian[1];
  // }

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

  public DiagonalAffineTransform(Vector<Double> d, Vector<Double> coordinates) {
    this.mat = new double[2][d.size()];
    // this.mat_inv = new double[d.length];
    this.delta = new double[2][d.size()];
    // this.delta_inv = new double[d.length];
    double determinant = 1.;
    for (int i = 0; i < d.size(); ++i) {
      double dvalue = d.get(i);
      determinant *= dvalue;
      mat[0][i] = dvalue;
      mat[1][i] = 1 / dvalue;
      delta[0][i] = coordinates.get(i);
      delta[1][i] = -coordinates.get(i) / dvalue;
    }
    this.absJacobian = new double[2];
    this.absJacobian[0] = Math.abs(determinant);
    this.absJacobian[1] = Math.abs(1 / determinant);
  }

  // @Override
  // public double apply(double[] in, double[] out) {
  // for (int i = 0; i < out.length; i++) {
  // out[i] = in[i] * mat[i] + delta[i];
  // }
  // return this.absJacobian;
  // }
  //
  // @Override
  // public double inverse(double[] in, double[] out) {
  // for (int i = 0; i < out.length; i++) {
  // out[i] = in[i] * mat_inv[i] + delta_inv[i];
  // }
  // return this.absJacobian_inv;
  // }

  // @Override
  // public int dimension() {
  // return this.mat.length;
  // }

  @Override
  public int dimension(int n0, int n1) {
    return this.mat[0].length;
  }

  @Override
  public double apply(boolean direct, Vector<Double> val0, Vector<Double> var0,
      Vector<Double> val1, Vector<Double> var1) {
    return apply(direct ? 0 : 1, val0, var0, val1, var1);
  }

  private double apply(int index, Vector<Double> val0, Vector<Double> var0, Vector<Double> val1,
      Vector<Double> var1) {
    for (int i = 0; i < val0.size() + var0.size(); i++) {
      double val = ((i < val0.size()) ? val0.get(i) : var0.get(i - val0.size())) * mat[index][i]
          + delta[index][i];
      if (i < val1.size()) {
        val1.set(i, val);
      } else {
        var1.set(i - val1.size(), val);
      }
    }
    return this.absJacobian[index];
  }
}
