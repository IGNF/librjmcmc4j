package fr.ign.circlepacking.fixedconfiguration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.ign.mpp.configuration.ListConfiguration;

public class CirclePackingFixedConfiguration implements ListConfiguration<IndexedCircle2D, CirclePackingFixedConfiguration, CirclePackingFixedModification> {
  Map<Integer, List<Double>> idPositionMap;
  List<IndexedCircle2D> circles;
  double unary;
  double binary;

  public CirclePackingFixedConfiguration(Map<Integer, List<Double>> map, double u, double b) {
    this.idPositionMap = map;
    this.circles = new ArrayList<>();
    this.unary = u;
    this.binary = b;
  }

  @Override
  public double deltaEnergy(CirclePackingFixedModification m) {
    double uEnergy = 0.0;
    double bEnergy = 0.0;
    switch (m.type) {
    case 0:
      uEnergy += m.circle.getArea();
      for (IndexedCircle2D c : this.circles) {
        bEnergy += m.circle.intersectionArea(c);
      }
      break;
    case 1:
      uEnergy -= m.circle.getArea();
      for (IndexedCircle2D c : this.circles) {
        if (c.index != m.circle.index) {
          bEnergy -= m.circle.intersectionArea(c);
        }
      }
      break;
    default:
      uEnergy -= m.circle.getArea();
      for (IndexedCircle2D c : this.circles) {
        if (c.index != m.circle.index) {
          bEnergy -= m.circle.intersectionArea(c);
        }
      }
      uEnergy += m.circle.getArea();
      for (IndexedCircle2D c : this.circles) {
        if (c.index != m.circle.index) {
          bEnergy += m.circle.intersectionArea(c);
        }
      }
    }
    return this.unary * uEnergy + this.binary * bEnergy;
  }

  @Override
  public double getEnergy() {
    double uEnergy = 0.0;
    double bEnergy = 0.0;
    for (int index = 0; index < this.circles.size(); index++) {
      IndexedCircle2D circle = this.circles.get(index);
      uEnergy += circle.getArea();
      for (int index2 = 0; index2 < index; index2++) {
        IndexedCircle2D circle2 = this.circles.get(index2);
        bEnergy += circle.intersectionArea(circle2);
      }
    }
    return this.unary * uEnergy + this.binary * bEnergy;
  }

  @Override
  public CirclePackingFixedModification newModification() {
    return new CirclePackingFixedModification();
  }

  public int size() {
    return this.circles.size();
  }

  public void clear() {
    this.circles.clear();
  }

  public List<Integer> getAvailableIds() {
    List<Integer> availableIds = new ArrayList<>(this.idPositionMap.keySet());
    for (IndexedCircle2D c : this.circles) {
      availableIds.remove(new Integer(c.index));
    }
    return availableIds;
  }

  @Override
  public void insert(IndexedCircle2D v) {
    this.circles.add(v);
  }

  @Override
  public void remove(IndexedCircle2D v) {
    this.circles.remove(v);
  }

  @Override
  public Iterator<IndexedCircle2D> iterator() {
    return this.circles.iterator();
  }

  @Override
  public double getUnaryEnergy() {
    return 0;
  }

  @Override
  public double getBinaryEnergy() {
    return 0;
  }

  @Override
  public double getUnaryEnergy(IndexedCircle2D o) {
    return 0;
  }

  @Override
  public double getGlobalEnergy() {
    return 0;
  }
}
