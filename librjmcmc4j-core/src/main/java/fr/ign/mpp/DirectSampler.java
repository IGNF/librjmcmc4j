package fr.ign.mpp;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.math3.random.RandomGenerator;

import fr.ign.mpp.configuration.AbstractBirthDeathModification;
import fr.ign.mpp.configuration.AbstractGraphConfiguration;
import fr.ign.mpp.kernel.ObjectSampler;
import fr.ign.rjmcmc.distribution.Distribution;
import fr.ign.rjmcmc.kernel.SimpleObject;
import fr.ign.rjmcmc.sampler.Density;

public class DirectSampler<O extends SimpleObject, C extends AbstractGraphConfiguration<O, C, M>, M extends AbstractBirthDeathModification<O, C, M>>
		implements Density<C, M> {
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
	public void init(RandomGenerator e, C c) {
		c.clear();
		int n = this.density.sample(e);
		LOGGER.log(Level.FINE, "density proposed " + n);
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
	public double pdfRatio(C c, M m) {
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
