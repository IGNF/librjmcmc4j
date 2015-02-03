package fr.ign.rjmcmc.configuration;

public interface ConfigurationModificationPredicate<C extends Configuration<C, M>, M extends Modification<C, M>> {
	boolean check(C c, M m);
}
