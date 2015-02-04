package fr.ign.rjmcmc.kernel;

import java.util.Arrays;
import java.util.Vector;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DiagonalAffineTransformTest {
  Vector<Double> d;
  Vector<Double> o;
  DiagonalAffineTransform dat;

  @Before
  public void setUp() throws Exception {
    d = new Vector<Double>(Arrays.asList(100., 100., 40., 40., 4.8));
    o = new Vector<Double>(Arrays.asList(0.0, 0.0, -20.0, -20.0, 0.2));
    dat = new DiagonalAffineTransform(d, o);
  }

  @Test
  public void testGetDeterminant() {
    Assert.assertEquals(100 * 100 * 40 * 40 * 4.8, dat.getAbsJacobian(true), 0);
  }

  @Test
  public void testDiagonalAffineTransform() {
    Assert.assertArrayEquals(Util.toArray(d), dat.getMat(), 0.001);
    Assert.assertArrayEquals(Util.toArray(o), dat.getDelta(), 0.001);
    Vector<Double> in = new Vector<>(Arrays.asList(1., 1., 1., 1., 1.));
    Vector<Double> out = new Vector<>();
    out.setSize(5);
    dat.apply(true, in, new Vector<Double>(), out, new Vector<Double>());
    Vector<Double> outInv = new Vector<>();
    outInv.setSize(5);
    dat.apply(false, out, new Vector<Double>(), outInv, new Vector<Double>());
    Assert.assertArrayEquals(Util.toArray(in), Util.toArray(outInv), 0.001);
  }

  @Test
  public void testApply() {
    Vector<Double> in = new Vector<>(Arrays.asList(1., 1., 1., 1., 1.));
    Vector<Double> out = new Vector<>();
    out.setSize(5);
    dat.apply(true, in, new Vector<Double>(), out, new Vector<Double>());
    Assert.assertArrayEquals(new double[] { 100.0, 100.0, 20.0, 20.0, 5.0 }, Util.toArray(out), 0.001);
  }

  @Test
  public void testInverse() {
    Vector<Double> in = new Vector<>(Arrays.asList(1., 1., 1., 1., 1.));
    Vector<Double> out = new Vector<>();
    out.setSize(5);
    dat.apply(true, in, new Vector<Double>(), out, new Vector<Double>());
    Vector<Double> outInv = new Vector<>();
    outInv.setSize(5);
    dat.apply(false, out, new Vector<Double>(), outInv, new Vector<Double>());
    Assert.assertArrayEquals(Util.toArray(in), Util.toArray(outInv), 0.001);
  }

  @Test
  public void testDimension() {
    Assert.assertEquals(5, dat.dimension(5, 5));
  }
}
