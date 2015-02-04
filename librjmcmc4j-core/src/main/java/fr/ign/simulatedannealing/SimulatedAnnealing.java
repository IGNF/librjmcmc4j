package fr.ign.simulatedannealing;

import org.apache.commons.math3.random.RandomGenerator;

import fr.ign.rjmcmc.configuration.Configuration;
import fr.ign.rjmcmc.configuration.Modification;
import fr.ign.rjmcmc.sampler.Sampler;
import fr.ign.simulatedannealing.endtest.EndTest;
import fr.ign.simulatedannealing.schedule.Schedule;
import fr.ign.simulatedannealing.temperature.Temperature;
import fr.ign.simulatedannealing.visitor.Visitor;

public class SimulatedAnnealing {
	public static <C extends Configuration<C, M>, M extends Modification<C, M>> void optimize(
			RandomGenerator e, C config, Sampler<C, M> sampler,
			Schedule<? extends Temperature> schedule, EndTest endTest,
			Visitor<C, M> visitor) {
		// [simulated_annealing_loop
		Temperature t = schedule.getTemperature();
		if (visitor != null) {
			visitor.begin(config, sampler, t);
		}

		for (; !endTest.evaluate(config, sampler, t); t = (!sampler
				.blockTemperature()) ? schedule.next().getTemperature() : t) {

			sampler.sample(e, config, t);

			if (visitor != null) {
				visitor.visit(config, sampler, t);
			}
		}
		if (visitor != null) {
			visitor.end(config, sampler, t);
		}
		// ]
	}
}
