package fr.ign.mpp;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;

import fr.ign.mpp.configuration.BirthDeathModification;
import fr.ign.mpp.configuration.GraphConfiguration;
import fr.ign.mpp.kernel.ObjectSampler;
import fr.ign.rjmcmc.distribution.Distribution;
import fr.ign.rjmcmc.kernel.SimpleObject;
import fr.ign.rjmcmc.sampler.Density;

public class DirectSampler<O extends SimpleObject>
		implements Density<GraphConfiguration<O>, BirthDeathModification<O>> {
	/**
	 * Logger.
	 */
	static Logger LOGGER = Logger.getLogger(DirectSampler.class.getName());

	Distribution density;
	ObjectSampler<O> sampler;

	public DirectSampler(Distribution density, ObjectSampler<O> sampler) {
		this.density = density;
		this.sampler = sampler;
	}

	@Override
	public void init(RandomGenerator e, GraphConfiguration<O> c) {
		c.clear();
		int n = this.density.sample(e);
		System.out.println("density proposed " + n);
		for (int i = 0; i < n; ++i) {
			double v = 0;
			while (v == 0) {
				v = this.sampler.sample(e);
			}
			O res = this.sampler.getObject();
			c.insert(res);
		}
	}

	@Override
	public double pdfRatio(GraphConfiguration<O> c, BirthDeathModification<O> m) {
		int n0 = c.size();
		int n1 = n0 + m.getBirth().size() - m.getDeath().size();
		double ratio = this.density.pdfRatio(n0, n1);
		for (O t : m.getBirth()) {
			double pdf = this.sampler.pdf(t);
			if (pdf == 0) {
				// System.out.println("pdf = 0 for birth " + t);
				return 0;
			}
			ratio *= pdf;
		}
		for (O t : m.getDeath()) {
			double pdf = this.sampler.pdf(t);
			if (pdf == 0) {
				// System.out.println("pdf = 0 for death " + t);
				return 0;
			}
			ratio /= pdf;
		}
		return ratio;
	}
}
