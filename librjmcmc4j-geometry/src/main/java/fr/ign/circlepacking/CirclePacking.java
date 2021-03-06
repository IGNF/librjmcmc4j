package fr.ign.circlepacking;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import fr.ign.geometry.Circle2D;
import fr.ign.mpp.DirectSampler;
import fr.ign.mpp.configuration.BirthDeathModification;
import fr.ign.mpp.configuration.GraphConfiguration;
import fr.ign.mpp.energy.AreaUnaryEnergy;
import fr.ign.mpp.energy.IntersectionAreaBinaryEnergy;
import fr.ign.mpp.kernel.KernelFactory;
import fr.ign.mpp.kernel.ObjectBuilder;
import fr.ign.mpp.kernel.UniformBirth;
import fr.ign.parameters.XmlParameters;
import fr.ign.rjmcmc.acceptance.Acceptance;
import fr.ign.rjmcmc.acceptance.MetropolisAcceptance;
import fr.ign.rjmcmc.distribution.PoissonDistribution;
import fr.ign.rjmcmc.energy.BinaryEnergy;
import fr.ign.rjmcmc.energy.ConstantEnergy;
import fr.ign.rjmcmc.energy.MultipliesBinaryEnergy;
import fr.ign.rjmcmc.energy.MultipliesUnaryEnergy;
import fr.ign.rjmcmc.energy.UnaryEnergy;
import fr.ign.rjmcmc.kernel.Kernel;
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

public class CirclePacking {
  public static GraphConfiguration<Circle2D> create_configuration(XmlParameters p) {
    ConstantEnergy<Circle2D, Circle2D> c1 = new ConstantEnergy<>(p.getDouble("energy"));
    ConstantEnergy<Circle2D, Circle2D> c2 = new ConstantEnergy<>(p.getDouble("surface"));
    UnaryEnergy<Circle2D> u1 = new AreaUnaryEnergy<>();
    UnaryEnergy<Circle2D> u2 = new MultipliesUnaryEnergy<>(c1, u1);
    BinaryEnergy<Circle2D, Circle2D> b1 = new IntersectionAreaBinaryEnergy<>();
    BinaryEnergy<Circle2D, Circle2D> b2 = new MultipliesBinaryEnergy<>(c2, b1);
    // empty initial configuration
    boolean useArea = false;
    GraphConfiguration<Circle2D> conf = new GraphConfiguration<Circle2D>(useArea ? u2 : c1, b2);
    conf.setSpecs("the_geom:Polygon");
    return conf;
  }
  static DirectSampler<Circle2D, GraphConfiguration<Circle2D>, BirthDeathModification<Circle2D>> create_sampler(XmlParameters p, RandomGenerator rng) {
    ObjectBuilder<Circle2D> builder = new ObjectBuilder<Circle2D>() {
      @Override
      public Circle2D build(double[] coordinates) {
        return new Circle2D(coordinates[0], coordinates[1], coordinates[2]);
      }

      @Override
      public int size() {
        return 3;
      }

      @Override
      public void setCoordinates(Circle2D t, double[] coordinates) {
        coordinates[0] = t.center_x;
        coordinates[1] = t.center_y;
        coordinates[2] = t.radius;
      }
    };
    double minx = p.getDouble("minx");
    double miny = p.getDouble("miny");
    double maxx = p.getDouble("maxx");
    double maxy = p.getDouble("maxy");
    double minradius = p.getDouble("minradius");
    double maxradius = p.getDouble("maxradius");
    UniformBirth<Circle2D> birth = new UniformBirth<Circle2D>(rng, new Circle2D(minx, miny, minradius), new Circle2D(maxx, maxy, maxradius), builder);
    PoissonDistribution distribution = new PoissonDistribution(rng, p.getDouble("poisson"));
    return new DirectSampler<>(distribution, birth);    
  }
  static Sampler<GraphConfiguration<Circle2D>, BirthDeathModification<Circle2D>> create_sampler(XmlParameters p, RandomGenerator rng, DirectSampler<Circle2D, GraphConfiguration<Circle2D>, BirthDeathModification<Circle2D>> ds) {
    List<Kernel<GraphConfiguration<Circle2D>, BirthDeathModification<Circle2D>>> kernels = new ArrayList<>(3);
    KernelFactory<Circle2D, GraphConfiguration<Circle2D>, BirthDeathModification<Circle2D>> factory = new KernelFactory<>();
    double p_birthdeath = p.getDouble("pbirthdeath");
    UniformBirth<Circle2D> birth = (UniformBirth<Circle2D>) ds.getSampler();
    kernels.add(factory.make_uniform_birth_death_kernel(rng, birth.getBuilder(), birth, p_birthdeath, 1.0, "BirthDeath"));
    CircleRadiusTransform rTransform = new CircleRadiusTransform(p.getDouble("radiusrange"));
    double p_radius = p.getDouble("pradius");
    kernels.add(factory.make_uniform_modification_kernel(rng, birth.getBuilder(), rTransform, p_radius, 1.0, "Radius"));
    CircleCenterTransform cTransform = new CircleCenterTransform(p.getDouble("range"));
    double p_translate = p.getDouble("ptranslate");
    kernels.add(factory.make_uniform_modification_kernel(rng, birth.getBuilder(), cTransform, p_translate, 1.0, "Translate"));
    Acceptance<SimpleTemperature> acceptance = new MetropolisAcceptance<>();
    Sampler<GraphConfiguration<Circle2D>, BirthDeathModification<Circle2D>> s = new GreenSampler<>(rng, ds, acceptance, kernels);
    return s;
  }

