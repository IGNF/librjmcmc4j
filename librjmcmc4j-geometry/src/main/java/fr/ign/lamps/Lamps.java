package fr.ign.lamps;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.operation.union.CascadedPolygonUnion;
import com.vividsolutions.jts.precision.GeometryPrecisionReducer;

import fr.ign.geometry.Circle2D;
import fr.ign.mpp.DirectSampler;
import fr.ign.mpp.configuration.BirthDeathModification;
import fr.ign.mpp.configuration.GraphConfiguration;
import fr.ign.mpp.kernel.KernelFactory;
import fr.ign.mpp.kernel.ObjectBuilder;
import fr.ign.mpp.kernel.UniformBirth;
import fr.ign.parameters.Parameters;
import fr.ign.rjmcmc.acceptance.Acceptance;
import fr.ign.rjmcmc.acceptance.MetropolisAcceptance;
import fr.ign.rjmcmc.distribution.PoissonDistribution;
import fr.ign.rjmcmc.energy.BinaryEnergy;
import fr.ign.rjmcmc.energy.CollectionEnergy;
import fr.ign.rjmcmc.energy.ConstantEnergy;
import fr.ign.rjmcmc.energy.MultipliesBinaryEnergy;
import fr.ign.rjmcmc.energy.MultipliesUnaryEnergy;
import fr.ign.rjmcmc.energy.UnaryEnergy;
import fr.ign.rjmcmc.kernel.Kernel;
import fr.ign.rjmcmc.sampler.GreenSampler;
import fr.ign.rjmcmc.sampler.Sampler;
import fr.ign.simulatedannealing.SalamonInitialSchedule;
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

