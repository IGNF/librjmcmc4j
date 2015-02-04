package fr.ign.rjmcmc.energy;

import java.util.Collection;

public class PlusCollectionEnergy<T> implements CollectionEnergyOperator<T> {
	CollectionEnergy<T> energy1;
	CollectionEnergy<T> energy2;

	public PlusCollectionEnergy(CollectionEnergy<T> e1, CollectionEnergy<T> e2) {
		this.energy1 = e1;
		this.energy2 = e2;
	}

	@Override
	public double getValue(Collection<T> t) {
		return this.energy1.getValue(t) + this.energy2.getValue(t);
	}

}
