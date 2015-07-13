package fr.ign.rjmcmc.kernel;


public class IdentityTransform implements Transform {

  // @Override
  // public double getAbsJacobian(boolean direct) {
  // return 1;
  // }
  int dimension;

  @Override
  public int dimension() {
    return this.dimension;
  }

  public IdentityTransform(int dim) {
    this.dimension = dim;
  }

  @Override
  public double apply(boolean direct, double[] in, double[] out) {
    for (int index = 0; index < in.length; index++) {
      out[index] = in[index];
    }
    return 1;
  }
}
