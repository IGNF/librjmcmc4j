package fr.ign.rjmcmc.distribution;

import org.apache.commons.math3.random.RandomGenerator;

public class ConstantDistribution implements Distribution {
	int value;

	public ConstantDistribution(int c) {
		this.value = c;
	}

	@Override
	public double pdfRatio(int n0, int n1) {
		return 1;
	}

	@Override
	public double pdf(int n) {
		return 1;
	}

	@Override
	public int sample(RandomGenerator e) {
		return this.value;
	}
}
