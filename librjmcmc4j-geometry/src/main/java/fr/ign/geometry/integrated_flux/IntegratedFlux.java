package fr.ign.geometry.integrated_flux;

import fr.ign.geometry.IsoRectangle2D;
import fr.ign.geometry.Point2D;
import fr.ign.geometry.Rectangle2D;
import fr.ign.geometry.Segment2D;
import fr.ign.geometry.Segment2DIterator;
import fr.ign.geometry.Vector2D;
import fr.ign.image.Locator;
import fr.ign.image.OrientedView;

public class IntegratedFlux {
  public static double compute(OrientedView v, Segment2D s0) {
    int x0 = v.x0();
    int y0 = v.y0();
    int x1 = x0 + v.width();
    int y1 = y0 + v.height();
    Segment2D s = new Segment2D(s0);
    IsoRectangle2D bbox = new IsoRectangle2D(x0, y0, x1, y1);
    s = bbox.clip(s);
    if (s == null) {
      return 0;
    }
    Segment2DIterator it = new Segment2DIterator(s);
    Locator loc_grad = v.xy_at((it.x() - x0), (it.y() - y0));
    Point2D[] movement = { new Point2D(it.step(0), 0), new Point2D(0, it.step(1)) };
    int nbChannels = v.numberOfChannels();
    float[] gradient_sum;
    if (nbChannels == 2) {
      gradient_sum = new float[] { 0.f, 0.f };
    } else {
      gradient_sum = new float[] { 0.0f };
    }
    for (; !it.end(); it.next()) {
      if (it.x() >= x0 && it.x() < x1 && it.y() >= y0 && it.y() < y1) {
        double length = it.length();
        double[] grad = loc_grad.getValue();
        gradient_sum[0] += length * grad[0];
        if (nbChannels == 2) {
          gradient_sum[1] += length * grad[1];
        }
      }
      loc_grad.move(movement[it.axis()]);
    }
    if (nbChannels == 1) {
      return gradient_sum[0];
    }
    Vector2D arete = s.getEnd().subtract(s.getStart());
    Vector2D normal = new Vector2D(arete.y(), -arete.x());// FIXME FIX THAT
    Vector2D sum = new Vector2D(gradient_sum[0], gradient_sum[1]);
    double result = normal.dotProduct(sum);
    return result;// FIXME THAT TOO
  }

  public static double compute(OrientedView v, Rectangle2D r) {
    return Math.max(0, compute(v, r.segment(0))) + Math.max(0, compute(v, r.segment(1)))
        + Math.max(0, compute(v, r.segment(2))) + Math.max(0, compute(v, r.segment(3)));
  }

  public static <T> double compute(OrientedView image, T t) {
    if (!(t instanceof Rectangle2D)) {
      return 0;
    }
    return compute(image, (Rectangle2D) t);
  }
}
