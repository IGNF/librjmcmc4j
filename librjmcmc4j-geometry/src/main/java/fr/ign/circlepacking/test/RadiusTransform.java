package fr.ign.circlepacking.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ign.rjmcmc.kernel.Transform;

public class RadiusTransform implements Transform {
  private double d;
  public Map<Integer, Integer> map;

  public RadiusTransform(double d, Map<Integer, List<Double>> fixedPositionsMap) {
    this.d = d;
    this.map = new HashMap<>();
    for (int id : fixedPositionsMap.keySet()) this.map.put(id, 0);
  }

  @Override
  public int dimension() {
    return 3;
  }

  @Override
  public double apply(boolean direct, double[] in, double[] out) {
    int id = (int) in[0];
    this.map.put(id, this.map.get(id) + 1);
    double radius = in[1];
    double s = in[2];
    out[0] = id;
    out[1] = (s - 0.5) * d + radius;
    out[2] = 1 - s;
    // abs(determinant(jacobian([(s - 0.5) * d + radius, 1-s],[radius, s]))) = 1
    return 1.0;
  }
}
