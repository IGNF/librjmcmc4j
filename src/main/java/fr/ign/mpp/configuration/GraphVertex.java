package fr.ign.mpp.configuration;

public class GraphVertex<T> {
	T value;
	double energy;

	public GraphVertex(T obj, double value) {
		this.value = obj;
		this.energy = value;
	}

	public double getEnergy() {
		return this.energy;
	}

	public T getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		return "" + this.value + " (" + this.energy + ")";
	}
}
