package fr.ign.rjmcmc.configuration;


/**
 * Represents a configuration of objects.
 * @param <C> Configuration
 * @param <M> Modification
 */
public interface Configuration<C extends Configuration<C, M>, M extends Modification<C, M>> {

	/**
	 * Compute the energy difference caused by the application of the given modification.
	 * 
	 * @param m
	 *            a modification
	 * @return energy difference caused by the application of the given modification
	 */
	double deltaEnergy(M m);

	/**
	 * the current energy of the configuration.
	 * @return the current energy of the configuration.
	 */
	double getEnergy();

	M newModification();

	/**
	 * @return the current unary energy, i.e. the sum of the energies of the
	 *         objects.
	 */
	// double getUnaryEnergy();

	/**
	 * @return the current binary energy, i.e. the sum of the interaction
	 *         energies between the objects.
	 */
	// double getBinaryEnergy();

	/**
	 * @param v
	 *            an object
	 * @return the unary energy of the given object.
	 */
	// double getEnergy(T v);
	// double getUnaryEnergy(T o);
	// double getGlobalEnergy();
	// int size();

	// Iterator<T> iterator();
}
