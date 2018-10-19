package fr.ign.circlepacking.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import fr.ign.circlepacking.fixedconfiguration.IndexedCircle2D;
import fr.ign.mpp.DirectSampler;
import fr.ign.mpp.configuration.BirthDeathModification;
import fr.ign.mpp.configuration.GraphConfiguration;
import fr.ign.mpp.energy.AreaUnaryEnergy;
import fr.ign.mpp.energy.IntersectionAreaBinaryEnergy;
import fr.ign.mpp.kernel.KernelFactory;
import fr.ign.mpp.kernel.ObjectBuilder;
import fr.ign.parameters.Parameters;
import fr.ign.rjmcmc.acceptance.Acceptance;
import fr.ign.rjmcmc.acceptance.MetropolisAcceptance;
import fr.ign.rjmcmc.distribution.PoissonDistribution;
import fr.ign.rjmcmc.energy.BinaryEnergy;
import fr.ign.rjmcmc.energy.ConstantEnergy;
import fr.ign.rjmcmc.energy.MultipliesBinaryEnergy;
import fr.ign.rjmcmc.energy.MultipliesUnaryEnergy;
import fr.ign.rjmcmc.energy.UnaryEnergy;
import fr.ign.rjmcmc.kernel.Kernel;
import fr.ign.rjmcmc.kernel.KernelProbability;
import fr.ign.rjmcmc.kernel.KernelProposalRatio;
import fr.ign.rjmcmc.kernel.NullView;
import fr.ign.rjmcmc.kernel.Variate;
import fr.ign.rjmcmc.kernel.View;
import fr.ign.rjmcmc.sampler.GreenSampler;
import fr.ign.rjmcmc.sampler.Sampler;
import fr.ign.simulatedannealing.SimulatedAnnealing;
import fr.ign.simulatedannealing.endtest.EndTest;
import fr.ign.simulatedannealing.endtest.MaxIterationEndTest;
import fr.ign.simulatedannealing.schedule.GeometricSchedule;
import fr.ign.simulatedannealing.schedule.Schedule;
import fr.ign.simulatedannealing.temperature.SimpleTemperature;
import fr.ign.simulatedannealing.visitor.CompositeVisitor;
import fr.ign.simulatedannealing.visitor.OutputStreamVisitor;
import fr.ign.simulatedannealing.visitor.ShapefileVisitor;
import fr.ign.simulatedannealing.visitor.Visitor;

public class FixedPositionsCirclePacking {
  /**
   * Logger.
   */
  static Logger LOGGER = Logger.getLogger(FixedPositionsCirclePacking.class.getName());

  public static GraphConfiguration<IndexedCircle2D> create_configuration(Parameters p) {
    ConstantEnergy<IndexedCircle2D, IndexedCircle2D> c1 = new ConstantEnergy<>(p.getDouble("energy"));
    ConstantEnergy<IndexedCircle2D, IndexedCircle2D> c2 = new ConstantEnergy<>(p.getDouble("surface"));
    UnaryEnergy<IndexedCircle2D> u1 = new AreaUnaryEnergy<IndexedCircle2D>();
    MultipliesUnaryEnergy<IndexedCircle2D> u2 = new MultipliesUnaryEnergy<>(c1, u1);
    BinaryEnergy<IndexedCircle2D, IndexedCircle2D> b1 = new IntersectionAreaBinaryEnergy<>();
    BinaryEnergy<IndexedCircle2D, IndexedCircle2D> b2 = new MultipliesBinaryEnergy<>(c2, b1);
    // empty initial configuration
    GraphConfiguration<IndexedCircle2D> conf = new GraphConfiguration<>(u2, b2);
    conf.setSpecs("the_geom:Polygon,area:double");
    return conf;
  }

  static Map<Integer, List<Double>> fixedPositionsMap;

