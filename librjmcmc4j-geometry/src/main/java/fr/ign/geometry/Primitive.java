package fr.ign.geometry;

import org.locationtech.jts.geom.Geometry;

import fr.ign.rjmcmc.kernel.SimpleObject;

public interface Primitive extends SimpleObject {
  double intersectionArea(Primitive p);
  Geometry toGeometry();
  double getArea();
}
