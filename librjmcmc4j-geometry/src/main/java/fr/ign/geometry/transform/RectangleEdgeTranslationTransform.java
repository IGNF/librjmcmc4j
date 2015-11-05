package fr.ign.geometry.transform;

import fr.ign.rjmcmc.kernel.Transform;

public class RectangleEdgeTranslationTransform implements Transform {
  int edgeNumber;
  double m_rmin;
  double m_rrange;

  public RectangleEdgeTranslationTransform(int n, double minRatio, double maxRatio) {
    this.edgeNumber = n;
    this.m_rmin = minRatio;
    this.m_rrange = maxRatio - minRatio;
  }

  @Override
  public double apply(boolean direct, double[] in, double[] out) {
    double x = in[0];
    double y = in[1];
    double u = in[2];
    double v = in[3];
    double r = in[4];
    double p = m_rmin + m_rrange * in[5];
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
          out[0] = x + dx;
          out[1] = y + dy;
          break;
        case 1:
          out[0] = x - dx;
          out[1] = y - dy;
          break;
      }
      out[2] = u;
      out[3] = v;
      out[4] = p;
      out[5] = (r - m_rmin) / m_rrange;
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
          out[0] = x + dx;
          out[1] = y + dy;
          break;
        case 3:
          out[0] = x - dx;
          out[1] = y - dy;
          break;
      }
      out[2] = pru;
      out[3] = prv;
      out[4] = 1. / p;
      out[5] = (1. - r * m_rmin) / (r * m_rrange);
    }
    // maxima rot90: abs(determinant(jacobian([x,y,-r*v,r*u,p,1/r],[x,y,u,v,r,p]))) = 1;
    return 1;
  }

  @Override
  public int dimension() {
    return 6;
  }
}
