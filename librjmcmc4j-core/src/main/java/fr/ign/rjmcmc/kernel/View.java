package fr.ign.rjmcmc.kernel;

import java.util.Vector;

import org.apache.commons.math3.random.RandomGenerator;

import fr.ign.rjmcmc.configuration.Configuration;
import fr.ign.rjmcmc.configuration.Modification;

public interface View<C extends Configuration<C,M>, M extends Modification<C,M>> {
	// public double apply(RandomGenerator e, Configuration<T> configuration,
	// Modification<T,
	// Configuration<T>> modification, double[] out);
	// public double inversePdf(Configuration<T> configuration, Modification<T,
	// Configuration<T>>
	// modification, double[] obj);
	/**
	 * Returns the discrete probability that samples the portion of the
	 * configuration that is being modified (stored in the modif input).
	 * 
	 * @param direct
	 *            true if direct view sampling, false otherwise
	 * @param e
	 *            the random generator
	 * @param c
	 *            the configuration
	 * @param modif
	 *            the input modification
	 * @return the discrete probability that samples the portion of the
	 *         configuration that is being modified (stored in the modif input)
	 */
	public double select(boolean direct, RandomGenerator e, C c, M modif);

	public int dimension(boolean direct, C c, M modif);

	public void get(C c, M modif, Vector<Double> val0);

	public void set(C c, M modif, Vector<Double> val1);
}
