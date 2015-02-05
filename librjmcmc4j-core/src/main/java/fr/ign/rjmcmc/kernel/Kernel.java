package fr.ign.rjmcmc.kernel;

import java.util.Vector;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;

import fr.ign.mpp.configuration.AbstractBirthDeathModification;
import fr.ign.mpp.configuration.AbstractGraphConfiguration;
import fr.ign.mpp.kernel.ObjectBuilder;
import fr.ign.mpp.kernel.UniformBirth;
import fr.ign.mpp.kernel.UniformView;
import fr.ign.rjmcmc.configuration.Configuration;
import fr.ign.rjmcmc.configuration.Modification;

public class Kernel<C extends Configuration<C, M>, M extends Modification<C, M>> {
	/**
	 * Logger.
	 */
	static Logger logger = Logger.getLogger(Kernel.class.getName());

	double p;
	double p01;
	double p10;
	View<C, M> view0;
	View<C, M> view1;
	Variate variate0;
	Variate variate1;
	Transform transform;
	int kernelId;

	/**
	 * Construct a new Kernel.
	 * 
	 * @param v0
	 *            a view
	 * @param v1
	 *            another view
	 * @param x0
	 *            a variate
	 * @param x1
	 *            another variate
	 * @param t
	 *            a transform
	 * @param p
	 *            probability to apply the kernel
	 * @param q
	 *            probability to choose the direct transform
	 */
	public Kernel(View<C, M> v0, View<C, M> v1, Variate x0, Variate x1,
			Transform t, double p, double q) {
		this.view0 = v0;
		this.view1 = v1;
		this.variate0 = x0;
		this.variate1 = x1;
		this.transform = t;
		this.p01 = p * q;
		this.p10 = p * (1 - q);
		this.p = p;
	}

	/**
	 * Construct a new Kernel.
	 * 
	 * @param v0
	 *            a view
	 * @param v1
	 *            another view
	 * @param x0
	 *            a variate
	 * @param x1
	 *            another variate
	 * @param t
	 *            a transform
	 * @param p
	 *            probability to apply the kernel
	 * @param q
	 *            probability to choose the direct transform
	 */
	public Kernel(View<C, M> v0, View<C, M> v1, Variate x0, Variate x1,
			Transform t, double p, double q, String name) {
		this.view0 = v0;
		this.view1 = v1;
		this.variate0 = x0;
		this.variate1 = x1;
		this.transform = t;
		this.p01 = p * q;
		this.p10 = p * (1 - q);
		this.p = p;
		this.name = name;
	}

	/**
	 * Construct a new Kernel.
	 * 
	 * @param v0
	 *            a view
	 * @param v1
	 *            another view
	 * @param x0
	 *            a variate
	 * @param x1
	 *            another variate
	 * @param t
	 *            a transform
	 */
	public Kernel(View<C, M> v0, View<C, M> v1, Variate x0, Variate x1,
			Transform t) {
		this(v0, v1, x0, x1, t, 1.0, 0.5);
	}

	public int getKernelId() {
		return this.kernelId;
	}

	/**
	 * Name of the kernel (mostly useful for logging and debug).
	 */
	String name = "kernel";

	/**
	 * @return the name of the kernel.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Change the name of the kernel.
	 * 
	 * @param n
	 *            the new name
	 */
	public void setName(String n) {
		this.name = n;
	}

	/**
	 * @return the probability of the kernel.
	 */
	public double probability() {
		return this.p;
	}

