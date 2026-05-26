package fr.ign.mpp.kernel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Pair;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.random.RandomGenerator;

import fr.ign.rjmcmc.kernel.SimpleObject;

public class MultiObjectUniformBirth<T extends SimpleObject> implements ObjectSampler<T> {
    private record Values<T,U extends T>(Class<U> aClass, double value, T min, T max, ObjectBuilder<T> builder) {
    }

    List<Values<T,? extends T>> values = new ArrayList<>();
    EnumeratedDistribution<Class<? extends T>> distribution = null;
    Map<Class<? extends T>, UniformBirth<T>> births = new HashMap<>();
    Class<? extends T> latestChoice;

    public MultiObjectUniformBirth() {
    }

    public <U extends T> void add(RandomGenerator e, Class<U> aClass, double value, U min, U max, ObjectBuilder<T> builder) {
        this.values.add(new Values<>(aClass, value, min, max, builder));
        this.births.put(aClass, new UniformBirth<>(e, min, max, builder));
    }

    @Override
    public double sample(RandomGenerator e) {
        if (distribution == null) {
            distribution = new EnumeratedDistribution<>(
                    e,
                    this.values.stream().map(v -> new Pair<Class<? extends T>, Double>(v.aClass, v.value))
                            .collect(Collectors.toList()));
        }
        Class<? extends T> choice = distribution.sample();
        latestChoice = choice;
        return this.births.get(choice).sample(e);
    }

    @Override
    public T getObject() {
        return this.births.get(latestChoice).getObject();
    }

    @Override
    public double pdf(T t) {
        return this.births.get(t.getClass()).pdf(t);
    }

    public Set<Class<? extends T>> getClasses() {
        return births.keySet();
    }
    
    public UniformBirth<T> getBirth(Class<? extends T> aClass) {
        return this.births.get(aClass);
    }
}
