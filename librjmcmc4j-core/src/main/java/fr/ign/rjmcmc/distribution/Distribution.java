package fr.ign.rjmcmc.distribution;

import org.apache.commons.math3.random.RandomGenerator;

public interface Distribution {
  public double pdfRatio(int n0, int n1);
  public double pdf(int n);
  public int sample(RandomGenerator e);
}
