package fr.ign.image;

import fr.ign.geometry.Point2D;

public class LocatorBuffedImageWrapper implements Locator {
  OrientedView image;
  int x;
  int y;

  public LocatorBuffedImageWrapper(OrientedView im, int i, int j) {
    this.image = im;
    this.x = i;
    this.y = j;
  }

  @Override
  public double[] getValue() {
    return this.image.getValueAt(this.x, this.y);
  }

  @Override
  public void move(Point2D point2d) {
    this.x += point2d.x();
    this.y += point2d.y();
  }
}
