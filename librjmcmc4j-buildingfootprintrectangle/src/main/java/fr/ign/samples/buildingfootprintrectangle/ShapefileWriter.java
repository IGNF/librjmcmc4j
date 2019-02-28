package fr.ign.samples.buildingfootprintrectangle;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.geotools.data.DataStore;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FileDataStoreFactorySpi;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.locationtech.jts.geom.GeometryFilter;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import fr.ign.geometry.Rectangle2D;
import fr.ign.mpp.configuration.GraphConfiguration;
import fr.ign.mpp.configuration.GraphVertex;

public class ShapefileWriter {
  GeometryFilter filter;

  public ShapefileWriter(GeometryFilter geometryFilter) {
    this.filter = geometryFilter;
  }

  public void writeShapefile(File aFile, GraphConfiguration<Rectangle2D> config) {
    try {
      FileDataStoreFactorySpi factory = new ShapefileDataStoreFactory();
      DataStore dataStore = factory.createDataStore(aFile.toURI().toURL());
      String specs = "geom:Polygon,energy:double"; //$NON-NLS-1$
      String featureTypeName = "Object";
      SimpleFeatureType featureType = DataUtilities.createType(featureTypeName, specs);
      dataStore.createSchema(featureType);
      Transaction transaction = new DefaultTransaction("create");
      String typeName = dataStore.getTypeNames()[0];
      SimpleFeatureSource featureSource = dataStore.getFeatureSource(typeName);
      SimpleFeatureType type = featureSource.getSchema();
      SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
      ListFeatureCollection collection = new ListFeatureCollection(featureType);
      featureStore.setTransaction(transaction);
      int i = 1;
      for (GraphVertex<Rectangle2D> v : config.getGraph().vertexSet()) {
        Object[] values = new Object[2];
        Polygon polygon = (Polygon) v.getValue().toGeometry().clone();
        if (this.filter != null) {
          polygon.apply(this.filter);
        }
        values[0] = polygon;
        values[1] = v.getEnergy();
        SimpleFeature simpleFeature = SimpleFeatureBuilder.build(type, values, String.valueOf(i++));
        collection.add(simpleFeature);
      }
      try {
        featureStore.addFeatures(collection);
        transaction.commit();
      } catch (Exception problem) {
        problem.printStackTrace();
        transaction.rollback();
      } finally {
        transaction.close();
        dataStore.dispose();
      }
      // writer.addFeatures(collection);
      // transaction.commit();
      // transaction.close();
      // store.dispose();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (SchemaException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
