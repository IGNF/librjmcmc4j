package fr.ign.mpp.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
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

abstract class AbstractGraphConfiguration<T extends SimpleObject, C extends AbstractGraphConfiguration<T, C, M>, M extends AbstractBirthDeathModification<T, C, M>>
		implements ListConfiguration<T, C, M> {

	/**
	 * Logger.
	 */
	static Logger LOGGER = Logger.getLogger(GraphConfiguration.class.getName());

	public UnaryEnergy<T> unaryEnergy;
	public BinaryEnergy<T, T> binaryEnergy;
	public CollectionEnergy<T> globalEnergy;
	double unary;

	@Override
	public double getUnaryEnergy() {
		return this.unary;
	}

	double binary;

	@Override
	public double getBinaryEnergy() {
		return this.binary;
	}

	double global;

	@Override
	public double getGlobalEnergy() {
		return this.global;
	}

	double proposedGlobal;

	@Override
	public double getEnergy() {
		if (this.dirty) {
			if (this.globalEnergy != null) {
				if (useCache) {
					this.global = this.proposedGlobal;
				} else {
					this.global = this.globalEnergy.getValue(this.vertexMap
							.keySet());
				}
			}
			this.dirty = false;
		}
		return this.unary + this.binary + this.global;
	}

	SimpleWeightedGraph<GraphVertex<T>, GraphEdge> graph;

	public SimpleWeightedGraph<GraphVertex<T>, GraphEdge> getGraph() {
		return graph;
	}

	Map<T, GraphVertex<T>> vertexMap;

	boolean dirty;
	boolean useCache;

	@Override
	public void clear() {
		this.unary = this.binary = this.global = 0;
		Set<GraphVertex<T>> vertices = new HashSet<GraphVertex<T>>(
				this.graph.vertexSet());
		this.graph.removeAllVertices(vertices);
		this.vertexMap.clear();
		this.dirty = false;
	}

	@Override
	public void insert(T obj) {
		if (useCache)
			this.insertWithCache(obj);
		else {
			double value = unaryEnergy.getValue(obj);
			GraphVertex<T> n = new GraphVertex<>(obj, value);
			this.vertexMap.put(obj, n);
			this.unary += value;
			this.graph.addVertex(n);
			for (GraphVertex<T> v : this.graph.vertexSet()) {
				if (v == n) {
					continue;
				}
				double e = binaryEnergy.getValue(obj, v.getValue());
				if (e == 0) {
					continue;
				}
				GraphEdge newEdge = this.graph.addEdge(n, v);
				this.graph.setEdgeWeight(newEdge, e);
				this.binary += e;
			}
		}
		this.dirty = true;
	}

	public void insertWithCache(T obj) {
		Double value = cacheUnaryBirth.get(obj);
		if (value == null) {
			value = new Double(this.unaryEnergy.getValue(obj));
		}
		GraphVertex<T> n = new GraphVertex<>(obj, value.doubleValue());
		this.vertexMap.put(obj, n);
		this.unary += value.doubleValue();
		this.graph.addVertex(n);
		Map<T, Double> map = cacheBinaryBirth.get(obj);
		if (map == null) {
			return;
		}
		for (GraphVertex<T> v : this.graph.vertexSet()) {
			if (v == n) {
				continue;
			}
			Double e = map.get(v.value);
			if (e == null) {
				continue;
			}
			GraphEdge newEdge = this.graph.addEdge(n, v);
			this.graph.setEdgeWeight(newEdge, e.doubleValue());
			this.binary += e.doubleValue();
		}
	}

	@Override
	public void remove(T v) {
		GraphVertex<T> vertex = this.vertexMap.get(v);
		Set<GraphEdge> edges = this.graph.edgesOf(vertex);
		for (GraphEdge e : edges) {
			this.binary -= this.graph.getEdgeWeight(e);
		}
		this.unary -= vertex.getEnergy();
		boolean removed = this.graph.removeVertex(vertex);
		if (!removed) {
			LOGGER.info("REMOVE FAILED FOR " + v);
			System.exit(1);
		}
		this.vertexMap.remove(v);
		this.dirty = true;
	}

	@Override
	public int size() {
		return this.graph.vertexSet().size();
	};

	@Override
	public Iterator<T> iterator() {
		return new GraphIterator(this.graph.vertexSet().iterator());
	}

	class GraphIterator implements Iterator<T> {
		Iterator<GraphVertex<T>> iterator;

		public GraphIterator(Iterator<GraphVertex<T>> iterator) {
			this.iterator = iterator;
		}

		@Override
		public boolean hasNext() {
			return this.iterator.hasNext();
		}

		@Override
		public T next() {
			return this.iterator.next().getValue();
		}

		@Override
		public void remove() {
			this.iterator.remove();
		}
	}

	@Override
	public double deltaEnergy(M modif) {
		return deltaBirth(modif) + deltaDeath(modif) + deltaGlobal(modif);
	}

	private Map<T, Double> cacheUnaryBirth = new HashMap<T, Double>();
	private Map<T, Map<T, Double>> cacheBinaryBirth = new HashMap<T, Map<T, Double>>();

	@Override
	public double getUnaryEnergy(T o) {
		return this.unaryEnergy.getValue(o);
	}

	private long timeDeltaBirth;

	public double deltaBirth(M modif) {
		if (useCache)
			return this.deltaBirthWithCache(modif);
		double delta = 0;
		long start = System.currentTimeMillis();
		for (int index = 0; index < modif.getBirth().size(); index++) {
			T b = modif.getBirth().get(index);
			double value = this.unaryEnergy.getValue(b);
			delta += value;
			for (GraphVertex<T> v : this.graph.vertexSet()) {
				if (!modif.getDeath().contains(v.value)) {
					value = this.binaryEnergy.getValue(b, v.value);
					delta += value;
				}
			}
			for (int index2 = index + 1; index2 < modif.getBirth().size(); index2++) {
				T other = modif.getBirth().get(index2);
				value = this.binaryEnergy.getValue(b, other);
				delta += value;
			}
		}
		long end = System.currentTimeMillis();
		this.timeDeltaBirth = (end - start);
		return delta;
	}

	public double deltaBirthWithCache(M modif) {
		cacheUnaryBirth.clear();
		cacheBinaryBirth.clear();
		double delta = 0;
		long start = System.currentTimeMillis();
		for (int index = 0; index < modif.getBirth().size(); index++) {
			T b = modif.getBirth().get(index);
			double value = this.unaryEnergy.getValue(b);
			delta += value;
			cacheUnaryBirth.put(b, new Double(value));
			for (GraphVertex<T> v : this.graph.vertexSet()) {
				if (!modif.getDeath().contains(v.value)) {
					value = this.binaryEnergy.getValue(b, v.value);
					delta += value;
					if (value != 0) {
						Map<T, Double> map = cacheBinaryBirth.get(b);
						if (map == null) {
							map = new HashMap<T, Double>();
							cacheBinaryBirth.put(b, map);
						}
						map.put(v.value, new Double(value));
					}
				}
			}
			for (int index2 = index + 1; index2 < modif.getBirth().size(); index2++) {
				T other = modif.getBirth().get(index2);
				value = this.binaryEnergy.getValue(b, other);
				delta += value;
				if (value != 0) {
					Map<T, Double> map = cacheBinaryBirth.get(b);
					if (map == null) {
						map = new HashMap<T, Double>();
						cacheBinaryBirth.put(b, map);
					}
					map.put(other, new Double(value));
				}
			}
		}
		long end = System.currentTimeMillis();
		this.timeDeltaBirth = (end - start);
		return delta;
	}

	private long timeDeltaDeath;

	public long getTimeDeltaBirth() {
		return this.timeDeltaBirth;
	}

	public long getTimeDeltaDeath() {
		return this.timeDeltaDeath;
	}

	public double deltaDeath(M modif) {
		double delta = 0;
		long start = System.currentTimeMillis();
		for (int index = 0; index < modif.getDeath().size(); index++) {
			T death = modif.getDeath().get(index);
			GraphVertex<T> v = this.vertexMap.get(death);
			delta -= v.getEnergy();
			Set<GraphEdge> edges = this.graph.edgesOf(v);
			for (GraphEdge e : edges) {
				GraphVertex<T> target = this.graph.getEdgeTarget(e);
				if (target == v) {
					target = this.graph.getEdgeSource(e);
				}
				boolean found = false;
				for (int index2 = index + 1; index2 < modif.getDeath().size()
						&& !found; index2++) {
					T death2 = modif.getDeath().get(index2);
					GraphVertex<T> dv = this.vertexMap.get(death2);
					found = (dv == target);
				}
				if (!found) {
					delta -= this.graph.getEdgeWeight(e);
				}
			}
		}
		long end = System.currentTimeMillis();
		this.timeDeltaBirth = (end - start);
		return delta;
	}

	public double deltaGlobal(M modif) {
		if (this.globalEnergy == null) {
			return 0;
		}
		List<T> collection = new ArrayList<>();
		collection.addAll(this.vertexMap.keySet());
		collection.removeAll(modif.getDeath());
		collection.addAll(modif.getBirth());
		this.proposedGlobal = this.globalEnergy.getValue(collection);
		return this.proposedGlobal - this.global;
	}

	// public void apply(Modification m) {
	// BirthDeathModification<T> modif = (BirthDeathModification<T>) m;
	// for (T d : modif.getDeath()) {
	// this.remove(d);
	// }
	// for (T b : modif.getBirth()) {
	// this.insert(b);
	// }
	// if (useCache) {
	// this.global = this.proposedGlobal;
	// }
	// }

	@Override
	public String toString() {
		String result = "Graph with " + this.graph.vertexSet().size()
				+ " vertices\n";
		for (GraphVertex<T> v : this.graph.vertexSet()) {
			result += v.getValue().toString() + "\n";
		}
		for (GraphVertex<T> v : this.graph.vertexSet()) {
			result += v.getEnergy() + "\n";
		}
		for (GraphEdge e : this.graph.edgeSet()) {
			result += e.toString() + "\n";
		}
		for (GraphEdge e : this.graph.edgeSet()) {
			result += this.graph.getEdgeWeight(e) + "\n";
		}
		return result;
	}

	String specs;

	public void setSpecs(String specs) {
		this.specs = specs;
	}

	public String getSpecs() {
		return specs;
	}
}
