package fr.ign.rjmcmc.energy;

import java.util.Collection;

public class ConstantEnergy<T, U> implements UnaryEnergy<T>,
		BinaryEnergy<T, U>, CollectionEnergy<T> {
	double value;

	public ConstantEnergy(double e) {
		this.value = e;
	}

	public double getValue() {
		return this.value;
	}

	@Override
	public double getValue(T t) {
		return this.value;
	}

	@Override
	public double getValue(T t, U u) {
		return this.value;
	}

	@Override
	public double getValue(Collection<T> t) {
		return this.value;
	}
}
