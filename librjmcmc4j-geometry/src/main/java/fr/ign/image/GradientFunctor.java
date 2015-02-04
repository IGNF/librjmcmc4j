package fr.ign.image;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;

public class GradientFunctor {
  public static float[] initKernelGaussian1D(int size, int center, float m_sigma) {
    // Gaussian smoothing
    float z = (float) (1.0 / (Math.sqrt(2 * Math.PI) * m_sigma));
    float m_sigmasquared = m_sigma * m_sigma;
    float x = -1.0f * center;
    float sum = 0.f;
    float[] array = new float[size];
    for (int i = 0; i < size; ++i, ++x) {
      array[i] = (float) (z * (Math.exp(-0.5 * (x * x / m_sigmasquared))));
      sum += array[i];
    }
    for (int i = 0; i < size; ++i) {
      array[i] /= sum;
//      System.out.println(array[i] + " ");
    }
    return array;
  }

  public static float[] initKernelGaussianDeriv1D(int size, int center, float m_sigma) {
    // Gaussian derivative smoothing
    float z = (float) (1.0 / (Math.sqrt(2 * Math.PI) * m_sigma));
    float m_sigmasquared = m_sigma * m_sigma;
    float x = -1.0f * center;
    float sum = 0.f;
    float[] array = new float[size];
    for (int i = 0; i < size; ++i, ++x) {
      array[i] = (float) (-(x / m_sigmasquared) * z * (Math.exp(-0.5 * (x * x / m_sigmasquared))));
      sum += array[i] * x;
    }
    for (int i = 0; i < size; ++i) {
      array[i] /= sum;
//      System.out.println(array[i] + " ");
    }
    return array;
  }
public static float[] product(float[] a, float[] b) {
  float[] result = new float[a.length * b.length];
  for (int i = 0; i < a.length; i++) {
    for (int j = 0; j < b.length; j++) {
      result[i*b.length + j] = a[i] * b[j];
    }
  }
  return result;
}
static String dataType(SampleModel model) {
  switch (model.getDataType()) {
    case DataBuffer.TYPE_BYTE:
      return "byte";
    case DataBuffer.TYPE_DOUBLE:
      return "double";
    case DataBuffer.TYPE_FLOAT:
      return "float";
    case DataBuffer.TYPE_INT:
      return "int";
    case DataBuffer.TYPE_SHORT:
      return "short";
    case DataBuffer.TYPE_UNDEFINED:
      return "undefined";
    case DataBuffer.TYPE_USHORT:
      return "ushort";
  }
  return "WTF";
}
  public static void gradientFunctor(PlanarImage im0, float m_sigma, OrientedView view) {
    int half_size = (int) (3 * m_sigma);
    int kws = 2 * half_size + 1;
    float[] smooth = GradientFunctor.initKernelGaussian1D(kws, kws / 2, m_sigma);
    float[] deriv = GradientFunctor.initKernelGaussianDeriv1D(kws, kws / 2, m_sigma);
    KernelJAI kernel1 = new KernelJAI(kws, kws, product(smooth, deriv));
    KernelJAI kernel2 = new KernelJAI(kws, kws, product(deriv, smooth));
    PlanarImage im1 = (PlanarImage) JAI.create("convolve", im0, kernel1);
//    System.out.println("im1 of type : " + dataType(im1.getSampleModel()) + " with " + im1.getSampleModel().getNumBands() + " " + im1.getColorModel().getNumComponents());
    PlanarImage im2 = (PlanarImage) JAI.create("convolve", im0, kernel2);
//    System.out.println("im2 of type : " + dataType(im2.getSampleModel()) + " with " + im2.getSampleModel().getNumBands() + " " + im2.getColorModel().getNumComponents());
    
    ParameterBlock pb = new ParameterBlock();
    pb.setSource(im1, 0);
    pb.setSource(im2, 1);
    PlanarImage result = JAI.create("bandmerge", pb, null);
    view.setGradient(result);
//    view.setChannel0(im1);
//    view.setChannel1(im2);
//    BufferedImage bi = im1.getAsBufferedImage();
//    File f1 = new File("D:\\Users\\JulienPerret\\workspace\\librjmcmc-java\\output_h.tif");
//    ImageIO.write(bi, "TIFF", f1);
//    BufferedImage bj = im2.getAsBufferedImage();
//    File f2 = new File("D:\\Users\\JulienPerret\\workspace\\librjmcmc-java\\output_v.tif");
//    ImageIO.write(bj, "TIFF", f2);
  }
  public static BufferedImage gradientFunctor(BufferedImage im0, float m_sigma) {
    int half_size = (int) (3 * m_sigma);
    int kws = 2 * half_size + 1;
    float[] smooth = GradientFunctor.initKernelGaussian1D(kws, kws / 2, m_sigma);
    float[] deriv = GradientFunctor.initKernelGaussianDeriv1D(kws, kws / 2, m_sigma);
    KernelJAI kernel1 = new KernelJAI(kws, kws, product(smooth, deriv));
    KernelJAI kernel2 = new KernelJAI(kws, kws, product(deriv, smooth));
    PlanarImage im1 = (PlanarImage) JAI.create("convolve", im0, kernel1);
    PlanarImage im2 = (PlanarImage) JAI.create("convolve", im0, kernel2);    
    ParameterBlock pb = new ParameterBlock();
    pb.setSource(im1, 0);
    pb.setSource(im2, 1);
    PlanarImage result = JAI.create("bandmerge", pb, null);
    return result.getAsBufferedImage();
//    view.setChannel0(im1);
//    view.setChannel1(im2);
//    BufferedImage bi = im1.getAsBufferedImage();
//    File f1 = new File("D:\\Users\\JulienPerret\\workspace\\librjmcmc-java\\output_h.tif");
//    ImageIO.write(bi, "TIFF", f1);
//    BufferedImage bj = im2.getAsBufferedImage();
//    File f2 = new File("D:\\Users\\JulienPerret\\workspace\\librjmcmc-java\\output_v.tif");
//    ImageIO.write(bj, "TIFF", f2);
  }

}
