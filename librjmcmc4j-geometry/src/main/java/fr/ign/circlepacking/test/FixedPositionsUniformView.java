package fr.ign.circlepacking.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.random.RandomGenerator;

import fr.ign.mpp.configuration.AbstractBirthDeathModification;
import fr.ign.mpp.configuration.AbstractGraphConfiguration;
import fr.ign.mpp.configuration.GraphVertex;
import fr.ign.mpp.kernel.ObjectBuilder;
import fr.ign.rjmcmc.kernel.SimpleObject;
import fr.ign.rjmcmc.kernel.View;

public class FixedPositionsUniformView<T extends SimpleObject, C extends AbstractGraphConfiguration<T, C, M>, M extends AbstractBirthDeathModification<T, C, M>>
    implements View<C, M> {
  /**
   * Logger.
   */
  static Logger LOGGER = Logger.getLogger(FixedPositionsUniformView.class.getName());

  private int dimension = 0;
  private ObjectBuilder<T> builder;
  private Integer[] ids;
  public Map<Integer, Integer> mapBirth;
  public Map<Integer, Integer> mapDeath;

  public FixedPositionsUniformView(ObjectBuilder<T> b, Map<Integer, List<Double>> map) {
    this.builder = b;
    this.dimension = b.size();
    this.ids = map.keySet().toArray(new Integer[map.size()]);
    this.mapBirth = new HashMap<>();
    this.mapDeath = new HashMap<>();
    for (int i = 0; i < this.ids.length; i++) {
      this.mapBirth.put(this.ids[i], 0);
      this.mapDeath.put(this.ids[i], 0);
    }
  }

  private int sample(RandomGenerator rng, int n) {
    return new UniformIntegerDistribution(rng, 0, n - 1).sample();
  }

  @Override
  public double select(boolean direct, RandomGenerator e, C conf, M modif, double[] val) {
    return direct ? selectDeath(e, conf, modif, val) : selectBirth(e, conf, modif, val);
  }

  private double selectDeath(RandomGenerator e, C conf, M modif, double[] out) {
    int size = conf.size();
    int denom = size;
//    int index = (size == 1) ? 0 : sample(e, size);
    List<Integer> idList = new ArrayList<>();
    for (GraphVertex<T> vertex : conf.getGraph().vertexSet()) {
      Integer currentid = Integer.valueOf((int) vertex.getValue().toArray()[0]);
      idList.add(currentid);
    }
    LOGGER.log(Level.SEVERE, "\tDEATH " + idList + " available");
    int index = sample(e, size);
    Iterator<T> it = conf.iterator();
    for (int j = 0; j < index; j++) {
      it.next();
    }
    T t = it.next();
    modif.insertDeath(t);
    this.builder.setCoordinates(t, out);
    int id = (int) out[0];
    LOGGER.log(Level.SEVERE, "DEATH chose id = " + id);
    this.mapDeath.put(id, this.mapDeath.get(id) + 1);
    return 1. / (double) denom;
  }

  private double selectBirth(RandomGenerator e, C conf, M modif, double[] in) {
    int beg = conf.size() - modif.getDeath().size() + 1;
    int denom = beg;
    double[] v = new double[this.dimension];
    for (int i = 1; i < this.dimension; i++) {
      v[i] = in[i];
    }
    // replace first value by id
    // LOGGER.log(Level.INFO, "size = " + conf.size() + " so " + (ids.length - conf.size()) + " ids available");
    // LOGGER.log(Level.INFO, "chose index = " + sample);
    List<Integer> idList = new ArrayList<>(Arrays.asList(ids));
    for (GraphVertex<T> vertex : conf.getGraph().vertexSet()) {
      Integer id = Integer.valueOf((int) vertex.getValue().toArray()[0]);
      idList.remove(id);
      // LOGGER.log(Level.INFO, "\t" + id + " taken");
    }
    LOGGER.log(Level.SEVERE, "\tBIRTH " + idList + " available");
    int sampleIndex = sample(e, idList.size());
    int id = idList.get(sampleIndex);
    LOGGER.log(Level.SEVERE, "BIRTH chose id = " + id);
    v[0] = id;
    this.mapBirth.put(id, this.mapBirth.get(id) + 1);
    modif.insertBirth(this.builder.build(v));
    return 1. / (double) denom;
  }

  @Override
  public int dimension() {
    return this.dimension;
  }
}
