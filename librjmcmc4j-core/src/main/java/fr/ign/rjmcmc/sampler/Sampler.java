package fr.ign.rjmcmc.sampler;

import java.util.List;

import org.apache.commons.math3.random.RandomGenerator;

import fr.ign.rjmcmc.configuration.Configuration;
import fr.ign.rjmcmc.configuration.Modification;
import fr.ign.rjmcmc.kernel.Kernel;
import fr.ign.simulatedannealing.temperature.Temperature;

public interface Sampler<C extends Configuration<C, M>, M extends Modification<C, M>> {
	/**
	 * Sample a new configuration.
	 * @param e
	 * @param config
	 * @param t
	 */
	void sample(RandomGenerator e, C config, Temperature t);
  // simulated annealing but not necessary (used for statistics)
  boolean accepted();
  // simulated annealing but not necessary (used for statistics)
  double acceptanceProbability();
  // simulated annealing but not necessary (used for statistics)
  Temperature temperature();
  // simulated annealing but not necessary (used for statistics)
  double delta();

	// rjmcmc specific but not necessary for simulated annealing (used for statistics)
	int kernelSize();
  // rjmcmc specific but not necessary for simulated annealing (used for statistics)
	int kernelId();
  // rjmcmc specific but not necessary for simulated annealing (used for statistics)
	String kernelName(int i);
  // rjmcmc specific but not necessary for simulated annealing (used for statistics)
	List<Kernel<C, M>> getKernels();
  // rjmcmc specific but not necessary for simulated annealing (used for statistics)
	double greenRatio();
  // rjmcmc specific but not necessary for simulated annealing (used for statistics)
	double kernelRatio();
  // rjmcmc specific but not necessary for simulated annealing (used for statistics)
	double refPdfRatio();
  // rjmcmc specific but not necessary for simulated annealing (used for statistics)
	long getTimeRandomApply();
	 // rjmcmc specific but not necessary for simulated annealing (used for statistics)
	long getTimeGreenRatio();
  // rjmcmc specific but not necessary for simulated annealing (used for statistics)
	long getTimeDelta();
  // rjmcmc specific but not necessary for simulated annealing (used for statistics)
	long getTimeAcceptance();
  // rjmcmc specific but not necessary for simulated annealing (used for statistics)
	long getTimeApply();
  // rjmcmc specific but not necessary for simulated annealing (used for specific schedule)
	boolean blockTemperature();
}
