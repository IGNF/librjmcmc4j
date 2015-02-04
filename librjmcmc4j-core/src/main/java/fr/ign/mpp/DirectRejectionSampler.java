package fr.ign.mpp;

import org.apache.commons.math3.random.RandomGenerator;

import fr.ign.mpp.configuration.BirthDeathModification;
import fr.ign.mpp.configuration.GraphConfiguration;
import fr.ign.mpp.kernel.ObjectSampler;
import fr.ign.rjmcmc.configuration.ConfigurationModificationPredicate;
import fr.ign.rjmcmc.distribution.Distribution;
import fr.ign.rjmcmc.kernel.SimpleObject;

public class DirectRejectionSampler<O extends SimpleObject> extends
		DirectSampler<O> {
	ConfigurationModificationPredicate<GraphConfiguration<O>, BirthDeathModification<O>> pred;

	public DirectRejectionSampler(
			Distribution density,
			ObjectSampler<O> sampler,
			ConfigurationModificationPredicate<GraphConfiguration<O>, BirthDeathModification<O>> pred) {
		super(density, sampler);
		this.pred = pred;
	}

	@Override
	public void init(RandomGenerator e, GraphConfiguration<O> c) {
		c.clear();
		BirthDeathModification<O> m = c.newModification();
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
	public double pdfRatio(GraphConfiguration<O> c, BirthDeathModification<O> m) {
		double ratio = super.pdfRatio(c, m);
		if (!pred.check(c, m)) {
			return 0; // sampling failure code
		}
		return ratio;
	}
}
