package fr.ign.rjmcmc.kernel;

import java.util.Vector;

import org.apache.commons.math3.random.RandomGenerator;

import fr.ign.rjmcmc.configuration.Configuration;
import fr.ign.rjmcmc.configuration.Modification;

public class NullView<C extends Configuration<C, M>, M extends Modification<C, M>>
		implements View<C, M> {
	public NullView() {
	}

	@Override
	public double select(boolean direct, RandomGenerator e, C c, M modif) {
		return 1;
	}

	@Override
	public int dimension(boolean direct, C c, M modif) {
		return 0;
	}

	@Override
	public void get(C c, M modif, Vector<Double> val0) {
	}

	@Override
	public void set(C c, M modif, Vector<Double> val1) {
	}
}
