package fr.ign.simulatedannealing.endtest;

import fr.ign.rjmcmc.configuration.Configuration;
import fr.ign.rjmcmc.configuration.Modification;
import fr.ign.rjmcmc.kernel.SimpleObject;
import fr.ign.rjmcmc.sampler.Sampler;
import fr.ign.simulatedannealing.temperature.Temperature;
/**
 * Stability end test.
 * @author Brasebin Mickaël
 * 
 * @version 1.0
 * @param <O> Simple Object type
 **/ 
public class StabilityEndTest<O extends SimpleObject> implements EndTest {
  int iterations;
  double lastEnergy;
  int iterationCount;
  double delta;
  int nbTotalIteration = 0;

  public StabilityEndTest(int n, double delta) {
    this.iterations = n;
    iterationCount = 0;
    lastEnergy = Double.POSITIVE_INFINITY;
  }

  @Override
  public <C extends Configuration<C, M>, M extends Modification<C, M>> boolean evaluate(C config, Sampler<C, M> sampler, Temperature t) {
    nbTotalIteration++;
    double currentEnergy = config.getEnergy();
    if (currentEnergy != 0 && Math.abs(currentEnergy - lastEnergy) > delta) {
      lastEnergy = currentEnergy;
      iterationCount = 0;
      return false;
    }
    iterationCount++;
    return iterationCount > iterations;
  }

  @Override
  public void stop() {
    this.iterationCount = 0;
  }
  
  public int getIterations(){
    return nbTotalIteration;
  }

  @Override
  public String toString() {
    return "" + this.iterations;
  }
}
