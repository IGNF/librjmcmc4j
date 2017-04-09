package fr.ign.rjmcmc.kernel;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.RandomGenerator;

public class RasterVariate extends Variate {
	int[] m_size;
	int N;
	int m_totsize;
	double[] m_cdf;
	double m_sum;
	UniformRealDistribution m_rand;

	public RasterVariate(double[] pdf, int[] size, RandomGenerator rng) {
		super(rng);
		N = size.length;
		m_size = new int[N];
		m_totsize = 1;
		for (int i = 0; i < N; i++) {
			m_totsize *= size[i];
			m_size[i] = size[i];
		}
		m_sum = 0;
		// double sum = 0.0;
		// List<Double> cdf = new ArrayList<>();
		// cdf.add(new Double(0));
		// for (int i = 0; i < totsize; i++) {
		// sum += pdf[i];
		// cdf.add(new Double(sum));
		// }
		// List<Double> res = new ArrayList<>(cdf.size());
		// for (int i = 0; i < totsize; i++) {
		// System.out.println(i + " = " + new Double(cdf.get(i) / sum));
		// res.add(new Double(cdf.get(i) / sum));
		// }
		//
		m_cdf = new double[m_totsize + 1];
		m_cdf[0] = 0;
		for (int i = 0; i < m_totsize; i++)
			m_sum = m_cdf[i + 1] = m_sum + pdf[i];
		for (int i = 0; i < m_totsize; i++)
			m_cdf[i + 1] /= m_sum;
		m_rand = new org.apache.commons.math3.distribution.UniformRealDistribution(rng, 0.0, 1.0);
	}

	@Override
	public double compute(double[] var0, int d) {
		double x = m_rand.sample();
		// System.out.println("x=" + x);
		int offset;
		for (offset = 0; offset < m_cdf.length; offset++) {
			if (m_cdf[offset] > x) {
				break;
			}
		}
		offset--;
		// System.out.println("offset=" + offset);
		double pdf = (m_cdf[offset + 1] - m_cdf[offset]) * m_totsize;
		for (int i = 0; i < N; i++) {
			double ix = offset % m_size[i];
			var0[i + d] = (ix + m_rand.sample()) / m_size[i];
			// System.out.println("var[" + i + "]=" + var0[i + d]);
			offset /= m_size[i];
		}
		return pdf;
	}

	@Override
	public double pdf(double[] var1, int d) {
		int offset = getOffset(var1, d);
		if (offset < 0)
			return 0;
		return (m_cdf[offset + 1] - m_cdf[offset]) * m_totsize;
	}

	private int getOffset(double[] it, int d) {
		int offset = 0;
		int stride = 1;
		for (int i = 0; i < N; i++) {
			double x = it[i + d];
			if (x < 0.0 || x > 1.0) {
				return -1;
			}
			int ix = (int) (x * m_size[i]);
			offset += stride * ix;
			stride *= m_size[i];
		}
		return offset;
	}
}
