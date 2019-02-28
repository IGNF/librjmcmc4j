package fr.ign.rjmcmc.kernel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

import fr.ign.geometry.Rectangle2D;
import fr.ign.geometry.transform.RectangleSplitMergeTransform;
import fr.ign.random.Random;

public class RectangleSplitMergeTransformTest {
  RectangleSplitMergeTransform t;
  List<double[]> l = new ArrayList<>();

  @Before
  public void setUp() throws Exception {
    t = new RectangleSplitMergeTransform(1);
    double[] val0 = new double[] { 0., 0., 10., 10., 5., 0., 0., 0., 0., 0. };
    double[] val1 = new double[10];
    Rectangle2D r = new Rectangle2D(val0);
    // System.out.println("in " + r.toGeometry());
    // System.out.println("in " + r.toGeometry().getArea());
    RandomGenerator ran = Random.random();
    GeometryFactory f = new GeometryFactory();
    // String rs = "";
    for (int iter = 0; iter < 100; iter++) {
      for (int i = 0; i < 5; i++) {
        val0[5 + i] = ran.nextDouble();
      }
      double res = t.apply(true, val0, val1);
      Rectangle2D rout1 = new Rectangle2D(Arrays.copyOfRange(val1, 0, 5));
      Rectangle2D rout2 = new Rectangle2D(Arrays.copyOfRange(val1, 5, 10));
      MultiPolygon mp = f.createMultiPolygon(new Polygon[] { rout1.toGeometry(), rout2.toGeometry() });
      // System.out.println(mp);
      // rs += res + "\n";
      l.add(Arrays.copyOf(val1, val1.length));
    }
    // System.out.println(rs);
  }

  @Test
  public void testDimension() {
    Assert.assertEquals(10, t.dimension());
  }

  @Test
  public void testApply() {
    double[] val0 = new double[] { 0., 0., 10., 10., 5., 0., 0., 0., 0., 0. };
    double[] val1 = new double[10];
    Rectangle2D r = new Rectangle2D(val0);
    System.out.println("in " + r.toGeometry());
    System.out.println("in " + r.toGeometry().getArea());
    RandomGenerator ran = Random.random();
    GeometryFactory f = new GeometryFactory();
    String rs = "";
    for (int iter = 0; iter < 100; iter++) {
      for (int i = 0; i < 5; i++) {
        val0[5 + i] = ran.nextDouble();
      }
      double res = t.apply(true, val0, val1);
      Rectangle2D rout1 = new Rectangle2D(Arrays.copyOfRange(val1, 0, 5));
      Rectangle2D rout2 = new Rectangle2D(Arrays.copyOfRange(val1, 5, 10));
      MultiPolygon mp = f.createMultiPolygon(new Polygon[] { rout1.toGeometry(), rout2.toGeometry() });
      System.out.println(mp);
      rs += res + "\n";
      l.add(Arrays.copyOf(val1, val1.length));
    }
    System.out.println(rs);
    // t.apply(in, out);
    // for (int i = 0; i < t.dimension(); i++) {
    // System.out.println(out[i]);
    // }
    // System.out.println("center 1 " + f.createPoint(new Coordinate(out[0], out[1])));
    // System.out.println("out1 " + rout.toGeometry());
    // System.out.println("out1 " + rout.toGeometry().getArea());
    // System.out.println("center 2 " + f.createPoint(new Coordinate(out[5], out[6])));
    // System.out.println("out2 " + rout2.toGeometry());
    // System.out.println("out2 " + rout2.toGeometry().getArea());
  }

  @Test
  public void testGetAbsJacobian() {
    Assert.assertEquals(1d, t.getAbsJacobian(true), 0.001);
  }

