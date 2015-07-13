package fr.ign.rjmcmc.kernel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ign.geometry.Rectangle2D;
import fr.ign.geometry.transform.RectangleEdgeTranslationTransform;

public class RectangleEdgeTranslationTransformTest {
  RectangleEdgeTranslationTransform t;

  @Before
  public void setUp() throws Exception {
    t = new RectangleEdgeTranslationTransform(0, 1, 10);
  }

  @Test
  public void testDimension() {
    Assert.assertEquals(6, t.dimension());
  }

  @Test
  public void testApply() {
    double[] val0 = new double[]{0., 0., 1., 0., 1., 0.5};
    double[] val1 = new double[6];
    Rectangle2D r = new Rectangle2D(val0);
    System.out.println("in " + r.toGeometry());
    System.out.println("in " + r.toGeometry().getArea());
    t.apply(true, val0, val1);
//    for (int i = 0; i < t.dimension(); i++) {
//      System.out.println(out[i]);
//    }
    Rectangle2D rout = new Rectangle2D(val1);
    System.out.println("out " + rout.toGeometry());
    System.out.println("out " + rout.toGeometry().getArea());
  }

  @Test
  public void testGetAbsJacobian() {
    Assert.assertEquals(1d, t.getAbsJacobian(true), 0.001);
  }

  @Test
  public void testInverse() {
    double[] val0 = new double[]{0., 0., 1., 0., 1.,0.5};
    double[] val1 = new double[6];
    Rectangle2D r = new Rectangle2D(val0);
    System.out.println("in " + r.toGeometry());
    System.out.println("in " + r.toGeometry().getArea());
    t.apply(true, val0, val1);
//    for (int i = 0; i < t.dimension(); i++) {
//      System.out.println(out[i]);
//    }
    Rectangle2D rout = new Rectangle2D(val1);
    System.out.println("out " + rout.toGeometry());
    System.out.println("out " + rout.toGeometry().getArea());
    double[] outInv = new double[6];
    t.apply(false, val1, outInv);
//    for (int i = 0; i < t.dimension(); i++) {
//      System.out.println(outInv[i]);
//    }
    Rectangle2D routInv = new Rectangle2D(outInv);
    System.out.println("outInv " + routInv.toGeometry());
    System.out.println("outInv " + routInv.toGeometry().getArea());
    Assert.assertArrayEquals(val0, outInv, 0.001);
  }
}
