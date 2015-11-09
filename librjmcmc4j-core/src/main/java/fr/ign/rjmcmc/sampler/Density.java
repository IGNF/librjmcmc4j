package fr.ign.rjmcmc.sampler;

import fr.ign.rjmcmc.configuration.Configuration;
import fr.ign.rjmcmc.configuration.Modification;

public interface Density<C extends Configuration<C, M>, M extends Modification<C, M>> {
	double pdfRatio(C config, M modif);
}
