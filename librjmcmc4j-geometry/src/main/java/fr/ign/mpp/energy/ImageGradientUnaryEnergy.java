package fr.ign.mpp.energy;

import fr.ign.geometry.integrated_flux.IntegratedFlux;
import fr.ign.image.OrientedView;
import fr.ign.rjmcmc.energy.UnaryEnergy;

public class ImageGradientUnaryEnergy<T> implements UnaryEnergy<T> {
  private OrientedView image;
  public ImageGradientUnaryEnergy(final OrientedView im) {
    this.image = im;
  }

  @Override
  public double getValue(T t) {
    return IntegratedFlux.compute(this.image, t);
  }

}
