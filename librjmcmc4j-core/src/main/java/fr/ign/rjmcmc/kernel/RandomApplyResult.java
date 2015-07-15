package fr.ign.rjmcmc.kernel;

public class RandomApplyResult {
  public double kernelRatio;
  public int kernelId;
  public RandomApplyResult(double r, int id) {
    this.kernelRatio = r;
    this.kernelId = id;
  }
}
