package fr.ign.geometry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

public class Rectangle2DTest {
  Rectangle2D r1;
  Rectangle2D r2;
  GeometryFactory factory;

  @Before
  public void setUp() throws Exception {
    r1 = new Rectangle2D(0, 0, 1, 0, 1);
    r2 = new Rectangle2D(0, 0, Math.sqrt(2) / 2, Math.sqrt(2) / 2, 1);
    factory = new GeometryFactory();
  }

  @Test
  public void testSegment() {
    System.out.println("segment 0 = " + line(r1.segment(0)));
    System.out.println("segment 1 = " + line(r1.segment(1)));
    System.out.println("segment 2 = " + line(r1.segment(2)));
    System.out.println("segment 3 = " + line(r1.segment(3)));
    System.out.println("segment 4 = " + line(r1.segment(4)));
  }

  private String line(Segment2D segment) {
    return factory.createLineString(
        new Coordinate[] { coord(segment.getStart()), coord(segment.getEnd()) }).toString();
  }

  private Coordinate coord(Point2D v) {
    return new Coordinate(v.x(), v.x());
  }

  @Test
  public void testIntersectionArea() {
    double area = r1.intersectionArea(r2);
    System.out.println("area = " + area);
    Assert.assertEquals("", 3.3137, area, 0.001);
    area = IntersectionArea.intersection_area(r1, r2);
    System.out.println("area = " + area);
    Assert.assertEquals("", 3.3137, area, 0.001);
  }

  @Test
  public void testToGeometry() {
    System.out.println("r1 = " + r1.toGeometry());
    System.out.println("r2 = " + r2.toGeometry());
  }

  @Test
  public void testFromGeometry() {
    Rectangle2D rr1 = Rectangle2D.fromGeometry(r1.toGeometry());
    Rectangle2D rr2 = Rectangle2D.fromGeometry(r2.toGeometry());
    Assert.assertArrayEquals(r1.toArray(), rr1.toArray(), 0.001);
    Assert.assertArrayEquals(r2.toArray(), rr2.toArray(), 0.001);
    System.out.println("rr1 = " + rr1.toGeometry());
    System.out.println("rr2 = " + rr2.toGeometry());
  }

  @Test
  public void testScaledEdge() {
    System.out.println("r1.scaledEdge(0, 2) = " + r1.scaledEdge(0, 2));
    System.out.println("r1.scaledEdge(0, 3) = " + r1.scaledEdge(0, 3));
    System.out.println("r2.scaledEdge(0, 2) = " + r2.scaledEdge(0, 2));
    System.out.println("r2.scaledEdge(0, 3) = " + r2.scaledEdge(0, 3));
  }

}
