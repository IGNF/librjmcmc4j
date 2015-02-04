package fr.ign.rjmcmc.kernel;

import org.apache.commons.math3.random.RandomGenerator;

import fr.ign.rjmcmc.configuration.Configuration;
import fr.ign.rjmcmc.configuration.Modification;

public class KernelFunctor<C extends Configuration<C, M>, M extends Modification<C, M>> {
	C config;
	M modif;

	public C getConfig() {
		return config;
	}

	public M getModif() {
		return modif;
	}

	RandomGenerator engine;

	public KernelFunctor(RandomGenerator e, C c, M m) {
		this.config = c;
		this.modif = m;
		this.engine = e;
	}

	public double apply(double x, Kernel<C, M> k) {
		return k.operator(this.engine, x, this.config, this.modif);
	}
}
