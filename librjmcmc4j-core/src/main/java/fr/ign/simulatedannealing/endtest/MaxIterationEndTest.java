package fr.ign.simulatedannealing.endtest;

import fr.ign.rjmcmc.configuration.Configuration;
import fr.ign.rjmcmc.configuration.Modification;
import fr.ign.rjmcmc.sampler.Sampler;
import fr.ign.simulatedannealing.temperature.Temperature;

public class MaxIterationEndTest implements EndTest {
	int iterations;

	public MaxIterationEndTest(int n) {
		this.iterations = n;
	}

	@Override
	public <C extends Configuration<C, M>, M extends Modification<C, M>> boolean evaluate(C config, Sampler<C, M> sampler, Temperature t) {
		return --this.iterations <= 0;
	}

	@Override
	public void stop() {
		this.iterations = 0;
	}

	@Override
	public String toString() {
		return "" + this.iterations;
	}
}
