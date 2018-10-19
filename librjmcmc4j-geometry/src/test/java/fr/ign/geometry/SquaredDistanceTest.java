package fr.ign.geometry;

import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Before;
import org.junit.Test;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import fr.ign.random.Random;
import junit.framework.Assert;

public class SquaredDistanceTest {
  Rectangle2D r1;
  Rectangle2D r2;
  Geometry g1;
  Geometry g2;
  GeometryFactory factory;

  @Before
  public void setUp() throws Exception {
    r1 = new Rectangle2D(0, 0, 1, 0, 1);
    factory = new GeometryFactory();
    g1 = r1.toGeometry();
    System.out.println(g1);
  }

  @Test
  public void testSquaredDistance() {
    RandomGenerator gen = Random.random();
    gen.setSeed(0);
    for (int i = 0; i < 100; i++) {
      System.out.println(i);
      r2 = new Rectangle2D(gen.nextDouble() * 100, gen.nextDouble() * 100, gen.nextDouble() * 100, gen.nextDouble() * 100, gen.nextDouble());
      g2 = r2.toGeometry();
      System.out.println(g2);
      SquaredDistance d = new SquaredDistance(r1, r2);
      long start = System.currentTimeMillis();
      double distance1 = Math.sqrt(d.getSquaredDistance());
      long end = System.currentTimeMillis();
      long time1 = end - start;
      System.out.println("distance    = " + distance1);
      start = System.currentTimeMillis();
      double distance2 = g1.distance(g2);
      end = System.currentTimeMillis();
      long time2 = end - start;
      Assert.assertEquals("Probable error in distance computation", distance2, distance1, 0.0001);
      System.out.println("distanceJTS = " + distance2);
      System.out.println("time = " + time1 + " / " + time2);
    }
  }
}
