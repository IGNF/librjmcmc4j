package fr.ign.rjmcmc.configuration;

/**
 * A modification interface.
 * @param <C> configuration
 * @param <M> modification
 */
public interface Modification<C extends Configuration<C, M>, M extends Modification<C, M>> {
	/**
	 * Apply the modification.
	 * @param c configuration to apply the modification to
	 */
	void apply(C c);
}
