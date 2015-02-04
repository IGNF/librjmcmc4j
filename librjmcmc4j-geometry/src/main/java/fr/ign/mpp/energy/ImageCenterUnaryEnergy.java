package fr.ign.mpp.energy;

import fr.ign.geometry.CenteredPrimitive;
import fr.ign.geometry.Primitive;
import fr.ign.image.OrientedView;
import fr.ign.rjmcmc.energy.UnaryEnergy;

public class ImageCenterUnaryEnergy implements UnaryEnergy<Primitive> {
  private OrientedView image;
  private double out;

  public ImageCenterUnaryEnergy(final OrientedView im) {
    this(im, 0);
  }

  public ImageCenterUnaryEnergy(final OrientedView im, double out) {
    this.image = im;
    this.out = out;
  }

  @Override
  public double getValue(Primitive t) {
    if (!(t instanceof CenteredPrimitive)) {
      return this.out;
    }
    CenteredPrimitive centered = (CenteredPrimitive) t;
    int x0 = this.image.x0();
    int y0 = this.image.y0();
    int x = (int) centered.center().x()-x0;
    int y = (int) centered.center().y()-y0;
    if(x<0 || y<0 || x>= this.image.width() || y>= this.image.height()) return this.out;
    return this.image.getValueAt(x,y)[0];    
  }

}
