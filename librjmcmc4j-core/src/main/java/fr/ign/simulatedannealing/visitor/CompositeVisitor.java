package fr.ign.simulatedannealing.visitor;

import java.util.List;

import fr.ign.rjmcmc.configuration.Configuration;
import fr.ign.rjmcmc.configuration.Modification;
import fr.ign.rjmcmc.sampler.Sampler;
import fr.ign.simulatedannealing.temperature.Temperature;

public class CompositeVisitor<C extends Configuration<C, M>, M extends Modification<C, M>>
		implements Visitor<C, M> {
	List<Visitor<C, M>> visitors;

	public CompositeVisitor(List<Visitor<C, M>> visitors) {
		this.visitors = visitors;
	}

	@Override
	public void init(int dump, int save) {
		for (Visitor<C, M> visitor : this.visitors) {
			visitor.init(dump, save);
		}
	}

	@Override
	public void begin(C config, Sampler<C, M> sampler, Temperature t) {
		for (Visitor<C, M> visitor : this.visitors) {
			visitor.begin(config, sampler, t);
		}
	}

	@Override
	public void visit(C config, Sampler<C, M> sampler, Temperature t) {
		for (Visitor<C, M> visitor : this.visitors) {
			visitor.visit(config, sampler, t);
		}
	}

	@Override
	public void end(C config, Sampler<C, M> sampler, Temperature t) {
		for (Visitor<C, M> visitor : this.visitors) {
			visitor.end(config, sampler, t);
		}
	}
}
