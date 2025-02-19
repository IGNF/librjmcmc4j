package fr.ign.mpp.configuration;

import fr.ign.rjmcmc.kernel.SimpleObject;

/**
 * Represents the proposed modifications of the current configuration.
 * 
 * @author Julien Perret
 * @param <T>
 *            The type of objects held by the configuration
 */

public class BirthDeathModification<T extends SimpleObject>
		extends
		AbstractBirthDeathModification<T, GraphConfiguration<T>, BirthDeathModification<T>> {
	/**
	 * Create a new empty configuration.
	 */
	public BirthDeathModification() {
	}
}
