package fr.ign.geometry.transform;

import java.util.Vector;

import org.apache.log4j.Logger;

import fr.ign.rjmcmc.kernel.Transform;

public class RectangleSplitMergeTransform implements Transform {
  /**
   * Logger.
   */
  static Logger LOGGER = Logger.getLogger(RectangleSplitMergeTransform.class.getName());

  // @Override
  // public int dimension() {
  // return 10;
  // }

  double m_d;
  double m_d4;

  public RectangleSplitMergeTransform() {
    this(50);
  }

  public RectangleSplitMergeTransform(double d) {
    this.m_d = d;
    this.m_d4 = d * d * d * d;
  }

  @Override
  public double apply(boolean direct, Vector<Double> val0, Vector<Double> var0,
      Vector<Double> val1, Vector<Double> var1) {
    if (direct) {
      double x = val0.get(0);
      double y = val0.get(1);
      double u = val0.get(2);
      double v = val0.get(3);
      double r = val0.get(4);
      double p = var0.get(0);
      double q = var0.get(1);
      double s = var0.get(2);
      double t = var0.get(3);
      double g = var0.get(4);

      // split rectangle at fraction f
      // Vector_2 m(-r*n.y(),r*n.x());
      // case 0 : Point_2 p(c-f*m); return std::make_pair(Rectangle_2(p+m,
      // n,f*r),Rectangle_2(p,n,(1-f)*r)); }
      // perturbate the center and normal of the second rectangle

      double f = r * g;
      double dx = m_d * (p - 0.5);
      double dy = m_d * (q - 0.5);
      double du = m_d * (s - 0.5);
      double dv = m_d * (t - 0.5);

      val1.set(0, x + (r - f) * v);
      val1.set(1, y - (r - f) * u);
      val1.set(2, u);
      val1.set(3, v);
      val1.set(4, f);

      val1.set(5, x - f * v + dx);
      val1.set(6, y + f * u + dy);
      val1.set(7, u + du);
      val1.set(8, v + dv);
      val1.set(9, r - f);
      // maxima : factor(determinant(jacobian([x+(r-f)*v, y-(r-f)*u, u, v, f, x-f*v+dx, y+f*u+dy,
      // u+du, v+dv, r-f ] ,[x,y,u,v,r,du,dv,dx,dy,f] )));
      // -1
      // Rectangle2D rin = new Rectangle2D(x, y, u, v, r);
      // Rectangle2D rout1 = new Rectangle2D(out[0], out[1], out[2], out[3], out[4]);
      // Rectangle2D rout2 = new Rectangle2D(out[5], out[6], out[7], out[8], out[9]);
      // System.out.println("in=" + rin.toGeometry());
      // System.out.println("out1=" + rout1.toGeometry());
      // System.out.println("out2=" + rout2.toGeometry());
      return r * m_d4;
    }

    double x0 = val0.get(0); // x+(r-f)*v
    double y0 = val0.get(1); // y-(r-f)*u
    double u = val0.get(2);
    double v = val0.get(3);
    double f = val0.get(4);
    double x1 = val0.get(5); // x-f*v+dx
    double y1 = val0.get(6); // y+f*u+dy
    double u1 = val0.get(7); // u+du
    double v1 = val0.get(8); // v+dv
    double q = val0.get(9); // r-f

    double x = x0 - q * v;
    double y = y0 + q * u;
    double r = f + q;
    double dx = x1 - x + f * v;
    double dy = y1 - y - f * u;
    double du = u1 - u;
    double dv = v1 - v;

    val1.set(0, x);
    val1.set(1, y);
    val1.set(2, u);
    val1.set(3, v);
    val1.set(4, r);
    var1.set(0, 0.5 + dx / m_d);
    var1.set(1, 0.5 + dy / m_d);
    var1.set(2, 0.5 + du / m_d);
    var1.set(3, 0.5 + dv / m_d);
    var1.set(4, f / r);

    return 1. / (m_d4 * r);
  }

  @Override
  public int dimension(int n0, int n1) {
    return 10;
  }

  @Override
  public double getAbsJacobian(boolean d) {
    return 1;
  }
}
