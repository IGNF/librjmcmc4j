package fr.ign.geometry.transform;

import java.util.Vector;

import org.apache.log4j.Logger;

import fr.ign.rjmcmc.kernel.Transform;

public class RectangleEdgeTranslationTransform implements Transform {
  /**
   * Logger.
   */
  static Logger LOGGER = Logger.getLogger(RectangleEdgeTranslationTransform.class.getName());

  // @Override
  // public int dimension() {
  // return 6;
  // }

  int edgeNumber;
  double m_rmin;
  double m_rrange;

  public RectangleEdgeTranslationTransform(int n, double minRatio, double maxRatio) {
    this.edgeNumber = n;
    this.m_rmin = minRatio;
    this.m_rrange = maxRatio - minRatio;
  }

  @Override
  public double apply(boolean direct, Vector<Double> val0, Vector<Double> var0,
      Vector<Double> val1, Vector<Double> var1) {
    double x = val0.get(0);
    double y = val0.get(1);
    double u = val0.get(2);
    double v = val0.get(3);
    double r = val0.get(4);
    double p = m_rmin + m_rrange * var0.get(0);
    if (this.edgeNumber < 2) {
      // maxima
      // abs(determinant(jacobian([x-v*(r-p)*e,y+u*(r-p)*e,u,v,p,r],[x,y,u,v,r,p]))) = 1;
      // ratsimp(sublis([x=x-v*(r-p)*e,y=y+u*(r-p)*e,u=u,v=v,p=r,r=p],[x-v*(r-p)*e,y+u*(r-p)*e,u,v,p,r]))
      // = [x, y, u, v, r, p];
      double d = r - p;
      double dx = -v * d;
      double dy = u * d;
      switch (this.edgeNumber) {
        case 0:
          val1.set(0, x + dx);
          val1.set(1, y + dy);
          break;
        case 1:
          val1.set(0, x - dx);
          val1.set(1, y - dy);
          break;
      }
      val1.set(2, u);
      val1.set(3, v);
      val1.set(4, p);
      var1.set(0, (r - m_rmin) / m_rrange);
    } else {
      // maxima
      // abs(determinant(jacobian([x-(u-p*r*u)*e,y-(v-p*r*v)*e,p*r*u,p*r*v,1/p,1/r],[x,y,u,v,r,p])))
      // = 1;
      // ratsimp(sublis([x=x-(u-p*r*u)*e,y=y-(v-p*r*v)*e,u=p*r*u,v=p*r*v,r=1/p,p=1/r],[x-(u-p*r*u)*e,y-(v-p*r*v)*e,p*r*u,p*r*v,1/p,1/r]))
      // = [x, y, u, v, r, p];
      double pr = p * r;
      double pru = pr * u;
      double prv = pr * v;
      double dx = u - pru;
      double dy = v - prv;
      switch (this.edgeNumber) {
        case 2:
          val1.set(0, x + dx);
          val1.set(1, y + dy);
          break;
        case 3:
          val1.set(0, x - dx);
          val1.set(1, y - dy);
          break;
      }
      val1.set(2, pru);
      val1.set(3, prv);
      val1.set(4, 1. / p);
      var1.set(0, (1. - r * m_rmin) / (r * m_rrange));
    }
    // maxima rot90: abs(determinant(jacobian([x,y,-r*v,r*u,p,1/r],[x,y,u,v,r,p]))) = 1;
    return 1;
  }

  // @Override
  // public double inverse(double[] in, double[] out) {
  // return this.apply(in, out);
  // }

  @Override
  public double getAbsJacobian(boolean d) {
    return 1;
  }

  // @Override
  // public double getInverseAbsJacobian(double[] d) {
  // return 1;
  // }

  @Override
  public int dimension(int n0, int n1) {
    return 6;
  }
}
