package fr.ign.mpp;

import org.apache.commons.math3.random.RandomGenerator;

import fr.ign.mpp.configuration.AbstractBirthDeathModification;
import fr.ign.mpp.configuration.AbstractGraphConfiguration;
import fr.ign.mpp.kernel.ObjectSampler;
import fr.ign.rjmcmc.configuration.ConfigurationModificationPredicate;
import fr.ign.rjmcmc.distribution.Distribution;
import fr.ign.rjmcmc.kernel.SimpleObject;

public class DirectRejectionSampler<O extends SimpleObject, C extends AbstractGraphConfiguration<O, C, M>, M extends AbstractBirthDeathModification<O, C, M>> extends
		DirectSampler<O,C,M> {
	ConfigurationModificationPredicate<C, M> pred;

	public DirectRejectionSampler(
			Distribution density,
			ObjectSampler<O> sampler,
			ConfigurationModificationPredicate<C, M> pred) {
		super(density, sampler);
		this.pred = pred;
	}

	@Override
	public void init(RandomGenerator e, C c) {
		c.clear();
		M m = c.newModification();
		do {
			m.clear();
			int n = this.density.sample(e);
			// System.out.println(n);
			for (int i = 0; i < n; ++i) {
				double v = 0;
				while (v == 0) {
					v = this.sampler.sample(e);
				}
				O res = this.sampler.getObject();
				// c.insert(res);
				m.insertBirth(res);
			}
		} while (!this.pred.check(c, m));
		// System.out.println("init");
		// c.apply(m);
		m.apply(c);
	}

	@Override
	public double pdfRatio(C c, M m) {
		double ratio = super.pdfRatio(c, m);
		if (!pred.check(c, m)) {
			return 0; // sampling failure code
		}
		return ratio;
	}
}
