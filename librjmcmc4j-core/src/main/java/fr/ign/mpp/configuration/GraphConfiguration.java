package fr.ign.mpp.configuration;

import java.util.HashMap;

import org.jgrapht.graph.SimpleWeightedGraph;

import fr.ign.rjmcmc.energy.BinaryEnergy;
import fr.ign.rjmcmc.energy.CollectionEnergy;
import fr.ign.rjmcmc.energy.UnaryEnergy;
import fr.ign.rjmcmc.kernel.SimpleObject;

public class GraphConfiguration<T extends SimpleObject>
		extends
		AbstractGraphConfiguration<T, GraphConfiguration<T>, BirthDeathModification<T>> {
	public GraphConfiguration(UnaryEnergy<T> unary_energy,
			BinaryEnergy<T, T> binary_energy) {
		this(unary_energy, binary_energy, null);
	}

	public GraphConfiguration(UnaryEnergy<T> unary_energy,
			BinaryEnergy<T, T> binary_energy, CollectionEnergy<T> global_energy) {
		this.unaryEnergy = unary_energy;
		this.binaryEnergy = binary_energy;
		this.globalEnergy = global_energy;
		this.unary = this.binary = this.global = 0;
		this.graph = new SimpleWeightedGraph<GraphVertex<T>, GraphEdge>(
				GraphEdge.class);
		this.vertexMap = new HashMap<T, GraphVertex<T>>();
		this.dirty = false;
		this.useCache = false;
	}

	@Override
	public BirthDeathModification<T> newModification() {
		return new BirthDeathModification<T>();
	}
}