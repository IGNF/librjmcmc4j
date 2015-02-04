package fr.ign.rjmcmc.sampler;

import java.util.List;

import org.apache.commons.math3.random.RandomGenerator;

import fr.ign.rjmcmc.configuration.Configuration;
import fr.ign.rjmcmc.configuration.Modification;
import fr.ign.rjmcmc.kernel.Kernel;
import fr.ign.simulatedannealing.temperature.Temperature;

public class AnySampler<C extends Configuration<C, M>, M extends Modification<C, M>>
		implements Sampler<C, M> {
	public AnySampler(Sampler<C, M> samp) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void sample(RandomGenerator e, C config, Temperature t) {
		// TODO NOT IMPLEMENTED
	}

	@Override
	public double acceptanceProbability() {
		// TODO NOT IMPLEMENTED
		return 0;
	}

	@Override
	public boolean accepted() {
		// TODO NOT IMPLEMENTED
		return false;
	}

	@Override
	public double delta() {
		// TODO NOT IMPLEMENTED
		return 0;
	}

	@Override
	public double greenRatio() {
		// TODO NOT IMPLEMENTED
		return 0;
	}

	@Override
	public int kernelId() {
		// TODO NOT IMPLEMENTED
		return 0;
	}

	@Override
	public String kernelName(int i) {
		// TODO NOT IMPLEMENTED
		return "";
	}

	@Override
	public double kernelRatio() {
		// TODO NOT IMPLEMENTED
		return 0;
	}

	@Override
	public int kernelSize() {
		// TODO NOT IMPLEMENTED
		return 0;
	}

	@Override
	public double refPdfRatio() {
		// TODO NOT IMPLEMENTED
		return 0;
	}

	@Override
	public Temperature temperature() {
		// TODO NOT IMPLEMENTED
		return null;
	}

	@Override
	public long getTimeAcceptance() {
		// TODO NOT IMPLEMENTED
		return 0;
	}

	@Override
	public long getTimeApply() {
		// TODO NOT IMPLEMENTED
		return 0;
	}

	@Override
	public long getTimeDelta() {
		// TODO NOT IMPLEMENTED
		return 0;
	}

	@Override
	public long getTimeGreenRatio() {
		// TODO NOT IMPLEMENTED
		return 0;
	}

	@Override
	public long getTimeRandomApply() {
		// TODO NOT IMPLEMENTED
		return 0;
	}

	@Override
	public List<Kernel<C, M>> getKernels() {
		return null;
	}

	public boolean blockTemperature() {
		return false;
	}
}
