package fr.ign.image;

import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Locale;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.util.factory.GeoTools;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import fr.ign.geotools.GeoToolsFactoryIteratorProvider;

public class OrientedPlanarImageWrapper implements OrientedView {

  PlanarImage image;
  PlanarImage gradient;
  PlanarImage channel0;
  PlanarImage channel1;
  double[] pixels;
  int width;
  int height;

  public void setChannel0(PlanarImage im) {
    this.channel0 = im;
  }

  public void setChannel1(PlanarImage im) {
    this.channel1 = im;
  }

  public void setGradient(PlanarImage im) {
    this.gradient = im;
    Raster raster = this.gradient.getData();
    pixels = new double[2 * width * height];
    raster.getPixels(0, 0, width, height, pixels);
  }

  double[][] range;
  CoordinateReferenceSystem[] crs;

  public OrientedPlanarImageWrapper(String imFile, float sigma) {
    this(new File(imFile), sigma);
  }

  public OrientedPlanarImageWrapper(File file, float sigma) {

    // try {
    // System.out.println("once");
    // Writer writer = new OutputStreamWriter(System.out);
    // ReferencingFactoryFinder.listProviders(writer, Locale.ENGLISH);
    // writer.flush();
    // } catch (IOException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    //
    // try {
    // System.out.println("4326 = " +
    // AllAuthoritiesFactory.DEFAULT.createCoordinateReferenceSystem("EPSG:4326"));
    // } catch (FactoryException e1) {
    // // TODO Auto-generated catch block
    // e1.printStackTrace();
    // }
    // ReferencingFactoryFinder.reset();
    // // CRSAuthority
    // ServiceLoader.load(CRSAuthorityFactory.class,
    // ThreadedHsqlEpsgFactory.class.getClassLoader());
    // /*
    // * org.geotools.referencing.factory.epsg.DefaultFactory
    // * org.geotools.referencing.factory.epsg.FactoryUsingWKT
    // * org.geotools.referencing.factory.epsg.LongitudeFirstFactory
    // * org.geotools.referencing.factory.epsg.CartesianAuthorityFactory
    // * org.geotools.referencing.factory.wms.AutoCRSFactory
    // * org.geotools.referencing.factory.wms.WebCRSFactory
    // */
    //
    // //CRSFactory
    // ServiceLoader.load(CRSFactory.class, ReferencingObjectFactory.class.getClassLoader());
    //
    // // CSAuthority
    // ServiceLoader.load(CSAuthorityFactory.class, ThreadedHsqlEpsgFactory.class.getClassLoader());
    // /*
    // * org.geotools.referencing.factory.epsg.DefaultFactory
    // * org.geotools.referencing.factory.epsg.LongitudeFirstFactory
    // */
    //
    // // CS
    // ServiceLoader.load(CSFactory.class, ReferencingObjectFactory.class.getClassLoader());
    //
    // // DatumAuthority
    // ServiceLoader.load(DatumAuthorityFactory.class,
    // ThreadedHsqlEpsgFactory.class.getClassLoader());
    // /*
    // * org.geotools.referencing.factory.epsg.DefaultFactory
    // * org.geotools.referencing.factory.epsg.LongitudeFirstFactory
    // */
    //
    // // Datum
    // ServiceLoader.load(DatumFactory.class, ReferencingObjectFactory.class.getClassLoader());
    // ServiceLoader.load(DatumFactory.class, DatumAliases.class.getClassLoader());
    //
    // // CoordinateOperationAthoirrer
    // ServiceLoader.load(CoordinateOperationAuthorityFactory.class,
    // ThreadedHsqlEpsgFactory.class.getClassLoader());
    // /*
    // * org.geotools.referencing.factory.epsg.CoordinateOperationFactoryUsingWKT
    // * org.geotools.referencing.factory.epsg.DefaultFactory
    // * org.geotools.referencing.factory.epsg.LongitudeFirstFactory
    // */
    //
    // // CoordinateOperation
    // ServiceLoader.load(CoordinateOperationFactory.class,
    // DefaultCoordinateOperationFactory.class.getClassLoader());
    // ServiceLoader.load(CoordinateOperationFactory.class,
    // AuthorityBackedFactory.class.getClassLoader());
    // ServiceLoader.load(CoordinateOperationFactory.class,
    // BufferedCoordinateOperationFactory.class.getClassLoader());
    //
    // //MathTransform
    // ServiceLoader.load(MathTransformFactory.class,
    // DefaultMathTransformFactory.class.getClassLoader());
    //
    // //CartesianAuthority
    // ServiceLoader.load(CartesianAuthorityFactory.class,
    // CartesianAuthorityFactory.class.getClassLoader());
    //
    //
    // ServiceLoader.load(GridCoverageFactory.class, GridCoverageFactory.class.getClassLoader());
    //
    // // ClasspathGridShiftLocator
    // /*
    // * org.geotools.referencing.operation.transform.LogarithmicTransform1D$Provider
    // * org.geotools.referencing.operation.transform.ExponentialTransform1D$Provider
    // * org.geotools.referencing.operation.transform.ProjectiveTransform$ProviderAffine
    // * org.geotools.referencing.operation.transform.ProjectiveTransform$ProviderLongitudeRotation
    // * org.geotools.referencing.operation.transform.GeocentricTranslation$Provider
    // * org.geotools.referencing.operation.transform.GeocentricTranslation$ProviderSevenParam
    // * org.geotools.referencing.operation.transform.GeocentricTranslation$ProviderFrameRotation
    // * org.geotools.referencing.operation.transform.GeocentricTransform$Provider
    // * org.geotools.referencing.operation.transform.GeocentricTransform$ProviderInverse
    // * org.geotools.referencing.operation.transform.MolodenskiTransform$Provider
    // * org.geotools.referencing.operation.transform.MolodenskiTransform$ProviderAbridged
    // * org.geotools.referencing.operation.transform.NADCONTransform$Provider
    // * org.geotools.referencing.operation.transform.NTv2Transform$Provider
    // * org.geotools.referencing.operation.transform.SimilarityTransformProvider
    // * org.geotools.referencing.operation.transform.WarpTransform2D$Provider
    // * org.geotools.referencing.operation.projection.EquidistantCylindrical$Provider
    // * org.geotools.referencing.operation.projection.EquidistantCylindrical$SphericalProvider
    // * org.geotools.referencing.operation.projection.PlateCarree$Provider
    // * org.geotools.referencing.operation.projection.Mercator1SP$Provider
    // * org.geotools.referencing.operation.projection.Mercator2SP$Provider
    // * org.geotools.referencing.operation.projection.MercatorPseudoProvider
    // * org.geotools.referencing.operation.projection.TransverseMercator$Provider
    // * org.geotools.referencing.operation.projection.TransverseMercator$Provider_SouthOrientated
    // * org.geotools.referencing.operation.projection.ObliqueMercator$Provider
    // * org.geotools.referencing.operation.projection.ObliqueMercator$Provider_TwoPoint
    // * org.geotools.referencing.operation.projection.HotineObliqueMercator$Provider
    // * org.geotools.referencing.operation.projection.HotineObliqueMercator$Provider_TwoPoint
    // * org.geotools.referencing.operation.projection.AlbersEqualArea$Provider
    // * org.geotools.referencing.operation.projection.LambertConformal1SP$Provider
    // * org.geotools.referencing.operation.projection.LambertConformal2SP$Provider
    // * org.geotools.referencing.operation.projection.LambertConformalBelgium$Provider
    // * org.geotools.referencing.operation.projection.LambertAzimuthalEqualArea$Provider
    // * org.geotools.referencing.operation.projection.Orthographic$Provider
    // * org.geotools.referencing.operation.projection.Stereographic$Provider
    // * org.geotools.referencing.operation.projection.ObliqueStereographic$Provider
    // * org.geotools.referencing.operation.projection.PolarStereographic$ProviderA
    // * org.geotools.referencing.operation.projection.PolarStereographic$ProviderB
    // * org.geotools.referencing.operation.projection.PolarStereographic$ProviderNorth
    // * org.geotools.referencing.operation.projection.PolarStereographic$ProviderSouth
    // * org.geotools.referencing.operation.projection.NewZealandMapGrid$Provider
    // * org.geotools.referencing.operation.projection.Krovak$Provider
    // * org.geotools.referencing.operation.projection.CassiniSoldner$Provider
    // * org.geotools.referencing.operation.projection.EquidistantConic$Provider
    // * org.geotools.referencing.operation.projection.Polyconic$Provider
    // * org.geotools.referencing.operation.projection.Robinson$Provider
    // * org.geotools.referencing.operation.projection.WinkelTripel$WinkelProvider
    // * org.geotools.referencing.operation.projection.WinkelTripel$AitoffProvider
    // * org.geotools.referencing.operation.projection.EckertIV$Provider
    // * org.geotools.referencing.operation.projection.Mollweide$MollweideProvider
    // * org.geotools.referencing.operation.projection.Mollweide$WagnerIVProvider
    // * org.geotools.referencing.operation.projection.WorldVanDerGrintenI$Provider
    // */
    //
    //
    // try {
    // System.out.println("twice");
    // Writer writer = new OutputStreamWriter(System.out);
    // ReferencingFactoryFinder.listProviders(writer, Locale.ENGLISH);
    // writer.flush();
    // } catch (IOException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // try {
    // System.out.println("4326 = " +
    // AllAuthoritiesFactory.DEFAULT.createCoordinateReferenceSystem("EPSG:4326"));
    // } catch (FactoryException e1) {
    // // TODO Auto-generated catch block
    // e1.printStackTrace();
    // }

    CommonFactoryFinder.reset();
    
    GeoTools.addFactoryIteratorProvider(new GeoToolsFactoryIteratorProvider());
    try {
      Writer writer = new OutputStreamWriter(System.out);
      ReferencingFactoryFinder.listProviders(writer, Locale.ENGLISH);
      writer.flush();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    final AbstractGridFormat format = new GeoTiffFormat();
    GeoTiffReader reader = (GeoTiffReader) format.getReader(file);
    // Get the image properties
    GridCoverage2D coverage;
    try {
      coverage = reader.read(null);
    } catch (IOException ex) {
      ex.printStackTrace();
      return;
    }
    RenderedImage renderedImage = coverage.getRenderedImage();
    Envelope env = coverage.getEnvelope();
    range = new double[2][2];
    // Range
    range[0][0] = env.getMinimum(0);
    range[0][1] = env.getMaximum(0);
    range[1][0] = env.getMinimum(1);
    range[1][1] = env.getMaximum(1);

    this.crs = new CoordinateReferenceSystem[1];
    this.crs[0] = coverage.getCoordinateReferenceSystem2D();
    // Return image
    this.image = PlanarImage.wrapRenderedImage(renderedImage);
    this.width = this.image.getWidth();
    this.height = this.image.getHeight();
    ParameterBlock pbConvert = new ParameterBlock();
    pbConvert.addSource(image);
    pbConvert.add(DataBuffer.TYPE_FLOAT);
    this.image = JAI.create("format", pbConvert);

    GradientFunctor.gradientFunctor(this.image, sigma, this);
  }

  public OrientedPlanarImageWrapper(PlanarImage im, float sigma) {
    this.image = im;
    GradientFunctor.gradientFunctor(im, sigma, this);
  }

  @Override
  public double[] getValueAt(int x, int y) {
    int offset = y * width * 2 + x * 2;
    return new double[] { pixels[offset], pixels[offset + 1] };
  }

  @Override
  public int height() {
    return this.height;
  }

  @Override
  public int width() {
    return this.width;
  }

  @Override
  public int x0() {
    return this.image.getMinX();
  }

  @Override
  public int y0() {
    return this.image.getMinY();
  }

  @Override
  public Locator xy_at(int i, int j) {
    return new LocatorBuffedImageWrapper(this, i, j);
  }

  @Override
  public int numberOfChannels() {
    return 2;
  }
}
