package fr.ign.geotools;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.referencing.factory.DatumAliases;
import org.geotools.referencing.factory.ReferencingFactoryContainer;
import org.geotools.referencing.factory.ReferencingObjectFactory;
import org.geotools.referencing.factory.epsg.AnsiDialectEpsgFactory;
import org.geotools.referencing.factory.epsg.CartesianAuthorityFactory;
import org.geotools.referencing.factory.epsg.LongitudeFirstFactory;
import org.geotools.referencing.factory.epsg.hsql.ThreadedHsqlEpsgFactory;
import org.geotools.referencing.factory.gridshift.ClasspathGridShiftLocator;
import org.geotools.referencing.factory.gridshift.GridShiftLocator;
import org.geotools.referencing.operation.AuthorityBackedFactory;
import org.geotools.referencing.operation.BufferedCoordinateOperationFactory;
import org.geotools.referencing.operation.DefaultCoordinateOperationFactory;
import org.geotools.referencing.operation.DefaultMathTransformFactory;
import org.geotools.util.factory.FactoryIteratorProvider;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.cs.CSAuthorityFactory;
import org.opengis.referencing.cs.CSFactory;
import org.opengis.referencing.datum.DatumAuthorityFactory;
import org.opengis.referencing.datum.DatumFactory;
import org.opengis.referencing.operation.CoordinateOperationAuthorityFactory;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.MathTransformFactory;

public class GeoToolsFactoryIteratorProvider implements FactoryIteratorProvider {

  @Override
  public <T> Iterator<T> iterator(Class<T> category) {
    List<T> list = getList(category);
    if (list == null) {
      System.out.println("NULL FOR CATEGORY " + category);
      list = Collections.emptyList();
    }
    return list.iterator();
  }

  @SuppressWarnings("unchecked")
  public <T> List<T> getList(Class<T> category) {
    if (category.equals(CoordinateOperationAuthorityFactory.class)) {
      return (List<T>) Arrays.asList(AnsiDialectEpsgFactory.class, ThreadedHsqlEpsgFactory.class, LongitudeFirstFactory.class);
    }
    if (category.equals(CoordinateOperationFactory.class)) {
      return (List<T>) Arrays.asList(BufferedCoordinateOperationFactory.class,
          AuthorityBackedFactory.class, DefaultCoordinateOperationFactory.class);
    }
    if (category.equals(CRSAuthorityFactory.class)) {
      return (List<T>) Arrays.asList(AnsiDialectEpsgFactory.class, ThreadedHsqlEpsgFactory.class, LongitudeFirstFactory.class,
          CartesianAuthorityFactory.class);
    }
    if (category.equals(CRSFactory.class)) {
      return (List<T>) Arrays.asList(ReferencingObjectFactory.class);
    }
    if (category.equals(CSAuthorityFactory.class)) {
      return (List<T>) Arrays.asList(AnsiDialectEpsgFactory.class, ThreadedHsqlEpsgFactory.class, LongitudeFirstFactory.class);
    }
    if (category.equals(CSFactory.class)) {
      return (List<T>) Arrays.asList(ReferencingObjectFactory.class);
    }
    if (category.equals(DatumAuthorityFactory.class)) {
      return (List<T>) Arrays.asList(AnsiDialectEpsgFactory.class, ThreadedHsqlEpsgFactory.class, LongitudeFirstFactory.class);
    }
    if (category.equals(DatumFactory.class)) {
      return (List<T>) Arrays.asList(DatumAliases.class, ReferencingObjectFactory.class);
    }
    if (category.equals(GridShiftLocator.class)) {
      return (List<T>) Arrays.asList(ClasspathGridShiftLocator.class);
    }
    if (category.equals(MathTransformFactory.class)) {
      return (List<T>) Arrays.asList(DefaultMathTransformFactory.class);
    }
    if (category.equals(ReferencingFactoryContainer.class)) {
      return (List<T>) Arrays.asList(ReferencingFactoryContainer.class);
    }
    //
    if (category.equals(GridCoverageFactory.class)) {
      return (List<T>) Arrays.asList(GridCoverageFactory.class);
    }
    return null;
  }
}
