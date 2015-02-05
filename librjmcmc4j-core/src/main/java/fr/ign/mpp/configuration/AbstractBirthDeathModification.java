package fr.ign.mpp.configuration;

import java.util.ArrayList;
import java.util.List;

import fr.ign.rjmcmc.configuration.Modification;
import fr.ign.rjmcmc.kernel.SimpleObject;

public abstract class AbstractBirthDeathModification<T extends SimpleObject, C extends ListConfiguration<T, C, M>, M extends Modification<C, M>>
		implements Modification<C, M> {
	/**
	 * A list of objects to be inserted to the configuration.
	 */
	List<T> birth = new ArrayList<>();

	/**
	 * @return the list of objects to be inserted to the configuration.
	 */
	public List<T> getBirth() {
		return this.birth;
	}

	/**
	 * A list of objects to be removed from the configuration.
	 */
	List<T> death = new ArrayList<>();

	/**
	 * @return the list of objects to be removed from the configuration.
	 */
	public List<T> getDeath() {
		return this.death;
	}

	/**
	 * Insert an object to the modification to be inserted into the
	 * configuration.
	 * 
	 * @param t
	 *            a new object.
	 */
	public void insertBirth(T t) {
		this.birth.add(t);
	}

	/**
	 * Insert an object to the modification to be removed from the
	 * configuration.
	 * 
	 * @param t
	 *            an object (it has to belong to the configuration)
	 */
	public void insertDeath(T t) {
		this.death.add(t);
	}

	/**
	 * @return the difference between the number of object to be inserted and
	 *         the number to be removed from the configuration.
	 */
	public int deltaSize() {
		return this.birth.size() - this.death.size();
	}

	/**
	 * Clear the modification.
	 */
	public void clear() {
		this.birth.clear();
		this.death.clear();
	}

	@Override
	public void apply(C c) {
		for (T d : this.getDeath()) {
			c.remove(d);
		}
		for (T b : this.getBirth()) {
			c.insert(b);
		}
	}
}
