package fr.ign.samples.buildingfootprintrectangle;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.geotools.data.FeatureReader;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.GeometryFilter;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import fr.ign.geometry.Rectangle2D;

public class ShapefileReader {
  GeometryFilter filter;

  public ShapefileReader(GeometryFilter geometryFilter) {
    this.filter = geometryFilter;
  }

  public Rectangle2D[] readShapefile(File aFile) {
    try {
      ShapefileDataStore store = new ShapefileDataStore(aFile.toURI().toURL());
      FeatureReader<SimpleFeatureType, SimpleFeature> featureReader = store.getFeatureReader();
      List<Polygon> polygons = new ArrayList<>();
      while (featureReader.hasNext()) {
        SimpleFeature feature = featureReader.next();
        Object geom = feature.getDefaultGeometry();
        if (geom instanceof Polygon) {
          Polygon polygon = (Polygon) geom;
          polygons.add(polygon);
        } else {
          if (geom instanceof MultiPolygon) {
            Polygon polygon = (Polygon) ((MultiPolygon) geom).getGeometryN(0);
            polygons.add(polygon);
          }
        }
      }
      store.dispose();
      Rectangle2D[] result = new Rectangle2D[polygons.size()];
      int index = 0;
      for (Polygon p : polygons) {
        p.apply(this.filter);
        result[index++] = Rectangle2D.fromGeometry(p);
      }
      return result;
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
