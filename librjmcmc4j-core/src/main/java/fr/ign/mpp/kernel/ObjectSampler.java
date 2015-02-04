package fr.ign.mpp.kernel;

import org.apache.commons.math3.random.RandomGenerator;

public interface ObjectSampler<T> {
  double sample(RandomGenerator e);
  T getObject();
  double pdf(T t);
}
