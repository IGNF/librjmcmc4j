package fr.ign.rjmcmc.kernel;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Test;

public class RasterVariateTest {

  @Test
  public void testCompute() {
    int iter = 1000000;
    double[] value = { //
        1, 2, 3, 2, 1, //
        0, 3, 3, 3, 0, //
        1, 2, 3, 2, 1, //
        5, 2, 0, 2, 1, //
        1, 2, 3, 2, 1, //
        6, 2, 7, 2, 9 };
    int[] size = { 5, 6 };
    RandomGenerator rng = new MersenneTwister(1);
    Variate variate = new RasterVariate(value, size, rng);
    int[] count = new int[size[0] * size[1]];
    for (int i = 0; i < iter; i++) {
      double[] val = new double[2];
      double pdf = variate.compute(val, 0);
      if (pdf == 0)
        System.out.println("sampling failed");
      if (pdf != variate.pdf(val, 0))
        System.out.println("pdf mismatch : " + pdf + "=" + variate.pdf(val, 0));
      int x = (int) (val[0] * size[0]);
      int y = (int) (val[1] * size[1]);
      count[x + y * size[0]] += 1;
    }
    boolean displayValues = true;
    boolean displayCounts = true;
    if (displayValues) {
      for (int y = 0; y < size[1]; y++) {
        for (int x = 0; x < size[0]; x++) {
          System.out.print(value[x + y * size[0]] + " ");
        }
        System.out.println();
      }
      System.out.println();
    }
    double countsum = 0;
    double valuesum = 0;
    for (int i = 0; i < value.length; i++) {
      countsum += count[i];
      valuesum += value[i];
    }
    if (displayCounts) {
      System.out.println("counts for visual comparison");
      double sum = countsum / valuesum;
      for (int y = 0; y < size[1]; y++) {
        for (int x = 0; x < size[0]; x++) {
          System.out.print(((double) count[x + y * size[0]]) / sum + " ");
        }
        System.out.println();
      }
      System.out.println();
    }
    int nbZeros = 0;
    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;
    List<Double> apdBuffer = new ArrayList<>();
    double apdBuffersum = 0;
    for (int i = 0; i < value.length; i++) {
      double ratio = value[i] / valuesum;
      if (ratio > 0) {
        double apd = Math.abs((((double) count[i]) / countsum - ratio) / ratio);
        apdBuffer.add(apd);
        min = Math.min(min, apd);
        max = Math.max(max, apd);
        apdBuffersum += apd;
      } else {
        nbZeros += 1;
      }
    }
    System.out.println("absolute percentage difference = " + (min * 100) + "% " + (apdBuffersum / apdBuffer.size() * 100) + "% " + (max * 100) + "%");
    System.out.println(nbZeros + " zeros");
  }

  @Test
  public void testCompute1D() {
    int iter = 1000000;
    double[] value = { 8, 2, 7, 3, 5, 1, 6, 4 };
    int[] size = { 8 };
    RandomGenerator rng = new MersenneTwister(1);
    Variate variate = new RasterVariate(value, size, rng);
    int[] count = new int[size[0]];
    for (int i = 0; i < iter; i++) {
      double[] val = new double[1];
      double pdf = variate.compute(val, 0);
      if (pdf == 0)
        System.out.println("sampling failed");
      if (pdf != variate.pdf(val, 0))
        System.out.println("pdf mismatch : " + pdf + "=" + variate.pdf(val, 0));
      int x = (int) (val[0] * size[0]);
      count[x]++;
    }
    boolean displayValues = true;
    boolean displayCounts = true;
    if (displayValues) {
      for (int x = 0; x < size[0]; x++) {
        System.out.print(value[x] + " ");
      }
      System.out.println();
    }
    double countsum = 0;
    double valuesum = 0;
    for (int i = 0; i < value.length; i++) {
      countsum += count[i];
      valuesum += value[i];
    }
    if (displayCounts) {
      System.out.println("counts for visual comparison");
      double sum = countsum / valuesum;
      for (int x = 0; x < size[0]; x++) {
        System.out.print(((double) count[x]) / sum + " ");
      }
      System.out.println();
    }
    int nbZeros = 0;
    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;
    List<Double> apdBuffer = new ArrayList<>();
    double apdBuffersum = 0;
    for (int i = 0; i < value.length; i++) {
      double ratio = value[i] / valuesum;
      if (ratio > 0) {
        double apd = Math.abs((((double) count[i]) / countsum - ratio) / ratio);
        apdBuffer.add(apd);
        min = Math.min(min, apd);
        max = Math.max(max, apd);
        apdBuffersum += apd;
      } else {
        nbZeros += 1;
      }
    }
    System.out.println("absolute percentage difference = " + (min * 100) + "% " + (apdBuffersum / apdBuffer.size() * 100) + "% " + (max * 100) + "%");
    System.out.println(nbZeros + " zeros");
  }
}
