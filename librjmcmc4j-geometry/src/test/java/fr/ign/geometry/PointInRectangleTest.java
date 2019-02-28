package fr.ign.geometry;

import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import fr.ign.random.Random;

public class PointInRectangleTest {
  Rectangle2D r1;
  Geometry g1;
  GeometryFactory factory;

  @Before
  public void setUp() throws Exception {
    r1 = new Rectangle2D(0, 0, 1, 0, 1);
    factory = new GeometryFactory();
    g1 = r1.toGeometry();
  }

  @Test
  public void testSquaredDistance() {
    RandomGenerator gen = Random.random();
    gen.setSeed(0);
    int nb = 100000;
    Point2D[] points2D = new Point2D[nb];
    Point[] points = new Point[nb];
    boolean[] values2D = new boolean[nb];
    boolean[] values = new boolean[nb];
    for (int i = 0; i < nb; i++) {
      Point2D p = new Point2D(gen.nextDouble() * 2, gen.nextDouble() * 2);
      Point g = factory.createPoint(new Coordinate(p.x, p.y));
      points2D[i] = p;
      points[i] = g;
    }
    long start = System.currentTimeMillis();
    for (int i = 0; i < nb; i++) {
      PointInRectangle d = new PointInRectangle(r1, points2D[i]);
      values2D[i] = d.isPointInRectangle();
    }
    long end = System.currentTimeMillis();
    long time1 = end - start;
    start = System.currentTimeMillis();
    for (int i = 0; i < nb; i++) {
      values[i] = g1.contains(points[i]);
    }
    end = System.currentTimeMillis();
    long time2 = end - start;
    for (int i = 0; i < nb; i++) {
      Assert.assertEquals("Probable error in point in rectangle computation", values[i], values2D[i]);
    }
    System.out.println("time = " + time1 + " / " + time2);
  }
}
