package fr.ign.image;

import java.awt.image.BufferedImage;

import javax.media.jai.PlanarImage;

public class OrientedBufferedImageWrapper implements OrientedView {

  BufferedImage image;
  int width;
  int height;
  double[] pixels;

  public OrientedBufferedImageWrapper(BufferedImage im) {
    this.image = im;
    this.width = this.image.getWidth();
    this.height = this.image.getHeight();
    this.pixels = new double[width * height];
    this.image.getRaster().getPixel(0, 0, pixels);
  }

  @Override
  public double[] getValueAt(int x, int y) {
    int offset = y * width + x;
    return new double[] { pixels[offset] };
    // return this.gradient.getData().getPixel(x, y, new double[2]);
    // return new double[] { this.channel0.getData().getPixel(x, y, new double[1])[0],
    // this.channel1.getData().getPixel(x, y, new double[1])[0] };
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
    return 1;
  }

  @Override
  public void setGradient(PlanarImage result) {
    //FIXME REMOVE FROM INTERFACE?
  }
}