	// p is uniform between 0 and probability()
	/**
	 * Apply the kernel to propose a new modification.
	 * 
	 * @param e
	 *            a random generator
	 * @param probability
	 *            a probability
	 * @param c
	 *            the current configuration
	 * @param modif
	 *            the proposed configuration (modified by the function)
	 * @return the inverse variate sampling
	 */
	public double operator(RandomGenerator e, double probability, C c, M modif) {
		// modif.clear();
		Vector<Double> var0 = new Vector<>();
		Vector<Double> var1 = new Vector<>();
		Vector<Double> val0 = new Vector<>();
		Vector<Double> val1 = new Vector<>();
		if (probability < this.p01) { // branch probability : m_p01
			this.kernelId = 0;
			// returns the discrete probability that samples the portion of the
			// configuration that is
			// being modified (stored in the modif input)
			double J01 = view0.select(true, e, c, modif);
			// returns the discrete probability of the inverse view sampling,
			// arguments are constant
			// except val1 that is encoded in modif
			double J10 = view1.select(false, e, c, modif);
			// logger.info("modif " + modif.getBirth().size() + " " +
			// modif.getDeath().size());
			if (J01 == 0 || J10 == 0) {
				return 0; // abort : view sampling failed
			}
			int n0 = view0.dimension(true, c, modif);
			int n1 = view1.dimension(false, c, modif);
			int ntotal = transform.dimension(n0, n1); // n0+n1? max(n0+n1) ? ...
			// logger.info("modif " + modif.getBirth().size() + " " +
			// modif.getDeath().size() + " -> " +
			// n0 + " " + n1 + " " + ntotal);
			val0.setSize(n0);
			var0.setSize(ntotal - n0);
			val1.setSize(n1);
			var1.setSize(ntotal - n1);
			// logger.info("transform " + transform.getClass().getSimpleName() +
			// " sizes = " + n0 + ", " + n1 + ", " + ntotal);
			// logger.info("modif " + modif.getBirth().size() + " " +
			// modif.getDeath().size() + " -> " +
			// n0 + " " + n1 + " " + ntotal);
			// returns the continuous probability that samples the completion
			// variates
			double phi01 = variate0.compute(var0);
			if (phi01 == 0) {
				return 0; // abort : variate sampling failed
			}
			view0.get(c, modif, val0);
			// logger.info("val0 " + val0);
			// logger.info("var0 " + var0);
			double jacob = transform.apply(true, val0, var0, val1, var1); // computes
																			// val1
																			// from
																			// val0
			if (jacob == 0) {
				return 0;
			}
			view1.set(c, modif, val1);
			// returns the continuous probability of the variate sampling,
			// arguments are constant
			double phi10 = variate1.pdf(var1);
			// logger.info("val1 " + val1);
			// logger.info("var1 " + var1);
			// for (T t : modif.getBirth()) {
			// logger.info("birth = " + t);
			// }
			// for (T t : modif.getDeath()) {
			// logger.info("death = " + t);
			// }
			// logger.info("result = " + (jacob * (this.p10 * J10 * phi10) /
			// (this.p01 * J01 * phi01)));
			return jacob * (p10 * J10 * phi10) / (p01 * J01 * phi01);

			// // returns the discrete probability that samples the portion of
			// the configuration that is
			// // being modified (stored in the modif input)
			// double J01 = this.view0.apply(e, c, modif, val0);
			// if (J01 == 0) {
			// logger.debug("abort : view sampling failed");
			// return 0; // abort : view sampling failed
			// }
			// // returns the continuous probability that samples the completion
			// variates
			// double phi01 = this.variate0.compute(e, val0);
			// if (phi01 == 0) {
			// logger.debug("abort : variate sampling failed");
			// return 0; // abort : variate sampling failed
			// }
			// double jacob = this.transform.apply(val0, val1);// computes val1
			// from val0
			// // returns the continuous probability of the inverse variate
			// sampling, arguments are
			// constant
			// double phi10 = this.variate1.pdf(val1);
			// if (phi10 == 0) {
			// String string = "";
			// for (int i = val1.length - this.variate1.getDimension(); i <
			// val1.length; i++) {
			// string += val1[i] + " ";
			// }
			// logger.debug("abort : inverse variate pdf failed: " + string);
			// return 0;
			// }
			// // returns the discrete probability of the inverse view sampling,
			// arguments are constant
			// // except val1 that is encoded in modif
			// double J10 = this.view1.inversePdf(c, modif, val1);
			// if (J10 == 0) {
			// String string = "";
			// for (int i = val1.length - this.variate1.getDimension(); i <
			// val1.length; i++) {
			// string += val1[i] + " ";
			// }
			// logger.debug("abort : inverse view sampling failed: " + string);
			// return 0;
			// }
			// if
			// (transform.getClass().equals(RectangleSplitMergeTransform.class))
			// {
			// logger.debug("success");
			// Rectangle2D r1 = new Rectangle2D(val1[0], val1[1], val1[2],
			// val1[3], val1[4]);
			// Rectangle2D r2 = new Rectangle2D(val1[5], val1[6], val1[7],
			// val1[8], val1[9]);
			// Rectangle2D r = new Rectangle2D(val0[0], val0[1], val0[2],
			// val0[3], val0[4]);
			// logger.debug("r = " + r.toGeometry());
			// logger.debug("r1 = " + r1.toGeometry());
			// logger.debug("r2 = " + r2.toGeometry());
			// logger.debug("result = " + (jacob * (this.p10 * J10 * phi10) /
			// (this.p01 * J01 * phi01)));
			// }
			// return jacob * (this.p10 * J10 * phi10) / (this.p01 * J01 *
			// phi01);
		} else { // branch probability : m_p10
			this.kernelId = 1;
			// returns the discrete probability of the inverse view sampling,
			// arguments are constant
			// except val1 that is encoded in modif
			double J10 = view1.select(true, e, c, modif);
			// returns the discrete probability that samples the portion of the
			// configuration that is
			// being modified (stored in the modif input)
			double J01 = view0.select(false, e, c, modif);
			if (J01 == 0 || J10 == 0)
				return 0; // abort : view sampling failed
			int n1 = view1.dimension(true, c, modif);
			int n0 = view0.dimension(false, c, modif);
			int ntotal = transform.dimension(n0, n1); // n0+n1? max(n0+n1) ? ...
			val0.setSize(n0);
			var0.setSize(ntotal - n0);
			val1.setSize(n1);
			var1.setSize(ntotal - n1);
			// logger.info("transform " + transform.getClass().getSimpleName() +
			// " sizes = " + n0 + ", " + n1 + ", " + ntotal);
			// logger.info("modif " + modif.getBirth().size() + " " +
			// modif.getDeath().size() + " -> " +
			// n0 + " " + n1 + " " + ntotal);
			// returns the continuous probability that samples the completion
			// variates
			double phi10 = variate1.compute(var1);
			if (phi10 == 0) {
				return 0; // abort : variate sampling failed
			}
			view1.get(c, modif, val1);
			// logger.info("val1 " + val1);
			// logger.info("var1 " + var1);
			double jacob = transform.apply(false, val1, var1, val0, var0); // computes
																			// val0
																			// from
																			// val1
			if (jacob == 0) {
				return 0;
			}
			view0.set(c, modif, val0);
			// returns the continuous probability of the variate sampling,
			// arguments are constant
			double phi01 = variate0.pdf(var0);
			// logger.info("val0 " + val0);
			// logger.info("var0 " + var0);
			// for (T t : modif.getBirth()) {
			// logger.info("birth = " + t);
			// }
			// for (T t : modif.getDeath()) {
			// logger.info("death = " + t);
			// }
			// logger.info("result = " + (jacob * (this.p01 * J01 * phi01) /
			// (this.p10 * J10 * phi10)));
			return jacob * (this.p01 * J01 * phi01) / (this.p10 * J10 * phi10);

			// // returns the discrete probability that samples the portion of
			// the configuration that is
			// // being modified (stored in the modif input)
			// double J10 = this.view1.apply(e, c, modif, val1);
			// if (J10 == 0) {
			// logger.debug("abort : view sampling failed");
			// return 0; // abort : view sampling failed
			// }
			// // returns the continuous probability that samples the completion
			// variates
			// double phi10 = this.variate1.compute(e, val1);
			// if (phi10 == 0) {
			// logger.debug("abort : variate sampling failed");
			// return 0; // abort : variate sampling failed
			// }
			// double jacob = this.transform.inverse(val1, val0); // computes
			// val0 from val1
			// double phi01 = this.variate0.pdf(val0);
			// if (phi01 == 0) {
			// if
			// (transform.getClass().equals(RectangleSplitMergeTransform.class))
			// {
			// String string = "";
			// for (int i = val0.length - this.variate0.getDimension(); i <
			// val0.length; i++) {
			// string += val0[i] + " ";
			// }
			// Rectangle2D r1 = new Rectangle2D(val1[0], val1[1], val1[2],
			// val1[3], val1[4]);
			// Rectangle2D r2 = new Rectangle2D(val1[5], val1[6], val1[7],
			// val1[8], val1[9]);
			// Rectangle2D r = new Rectangle2D(val0[0], val0[1], val0[2],
			// val0[3], val0[4]);
			// logger.debug("abort : direct variate pdf failed: " + string);
			// logger.debug("r1 = " + r1.toGeometry());
			// logger.debug("r2 = " + r2.toGeometry());
			// logger.debug("r = " + r.toGeometry());
			// } else {
			// logger.debug("abort : direct variate pdf failed");
			// }
			// return 0;
			// }
			// // returns the discrete probability of the inverse view sampling,
			// arguments are constant
			// // except val0 that is encoded in modif
			// double J01 = this.view0.inversePdf(c, modif, val0); // returns
			// the continuous probability
			// of
			// // the inverse variate sampling, arguments are constant
			// return jacob * (this.p01 * J01 * phi01) / (this.p10 * J10 *
			// phi10);
		}
	}
}
