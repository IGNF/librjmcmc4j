package fr.ign.circlepacking.fixedconfiguration;

import java.util.List;
import java.util.Map;

import org.apache.commons.math3.random.RandomGenerator;

import fr.ign.mpp.kernel.ObjectBuilder;
import fr.ign.rjmcmc.distribution.Distribution;
import fr.ign.rjmcmc.kernel.Transform;
import fr.ign.rjmcmc.kernel.Variate;
import fr.ign.rjmcmc.sampler.Density;

public class CirclePackingFixedDensity implements Density<CirclePackingFixedConfiguration, CirclePackingFixedModification> {

  Distribution density;
  RandomGenerator rng;
  ObjectBuilder<IndexedCircle2D> builder;
  Map<Integer, List<Double>> map;
  Transform transform;
  Variate variate;

  public CirclePackingFixedDensity(Distribution d, RandomGenerator g, Transform transform, Variate variate, ObjectBuilder<IndexedCircle2D> b,
      Map<Integer, List<Double>> m) {
    this.density = d;
    this.rng = g;
    this.builder = b;
    this.map = m;
    this.transform = transform;
    this.variate = variate;
  }

  public double pdf(IndexedCircle2D t) {
    double[] val1 = new double[this.builder.size()];
    this.builder.setCoordinates(t, val1);
    double[] val0 = new double[this.builder.size()];
    double J10 = this.transform.apply(false, val1, val0);
    double pdf = this.variate.pdf(val0, 1);// ignore id
    return pdf * J10;
  }

  @Override
  public double pdfRatio(CirclePackingFixedConfiguration config, CirclePackingFixedModification modif) {
    int n0 = config.size();
    int n1 = n0;
    switch (modif.type) {
    case 0:
      n1++;
      break;
    case 1:
      n1--;
      break;
    default:
    }
    double ratio = this.density.pdfRatio(n0, n1);
    double pdf;
    switch (modif.type) {
    case 0:
      pdf = this.pdf(modif.circle);
      if (pdf == 0) {
        return 0;
      }
      ratio *= pdf;
      break;
    case 1:
      pdf = this.pdf(modif.circle);
      if (pdf == 0) {
        return 0;
      }
      ratio /= pdf;
      break;
    default:
    }
    return ratio;
  }
}
