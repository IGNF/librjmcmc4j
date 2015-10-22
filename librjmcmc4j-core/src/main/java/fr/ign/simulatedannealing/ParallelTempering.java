package fr.ign.simulatedannealing;

import java.util.logging.Logger;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Assert;

import fr.ign.rjmcmc.configuration.Configuration;
import fr.ign.rjmcmc.configuration.Modification;
import fr.ign.rjmcmc.distribution.UniformDistribution;
import fr.ign.rjmcmc.kernel.SimpleObject;
import fr.ign.rjmcmc.sampler.Sampler;
import fr.ign.simulatedannealing.endtest.EndTest;
import fr.ign.simulatedannealing.schedule.Schedule;
import fr.ign.simulatedannealing.temperature.Temperature;
import fr.ign.simulatedannealing.visitor.Visitor;

public class ParallelTempering {
	/**
	 * Logger.
	 */
	static Logger LOGGER = Logger.getLogger(ParallelTempering.class.getName());

	/**
	 * Optimize using parallel tempering.
	 * 
	 * @param e
	 * @param config
	 *            an array of configurations
	 * @param samplers
	 *            a sampler
	 * @param schedules
	 *            an array of schedules
	 * @param endTest
	 *            the end test
	 * @param visitors
	 *            an array of visitors
	 */
	public static <O extends SimpleObject, C extends Configuration<C, M>, M extends Modification<C, M>> void optimize(
			RandomGenerator e, C[] config, Sampler<C, M> sampler,
			Schedule<? extends Temperature>[] schedules, EndTest endTest,
			Visitor<C, M>[] visitors) {
		int numberOfReplicas = schedules.length;
		Assert.assertTrue("Wrong number of replicas", numberOfReplicas > 1);
		// FIXME we could also handle 1 replica
		UniformDistribution distribution = new UniformDistribution(e, 0,
				numberOfReplicas - 2);
		UniformRealDistribution random = new UniformRealDistribution(e, 0.0,
				1.0);

		if (visitors != null) {
			Assert.assertEquals(numberOfReplicas, visitors.length);
		}
		Temperature[] t = new Temperature[numberOfReplicas];
		for (int i = 0; i < numberOfReplicas; i++) {
			t[i] = schedules[i].getTemperature();
		}
		if (visitors != null) {
			for (int i = 0; i < numberOfReplicas; i++) {
				if (visitors[i] != null) {
					visitors[i].begin(config[i], sampler, t[i]);
				}
			}
		}
		for (; !endTest.evaluate(config[0], sampler, t[0]);) {
			for (int i = 0; i < numberOfReplicas; i++) {
				sampler.sample(e, config[i], t[i]);
				if (visitors != null && visitors[i] != null) {
					visitors[i].visit(config[i], sampler, t[i]);
				}
				t[i] = schedules[i].next().getTemperature();
			}

			int index = distribution.sample(e);
			double deltaEnergy = config[index].getEnergy()
					- config[index + 1].getEnergy();
			double deltaTemperature = 1 / t[index].getTemperature(0) - 1
					/ t[index + 1].getTemperature(0);
			double acceptanceProb = Math.exp(deltaEnergy * deltaTemperature);

			if (random.sample() < acceptanceProb) {
				// LOGGER.info("Accepter swap at " + index +
				// " with probability " + acceptanceProb);
				// swapmodels
				C configSwap = config[index];
				config[index] = config[index + 1];
				config[index + 1] = configSwap;
			} else {
				// LOGGER.info("Rejected swap at " + index +
				// " with probability " + acceptanceProb);
			}
		}
		if (visitors != null) {
			for (int i = 0; i < numberOfReplicas; i++) {
				if (visitors[i] != null) {
					visitors[i].end(config[i], sampler, t[i]);
				}
			}
		}
	}
}
