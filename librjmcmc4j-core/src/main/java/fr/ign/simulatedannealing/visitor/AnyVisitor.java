package fr.ign.simulatedannealing.visitor;

import fr.ign.rjmcmc.configuration.Configuration;
import fr.ign.rjmcmc.configuration.Modification;
import fr.ign.rjmcmc.sampler.Sampler;
import fr.ign.simulatedannealing.temperature.Temperature;

public class AnyVisitor<C extends Configuration<C, M>, M extends Modification<C, M>>
		implements Visitor<C, M> {
	PlaceHolder<C, M> content;

	public AnyVisitor(Visitor<C, M> t) {
		this.content = new Holder<C, M>(t);
	}

	@Override
	public void init(int dump, int save) {
		this.content.init(dump, save);
	}

	@Override
	public void begin(C config, Sampler<C, M> sampler, Temperature t) {
		this.content.begin(config, sampler, t);
	}

	@Override
	public void visit(C config, Sampler<C, M> sampler, Temperature t) {
		this.content.visit(config, sampler, t);
	}

	@Override
	public void end(C config, Sampler<C, M> sampler, Temperature t) {
		this.content.end(config, sampler, t);
	}
}

abstract class PlaceHolder<C extends Configuration<C, M>, M extends Modification<C, M>>
		implements Visitor<C, M> {
	/**
	 * @param value
	 */
	public PlaceHolder(final Visitor<C, M> value) {
	}

	@Override
	public abstract PlaceHolder<C, M> clone();
}

class Holder<C extends Configuration<C, M>, M extends Modification<C, M>>
		extends PlaceHolder<C, M> {
	Visitor<C, M> held;

	/**
	 * @param value
	 */
	public Holder(final Visitor<C, M> value) {
		super(value);
		this.held = value;
	}

	@Override
	public void init(int dump, int save) {
		this.held.init(dump, save);
	}

	@Override
	public void begin(C config, Sampler<C, M> sampler, Temperature t) {
		this.held.begin(config, sampler, t);
	}

	@Override
	public void visit(C config, Sampler<C, M> sampler, Temperature t) {
		this.held.visit(config, sampler, t);
	}

	@Override
	public void end(C config, Sampler<C, M> sampler, Temperature t) {
		this.held.end(config, sampler, t);
	}

	@Override
	public Holder<C, M> clone() {
		return new Holder<C, M>(held);
	}
}