  public static void main(String[] args) throws Exception {
    /*
     * < Retrieve the singleton instance of the parameters object... initialize the parameters object with the default
     * values provided... parse the command line to eventually change the values >
     */
    XmlParameters p = XmlParameters.unmarshall(new File("./src/main/resources/circlepacking_parameters.xml"));
    RandomGenerator rng = new MersenneTwister(42);
    /*
     * < Before launching the optimization process, we create all the required stuffs: a configuration, a sampler, a
     * schedule scheme and an end test >
     */
    GraphConfiguration<Circle2D> conf = create_configuration(p);
    DirectSampler<Circle2D, GraphConfiguration<Circle2D>, BirthDeathModification<Circle2D>> ds = create_sampler(p, rng);
    Sampler<GraphConfiguration<Circle2D>, BirthDeathModification<Circle2D>> samp = create_sampler(p, rng, ds);
    Schedule<SimpleTemperature> sch = new GeometricSchedule<>(new SimpleTemperature(p.getDouble("temp")), p.getDouble("deccoef"));
    EndTest end = new MaxIterationEndTest(p.getInteger("nbiter"));
    /*
     * < Build and initialize simple visitor which prints some data on the standard output >
     */
    List<Visitor<GraphConfiguration<Circle2D>, BirthDeathModification<Circle2D>>> list = new ArrayList<>();
    list.add(new OutputStreamVisitor<GraphConfiguration<Circle2D>, BirthDeathModification<Circle2D>>(System.out));
    list.add(new ShapefileVisitor<Circle2D, GraphConfiguration<Circle2D>, BirthDeathModification<Circle2D>>("./target/circle_result", conf.getSpecs()));
    CompositeVisitor<GraphConfiguration<Circle2D>, BirthDeathModification<Circle2D>> mVisitor = new CompositeVisitor<>(list);
    mVisitor.init(p.getInteger("nbdump"), p.getInteger("nbsave"));
//    double temp = SalamonInitialSchedule.salamon_initial_schedule(rng, ds, conf, 10000);
//    System.out.println(temp);
    /*
     * < This is the way to launch the optimization process. Here, the magic happen... >
     */
    SimulatedAnnealing.optimize(rng, conf, samp, sch, end, mVisitor);
    return;
  }
}
