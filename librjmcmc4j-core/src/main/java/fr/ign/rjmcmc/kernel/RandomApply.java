package fr.ign.rjmcmc.kernel;

import java.util.List;

import fr.ign.rjmcmc.configuration.Configuration;
import fr.ign.rjmcmc.configuration.Modification;

public class RandomApply {
  public static <C extends Configuration<C, M>, M extends Modification<C, M>> RandomApplyResult random_apply_impl(int i, int n, double x, List<Kernel<C, M>> t,
      KernelFunctor<C, M> f) {
    if (i == n) {
      return new RandomApplyResult(0, n);
    }
    double p = t.get(i).probability(f.getConfig());
    double y = x - p;
    if (y > 0) {
      return random_apply_impl(i + 1, n, y, t, f);
    }
    return new RandomApplyResult(f.apply(x / p, t.get(i)), i);
  }

  public static <C extends Configuration<C, M>, M extends Modification<C, M>> double random_apply_normalisation(int i, int n, List<Kernel<C, M>> kernels, C c) {
    if (i == n) {
      return 0;
    }
    return kernels.get(i).probability(c) + random_apply_normalisation(i + 1, n, kernels, c);
  }

  public static <C extends Configuration<C, M>, M extends Modification<C, M>> RandomApplyResult randomApply(double x, List<Kernel<C, M>> t,
      KernelFunctor<C, M> f) {
    double normalisation = random_apply_normalisation(0, t.size(), t, f.getConfig());
//    System.out.println("normalisation = " + normalisation + " with " + t.size() + " ");
    return random_apply_impl(0, t.size(), x * normalisation, t, f);
  }
}
