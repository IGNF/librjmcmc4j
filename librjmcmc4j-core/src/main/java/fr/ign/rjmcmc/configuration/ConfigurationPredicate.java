package fr.ign.rjmcmc.configuration;

public interface ConfigurationPredicate<C extends Configuration<?,?>> {
	boolean check(C c);
}
