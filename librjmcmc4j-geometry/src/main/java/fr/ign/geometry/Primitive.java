package fr.ign.geometry;

import com.vividsolutions.jts.geom.Geometry;

import fr.ign.rjmcmc.kernel.SimpleObject;

public interface Primitive extends SimpleObject {
  double intersectionArea(Primitive p);
  Geometry toGeometry();
  double getArea();
}
