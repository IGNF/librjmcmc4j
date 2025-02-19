package fr.ign.mpp.kernel;

import org.apache.commons.math3.random.RandomGenerator;

/**
 * Object sampler interface
 * @param <T> type
 */
public interface ObjectSampler<T> {
  double sample(RandomGenerator e);
  T getObject();
  double pdf(T t);
}
