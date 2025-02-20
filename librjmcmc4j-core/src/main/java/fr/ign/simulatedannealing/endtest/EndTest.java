package fr.ign.simulatedannealing.endtest;

import fr.ign.rjmcmc.configuration.Configuration;
import fr.ign.rjmcmc.configuration.Modification;
import fr.ign.rjmcmc.sampler.Sampler;
import fr.ign.simulatedannealing.temperature.Temperature;

/**
 * The end test interface.
 */
public interface EndTest {
	/**
	 * @param <C> configuration
	 * @param <M> Modification
	 * @return evaluate
	 */
	<C extends Configuration<C, M>, M extends Modification<C, M>> boolean evaluate(C config, Sampler<C, M> sampler, Temperature t);
	/**
	 * 
	 */
	void stop();
}
