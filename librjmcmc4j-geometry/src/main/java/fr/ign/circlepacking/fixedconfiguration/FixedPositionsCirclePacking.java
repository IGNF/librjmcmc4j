package fr.ign.circlepacking.fixedconfiguration;

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

import fr.ign.circlepacking.test.RadiusTransform;
import fr.ign.mpp.kernel.ObjectBuilder;
import fr.ign.parameters.Parameters;
import fr.ign.rjmcmc.acceptance.Acceptance;
import fr.ign.rjmcmc.acceptance.MetropolisAcceptance;
import fr.ign.rjmcmc.distribution.Distribution;
import fr.ign.rjmcmc.distribution.UniformDistribution;
import fr.ign.rjmcmc.kernel.DiagonalAffineTransform;
import fr.ign.rjmcmc.kernel.Kernel;
import fr.ign.rjmcmc.kernel.KernelProbability;
import fr.ign.rjmcmc.kernel.KernelProposalRatio;
import fr.ign.rjmcmc.kernel.NullView;
import fr.ign.rjmcmc.kernel.Transform;
import fr.ign.rjmcmc.kernel.Variate;
import fr.ign.rjmcmc.kernel.View;
import fr.ign.rjmcmc.sampler.Density;
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

  static Map<Integer, List<Double>> map;

  static Sampler<CirclePackingFixedConfiguration, CirclePackingFixedModification> create_sampler(Parameters p, RandomGenerator rng) {
    ObjectBuilder<IndexedCircle2D> builder = new ObjectBuilder<IndexedCircle2D>() {
      @Override
      public IndexedCircle2D build(double[] coordinates) {
        int index = (int) coordinates[0];
        List<Double> v = map.get(index);
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
    // ObjectSampler<IndexedCircle2D> birth = new CirclePackingFixedObjectSampler(rng, new double[] { 0, minradius },
    // new double[] { 1, maxradius }, builder, map);
    double[] a = { 0, minradius };
    double[] d = { 1, maxradius - minradius };
    Transform transform = new DiagonalAffineTransform(d, a);
    // final Distribution distribution = new PoissonDistribution(rng, 4);
    final Distribution distribution = new UniformDistribution(rng, 0, map.size() - 1);
    Variate variate = new Variate(rng);
    Density<CirclePackingFixedConfiguration, CirclePackingFixedModification> ds = new CirclePackingFixedDensity(distribution, rng, transform, variate, builder, map);
    List<Kernel<CirclePackingFixedConfiguration, CirclePackingFixedModification>> kernels = new ArrayList<>();
    KernelProbability<CirclePackingFixedConfiguration, CirclePackingFixedModification> kpp = new KernelProbability<CirclePackingFixedConfiguration, CirclePackingFixedModification>() {
      @Override
      public double probability(CirclePackingFixedConfiguration c) {
        return p_birthdeath;
      }
    };
    KernelProposalRatio<CirclePackingFixedConfiguration, CirclePackingFixedModification> kpr = new KernelProposalRatio<CirclePackingFixedConfiguration, CirclePackingFixedModification>() {
      @Override
      public double probability(boolean d, CirclePackingFixedConfiguration c) {
        if (d) {
          double p = (c.size() == 0) ? 1.0 : (c.size() == map.size()) ? 0.0 : distribution.pdfRatio(c.size(), c.size() + 1);
          LOGGER.log(Level.FINEST, "proposal direct " + c.size() + " = " + p);
          return p;
        }
        double p = (c.size() == 0) ? 0.0 : (c.size() == map.size()) ? 1.0 : distribution.pdfRatio(c.size(), c.size() - 1);
        LOGGER.log(Level.FINEST, "proposal reverse " + c.size() + " = " + p);
        return p;
      }
    };
    View<CirclePackingFixedConfiguration, CirclePackingFixedModification> view0 = new NullView<>();
    view1 = new CirclePackingFixedView(builder);
    Kernel<CirclePackingFixedConfiguration, CirclePackingFixedModification> bdKernel = new Kernel<>(view0, view1, new Variate(rng), new Variate(rng), transform,
        kpp, kpr, "BirthDeath");
    kernels.add(bdKernel);
    radiusTransform = new RadiusTransform(0.1, map);
    CirclePackingFixedView rView0 = new CirclePackingFixedView(builder);
    CirclePackingFixedView rView1 = new CirclePackingFixedView(builder);
    Kernel<CirclePackingFixedConfiguration, CirclePackingFixedModification> rKernel = new Kernel<>(rView0, rView1, new Variate(rng), new Variate(rng),
        transform, p_radius, 1.0, "Radius");
    kernels.add(rKernel);
    Acceptance<SimpleTemperature> acceptance = new MetropolisAcceptance<>();
    Sampler<CirclePackingFixedConfiguration, CirclePackingFixedModification> s = new GreenSampler<>(rng, ds, acceptance, kernels);
    return s;
  }

  static RadiusTransform radiusTransform;
  static CirclePackingFixedView view1;

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
    map = new HashMap<>();
    map.put(60, Arrays.asList(1.0, 1.0));
    map.put(123, Arrays.asList(1.1, 1.1));
    map.put(23425, Arrays.asList(2.0, 1.0));
    map.put(342, Arrays.asList(2.0, 2.0));
    map.put(32, Arrays.asList(1.0, 2.0));
    // map.put(0, Arrays.asList(1.0, 2.0));
    // map.put(1, Arrays.asList(2.0, 1.0));
    // map.put(2, Arrays.asList(2.0, 2.0));
    // map.put(3, Arrays.asList(1.0, 1.0));
    // map.put(4, Arrays.asList(1.1, 1.1));
    CirclePackingFixedConfiguration conf = new CirclePackingFixedConfiguration(map, p.getDouble("energy"), p.getDouble("surface"));
    Sampler<CirclePackingFixedConfiguration, CirclePackingFixedModification> samp = create_sampler(p, rng);
    Schedule<SimpleTemperature> sch = new GeometricSchedule<>(new SimpleTemperature(p.getDouble("temp")), p.getDouble("deccoef"));
    EndTest end = new MaxIterationEndTest(p.getInteger("nbiter"));
    /*
     * < Build and initialize simple visitor which prints some data on the standard output >
     */
    Visitor<CirclePackingFixedConfiguration, CirclePackingFixedModification> visitor = new OutputStreamVisitor<>(System.out);
    Visitor<CirclePackingFixedConfiguration, CirclePackingFixedModification> shpVisitor = new ShapefileVisitor<IndexedCircle2D, CirclePackingFixedConfiguration, CirclePackingFixedModification>(
        "./target/indexedcircle_fixed_" + seed, "geom:Polygon,area:Double");
    List<Visitor<CirclePackingFixedConfiguration, CirclePackingFixedModification>> list = new ArrayList<>();
    list.add(visitor);
    list.add(shpVisitor);
    CompositeVisitor<CirclePackingFixedConfiguration, CirclePackingFixedModification> mVisitor = new CompositeVisitor<>(list);
    mVisitor.init(p.getInteger("nbdump"), p.getInteger("nbsave"));
    /*
     * < This is the way to launch the optimization process. Here, the magic happen... >
     */
    SimulatedAnnealing.optimize(rng, conf, samp, sch, end, mVisitor);
    // System.out.println("BirthView");
    // for (int id : map.keySet()) {
    // System.out.println("" + id + " => " + view1.mapBirth.get(id));
    // }
    // System.out.println("DeathView");
    // for (int id : map.keySet()) {
    // System.out.println("" + id + " => " + view1.mapDeath.get(id));
    // }
    // System.out.println("RadiusTransform");
    // for (int id : map.keySet()) {
    // System.out.println("" + id + " => " + radiusTransform.map.get(id));
    // }
    return;
  }
}
