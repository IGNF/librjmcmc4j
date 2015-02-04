package fr.ign.rjmcmc.sampler;

import java.util.List;

import org.apache.commons.math3.random.RandomGenerator;

import fr.ign.rjmcmc.configuration.Configuration;
import fr.ign.rjmcmc.configuration.Modification;
import fr.ign.rjmcmc.kernel.Kernel;
import fr.ign.simulatedannealing.temperature.Temperature;

public interface Sampler<C extends Configuration<C, M>, M extends Modification<C, M>> {
	void sample(RandomGenerator e, C config, Temperature t);

	int kernelSize();

	int kernelId();

	String kernelName(int i);

	List<Kernel<C, M>> getKernels();

	boolean accepted();

	double acceptanceProbability();

	Temperature temperature();

	double delta();

	double greenRatio();

	double kernelRatio();

	double refPdfRatio();

	long getTimeRandomApply();

	long getTimeGreenRatio();

	long getTimeDelta();

	long getTimeAcceptance();

	long getTimeApply();

	boolean blockTemperature();

}
