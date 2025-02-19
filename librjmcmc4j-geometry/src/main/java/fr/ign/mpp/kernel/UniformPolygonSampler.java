package fr.ign.mpp.kernel;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.geotools.api.data.FeatureStore;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.triangulate.ConformingDelaunayTriangulationBuilder;


public class UniformPolygonSampler {
  Polygon polygon;
  Polygon[] triangles;
  double totalArea;
  UniformRealDistribution td;
  UniformRealDistribution sd;

  public UniformPolygonSampler(Polygon polygon, double tolerance) {
    this.polygon = polygon;
    ConformingDelaunayTriangulationBuilder builder = new ConformingDelaunayTriangulationBuilder();
    builder.setSites(polygon);
    builder.setConstraints(polygon);
    builder.setTolerance(tolerance);
    GeometryCollection triangleCollection = (GeometryCollection) builder.getTriangles(polygon
        .getFactory());
    List<Polygon> list = new ArrayList<Polygon>();
    totalArea = 0;
    for (int i = 0; i < triangleCollection.getNumGeometries(); i++) {
      Polygon triangle = (Polygon) triangleCollection.getGeometryN(i);
      // test
      double area = triangle.getArea();
      if (triangle.intersection(polygon).getArea() > 0.99 * area) {
        list.add(triangle);
        totalArea += area;
        System.out.println(triangle);
      }
    }
    this.triangles = list.toArray(new Polygon[list.size()]);
    this.td = new UniformRealDistribution(0, this.totalArea);
    this.sd = new UniformRealDistribution(0, 1);
  }

  public Point sample() {
    double t = this.td.sample();
    int index = 0;
    double currentArea = this.triangles[index].getArea();
    while (currentArea < t) {
      t -= currentArea;
      index++;
      currentArea = this.triangles[index].getArea();
    }
    Polygon triangle = this.triangles[index];
    double s = this.sd.sample();
    t = t / currentArea;
    double a = 1 - Math.sqrt(t);
    double b = (1 - s) * Math.sqrt(t);
    double c = s * Math.sqrt(t);
    return this.polygon.getFactory().createPoint(
        new Coordinate(a * triangle.getCoordinates()[0].x + b * triangle.getCoordinates()[1].x + c
            * triangle.getCoordinates()[2].x, a * triangle.getCoordinates()[0].y + b
            * triangle.getCoordinates()[1].y + c * triangle.getCoordinates()[2].y));
  }

  public static void writeShapefile(String aFileName, Point[] points) {
    try {
      ShapefileDataStore store = new ShapefileDataStore(new File(aFileName).toURI().toURL());
      String specs = "geom:Point"; //$NON-NLS-1$
      String featureTypeName = "Building"; //$NON-NLS-1$
      SimpleFeatureType type = DataUtilities.createType(featureTypeName, specs);
      store.createSchema(type);
      @SuppressWarnings("unchecked")
	FeatureStore<SimpleFeatureType, SimpleFeature> featureStore = (FeatureStore<SimpleFeatureType, SimpleFeature>) store
          .getFeatureSource(featureTypeName);
      DefaultTransaction transaction = new DefaultTransaction();
      ListFeatureCollection collection = new ListFeatureCollection(type);
      int i = 1;
      for (Point p : points) {
        List<Object> liste = new ArrayList<Object>(0);
        liste.add(p);
        SimpleFeature simpleFeature = SimpleFeatureBuilder.build(type, liste.toArray(),
            String.valueOf(i++));
        collection.add(simpleFeature);
      }
      featureStore.addFeatures(collection);
      transaction.commit();
      transaction.close();
      store.dispose();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (SchemaException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public static void main(String[] args) throws ParseException {
    WKTReader reader = new WKTReader();
    Polygon polygon = (Polygon) reader
        .read("POLYGON (( 529 439, 499 800, 912 699, 886 855, 1278 900, 1277 300, 1043 654, 710 679, 787 417, 958 552, 1141 205, 709 163, 529 439 ))");
    UniformPolygonSampler sampler = new UniformPolygonSampler(polygon, 0.1);
    List<Point> list = new ArrayList<Point>();
    for (int i = 0; i < 100000; i++) {
      list.add(sampler.sample());
    }
    writeShapefile("H:\\Points.shp", list.toArray(new Point[list.size()]));
  }
}