  static Sampler<GraphConfiguration<IndexedCircle2D>, BirthDeathModification<IndexedCircle2D>> create_sampler(Parameters p, RandomGenerator rng) {
    fixedPositionsMap = new HashMap<>();
    fixedPositionsMap.put(60, Arrays.asList(1.0, 1.0));
    fixedPositionsMap.put(123, Arrays.asList(1.1, 1.1));
    fixedPositionsMap.put(23425, Arrays.asList(2.0, 1.0));
    fixedPositionsMap.put(342, Arrays.asList(2.0, 2.0));
    fixedPositionsMap.put(32, Arrays.asList(1.0, 2.0));
    // map.put(0, Arrays.asList(1.0, 2.0));
    // map.put(1, Arrays.asList(2.0, 1.0));
    // map.put(2, Arrays.asList(2.0, 2.0));
    // map.put(3, Arrays.asList(1.0, 1.0));
    // map.put(4, Arrays.asList(1.1, 1.1));
    ObjectBuilder<IndexedCircle2D> builder = new ObjectBuilder<IndexedCircle2D>() {
      @Override
      public IndexedCircle2D build(double[] coordinates) {
        int index = (int) coordinates[0];
        List<Double> v = fixedPositionsMap.get(index);
        return new IndexedCircle2D(index, v.get(0), v.get(1), coordinates[1]);
      }

      @Override
      public int size() {
        return 2;
      }

      @Override
      public void setCoordinates(IndexedCircle2D t, double[] coordinates) {
        coordinates[0] = t.index;
        coordinates[1] = t.radius;
      }
    };
    double minradius = p.getDouble("minradius");
    double maxradius = p.getDouble("maxradius");
    final double p_birthdeath = p.getDouble("pbirthdeath");
    double p_radius = p.getDouble("pradius");
    // double p_birth = p.getDouble("pbirth");
    FixedPositionsUniformBirth<IndexedCircle2D> birth = new FixedPositionsUniformBirth<>(rng, new double[] { 0, minradius }, new double[] { 1, maxradius },
        builder, fixedPositionsMap);
    final PoissonDistribution distribution = new PoissonDistribution(rng, 4);
    // final Distribution distribution = new UniformDistribution(rng, 0, 5);
    DirectSampler<IndexedCircle2D, GraphConfiguration<IndexedCircle2D>, BirthDeathModification<IndexedCircle2D>> ds = new DirectSampler<>(distribution, birth);
    List<Kernel<GraphConfiguration<IndexedCircle2D>, BirthDeathModification<IndexedCircle2D>>> kernels = new ArrayList<>();
    KernelFactory<IndexedCircle2D, GraphConfiguration<IndexedCircle2D>, BirthDeathModification<IndexedCircle2D>> factory = new KernelFactory<>();
    KernelProbability<GraphConfiguration<IndexedCircle2D>, BirthDeathModification<IndexedCircle2D>> kpp = new KernelProbability<GraphConfiguration<IndexedCircle2D>, BirthDeathModification<IndexedCircle2D>>() {
      @Override
      public double probability(GraphConfiguration<IndexedCircle2D> c) {
        return p_birthdeath;
      }
    };
    KernelProposalRatio<GraphConfiguration<IndexedCircle2D>, BirthDeathModification<IndexedCircle2D>> kpr = new KernelProposalRatio<GraphConfiguration<IndexedCircle2D>, BirthDeathModification<IndexedCircle2D>>() {
      @Override
      public double probability(boolean d, GraphConfiguration<IndexedCircle2D> c) {
        if (d) {
          double p = (c.size() == 0) ? 1.0 : (c.size() == fixedPositionsMap.size()) ? 0.0 : distribution.pdfRatio(c.size(), c.size() + 1);
          LOGGER.log(Level.FINEST, "proposal direct " + c.size() + " = " + p);
          return p;
        }
        double p = (c.size() == 0) ? 0.0 : (c.size() == fixedPositionsMap.size()) ? 1.0 : distribution.pdfRatio(c.size(), c.size() - 1);
        LOGGER.log(Level.FINEST, "proposal reverse " + c.size() + " = " + p);
        return p;
      }
    };
    View<GraphConfiguration<IndexedCircle2D>, BirthDeathModification<IndexedCircle2D>> view0 = new NullView<>();
    view1 = new FixedPositionsUniformView<>(builder, fixedPositionsMap);
    Kernel<GraphConfiguration<IndexedCircle2D>, BirthDeathModification<IndexedCircle2D>> bdKernel = new Kernel<>(view0, view1, birth.getVariate(),
        new Variate(rng), birth.getTransform(), kpp, kpr, "BirthDeath");
    kernels.add(bdKernel);
    radiusTransform = new RadiusTransform(0.1, fixedPositionsMap);
    kernels.add(factory.make_uniform_modification_kernel(rng, builder, radiusTransform, p_radius, 1.0, "Radius"));
    Acceptance<SimpleTemperature> acceptance = new MetropolisAcceptance<>();
    Sampler<GraphConfiguration<IndexedCircle2D>, BirthDeathModification<IndexedCircle2D>> s = new GreenSampler<>(rng, ds, acceptance, kernels);
    return s;
  }

