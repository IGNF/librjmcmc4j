package fr.ign.image;

import javax.media.jai.PlanarImage;

public interface OrientedView {

  int x0();

  int y0();

  int width();

  int height();

  Locator xy_at(int i, int j);

  double[] getValueAt(int x, int y);

  int numberOfChannels();

  void setGradient(PlanarImage result);
}
