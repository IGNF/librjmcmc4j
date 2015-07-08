package fr.ign.geometry.transform;

import java.util.Vector;

import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;

import fr.ign.random.Random;

public class PolygonTransformTest {
  PolygonTransform pt;
  GeometryFactory factory = new GeometryFactory();;
  WKTReader reader = new WKTReader();

  @Before
  public void setUp() throws Exception {
    pt = new PolygonTransform(
        (Polygon) reader
            .read("POLYGON (( 736 476, 634 605, 830 733, 862 567, 917 635, 1106 485, 887 374, 904 576, 764 240, 736 476 ))"),
        0.1);
  }

  @Test
  public void testApply() {
    for (int iter = 0; iter < 10000; iter++) {
      Vector<Double> val0 = new Vector<>();
      Vector<Double> var0 = new Vector<>();
      Vector<Double> val1 = new Vector<>();
      Vector<Double> var1 = new Vector<>();
      RandomGenerator generator = Random.random();
      double[] expectedVar = new double[] { generator.nextDouble(), generator.nextDouble() };
      var0.add(expectedVar[0]);
      var0.add(expectedVar[1]);
      // System.out.println(var0.get(0) + ", " + var0.get(1));
      val1.setSize(2);
      pt.apply(true, val0, var0, val1, var1);
      // System.out.println(r + " => " + factory.createPoint(new
      // Coordinate(val1.get(0), val1.get(1))));
      System.out.println(factory.createPoint(new Coordinate(val1.get(0), val1.get(1))));

      double[] expectedVal = new double[] { val1.get(0), val1.get(1) };

      val0 = new Vector<>();
      var0 = new Vector<>();
      val1 = new Vector<>();
      var1 = new Vector<>();
      var0.setSize(2);
      val1.add(expectedVal[0]);
      val1.add(expectedVal[1]);
      pt.apply(false, val1, var1, val0, var0);
      double[] actualVar = new double[] { var0.get(0), var0.get(1) };
      // System.out.println(r + " => " + var0.get(0) + ", " + var0.get(1));

      val0 = new Vector<>();
      var0 = new Vector<>();
      val1 = new Vector<>();
      var1 = new Vector<>();
      val1.setSize(2);
      var0.add(actualVar[0]);
      var0.add(actualVar[1]);
      pt.apply(true, val0, var0, val1, var1);
      // System.out.println(r + " => " + factory.createPoint(new
      // Coordinate(val1.get(0), val1.get(1))));
      double[] actualVal = new double[] { val1.get(0), val1.get(1) };
      Assert.assertArrayEquals("The apply method is not reversible", expectedVar, actualVar, 0.000001);
      Assert.assertArrayEquals("The apply method is not reversible", expectedVal, actualVal, 0.000001);
    }
  }
}
