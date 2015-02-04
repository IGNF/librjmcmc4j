package fr.ign.rjmcmc.kernel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import fr.ign.geometry.Rectangle2D;
import fr.ign.geometry.transform.RectangleSplitMergeTransform;
import fr.ign.random.Random;

public class RectangleSplitMergeTransformTest {
  RectangleSplitMergeTransform t;
  List<double[]> l = new ArrayList<>();

  @Before
  public void setUp() throws Exception {
    t = new RectangleSplitMergeTransform(1);
    Vector<Double> val0 = new Vector<>(Arrays.asList(0., 0., 10., 10., 5.));
    Vector<Double> var0 = new Vector<>(Arrays.asList(0., 0., 0., 0., 0. ));
    Vector<Double> val1 = new Vector<>();
    val1.setSize(10);
    Vector<Double> var1 = new Vector<>();
    Rectangle2D r = new Rectangle2D(val0);
    // System.out.println("in " + r.toGeometry());
    // System.out.println("in " + r.toGeometry().getArea());
    RandomGenerator ran = Random.random();
    GeometryFactory f = new GeometryFactory();
    // String rs = "";
    for (int iter = 0; iter < 100; iter++) {
      for (int i = 0; i < 5; i++) {
        var0.set(i, ran.nextDouble());
      }
      double res = t.apply(true, val0, var0, val1, var1);
      Rectangle2D rout1 = new Rectangle2D(val1.subList(0, 5));
      Rectangle2D rout2 = new Rectangle2D(val1.subList(5,10));
      MultiPolygon mp = f
          .createMultiPolygon(new Polygon[] { rout1.toGeometry(), rout2.toGeometry() });
      // System.out.println(mp);
      // rs += res + "\n";
      l.add(Arrays.copyOf(Util.toArray(val1), val1.size()));
    }
    // System.out.println(rs);
  }

  @Test
  public void testDimension() {
    Assert.assertEquals(10, t.dimension(5,10));
  }

  @Test
  public void testApply() {
    Vector<Double> val0 = new Vector<>(Arrays.asList(0., 0., 10., 10., 5.));
    Vector<Double> var0 = new Vector<>(Arrays.asList(0., 0., 0., 0., 0. ));
    Vector<Double> val1 = new Vector<>();
    val1.setSize(10);
    Vector<Double> var1 = new Vector<>();
    Rectangle2D r = new Rectangle2D(val0);
    System.out.println("in " + r.toGeometry());
    System.out.println("in " + r.toGeometry().getArea());
    RandomGenerator ran = Random.random();
    GeometryFactory f = new GeometryFactory();
    String rs = "";
    for (int iter = 0; iter < 100; iter++) {
      for (int i = 0; i < 5; i++) {
        var0.set(i, ran.nextDouble());
      }
      double res = t.apply(true, val0, var0, val1, var1);
      Rectangle2D rout1 = new Rectangle2D(val1.subList(0, 5));
      Rectangle2D rout2 = new Rectangle2D(val1.subList(5,10));
      MultiPolygon mp = f
          .createMultiPolygon(new Polygon[] { rout1.toGeometry(), rout2.toGeometry() });
      System.out.println(mp);
      rs += res + "\n";
      l.add(Arrays.copyOf(Util.toArray(val1), val1.size()));
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
    Vector<Double> val0 = new Vector<>(Arrays.asList(0., 0., 10., 10., 5.));
    Vector<Double> var0 = new Vector<>(Arrays.asList(0., 0., 0., 0., 0. ));
    Vector<Double> val1 = new Vector<>();
    val1.setSize(10);
    Vector<Double> var1 = new Vector<>();
    Vector<Double> val2 = new Vector<>();
    val2.setSize(5);
    Vector<Double> var2 = new Vector<>();
    var2.setSize(5);
//    Vector<Double> in = new double[] { 0, 0, 10, 10, 5, 0, 0, 0, 0, 0 };
//    Vector<Double> out = new double[t.dimension()];
//    Vector<Double> outInv = new double[t.dimension()];
    RandomGenerator ran = Random.random();
    // GeometryFactory f = new GeometryFactory();
    String rs = "";
    for (int iter = 0; iter < 100; iter++) {
      for (int i = 0; i < 5; i++) {
        var0.set(i, ran.nextDouble());
      }
      double res = t.apply(true, val0, var0, val1, var1);
      double resInv = t.apply(false, val1, var1, val2, var2);
      Assert.assertArrayEquals(Util.toArray(val0), Util.toArray(val2), 0.001);
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
    Vector<Double> in = new Vector<>(Arrays.asList(185.78318200148792, 585.5502705986888, 13.716023316068624,
        10.921384479841755, 3.6718006372070646, 109.36479888023415, 13.385514899653394,
        -13.558616306495155, -14.44145394655585, 1.494921419193108));
    Vector<Double> out = new Vector<>();
    out.setSize(5);
    Vector<Double> var1 = new Vector<>();
    var1.setSize(5);
    t.apply(false, in, new Vector<Double>(), out, var1);
    Rectangle2D r = new Rectangle2D(out);
//    for (int i = 0; i < 5; i++) {
//      System.out.println(out[5 + i]);
//    }
    System.out.println(r.toGeometry());
  }
}
