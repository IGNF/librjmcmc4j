package fr.ign.rjmcmc.kernel;

import java.util.Arrays;
import java.util.Vector;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ign.geometry.Rectangle2D;
import fr.ign.geometry.transform.RectangleScaledEdgeTransform;

public class RectangleScaledEdgeTransformTest {
  RectangleScaledEdgeTransform t;

  @Before
  public void setUp() throws Exception {
    t = new RectangleScaledEdgeTransform();
  }

  @Test
  public void testDimension() {
    Assert.assertEquals(6, t.dimension(5,5));
  }

  @Test
  public void testApply() {
    Vector<Double> val0 = new Vector<>(Arrays.asList(0., 0., 1., 0., 1.));
    Vector<Double> var0 = new Vector<>(Arrays.asList(1.));
    Vector<Double> val1 = new Vector<>();
    val1.setSize(5);
    Vector<Double> var1 = new Vector<>();
    var1.setSize(1);
    Rectangle2D r = new Rectangle2D(val0);
    System.out.println("in " + r.toGeometry());
    System.out.println("in " + r.toGeometry().getArea());
    t.apply(true, val0, var0, val1, var1);
//    for (int i = 0; i < t.dimension(); i++) {
//      System.out.println(out[i]);
//    }
    Rectangle2D rout = new Rectangle2D(val1);
    System.out.println("out " + rout.toGeometry());
    System.out.println("out " + rout.toGeometry().getArea());
  }

  @Test
  public void testGetAbsJacobian() {
    // fail("Not yet implemented"); // TODO
  }

  @Test
  public void testInverse() {
    Vector<Double> val0 = new Vector<>(Arrays.asList(0., 0., 1., 0., 1.));
    Vector<Double> var0 = new Vector<>(Arrays.asList(1.));
    Vector<Double> val1 = new Vector<>();
    val1.setSize(5);
    Vector<Double> var1 = new Vector<>();
    var1.setSize(1);
    Rectangle2D r = new Rectangle2D(val0);
    System.out.println("in" + r.toGeometry());
    System.out.println("in " + r.toGeometry().getArea());
    t.apply(true, val0, var0, val1, var1);
//    for (int i = 0; i < t.dimension(); i++) {
//      System.out.println(out[i]);
//    }
    Rectangle2D rout = new Rectangle2D(val1);
    System.out.println("out" + rout.toGeometry());
    System.out.println("out " + rout.toGeometry().getArea());
    Vector<Double> val2 = new Vector<>();
    val2.setSize(5);
    Vector<Double> var2 = new Vector<>();
    var2.setSize(1);
    t.apply(false, val1, var1, val2, var2);
//    for (int i = 0; i < t.dimension(); i++) {
//      System.out.println(outInv[i]);
//    }
    Rectangle2D routInv = new Rectangle2D(val2);
    System.out.println("outInv" + routInv.toGeometry());
    System.out.println("outInv " + routInv.toGeometry().getArea());
    Assert.assertArrayEquals(Util.toArray(val0), Util.toArray(val2), 0.001);
  }

}
