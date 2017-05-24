package fr.ign.simulatedannealing.endtest;

import java.util.Arrays;
import java.util.List;

import fr.ign.rjmcmc.configuration.Configuration;
import fr.ign.rjmcmc.configuration.Modification;
import fr.ign.rjmcmc.sampler.Sampler;
import fr.ign.simulatedannealing.temperature.Temperature;

/**
 * A composite entest that takes a list of end tests and evaluates them all. 
 * It should stop when the first end test wants to stop.
 */
public class CompositeEndTest implements EndTest {
	List<EndTest> tests;
	public CompositeEndTest(EndTest... t) {
		tests = Arrays.asList(t);
	}
	@Override
	public <C extends Configuration<C, M>, M extends Modification<C, M>> boolean evaluate(C config, Sampler<C, M> sampler, Temperature t) {
		boolean result = false;
		for (EndTest test : tests) {
			boolean r = test.evaluate(config, sampler, t);
			result = result | r;
		}
		return result;
	}

	@Override
	public void stop() {
		for (EndTest test : tests) {
			test.stop();
		}
	}
}
