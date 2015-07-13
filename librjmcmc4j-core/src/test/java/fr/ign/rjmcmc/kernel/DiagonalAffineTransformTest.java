package fr.ign.rjmcmc.kernel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DiagonalAffineTransformTest {
  double[] d;
  double[] o;
  DiagonalAffineTransform dat;

  @Before
  public void setUp() throws Exception {
    d = new double[]{100., 100., 40., 40., 4.8};
    o = new double[]{0.0, 0.0, -20.0, -20.0, 0.2};
    dat = new DiagonalAffineTransform(d, o);
  }

  @Test
  public void testGetDeterminant() {
    Assert.assertEquals(100 * 100 * 40 * 40 * 4.8, dat.getAbsJacobian(true), 0);
  }

  @Test
  public void testDiagonalAffineTransform() {
    Assert.assertArrayEquals(d, dat.getMat(), 0.001);
    Assert.assertArrayEquals(o, dat.getDelta(), 0.001);
    double[] in = new double[]{1., 1., 1., 1., 1.};
    double[] out = new double[5];
    dat.apply(true, in, out);
    double[] outInv = new double[5];
    dat.apply(false, out, outInv);
    Assert.assertArrayEquals(in, outInv, 0.001);
  }

  @Test
  public void testApply() {
    double[] in = new double[]{1., 1., 1., 1., 1.};
    double[] out = new double[5];
    dat.apply(true, in, out);
    Assert.assertArrayEquals(new double[] { 100.0, 100.0, 20.0, 20.0, 5.0 }, out, 0.001);
  }

  @Test
  public void testInverse() {
    double[] in = new double[]{1., 1., 1., 1., 1.};
    double[] out = new double[5];
    dat.apply(true, in, out);
    double[] outInv = new double[5];
    dat.apply(false, out, outInv);
    Assert.assertArrayEquals(in, outInv, 0.001);
  }

  @Test
  public void testDimension() {
    Assert.assertEquals(5, dat.dimension());
  }
}
