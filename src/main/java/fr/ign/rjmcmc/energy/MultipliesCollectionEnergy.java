package fr.ign.rjmcmc.energy;

import java.util.Collection;

public class MultipliesCollectionEnergy<T> implements CollectionEnergyOperator<T> {
	CollectionEnergy<T> energy1;
	CollectionEnergy<T> energy2;

	public MultipliesCollectionEnergy(CollectionEnergy<T> e1, CollectionEnergy<T> e2) {
		this.energy1 = e1;
		this.energy2 = e2;
	}

	@Override
	public double getValue(Collection<T> t) {
		double e1 = this.energy1.getValue(t);
		if (e1 == 0.0) return 0.0;
		return e1 * this.energy2.getValue(t);
	}

}
