package fr.ign.rjmcmc.kernel;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Before;
import org.junit.Test;

import fr.ign.mpp.configuration.BirthDeathModification;
import fr.ign.mpp.configuration.GraphConfiguration;
import fr.ign.random.Random;
import fr.ign.rjmcmc.configuration.Configuration;
import fr.ign.rjmcmc.distribution.Distribution;
import fr.ign.rjmcmc.distribution.PoissonDistribution;
import org.junit.Assert;

public class RandomApplyTest {

  @Before
  public void setUp() throws Exception {
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Test
  public void testRandom_apply_normalisation() {
   List<Kernel< GraphConfiguration<SimpleObject>,BirthDeathModification<SimpleObject>>> kernels = new ArrayList();
    // mock creation
    GraphConfiguration c = mock(GraphConfiguration.class);
    when(c.size()).thenReturn(0);
    double r0 = RandomApply.random_apply_normalisation(0, kernels.size(), kernels, c);
    Assert.assertEquals("Normalisation with no kernel", 0.0, r0, 0.05);
    RandomGenerator rng = Random.random();
    KernelProbability proposalBirthDeath = new KernelProbability() {
      @Override
      public double probability(Configuration c) {
        return 1.0;
      }
    };
    int lambda = 10;
    final int max = 100;
    final Distribution KDistribution = new PoissonDistribution(rng, lambda);
    KernelProposalRatio ratioBirthDeath = new KernelProposalRatio() {
      @Override
      public double probability(boolean direct, Configuration c) {
        GraphConfiguration gc = (GraphConfiguration) c;
        if (direct)
          return (gc.size() == 0) ? 1. : KDistribution.pdfRatio(gc.size(), gc.size() + 1);
        return (gc.size() == max) ? 1. : KDistribution.pdfRatio(gc.size(), gc.size() - 1);
      }
    };
    Kernel kBirthDeath = new Kernel(null, null, null, null, null, proposalBirthDeath, ratioBirthDeath, "BirthDeath");
    kernels.add(kBirthDeath);

    KernelProbability proposalModification = new KernelProbability() {
      @Override
      public double probability(Configuration c) {
        GraphConfiguration gc = (GraphConfiguration) c;
        return (gc.size() == 0) ? 0. : 1.0;
      }
    };
    KernelProposalRatio ratioModification = new KernelProposalRatio() {
      @Override
      public double probability(boolean direct, Configuration c) {
        return 1.;
      }
    };
    Kernel kModification = new Kernel(null, null, null, null, null, proposalModification, ratioModification, "Modification");
    kernels.add(kModification);
    double r1 = RandomApply.random_apply_normalisation(0, kernels.size(), kernels, c);
    Assert.assertEquals("Normalisation with 2 kernels but only 1 returns 1.", 1.0, r1, 0.05);
    when(c.size()).thenReturn(1);
    double r2 = RandomApply.random_apply_normalisation(0, kernels.size(), kernels, c);
    Assert.assertEquals("Normalisation with 2 kernels returning 1.", 2.0, r2, 0.05);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Test
  public void testRandomApply() {
    List kernels = new ArrayList();
    // mock creation
    GraphConfiguration c = mock(GraphConfiguration.class);
    when(c.size()).thenReturn(0);
    RandomGenerator rng = Random.random();
    rng.setSeed(3526);
    KernelProbability proposalBirthDeath = new KernelProbability() {
      @Override
      public double probability(Configuration c) {
        return 1.0;
      }
    };
    int lambda = 10;
    final int max = 100;
    final Distribution KDistribution = new PoissonDistribution(rng, lambda);
    KernelProposalRatio ratioBirthDeath = new KernelProposalRatio() {
      @Override
      public double probability(boolean direct, Configuration c) {
        GraphConfiguration gc = (GraphConfiguration) c;
        if (direct)
          return (gc.size() == 0) ? 1. : KDistribution.pdfRatio(gc.size(), gc.size() + 1);
        return (gc.size() == max) ? 1. : KDistribution.pdfRatio(gc.size(), gc.size() - 1);
      }
    };
    Kernel kBirthDeath = new Kernel(null, null, null, null, null, proposalBirthDeath, ratioBirthDeath, "BirthDeath");
    kernels.add(kBirthDeath);
    KernelProbability proposalModification = new KernelProbability() {
      @Override
      public double probability(Configuration c) {
        GraphConfiguration gc = (GraphConfiguration) c;
        return (gc.size() == 0) ? 0. : 1.0;
      }
    };
    KernelProposalRatio ratioModification = new KernelProposalRatio() {
      @Override
      public double probability(boolean direct, Configuration c) {
        return 1.;
      }
    };
    Kernel kModification = new Kernel(null, null, null, null, null, proposalModification, ratioModification, "Modification");
    kernels.add(kModification);
    KernelFunctor functor = mock(KernelFunctor.class);
    when(functor.apply(anyDouble(), any(Kernel.class))).thenReturn(1.0);
    when(functor.getConfig()).thenReturn(c);
    int nbiter = 100;
    for (int i = 0; i < nbiter; i++) {
      double x = rng.nextDouble();
      RandomApplyResult result0 = RandomApply.randomApply(x, kernels, functor);
      Assert.assertEquals(0, result0.kernelId);
    }
    when(c.size()).thenReturn(1);
    double sum = 0.;
    for (int i = 0; i < nbiter; i++) {
      double x = rng.nextDouble();
      RandomApplyResult result0 = RandomApply.randomApply(x, kernels, functor);
      System.out.println(x + " => " + result0.kernelId);
      if (result0.kernelId == 1)
        sum++;
    }
    System.out.println(sum / nbiter);
    Assert.assertEquals(0.5, sum / nbiter, 0.05);
  }
}
