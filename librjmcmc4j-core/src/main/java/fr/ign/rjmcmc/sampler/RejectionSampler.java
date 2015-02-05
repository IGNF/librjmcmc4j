package fr.ign.rjmcmc.sampler;

import java.util.List;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;

import fr.ign.rjmcmc.configuration.Configuration;
import fr.ign.rjmcmc.configuration.ConfigurationPredicate;
import fr.ign.rjmcmc.configuration.Modification;
import fr.ign.rjmcmc.kernel.Kernel;
import fr.ign.simulatedannealing.temperature.Temperature;

public class RejectionSampler<C extends Configuration<C, M>, M extends Modification<C, M>>
		implements Sampler<C, M> {
	/**
	 * Logger.
	 */
	static Logger LOGGER = Logger.getLogger(RejectionSampler.class.getName());

	Sampler<C, M> sampler;
	ConfigurationPredicate<C> predicate;

	public RejectionSampler(Sampler<C, M> sampler, ConfigurationPredicate<C> pred) {
		this.sampler = sampler;
		this.predicate = pred;
	}

	@Override
	public void sample(RandomGenerator e, C config, Temperature t) {
		int n = 0;
		boolean check = false;
		do {
			// it may take a while to come back to a valid configuration...
			this.sampler.sample(e, config, t);
			check = this.predicate.check(config);
			n++;
			if (check) {
				LOGGER.debug("Sampled " + n + " configurations before checking");
			} else {
				if (n % 10000 == 0) {
					LOGGER.debug("Sampled " + n + " configurations");
				}
			}
		} while (!check);
	}

	public boolean blockTemperature() {
		return false;
	}

	@Override
	public int kernelSize() {
		return this.sampler.kernelSize();
	}

	@Override
	public int kernelId() {
		return this.sampler.kernelId();
	}

	@Override
	public String kernelName(int i) {
		return this.sampler.kernelName(i);
	}

	@Override
	public boolean accepted() {
		return this.sampler.accepted();
	}

	@Override
	public double acceptanceProbability() {
		return this.sampler.acceptanceProbability();
	}

	@Override
	public Temperature temperature() {
		return this.sampler.temperature();
	}

	@Override
	public double delta() {
		return this.sampler.delta();
	}

	@Override
	public double greenRatio() {
		return this.sampler.greenRatio();
	}

	@Override
	public double kernelRatio() {
		return this.sampler.kernelRatio();
	}

	@Override
	public double refPdfRatio() {
		return this.sampler.refPdfRatio();
	}

	@Override
	public long getTimeRandomApply() {
		return this.sampler.getTimeRandomApply();
	}

	@Override
	public long getTimeGreenRatio() {
		return this.sampler.getTimeGreenRatio();
	}

	@Override
	public long getTimeDelta() {
		return this.sampler.getTimeDelta();
	}

	@Override
	public long getTimeAcceptance() {
		return this.sampler.getTimeAcceptance();
	}

	@Override
	public long getTimeApply() {
		return this.sampler.getTimeApply();
	}

	@Override
	public List<Kernel<C, M>> getKernels() {
		return this.sampler.getKernels();
	}
}
