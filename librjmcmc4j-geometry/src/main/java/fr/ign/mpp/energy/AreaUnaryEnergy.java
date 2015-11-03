package fr.ign.mpp.energy;

import fr.ign.geometry.Primitive;
import fr.ign.rjmcmc.energy.UnaryEnergy;

public class AreaUnaryEnergy <P extends Primitive> implements UnaryEnergy<P> {
  @Override
  public double getValue(P t) {
    return t.getArea();    
  }

}
