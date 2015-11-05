package fr.ign.simulatedannealing.visitor;

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
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFilter;

import fr.ign.mpp.configuration.ListConfiguration;
import fr.ign.rjmcmc.configuration.Modification;
import fr.ign.rjmcmc.kernel.SimpleObject;
import fr.ign.rjmcmc.sampler.Sampler;
import fr.ign.simulatedannealing.temperature.Temperature;

public class ShapefileVisitor<T extends SimpleObject, C extends ListConfiguration<T, C, M>, M extends Modification<C, M>> implements Visitor<C, M> {
  private int save;
  private int iter;
  private String fileName;
  private GeometryFilter filter;
  private String specs;

  public ShapefileVisitor(String fileName, String specs) {
    this(fileName, specs, null);
  }

  public ShapefileVisitor(String fileName, String specs, GeometryFilter filter) {
    this.fileName = fileName;
    this.specs = specs;
    this.filter = filter;
  }

  @Override
  public void init(int dump, int s) {
    this.iter = 0;
    this.save = s;
  }

  @Override
  public void begin(C config, Sampler<C, M> sampler, Temperature t) {
  }

  @Override
  public void end(C config, Sampler<C, M> sampler, Temperature t) {
    this.writeShapefile(fileName + "_" + String.format(formatInt, iter + 1).trim() + ".shp", config);
  }

  String formatInt = "%01$-10d";

  @Override
  public void visit(C config, Sampler<C, M> sampler, Temperature t) {
    ++iter;
    if ((save > 0) && (iter % save == 0)) {
      this.writeShapefile(fileName + "_" + String.format(formatInt, iter).trim() + ".shp", config);
    }
  }

  private void writeShapefile(String aFileName, C config) {
    try {
      FileDataStoreFactorySpi factory = new ShapefileDataStoreFactory();
      DataStore dataStore = factory.createDataStore(new File(aFileName).toURI().toURL());
      // String specs = config.getSpecs();
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
      for (T v : config) {
        Object[] values = v.getArray();
        Geometry geom = (Geometry) ((Geometry) values[0]).clone();
        if (this.filter != null) {
          geom.apply(this.filter);
        }
        values[0] = geom;
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
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (SchemaException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
