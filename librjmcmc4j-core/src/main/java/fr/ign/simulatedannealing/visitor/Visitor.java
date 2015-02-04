package fr.ign.simulatedannealing.visitor;

import fr.ign.rjmcmc.configuration.Configuration;
import fr.ign.rjmcmc.configuration.Modification;
import fr.ign.rjmcmc.sampler.Sampler;
import fr.ign.simulatedannealing.temperature.Temperature;

public interface Visitor<C extends Configuration<C, M>, M extends Modification<C, M>> {
	/**
	 * Intialize the visitor.
	 * @param dump
	 * @param save
	 */
	void init(int dump, int save);

	/**
	 * Called at the beginning of the simulated annealing.
	 * @param config
	 * @param sampler
	 * @param t
	 */
	void begin(C config, Sampler<C,M> sampler, Temperature t);

	/**
	 * Called at each iteration of the simulated annealing.
	 * @param config
	 * @param sampler
	 * @param t
	 */
	void visit(C config, Sampler<C,M> sampler, Temperature t);

	/**
	 * Called at the end of the simulated annealing.
	 * @param config
	 * @param sampler
	 * @param t
	 */
	void end(C config, Sampler<C,M> sampler, Temperature t);
}
