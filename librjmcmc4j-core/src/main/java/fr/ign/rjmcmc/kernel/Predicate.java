package fr.ign.rjmcmc.kernel;

import java.util.Vector;

public interface Predicate {

  boolean check(Vector<Double> val);

}
