package fr.ign.mpp.kernel;

import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;

import fr.ign.mpp.configuration.AbstractBirthDeathModification;
import fr.ign.mpp.configuration.AbstractGraphConfiguration;
import fr.ign.rjmcmc.kernel.SimpleObject;
import fr.ign.rjmcmc.kernel.View;

public class UniformView<T extends SimpleObject, C extends AbstractGraphConfiguration<T, C, M>, M extends AbstractBirthDeathModification<T, C, M>>
		implements View<C, M> {
	/**
	 * Logger.
	 */
	static Logger LOGGER = Logger.getLogger(UniformView.class.getName());

	int dimension = 0;
	ObjectBuilder<T> builder;
	int n = 1;

	public UniformView(ObjectBuilder<T> b) {
		this(b, 1);
	}

	public UniformView(ObjectBuilder<T> b, int n) {
		this.builder = b;
		this.dimension = b.size();
		this.n = n;
	}

	// private boolean contains(int[] d, int n, int sample) {
	// for (int i = 0; i < n; i++) {
	// if (d[i] == sample) {
	// return true;
	// }
	// }
	// return false;
	// }

	// @Override
	// public double apply(RandomGenerator e, Configuration<T> configuration,
	// Modification<T, Configuration<T>> modification, double[] out) {
	// // FIXME use e
	// int size = configuration.size();
	// if (size < this.n) {
	// return 0.;
	// }
	// // LOGGER.error(size + " " + this.n);
	// int denom = 1;
	// int d[] = new int[this.n];
	// for (int i = 0; i < this.n; ++i, --size) {
	// // int sample = (configuration.size() == 1) ? 0 :
	// sample(configuration.size());
	// // while (contains(d, i, sample)) {
	// // sample = (configuration.size() == 1) ? 0 :
	// sample(configuration.size());
	// // }
	// // d[i] = sample;
	// int sample = (size == 1) ? 0 : sample(size);
	// d[i] = sample;
	// for (int j = 0; j < i; ++j)
	// if (d[j] <= d[i])
	// ++d[i]; // skip already selected indices
	// for (int j = 0; j < i; ++j)
	// if (d[j] == d[i]) {
	// LOGGER.error("sampled " + d[i] + " twice");
	// }
	// Iterator<T> it = configuration.iterator();
	// for (int j = 0; j < d[i]; j++) {
	// it.next();
	// }
	// T t = it.next();
	// modification.insertDeath(t);
	// double[] outTmp = new double[this.builder.size()];
	// this.builder.setCoordinates(t, outTmp);
	// for (int j = 0; j < this.builder.size(); j++) {
	// out[i * this.builder.size() + j]= outTmp[j];
	// }
	// denom *= size;
	// }
	// // for (int i = 0; i < n; i++) {
	// // LOGGER.debug("d["+i+"] = " + d[i]);
	// // String s = "";
	// // for (int j = 0; j < this.dimension;j++) {
	// // s += out[i* this.dimension + j] + " ";
	// // }
	// // LOGGER.debug("\t" + s);
	// // }
	// return 1. / (double) denom;
	// // int sample = (n == 1) ? 0 : sample(n);
	// // Iterator<T> it = configuration.iterator();
	// // for (int i = 0; i < sample; i++) {
	// // it.next();
	// // }
	// // T t = it.next();
	// // modification.insertDeath(t);
	// // this.builder.setCoordinates(t, out);
	// // return 1. / n;
	// }

	private int sample(RandomGenerator rng, int n) {
		UniformIntegerDistribution distribution = new UniformIntegerDistribution(
				rng, 0, n - 1);
		// distribution.reseedRandomGenerator(System.currentTimeMillis());
		return distribution.sample();
	}

	// @Override
	// public double inversePdf(Configuration<T> configuration,
	// Modification<T, Configuration<T>> modification, double[] it) {
	// int beg = configuration.size() - modification.getDeath().size() + 1;
	// int end = beg + this.n;
	// int denom = 1;
	// int current = 0;
	// for (int size = beg; size < end; ++size) {
	// modification.insertBirth(this.builder.build(Arrays.copyOfRange(it,
	// current, current
	// + this.dimension)));
	// current += this.dimension;
	// denom *= size;
	// }
	// return 1. / (double) denom;
	// // T object = builder.build(it);
	// // modification.insertBirth(object);
	// // return 1. / (configuration.size() + modification.deltaSize());
	// }

	@Override
	public double select(boolean direct, RandomGenerator e, C conf, M modif) {
		return direct ? selectDeath(e, conf, modif) : selectBirth(e, conf,
				modif);
	}

	private double selectDeath(RandomGenerator e, C conf, M modif) {
		// FIXME use e
		int size = conf.size();
		if (size < this.n) {
			return 0.;
		}
		// LOGGER.error(size + " " + this.n);
		int denom = 1;
		int d[] = new int[this.n];
		for (int i = 0; i < this.n; ++i, --size) {
			d[i] = (size == 1) ? 0 : sample(e, size);
			for (int j = 0; j < i; ++j)
				if (d[j] <= d[i])
					++d[i]; // skip already selected indices
			for (int j = 0; j < i; ++j)
				if (d[j] == d[i]) {
					LOGGER.error("sampled " + d[i] + " twice");
				}
			Iterator<T> it = conf.iterator();
			for (int j = 0; j < d[i]; j++) {
				it.next();
			}
			T t = it.next();
			modif.insertDeath(t);
			// double[] outTmp = new double[this.builder.size()];
			// this.builder.setCoordinates(t, outTmp);
			// for (int j = 0; j < this.builder.size(); j++) {
			// out[i * this.builder.size() + j] = outTmp[j];
			// }
			denom *= size;
		}
		return 1. / (double) denom;
	}

	private double selectBirth(RandomGenerator e, C conf, M modif) {
		int beg = conf.size() - modif.getDeath().size() + 1;
		int end = beg + this.n;
		int denom = 1;
		// int current = 0;
		for (int size = beg; size < end; ++size) {
			Vector<Double> v = new Vector<Double>();
			v.setSize(this.dimension);
			for (int i = 0; i < this.dimension; i++) {
				v.set(i, 0.);
			}
			modif.insertBirth(this.builder.build(v));
			// current += this.dimension;
			denom *= size;
		}
		return 1. / (double) denom;
	}

	@Override
	public int dimension(boolean direct, C conf, M modif) {
		return this.dimension
				* (direct ? modif.getDeath().size() : modif.getBirth().size());
	}

	@Override
	public void get(C conf, M modif, Vector<Double> val0) {
		int index = 0;
		for (T t : modif.getDeath()) {
			this.builder.setCoordinates(t,
					val0.subList(index, index + this.dimension));
			index += this.dimension;
		}
	}

	@Override
	public void set(C conf, M modif, Vector<Double> val1) {
		int index = 0;
		// System.out.println("set " + modif.getBirth().size() + " dim = "
		// + this.dimension);
		for (T t : modif.getBirth()) {
			// System.out.println(val1.size() + " " + index + " "
			// + (index + this.dimension));
			t.set(val1.subList(index, index + this.dimension));
			index += this.dimension;
		}
	}
}
