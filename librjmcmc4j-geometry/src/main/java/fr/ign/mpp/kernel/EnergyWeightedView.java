package fr.ign.mpp.kernel;

import java.util.Vector;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;

import fr.ign.mpp.configuration.ListConfiguration;
import fr.ign.random.Random;
import fr.ign.rjmcmc.configuration.Modification;
import fr.ign.rjmcmc.kernel.SimpleObject;
import fr.ign.rjmcmc.kernel.View;

public class EnergyWeightedView<T extends SimpleObject, C extends ListConfiguration<T, C, M>, M extends Modification<C, M>>
		implements View<C, M> {
	/**
	 * Logger.
	 */
	static Logger LOGGER = Logger.getLogger(EnergyWeightedView.class.getName());

	int dimension = 0;
	ObjectBuilder<T> builder;
	RandomGenerator random;

	public EnergyWeightedView(ObjectBuilder<T> b) {
		this.builder = b;
		this.dimension = b.size();
		this.random = Random.random();
	}

	// @Override
	// public double apply(RandomGenerator e, Configuration<T> configuration,
	// Modification<T, Configuration<T>> modification, double[] out) {
	// // FIXME use e
	// int n = configuration.size();
	// if (n == 0) {
	// return 0.;
	// }
	// Iterator<T> it = configuration.iterator();
	// // FIX ME find a better normalization scheme
	// double normalizationFactor = 0;
	// for (int i = 0; i < n; i++) {
	// double energy = configuration.getEnergy(it.next()) - 250;
	// normalizationFactor += Math.exp(energy / 1000);
	// }
	//
	// double sample = random.nextDouble() * normalizationFactor;
	//
	// it = configuration.iterator();
	// T choice = null;
	// double choiceEnergy = 0;
	// while (it.hasNext() && choice == null) {
	// T current = it.next();
	// double energy = Math.exp((configuration.getEnergy(current) - 250) /
	// 1000);
	// if (sample < energy) {
	// choice = current;
	// choiceEnergy = energy;
	// } else {
	// sample -= energy;
	// }
	// }
	// if (choice == null) {
	// System.out.println("norm = " + normalizationFactor);
	// System.out.println("sample = " + sample);
	// it = configuration.iterator();
	// for (int i = 0; i < n; i++) {
	// double energy = configuration.getEnergy(it.next());
	// System.out.println(energy);
	// }
	//
	// System.exit(0);
	// }
	// modification.insertDeath(choice);
	// this.builder.setCoordinates(choice, out);
	// return choiceEnergy / normalizationFactor;
	// }
	//
	// @Override
	// public double inversePdf(Configuration<T> configuration,
	// Modification<T, Configuration<T>> modification, double[] it) {
	// T object = builder.build(it);
	// Iterator<T> iterator = configuration.iterator();
	// double normalizationFactor = 0;
	// while (iterator.hasNext()) {
	// double energy = configuration.getEnergy(iterator.next()) - 250;
	// normalizationFactor += Math.exp(energy / 1000);
	// }
	// double energy = Math.exp((configuration.getUnaryEnergy(object) - 250) /
	// 1000);
	// modification.insertBirth(object);
	// return energy / (normalizationFactor + energy);
	// }

	@Override
	public double select(boolean direct, RandomGenerator e, C c, M modif) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int dimension(boolean direct, C c, M modif) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void get(C c, M modif, Vector<Double> val0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void set(C c, M modif, Vector<Double> val1) {
		// TODO Auto-generated method stub

	}
}
