package fr.ign.rjmcmc.kernel;

import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.rjmcmc.configuration.Configuration;
import fr.ign.rjmcmc.configuration.Modification;

public class RandomApply {
	/**
	 * Logger.
	 */
	static Logger LOGGER = Logger.getLogger(RandomApply.class.getName());

	public static <C extends Configuration<C, M>, M extends Modification<C, M>> RandomApplyResult random_apply_impl(
			int i, int n, double x, List<Kernel<C, M>> t, KernelFunctor<C, M> f) {
		// LOGGER.info("random_apply_impl " + i + ", " + n + ", " + j + ", " + x
		// + ", " + t.size() + ", " + f);
		if (i == n) {
			RandomApplyResult result = new RandomApplyResult();
			result.kernelRatio = 0;
			result.kernelId = n;
			return result;
		}
		double y = x - t.get(i).probability();
		if (y > 0) {
			return random_apply_impl(i + 1, n, y, t, f);
		}
		// i = I;
		// LOGGER.info("APPLY KERNEL " + i + ", " + x);
		RandomApplyResult result = new RandomApplyResult();
		result.kernelRatio = f.apply(x, t.get(i));// tf.set(x,t.get(i));
		result.kernelId = i;
		return result;
	}

	public static <C extends Configuration<C, M>, M extends Modification<C, M>> double random_apply_normalisation(
			int i, int n, List<Kernel<C, M>> kernels) {
		if (i == n) {
			return 0;
		}
		return kernels.get(i).probability()
				+ random_apply_normalisation(i + 1, n, kernels);
	}

	public static <C extends Configuration<C, M>, M extends Modification<C, M>> RandomApplyResult randomApply(
			double x, List<Kernel<C, M>> t, KernelFunctor<C, M> f) {
		// LOGGER.info("randomApply " + i + ", " + x + ", " + t.size() + ", " +
		// f);
		double normalisation = random_apply_normalisation(0, t.size(), t);
		// LOGGER.info("\t normalisation = " + normalisation);
		return random_apply_impl(0, t.size(), x * normalisation, t, f);
	}
}
