package fr.ign.geometry.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.triangulate.ConformingDelaunayTriangulationBuilder;

import fr.ign.rjmcmc.kernel.Transform;

public class PolygonTransform implements Transform {
  /**
   * Logger.
   */
  static Logger LOGGER = Logger.getLogger(PolygonTransform.class.getName());
  Polygon polygon;
  List<Double> areas = new ArrayList<>();
  List<Polygon> triangles = new ArrayList<>();
  double totalArea;

  public PolygonTransform(Polygon p, double tolerance) {
    this.polygon = p;
    ConformingDelaunayTriangulationBuilder builder = new ConformingDelaunayTriangulationBuilder();
    builder.setSites(polygon);
    builder.setConstraints(polygon);
    builder.setTolerance(tolerance);
    GeometryCollection triangleCollection = (GeometryCollection) builder.getTriangles(polygon.getFactory());
    double areaSum = 0.;
    for (int index = 0; index < triangleCollection.getNumGeometries(); index++) {
      Polygon t = (Polygon) triangleCollection.getGeometryN(index);
      double area = t.getArea();
      if (t.intersection(polygon).getArea() > 0.99 * area) {
        areaSum += area;
        areas.add(areaSum);
        triangles.add(t);
      }
    }
    totalArea = areaSum;
  }

  @Override
  public double apply(boolean direct, Vector<Double> val0, Vector<Double> var0, Vector<Double> val1, Vector<Double> var1) {
    if (direct) {
      double s = var0.get(0) * totalArea;
      double t = var0.get(1);
      int triangleIndex = -1;
      for (int i = 0; i < areas.size() && triangleIndex == -1; i++) {
        if (s < areas.get(i))
          triangleIndex = i;
      }
      double area = areas.get(triangleIndex);
      double previousArea = (triangleIndex > 0) ? areas.get(triangleIndex - 1) : 0.;
      Polygon triangle = triangles.get(triangleIndex);
      double tmp = Math.sqrt((s - previousArea) / (area - previousArea));
      double a = 1 - tmp;
      double b = (1 - t) * tmp;
      double c = t * tmp;
      Coordinate[] coord = triangle.getCoordinates();
      Coordinate p1 = coord[0];
      Coordinate p2 = coord[1];
      Coordinate p3 = coord[2];
      double x1 = p1.x;
      double x2 = p2.x;
      double x3 = p3.x;
      double y1 = p1.y;
      double y2 = p2.y;
      double y3 = p3.y;
      double x = a * x1 + b * x2 + c * x3;
      double y = a * y1 + b * y2 + c * y3;
      val1.set(0, x);
      val1.set(1, y);
      return 1. / totalArea;
    }
    double s = val0.get(0);
    double t = val0.get(1);
    Point point = polygon.getFactory().createPoint(new Coordinate(s, t));
    Polygon triangle = null;
    for (Polygon tr : triangles) {
      if (tr.contains(point)) {
        triangle = tr;
        break;
      }
    }
    Coordinate[] coord = triangle.getCoordinates();
    Coordinate p1 = coord[0];
    Coordinate p2 = coord[1];
    Coordinate p3 = coord[2];
    double e0 = p2.x - p1.x;
    double e1 = p2.y - p1.y;
    double f0 = p3.x - p2.x;
    double f1 = p3.y - p2.y;
    double v2 = (e1 * (s - p1.x) - e0 * (t - p1.y)) / (f0 * e1 - f1 * e0);
    double v1 = (t - p1.y - v2 * f1) / e1;
    double r1 = v1 * v1;
    double r2 = v2 / v1;
    int ind = triangles.indexOf(triangle);
    double prev = (ind == 0) ? 0. : areas.get(ind - 1);
    double d = (r1 * (areas.get(ind) - prev) + prev) / totalArea;
    var1.set(0, d);
    var1.set(1, (Double.isNaN(r2)) ? 0. : r2);
    return totalArea;
  }

  @Override
  public double getAbsJacobian(boolean direct) {
    return (direct) ? 1 / totalArea : totalArea;
  }

  @Override
  public int dimension(int n0, int n1) {
    return 2;
  }
}
