package fr.ign.image;

import fr.ign.geometry.Point2D;

public interface Locator {

  double[] getValue();

  void move(Point2D point2d);

}
