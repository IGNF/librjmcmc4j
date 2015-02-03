package fr.ign.mpp.configuration;

import java.util.Iterator;

import fr.ign.rjmcmc.configuration.Configuration;
import fr.ign.rjmcmc.configuration.Modification;
import fr.ign.rjmcmc.kernel.SimpleObject;

public interface ListConfiguration<T extends SimpleObject, C extends ListConfiguration<T, C, M>, M extends Modification<C, M>>
		extends Configuration<C, M>, Iterable<T> {
	/**
	 * Clear the configuration: remove all objects from it.
	 */
	void clear();

	/**
	 * Insert an object into the configuration.
	 * 
	 * @param v
	 *            a new object
	 */
	void insert(T v);

	/**
	 * Remove an object from the configuration.
	 * <p>
	 * If the object does not belong to the configuration, the behaviour is not
	 * specified.
	 * 
	 * @param v
	 *            the object to remove
	 */
	void remove(T v);

	/**
	 * @return the number of objects in the configuration.
	 */
	int size();

	/**
	 * @return an iterator over the elements of the configuration.
	 */
	Iterator<T> iterator();

	double getUnaryEnergy();

	double getBinaryEnergy();

	double getUnaryEnergy(T o);

	double getGlobalEnergy();

}
