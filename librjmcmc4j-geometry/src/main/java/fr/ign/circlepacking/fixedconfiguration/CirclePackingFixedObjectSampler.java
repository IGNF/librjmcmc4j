package fr.ign.circlepacking.fixedconfiguration;

import java.util.List;
import java.util.Map;

import org.apache.commons.math3.random.RandomGenerator;

import fr.ign.mpp.kernel.ObjectBuilder;
import fr.ign.mpp.kernel.ObjectSampler;
import fr.ign.rjmcmc.kernel.DiagonalAffineTransform;
import fr.ign.rjmcmc.kernel.Transform;
import fr.ign.rjmcmc.kernel.Variate;

public class CirclePackingFixedObjectSampler implements ObjectSampler<IndexedCircle2D> {

  int dimension = 2;
  Variate variate;
  Transform transform;
  ObjectBuilder<IndexedCircle2D> builder;
  IndexedCircle2D object;

  public CirclePackingFixedObjectSampler(RandomGenerator rng, double[] a, double[] b, ObjectBuilder<IndexedCircle2D> builder, Map<Integer, List<Double>> map) {
    this.variate = new Variate(rng);
    double[] d = new double[a.length];
    for (int i = 0; i < d.length; i++)
      d[i] = b[i] - a[i];
    this.transform = new DiagonalAffineTransform(d, a);
    this.builder = builder;
  }

  @Override
  public double sample(RandomGenerator e) {
    double[] val0 = new double[this.dimension];
    double[] val1 = new double[this.dimension];
    double phi = this.variate.compute(val0, 0);
    double jacob = this.transform.apply(true, val0, val1);
    // val1[0] = this.ids[(int) (val1[0] * this.ids.length)];
    this.object = this.builder.build(val1);
    return phi / jacob;
  }

  @Override
  public double pdf(IndexedCircle2D t) {
    double[] val1 = new double[this.dimension];
    this.builder.setCoordinates(t, val1);
    double[] val0 = new double[this.dimension];
    double J10 = this.transform.apply(false, val1, val0);
    double pdf = this.variate.pdf(val0, 1);// ignore id
    return pdf * J10;
  }

  @Override
  public IndexedCircle2D getObject() {
    return this.object;
  }
  public Transform getTransform() {
    return this.transform;
  }

  public Variate getVariate() {
    return this.variate;
  }
}
