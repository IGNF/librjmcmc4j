package fr.ign.image;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

import it.geosolutions.imageioimpl.plugins.tiff.TIFFImageReaderSpi;

public class OrientedPlanarImageWrapperImageIO implements OrientedView {

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

  public OrientedPlanarImageWrapperImageIO(String imFile, float sigma) throws IOException {
    this(new File(imFile), sigma);
  }

  private static boolean osgi = true;
  public OrientedPlanarImageWrapperImageIO(File file, float sigma) throws IOException {
    if (osgi) {
      IIORegistry.getDefaultInstance().registerServiceProvider(new TIFFImageReaderSpi());
    }
    BufferedImage bufferedImage = ImageIO.read(file);

    this.image = PlanarImage.wrapRenderedImage(bufferedImage);
    this.width = this.image.getWidth();
    this.height = this.image.getHeight();
    ParameterBlock pbConvert = new ParameterBlock();
    pbConvert.addSource(image);
    pbConvert.add(DataBuffer.TYPE_FLOAT);
    this.image = JAI.create("format", pbConvert);

    GradientFunctor.gradientFunctor(this.image, sigma, this);
  }

  public OrientedPlanarImageWrapperImageIO(PlanarImage im, float sigma) {
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
