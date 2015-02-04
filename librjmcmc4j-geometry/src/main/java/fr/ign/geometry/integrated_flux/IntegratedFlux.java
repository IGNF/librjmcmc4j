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
  /**
   * Logger.
   */
  // private static Logger logger = Logger.getLogger(IntegratedFlux.class.getName());
  public static double compute(OrientedView v, Segment2D s0) {
    int x0 = v.x0();
    int y0 = v.y0();
    // logger.info("x0 = " + x0);
    // logger.info("y0 " + y0);
    // long start = System.currentTimeMillis();
    int x1 = x0 + v.width();
    int y1 = y0 + v.height();
    Segment2D s = new Segment2D(s0);
    IsoRectangle2D bbox = new IsoRectangle2D(x0, y0, x1, y1);
    s = bbox.clip(s);
    if (s == null) {
      return 0;
    }
    Segment2DIterator it = new Segment2DIterator(s);
    // long end = System.currentTimeMillis();
    // logger.debug("\tInit " + (end - start));
    // start = end;
    Locator loc_grad = v.xy_at((it.x() - x0), (it.y() - y0));
    // end = System.currentTimeMillis();
    // logger.debug("\tloc_grad " + (end - start));
    // start = end;
    // logger.info("locgrad at " + (it.x() - x0) + ", " + (it.y() - y0) + " = " +
    // loc_grad.getValue()[0]);
    Point2D[] movement = { new Point2D(it.step(0), 0), new Point2D(0, it.step(1)) };
    // logger.info("movement[0] = " + movement[0].x() + ", " + movement[0].y());
    // logger.info("movement[1] = " + movement[1].x() + ", " + movement[1].y());
    int nbChannels = v.numberOfChannels();
    float[] gradient_sum;
    if (nbChannels == 2) {
      gradient_sum = new float[] { 0.f, 0.f };
    } else {
      gradient_sum = new float[] { 0.0f };
    }
    for (; !it.end(); it.next()) {
      // logger.info("gradient_sum = " + gradient_sum[0] + " --- " + gradient_sum[1]);
      if (it.x() >= x0 && it.x() < x1 && it.y() >= y0 && it.y() < y1) {
        double length = it.length();
        // logger.info("length = " + length);
        double[] grad = loc_grad.getValue();
        // logger.info("grad = " + grad[0] + ", " + grad[1]);
        gradient_sum[0] += length * grad[0];
        if (nbChannels == 2) {
          gradient_sum[1] += length * grad[1];
        }
        // logger.info("gradient_sum[0] = " + gradient_sum[0]);
        // logger.info("gradient_sum[1] = " + gradient_sum[1]);
      }
      // logger.info("it.axis() = " + it.axis());
      loc_grad.move(movement[it.axis()]);
    }
    // end = System.currentTimeMillis();
    // logger.debug("\tstuff " + (end - start));
    // start = end;
    if (nbChannels == 1) {
      return gradient_sum[0];
    }
    Vector2D arete = s.getEnd().subtract(s.getStart());
    Vector2D normal = new Vector2D(arete.y(), -arete.x());// FIXME FIX THAT
    Vector2D sum = new Vector2D(gradient_sum[0], gradient_sum[1]);
    double result = normal.dotProduct(sum);
    // end = System.currentTimeMillis();
    // logger.debug("\tvector stuff " + (end - start));
    // start = end;
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
