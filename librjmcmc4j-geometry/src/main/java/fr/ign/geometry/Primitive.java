package fr.ign.geometry;

import com.vividsolutions.jts.geom.Geometry;

public interface Primitive {
  double intersectionArea(Primitive p);
  Geometry toGeometry();
}
