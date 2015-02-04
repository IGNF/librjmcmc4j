package fr.ign.simulatedannealing;

import org.apache.commons.math3.random.RandomGenerator;

import fr.ign.rjmcmc.configuration.Configuration;
import fr.ign.rjmcmc.configuration.Modification;
import fr.ign.rjmcmc.sampler.Density;

public class SalamonInitialSchedule {
	public static <C extends Configuration<C, M>, M extends Modification<C, M>> double salamon_initial_schedule(
			RandomGenerator rg, Density<C, M> sampler, C c, int iterations) {
		double e1 = 0;
		double e2 = 0;
		double inv = 1. / iterations;
		for (int i = 0; i < iterations; ++i) {
			if (i % 100 == 0) {
				System.out.println(i + " / " + iterations);
			}
			sampler.init(rg, c);
			double e = c.getEnergy();
			double inv_e = inv * e;
			e1 += inv_e;
			e2 += inv_e * e;
		}
		double std_dev = Math.sqrt(e2 - e1 * e1);
		return 2 * std_dev;
	}

}
