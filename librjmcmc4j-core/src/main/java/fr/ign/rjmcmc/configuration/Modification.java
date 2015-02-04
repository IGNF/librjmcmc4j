package fr.ign.rjmcmc.configuration;

public interface Modification<C extends Configuration<C, M>, M extends Modification<C, M>> {
	public void apply(C c);
}
