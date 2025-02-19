package fr.ign.geometry.integrated_flux;

import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.io.File;
import java.util.logging.Logger;

import javax.media.jai.PlanarImage;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ign.geometry.Point2D;
import fr.ign.geometry.Rectangle2D;
import fr.ign.geometry.Segment2D;
import fr.ign.image.OrientedPlanarImageWrapper;

public class IntegratedFluxTest {
  /**
   * Logger.
   */
  private static Logger logger = Logger.getLogger(IntegratedFluxTest.class.getName());
  double[][] range;
  CoordinateReferenceSystem[] crs;
  PlanarImage image;
  OrientedPlanarImageWrapper view;

  @Before
  public void setUp() throws Exception {
    // // Get the file
    // File file = new File("src\\test\\resources\\ZTerrain.tif");
    //
    // if (file == null) {
    // return;
    // }
    //
    // // Get the channel
    // // FileChannel fc = file.getChannel();
    // // hint for CRS
    // CoordinateReferenceSystem crs = CRS.decode("EPSG:4326", true);
    // final Hints hint = new Hints();
    // hint.put(Hints.DEFAULT_COORDINATE_REFERENCE_SYSTEM, crs);
    // final AbstractGridFormat format = new GeoTiffFormat();
    ////    logger.info("Start reading ZTerrain_c3.tif"); //$NON-NLS-1$
    // GeoTiffReader reader = (GeoTiffReader) format.getReader(file);
    // // logger.info("reader = " + reader.getClass());
    // // CoordinateReferenceSystem crsTest = reader.getCrs();
    // // logger.info("crs = " + crsTest);
    ////    logger.info("Done reading"); //$NON-NLS-1$
    //
    // // Get the image properties
    // GridCoverage2D coverage;
    // try {
    // coverage = reader.read(null);
    // } catch (IOException ex) {
    // ex.printStackTrace();
    // return;
    // }
    // RenderedImage renderedImage = coverage.getRenderedImage();
    // // System.out.println("RenderedImage of type : " + dataType(renderedImage.getSampleModel())
    // // + " with " + renderedImage.getSampleModel().getNumBands() + " "
    // // + renderedImage.getColorModel().getNumComponents());
    // Envelope env = coverage.getEnvelope();
    // range = new double[2][2];
    // // Range
    // range[0][0] = env.getMinimum(0);
    // range[0][1] = env.getMaximum(0);
    // range[1][0] = env.getMinimum(1);
    // range[1][1] = env.getMaximum(1);
    //
    // this.crs = new CoordinateReferenceSystem[1];
    // this.crs[0] = coverage.getCoordinateReferenceSystem2D();
    //
    // // Return image
    // image = PlanarImage.wrapRenderedImage(renderedImage);
    // // System.out.println("Image of type : " + dataType(image.getSampleModel()) + " with "
    // // + image.getNumBands() + " " + image.getColorModel().getNumComponents());
    // ParameterBlock pbConvert = new ParameterBlock();
    // pbConvert.addSource(image);
    // pbConvert.add(DataBuffer.TYPE_FLOAT);
    // PlanarImage newImage = JAI.create("format", pbConvert);
    // // System.out.println("new image of type : " + dataType(newImage.getSampleModel()) + " with "
    // // + newImage.getNumBands() + " " + newImage.getColorModel().getNumComponents());
    // this.image = newImage;
    view = new OrientedPlanarImageWrapper("src"+File.separator+"test"+File.separator+"resources"+File.separator+"ZTerrain.tif", 1.0f);
  }

  String dataType(SampleModel model) {
    switch (model.getDataType()) {
      case DataBuffer.TYPE_BYTE:
        return "byte";
      case DataBuffer.TYPE_DOUBLE:
        return "double";
      case DataBuffer.TYPE_FLOAT:
        return "float";
      case DataBuffer.TYPE_INT:
        return "int";
      case DataBuffer.TYPE_SHORT:
        return "short";
      case DataBuffer.TYPE_UNDEFINED:
        return "undefined";
      case DataBuffer.TYPE_USHORT:
        return "ushort";
    }
    return "WTF";
  }

  @Test
  public void testComputeOrientedViewSegment() {
    Point2D p1 = new Point2D(106, 15);
    Point2D p2 = new Point2D(106, 20);
    double flux = IntegratedFlux.compute(view, new Segment2D(p1, p2));
    logger.info("Segment flux = " + flux); // 0 ??? NOT TESTED IN C
    Assert.assertEquals(119.079, flux, 0.001);
    flux = IntegratedFlux.compute(view, new Segment2D(p2, p1));
    logger.info("Segment flux = " + flux); // 0 ??? NOT TESTED IN C
    Assert.assertEquals(-119.079, flux, 0.001);
    Point2D p3 = new Point2D(170, 58);
    Point2D p4 = new Point2D(182, 64);
    Point2D p5 = new Point2D(179, 36);
    flux = IntegratedFlux.compute(view, new Segment2D(p3, p4));
    logger.info("Segment flux = " + flux);
    Assert.assertEquals(-237.787, flux, 0.001); // TESTED WITH C
    flux = IntegratedFlux.compute(view, new Segment2D(p4, p3));
    logger.info("Segment flux = " + flux);
    Assert.assertEquals(237.787, flux, 0.001); // TESTED WITH C
    flux = IntegratedFlux.compute(view, new Segment2D(p3, p5));
    logger.info("Segment flux = " + flux);
    Assert.assertEquals(-3.64425, flux, 0.001); // TESTED WITH C
    Rectangle2D rect = new Rectangle2D(197, 56, 4.5, -10, 7);// 138,30
//    logger.info(rect.toGeometry());
    flux = IntegratedFlux.compute(view, rect);
    logger.info("Rectangle flux = " + flux);
  }

  @Test
  public void testComputeOrientedViewRectangle2D() {
    Rectangle2D r = new Rectangle2D(95, 18, 0, 1, 11);
    double flux = IntegratedFlux.compute(view, r);// 88.6233 without clip
    logger.info("Rectangle2D flux = " + flux);
    Assert.assertEquals(88.6233, flux, 0.001);

    r = new Rectangle2D(95, 18, 0, 1, 1);
    flux = IntegratedFlux.compute(view, r);// 3.39207 without clip
    logger.info("Rectangle2D flux = " + flux);
    Assert.assertEquals(3.39207, flux, 0.001);
  }

  @Test
  public void testComputeOrientedViewPrimitive() {
    double flux = IntegratedFlux.compute(view, new Double(5));
    logger.info("Double flux = " + flux);
    Assert.assertEquals(0, flux, 0);
  }

}