  @Test
  public void testInverse() {
    // double[] in = new double[] { 0, 0, 1, 0, 1, 0.1, 0.2, 0.5, 0.5, 0.5 };
    // double[] out = new double[t.dimension()];
    // System.out.println("md=" + t.m_d);
    // Rectangle2D r = new Rectangle2D(0, 0, 1, 0, 1);
    // System.out.println("in " + r.toGeometry());
    // System.out.println("in " + r.toGeometry().getArea());
    // t.apply(in, out);
    // for (int i = 0; i < t.dimension(); i++) {
    // System.out.println(out[i]);
    // }
    // Rectangle2D rout = new Rectangle2D(out[0], out[1], out[2], out[3], out[4]);
    // System.out.println("out " + rout.toGeometry());
    // System.out.println("out " + rout.toGeometry().getArea());
    // double[] outInv = new double[t.dimension()];
    // t.inverse(out, outInv);
    // for (int i = 0; i < t.dimension(); i++) {
    // System.out.println(outInv[i]);
    // }
    // Rectangle2D routInv = new Rectangle2D(outInv[0], outInv[1], outInv[2], outInv[3], outInv[4]);
    // System.out.println("outInv " + routInv.toGeometry());
    // System.out.println("outInv " + routInv.toGeometry().getArea());
    // Assert.assertArrayEquals(in, outInv, 0.001);
    //
    // for (double[] array : l) {
    // double[] inv = new double[t.dimension()];
    // double J = t.inverse(array, inv);
    // Rectangle2D rinv = new Rectangle2D(inv[0], inv[1], inv[2], inv[3], inv[4]);
    // System.out.println(rinv.toGeometry());
    // }
    double[] val0 = new double[] { 0., 0., 10., 10., 5., 0., 0., 0., 0., 0. };
    double[] val1 = new double[10];
    double[] val2 = new double[10];
    // Vector<Double> in = new double[] { 0, 0, 10, 10, 5, 0, 0, 0, 0, 0 };
    // Vector<Double> out = new double[t.dimension()];
    // Vector<Double> outInv = new double[t.dimension()];
    RandomGenerator ran = Random.random();
    // GeometryFactory f = new GeometryFactory();
    String rs = "";
    for (int iter = 0; iter < 100; iter++) {
      for (int i = 0; i < 5; i++) {
        val0[5 + i] = ran.nextDouble();
      }
      double res = t.apply(true, val0, val1);
      double resInv = t.apply(false, val1, val2);
      Assert.assertArrayEquals(val0, val2, 0.001);
      Assert.assertEquals(1.0, res * resInv, 0.001);
      // Rectangle2D rout1 = new Rectangle2D(out[0], out[1], out[2], out[3], out[4]);
      // Rectangle2D rout2 = new Rectangle2D(out[5], out[6], out[7], out[8], out[9]);
      // MultiPolygon mp = f
      // .createMultiPolygon(new Polygon[] { rout1.toGeometry(), rout2.toGeometry() });
      // System.out.println(mp);
      // rs += res + "\n";
      // l.add(Arrays.copyOf(out, out.length));
    }
    System.out.println(rs);
  }

  @Test
  public void test() {
    Rectangle2D r1 = new Rectangle2D(185.78318200148792, 585.5502705986888, 13.716023316068624,
        10.921384479841755, 3.6718006372070646);
    Rectangle2D r2 = new Rectangle2D(109.36479888023415, 13.385514899653394, -13.558616306495155,
        -14.44145394655585, 1.494921419193108);
    System.out.println(r1.toGeometry());
    System.out.println(r2.toGeometry());
    double[] in = new double[] { 185.78318200148792, 585.5502705986888, 13.716023316068624,
        10.921384479841755, 3.6718006372070646, 109.36479888023415, 13.385514899653394,
        -13.558616306495155, -14.44145394655585, 1.494921419193108 };
    double[] out = new double[10];
    t.apply(false, in, out);
    Rectangle2D r = new Rectangle2D(out);
    // for (int i = 0; i < 5; i++) {
    // System.out.println(out[5 + i]);
    // }
    System.out.println(r.toGeometry());
  }
}