  static RadiusTransform radiusTransform;
  static FixedPositionsUniformView<IndexedCircle2D, GraphConfiguration<IndexedCircle2D>, BirthDeathModification<IndexedCircle2D>> view1;

  public static void main(String[] args) throws Exception {
    /*
     * < Retrieve the singleton instance of the parameters object... initialize the parameters object with the default
     * values provided... parse the command line to eventually change the values >
     */
    Parameters p = Parameters.unmarshall(new File("./src/main/resources/circlepacking_parameters.xml"));
    long seed = p.getLong("seed");
    RandomGenerator rng = new MersenneTwister(seed);
    /*
     * < Before launching the optimization process, we create all the required stuffs: a configuration, a sampler, a
     * schedule scheme and an end test >
     */
    GraphConfiguration<IndexedCircle2D> conf = create_configuration(p);
    Sampler<GraphConfiguration<IndexedCircle2D>, BirthDeathModification<IndexedCircle2D>> samp = create_sampler(p, rng);
    Schedule<SimpleTemperature> sch = new GeometricSchedule<>(new SimpleTemperature(p.getDouble("temp")), p.getDouble("deccoef"));
    EndTest end = new MaxIterationEndTest(p.getInteger("nbiter"));
    /*
     * < Build and initialize simple visitor which prints some data on the standard output >
     */
    Visitor<GraphConfiguration<IndexedCircle2D>, BirthDeathModification<IndexedCircle2D>> visitor = new OutputStreamVisitor<>(System.out);
    Visitor<GraphConfiguration<IndexedCircle2D>, BirthDeathModification<IndexedCircle2D>> shpVisitor = new ShapefileVisitor<IndexedCircle2D, GraphConfiguration<IndexedCircle2D>, BirthDeathModification<IndexedCircle2D>>(
        "./target/indexedcircle_result_" + seed, conf.getSpecs());
    List<Visitor<GraphConfiguration<IndexedCircle2D>, BirthDeathModification<IndexedCircle2D>>> list = new ArrayList<>();
    list.add(visitor);
    list.add(shpVisitor);
    CompositeVisitor<GraphConfiguration<IndexedCircle2D>, BirthDeathModification<IndexedCircle2D>> mVisitor = new CompositeVisitor<>(list);
    mVisitor.init(p.getInteger("nbdump"), p.getInteger("nbsave"));
    /*
     * < This is the way to launch the optimization process. Here, the magic happen... >
     */
    SimulatedAnnealing.optimize(rng, conf, samp, sch, end, mVisitor);
    System.out.println("BirthView");
    for (int id : fixedPositionsMap.keySet()) {
      System.out.println("" + id + " => " + view1.mapBirth.get(id));
    }
    System.out.println("DeathView");
    for (int id : fixedPositionsMap.keySet()) {
      System.out.println("" + id + " => " + view1.mapDeath.get(id));
    }
    System.out.println("RadiusTransform");
    for (int id : fixedPositionsMap.keySet()) {
      System.out.println("" + id + " => " + radiusTransform.map.get(id));
    }
    return;
  }
}
