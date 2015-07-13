package fr.ign.geometry.transform;

import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Assert;
import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import fr.ign.random.Random;

public class PolygonTransformTest {
  PolygonTransform pt;
  GeometryFactory factory = new GeometryFactory();;
  WKTReader reader = new WKTReader();

  @Test
  public void testApplyPolygon() throws ParseException {
    pt = new PolygonTransform(
        (Polygon) reader
            .read("POLYGON (( 736 476, 634 605, 830 733, 862 567, 917 635, 1106 485, 887 374, 904 576, 764 240, 736 476 ))"),
        0.1);
    testApply();
  }

  @Test
  public void testApplyMultiPolygon() throws ParseException {
    pt = new PolygonTransform(
        (MultiPolygon) reader
            .read("MULTIPOLYGON ((( 1431.7068008705114 362.40198585418926, 1287.3809249006565 511.5121450053653, 1313.5723068552775 678.18457562568, 1418.0758977149076 667.961398258977, 1411.941326685801 596.6470100456122, 1348.7854733405877 637.2918661588683, 1351.0572905331883 560.050081610446, 1525.9872143634386 494.1673830250271, 1431.7068008705114 362.40198585418926 )), (( 1267.5007830915767 532.0512970401604, 1226.1073449401524 574.8168933623502, 1184.8566390570438 676.0299655291527, 1252.23324265506 696.3591131664853, 1267.5007830915767 532.0512970401604 )), (( 1177.1035971183262 695.0528931726041, 1115.9242110990208 845.163139281828, 1242.7034102564294 849.7345046360615, 1164.4514247766363 726.0963675779881, 1177.1035971183262 695.0528931726041 )), (( 1305.7719724324973 852.0086114452947, 1588.4621871599566 862.2017682263329, 1416.9399891186072 715.6695593035907, 1471.448270459355 762.2362367403886, 1334.0186615886835 712.2618335146898, 1305.7719724324973 852.0086114452947 )), (( 1416.9399891186072 715.6695593035907, 1754.3048422198042 610.0300598476604, 1712.2762241566923 263.5779379760608, 1578.2390097932537 489.6237486398258, 1426.6561348852663 587.1770839766493, 1465.8956234171742 561.9239477927483, 1571.4235582154517 561.1859902067464, 1576.816208471399 665.6073087991812, 1416.9399891186072 715.6695593035907 )))"),
        0.1);
    testApply();
  }

  public void testApply() {
    for (int iter = 0; iter < 1000; iter++) {
      double[] val0 = new double[2];
      double[] val1 = new double[2];
      RandomGenerator generator = Random.random();
      double[] expectedVar = new double[] { generator.nextDouble(), generator.nextDouble() };
      val0[0] = expectedVar[0];
      val0[1] = expectedVar[1];
      // System.out.println(var0.get(0) + ", " + var0.get(1));
      pt.apply(true, val0, val1);
      // System.out.println(r + " => " + factory.createPoint(new
      // Coordinate(val1.get(0), val1.get(1))));
      System.out.println(factory.createPoint(new Coordinate(val1[0], val1[1])));

      double[] expectedVal = new double[] { val1[0], val1[1] };

      val1[0] = expectedVal[0];
      val1[1] = expectedVal[1];
      pt.apply(false, val1, val0);
      double[] actualVar = new double[] { val0[0], val0[1] };
      // System.out.println(r + " => " + var0.get(0) + ", " + var0.get(1));

      val0[0] = actualVar[0];
      val0[1] = actualVar[1];
      pt.apply(true, val0, val1);
      // System.out.println(r + " => " + factory.createPoint(new
      // Coordinate(val1.get(0), val1.get(1))));
      double[] actualVal = new double[] { val1[0], val1[1] };
      Assert.assertArrayEquals("The apply method is not reversible", expectedVar, actualVar, 0.000001);
      Assert.assertArrayEquals("The apply method is not reversible", expectedVal, actualVal, 0.000001);
    }
  }
}
