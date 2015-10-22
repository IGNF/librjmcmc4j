package fr.ign.geometry.transform;

import fr.ign.rjmcmc.kernel.Transform;

public class RectangleSplitMergeTransform implements Transform {
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
  public double apply(boolean direct, double[] in, double[] out) {
    if (direct) {
      double x = in[0];
      double y = in[1];
      double u = in[2];
      double v = in[3];
      double r = in[4];
      double p = in[5];
      double q = in[6];
      double s = in[7];
      double t = in[8];
      double g = in[9];

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

      out[0] = x + (r - f) * v;
      out[1] = y - (r - f) * u;
      out[2] = u;
      out[3] = v;
      out[4] = f;

      out[5] = x - f * v + dx;
      out[6] = y + f * u + dy;
      out[7] = u + du;
      out[8] = v + dv;
      out[9] = r - f;
      // maxima : factor(determinant(jacobian([x+(r-f)*v, y-(r-f)*u, u, v, f,
      // x-f*v+dx, y+f*u+dy,
      // u+du, v+dv, r-f ] ,[x,y,u,v,r,du,dv,dx,dy,f] )));
      // -1
      // Rectangle2D rin = new Rectangle2D(x, y, u, v, r);
      // Rectangle2D rout1 = new Rectangle2D(out[0], out[1], out[2], out[3],
      // out[4]);
      // Rectangle2D rout2 = new Rectangle2D(out[5], out[6], out[7], out[8],
      // out[9]);
      // System.out.println("in=" + rin.toGeometry());
      // System.out.println("out1=" + rout1.toGeometry());
      // System.out.println("out2=" + rout2.toGeometry());
      return r * m_d4;
    }

    double x0 = in[0]; // x+(r-f)*v
    double y0 = in[1]; // y-(r-f)*u
    double u = in[2];
    double v = in[3];
    double f = in[4];
    double x1 = in[5]; // x-f*v+dx
    double y1 = in[6]; // y+f*u+dy
    double u1 = in[7]; // u+du
    double v1 = in[8]; // v+dv
    double q = in[9]; // r-f

    double x = x0 - q * v;
    double y = y0 + q * u;
    double r = f + q;
    double dx = x1 - x + f * v;
    double dy = y1 - y - f * u;
    double du = u1 - u;
    double dv = v1 - v;

    out[0] = x;
    out[1] = y;
    out[2] = u;
    out[3] = v;
    out[4] = r;
    out[5] = 0.5 + dx / m_d;
    out[6] = 0.5 + dy / m_d;
    out[7] = 0.5 + du / m_d;
    out[8] = 0.5 + dv / m_d;
    out[9] = f / r;

    return 1. / (m_d4 * r);
  }

  @Override
  public int dimension() {
    return 10;
  }

  // @Override
  public double getAbsJacobian(boolean d) {
    return 1;
  }
}