public class Lamps {
  public static GraphConfiguration<Circle2D> create_configuration(final double minX, final double minY, final double maxX, final double maxY, Parameters p) {
    PrecisionModel pm = new PrecisionModel(10000);
    final GeometryFactory factory = new GeometryFactory(pm);
    final GeometryPrecisionReducer reducer = new GeometryPrecisionReducer(pm);
    Envelope envelope = new Envelope(minX, maxX, minY, maxY);
    final Polygon room = (Polygon) factory.toGeometry(envelope);
    UnaryEnergy<Circle2D> u;
    BinaryEnergy<Circle2D, Circle2D> b;
    CollectionEnergy<Circle2D> g;
    final double weight = p.getDouble("weight");
    boolean usePrecise = false;
    boolean useDiscrete = false;
    final double totalArea = room.getArea();
    if (usePrecise) {
      u = new ConstantEnergy<>(0);
      b = new ConstantEnergy<>(0);
      g = new CollectionEnergy<Circle2D>() {
        @Override
        public double getValue(Collection<Circle2D> t) {
          Collection<Polygon> collUnion = new ArrayList<>(t.size());
          for (Circle2D c : t) {
            collUnion.add((Polygon) reducer.reduce(c.toGeometry()));
          }
          CascadedPolygonUnion cpuUnion = new CascadedPolygonUnion(collUnion);
          Geometry union = cpuUnion.union().intersection(room);
          Polygon[] array = collUnion.toArray(new Polygon[collUnion.size()]);
          Collection<Polygon> collOverlap = new ArrayList<>(t.size());
          for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < i; j++) {
              Polygon pi = array[i];
              Polygon pj = array[j];
              if (pi.overlaps(pj)) {
                collOverlap.add((Polygon) reducer.reduce(pi.intersection(pj)));
              }
            }
          }
          CascadedPolygonUnion cpuOverlap = new CascadedPolygonUnion(collOverlap);
          Geometry overlap = cpuOverlap.union();
          double overlapArea = 0.0;
          if (overlap != null && !overlap.isEmpty()) {
            overlap = overlap.intersection(room);
            if (overlap != null && !overlap.isEmpty()) {
              overlapArea = overlap.getArea();
            }
          }

          return -(union.getArea() - weight * overlapArea) / totalArea;
        }
      };
    } else {
      if (useDiscrete) {
        u = new ConstantEnergy<>(0);
        b = new ConstantEnergy<>(0);
        g = new CollectionEnergy<Circle2D>() {
          @Override
          public double getValue(Collection<Circle2D> t) {
            double enlightenedArea = 0.0;
            double overlapArea = 0.0;
            for (int x = (int) minX; x < maxX; x++) {
              for (int y = (int) minY; y < maxY; y++) {
                int enlightened = 0;
                for (Circle2D l : t) {
                  // double result = Math.pow(l.center_x - x, 2) + Math.pow(l.center_y - y, 2);
                  // if (result <= Math.pow(l.radius, 2)) {
                  // enlightened++;
                  // }
                  Point p = factory.createPoint(new Coordinate(l.center_x, l.center_y));
                  if (l.toGeometry().contains(p)) {
                    enlightened++;
                  }
                }
                if (enlightened > 0) {
                  enlightenedArea++;
                  if (enlightened > 1) {
                    overlapArea++;
                  }
                }
              }
            }
            return -(enlightenedArea - weight * overlapArea) / totalArea;
          }
        };
      } else {
        UnaryEnergy<Circle2D> u1 = new UnaryEnergy<Circle2D>() {
          @Override
          public double getValue(Circle2D t) {
            return t.toGeometry().intersection(room).getArea()/totalArea;
          }
        };
        BinaryEnergy<Circle2D, Circle2D> b1 = new BinaryEnergy<Circle2D, Circle2D>() {
          @Override
          public double getValue(Circle2D t, Circle2D u) {
            Geometry intersection = t.toGeometry().intersection(u.toGeometry());
            if (intersection != null) {
              intersection = intersection.intersection(room);
              if (intersection != null) { return intersection.getArea()/totalArea; }
            }
            return 0;
          }
        };
        ConstantEnergy<Circle2D, Circle2D> cu = new ConstantEnergy<>(-1);
        u = new MultipliesUnaryEnergy<>(cu, u1);
        ConstantEnergy<Circle2D, Circle2D> cb = new ConstantEnergy<>(weight);
        b = new MultipliesBinaryEnergy<>(cb, b1);
        g = new ConstantEnergy<>(0);
      }
    }
    // empty initial configuration
    GraphConfiguration<Circle2D> conf = new GraphConfiguration<Circle2D>(u, b, g);
    conf.setSpecs("the_geom:Polygon");
    return conf;
  }

  static DirectSampler<Circle2D, GraphConfiguration<Circle2D>, BirthDeathModification<Circle2D>> create_sampler(double minX, double minY, double maxX,
      double maxY, Parameters p, RandomGenerator rng) {
    final double radius = p.getDouble("radius");
    ObjectBuilder<Circle2D> builder = new ObjectBuilder<Circle2D>() {
      @Override
      public Circle2D build(double[] coordinates) {
        return new Circle2D(coordinates[0], coordinates[1], radius);
      }

      @Override
      public int size() {
        return 2;
      }

      @Override
      public void setCoordinates(Circle2D t, double[] coordinates) {
        coordinates[0] = t.center_x;
        coordinates[1] = t.center_y;
      }
    };
    UniformBirth<Circle2D> birth = new UniformBirth<Circle2D>(rng, new Circle2D(minX, minY, radius), new Circle2D(maxX, maxY, radius), builder);
    PoissonDistribution distribution = new PoissonDistribution(rng, p.getDouble("poisson"));
    return new DirectSampler<>(distribution, birth);
  }

  static Sampler<GraphConfiguration<Circle2D>, BirthDeathModification<Circle2D>> create_sampler(Parameters p, RandomGenerator rng,
      DirectSampler<Circle2D, GraphConfiguration<Circle2D>, BirthDeathModification<Circle2D>> ds) {
    List<Kernel<GraphConfiguration<Circle2D>, BirthDeathModification<Circle2D>>> kernels = new ArrayList<>(3);
    KernelFactory<Circle2D, GraphConfiguration<Circle2D>, BirthDeathModification<Circle2D>> factory = new KernelFactory<>();
    double p_birthdeath = p.getDouble("pbirthdeath");
    UniformBirth<Circle2D> birth = (UniformBirth<Circle2D>) ds.getSampler();
    kernels.add(factory.make_uniform_birth_death_kernel(rng, birth.getBuilder(), birth, p_birthdeath, 1.0, "BirthDeath"));
    CircleCenterTransform cTransform = new CircleCenterTransform(p.getDouble("range"));
    double p_translate = p.getDouble("ptranslate");
    kernels.add(factory.make_uniform_modification_kernel(rng, birth.getBuilder(), cTransform, p_translate, 1.0, "Translate"));
    Acceptance<SimpleTemperature> acceptance = new MetropolisAcceptance<>();
    Sampler<GraphConfiguration<Circle2D>, BirthDeathModification<Circle2D>> s = new GreenSampler<>(rng, ds, acceptance, kernels);
    return s;
  }

  public static void main(String[] args) throws Exception {
    /*
     * < Retrieve the singleton instance of the parameters object... initialize the parameters object with the default values provided...
     * parse the command line to eventually change the values >
     */
    Parameters p = Parameters.unmarshall(new File("./src/main/resources/lamps_parameters.xml"));
    RandomGenerator rng = new MersenneTwister(p.getLong("seed"));
    /*
     * < Before launching the optimization process, we create all the required stuffs: a configuration, a sampler, a schedule scheme and an
     * end test >
     */
    double minX = p.getDouble("minx");
    double minY = p.getDouble("miny");
    double maxX = p.getDouble("maxx");
    double maxY = p.getDouble("maxy");
    GraphConfiguration<Circle2D> conf = create_configuration(minX, minY, maxX, maxY, p);
    DirectSampler<Circle2D, GraphConfiguration<Circle2D>, BirthDeathModification<Circle2D>> ds = create_sampler(minX, minY, maxX, maxY, p, rng);
    Sampler<GraphConfiguration<Circle2D>, BirthDeathModification<Circle2D>> samp = create_sampler(p, rng, ds);
    Schedule<SimpleTemperature> sch = new GeometricSchedule<>(new SimpleTemperature(p.getDouble("temp")), p.getDouble("deccoef"));
    EndTest end = new MaxIterationEndTest(p.getInteger("nbiter"));
    /*
     * < Build and initialize simple visitor which prints some data on the standard output >
     */
    List<Visitor<GraphConfiguration<Circle2D>, BirthDeathModification<Circle2D>>> list = new ArrayList<>();
    list.add(new OutputStreamVisitor<GraphConfiguration<Circle2D>, BirthDeathModification<Circle2D>>(System.out));
    list.add(new ShapefileVisitor<Circle2D, GraphConfiguration<Circle2D>, BirthDeathModification<Circle2D>>("./target/lamps_discrete", conf.getSpecs()));
    CompositeVisitor<GraphConfiguration<Circle2D>, BirthDeathModification<Circle2D>> mVisitor = new CompositeVisitor<>(list);
    mVisitor.init(p.getInteger("nbdump"), p.getInteger("nbsave"));
     double temp = SalamonInitialSchedule.salamon_initial_schedule(rng, ds, conf, 1000);
     System.out.println(temp);
    /*
     * < This is the way to launch the optimization process. Here, the magic happen... >
     */
    SimulatedAnnealing.optimize(rng, conf, samp, sch, end, mVisitor);
    return;
  }
}
