package fr.ign.rectanglecirclepacking;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.random.RandomGenerator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFilter;

import fr.ign.geometry.Circle2D;
import fr.ign.geometry.Primitive;
import fr.ign.geometry.Rectangle2D;
import fr.ign.mpp.DirectSampler;
import fr.ign.mpp.configuration.BirthDeathModification;
import fr.ign.mpp.configuration.GraphConfiguration;
import fr.ign.mpp.energy.IntersectionAreaBinaryEnergy;
import fr.ign.mpp.kernel.ObjectBuilder;
import fr.ign.mpp.kernel.ObjectSampler;
import fr.ign.mpp.kernel.UniformTypeView;
import fr.ign.parameters.Parameters;
import fr.ign.random.Random;
import fr.ign.rjmcmc.acceptance.MetropolisAcceptance;
import fr.ign.rjmcmc.distribution.PoissonDistribution;
import fr.ign.rjmcmc.energy.BinaryEnergy;
import fr.ign.rjmcmc.energy.ConstantEnergy;
import fr.ign.rjmcmc.energy.MultipliesBinaryEnergy;
import fr.ign.rjmcmc.kernel.DiagonalAffineTransform;
import fr.ign.rjmcmc.kernel.Kernel;
import fr.ign.rjmcmc.kernel.NullView;
import fr.ign.rjmcmc.kernel.Transform;
import fr.ign.rjmcmc.kernel.Variate;
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

public class RectangleCirclePacking {
  static void init_visitor(Parameters p, Visitor<?, ?> v) {
    v.init(p.getInteger("nbdump"), p.getInteger("nbsave"));
  }

  public static GraphConfiguration<Primitive> create_configuration(Parameters p) {
    ConstantEnergy<Primitive, Primitive> c1 = new ConstantEnergy<Primitive, Primitive>(p.getDouble("energy"));
    ConstantEnergy<Primitive, Primitive> c2 = new ConstantEnergy<Primitive, Primitive>(p.getDouble("surface"));
    BinaryEnergy<Primitive, Primitive> b1 = new IntersectionAreaBinaryEnergy<Primitive>();
    BinaryEnergy<Primitive, Primitive> b2 = new MultipliesBinaryEnergy<Primitive, Primitive>(c2, b1);
    // empty initial configuration
    GraphConfiguration<Primitive> conf = new GraphConfiguration<Primitive>(c1, b2);
    conf.setSpecs("the_geom:Polygon");
    return conf;
  }

  static ObjectBuilder<Primitive> circlebuilder = new ObjectBuilder<Primitive>() {
    @Override
    public Primitive build(double[] coordinates) {
      return new Circle2D(coordinates[0], coordinates[1], coordinates[2]);
    }

    @Override
    public int size() {
      return 3;
    }

    @Override
    public void setCoordinates(Primitive t, double[] coordinates) {
      Circle2D circle = (Circle2D) t;
      coordinates[0] = circle.center_x;
      coordinates[1] = circle.center_y;
      coordinates[2] = circle.radius;
    }
  };

  static ObjectBuilder<Primitive> rectanglebuilder = new ObjectBuilder<Primitive>() {
    @Override
    public Primitive build(double[] coordinates) {
      return new Rectangle2D(coordinates[0], coordinates[1], coordinates[2], coordinates[3], 1.);
    }

    @Override
    public int size() {
      return 4;
    }

    @Override
    public void setCoordinates(Primitive t, double[] coordinates) {
      Rectangle2D rect = (Rectangle2D) t;
      coordinates[0] = rect.centerx;
      coordinates[1] = rect.centery;
      coordinates[2] = rect.normalx;
      coordinates[3] = rect.normaly;
    }
  };

  public static class PrimitiveSampler implements ObjectSampler<Primitive> {
    RandomGenerator engine;
    double p_circle;
    Primitive object;
    Variate variate;
    Transform transformCircle;
    Transform transformRectangle;

    public PrimitiveSampler(RandomGenerator e, double p_circle, Transform transformCircle, Transform transformRectangle) {
      this.engine = e;
      this.p_circle = p_circle;
      this.transformCircle = transformCircle;
      this.transformRectangle = transformRectangle;
      this.variate = new Variate(e);
    }

    @Override
    public double sample(RandomGenerator e) {
      double[] var0;
      double[] val1;
      if (engine.nextDouble() < p_circle) {
        var0 = new double[3];
        val1 = new double[3];
        double phi = this.variate.compute(var0, 0);
        double jacob = this.transformCircle.apply(true, var0, val1);
        this.object = circlebuilder.build(val1);
        return phi / jacob;
      }
      var0 = new double[4];
      val1 = new double[4];
      double phi = this.variate.compute(var0, 0);
      double jacob = this.transformRectangle.apply(true, var0, val1);
      this.object = rectanglebuilder.build(val1);
      return phi / jacob;
    }

    @Override
    public double pdf(Primitive t) {
      if (Circle2D.class.isInstance(t)) {
        double[] val1 = new double[3];
        circlebuilder.setCoordinates(t, val1);
        double[] val0 = new double[3];
        double J10 = this.transformCircle.apply(false, val1, val0);
        double pdf = this.variate.pdf(val0, 0);
        return pdf * J10;
      }
      double[] val1 = new double[4];
      rectanglebuilder.setCoordinates(t, val1);
      double[] val0 = new double[4];
      double J10 = this.transformRectangle.apply(false, val1, val0);
      double pdf = this.variate.pdf(val0, 0);
      return pdf * J10;
    }

    @Override
    public Primitive getObject() {
      return this.object;
    }
  }

  static Sampler<GraphConfiguration<Primitive>, BirthDeathModification<Primitive>> create_sampler(Parameters p,
      RandomGenerator rng) {

    double minx = p.getDouble("minx");
    double miny = p.getDouble("miny");
    double maxx = p.getDouble("maxx");
    double maxy = p.getDouble("maxy");
    double minradius = p.getDouble("minradius");
    double maxradius = p.getDouble("maxradius");
    double[] v = new double[]{minx, miny, minradius};
    double[] d = new double[]{maxx - minx, maxy - miny, maxradius - minradius};
    DiagonalAffineTransform transformCircle = new DiagonalAffineTransform(d, v);
    v = new double[]{minx,miny,minradius,minradius};
    d = new double[]{maxx - minx, maxy - miny, (maxradius - minradius) / 2, (maxradius - minradius) / 2};
    DiagonalAffineTransform transformRectangle = new DiagonalAffineTransform(d, v);

    double p_circle = p.getDouble("pcircle");
    PrimitiveSampler objectSampler = new PrimitiveSampler(rng, p_circle, transformCircle, transformRectangle);
    PoissonDistribution distribution = new PoissonDistribution(rng, p.getDouble("poisson"));
    DirectSampler<Primitive, GraphConfiguration<Primitive>, BirthDeathModification<Primitive>> ds = new DirectSampler<>(
        distribution, objectSampler);

    double p_birthdeath = p.getDouble("pbirthdeath");
    double p_birth = p.getDouble("pbirth");
    List<Kernel<GraphConfiguration<Primitive>, BirthDeathModification<Primitive>>> kernels = new ArrayList<>(3);

    Kernel<GraphConfiguration<Primitive>, BirthDeathModification<Primitive>> kernel1 = new Kernel<GraphConfiguration<Primitive>, BirthDeathModification<Primitive>>(
        new NullView<GraphConfiguration<Primitive>, BirthDeathModification<Primitive>>(),
        new UniformTypeView<Primitive, GraphConfiguration<Primitive>, BirthDeathModification<Primitive>>(
            Circle2D.class, circlebuilder), new Variate(rng), new Variate(rng), transformCircle, p_birthdeath, p_birth);
    kernel1.setName("BirthDeathCircle");
    kernels.add(kernel1);
    Kernel<GraphConfiguration<Primitive>, BirthDeathModification<Primitive>> kernel2 = new Kernel<GraphConfiguration<Primitive>, BirthDeathModification<Primitive>>(
        new NullView<GraphConfiguration<Primitive>, BirthDeathModification<Primitive>>(),
        new UniformTypeView<Primitive, GraphConfiguration<Primitive>, BirthDeathModification<Primitive>>(
            Rectangle2D.class, rectanglebuilder), new Variate(rng), new Variate(rng), transformRectangle,
        1 - p_birthdeath, p_birth);
    kernel2.setName("BirthDeathRectangle");
    kernels.add(kernel2);

    // kernels.add(factory.make_uniform_modification_kernel(rng, builder,
    // new RectangleEdgeTranslationTransform(0, minratio, maxratio),
    // p_edge, "EdgeTrans0"));
    // kernels.add(factory.make_uniform_modification_kernel(rng, builder,
    // new RectangleEdgeTranslationTransform(1, minratio, maxratio),
    // p_edge, "EdgeTrans1"));
    // kernels.add(factory.make_uniform_modification_kernel(rng, builder,
    // new RectangleEdgeTranslationTransform(2, minratio, maxratio),
    // p_edge, "EdgeTrans2"));
    // kernels.add(factory.make_uniform_modification_kernel(rng, builder,
    // new RectangleEdgeTranslationTransform(3, minratio, maxratio),
    // p_edge, "EdgeTrans3"));
    // kernels.add(factory.make_uniform_modification_kernel(rng, builder,
    // new RectangleCornerTranslationTransform(0), p_corner,
    // "CornTrans0"));
    // kernels.add(factory.make_uniform_modification_kernel(rng, builder,
    // new RectangleCornerTranslationTransform(1), p_corner,
    // "CornTrans1"));
    // kernels.add(factory.make_uniform_modification_kernel(rng, builder,
    // new RectangleCornerTranslationTransform(2), p_corner,
    // "CornTrans2"));
    // kernels.add(factory.make_uniform_modification_kernel(rng, builder,
    // new RectangleCornerTranslationTransform(3), p_corner,
    // "CornTrans3"));
    // kernels.add(factory.make_uniform_modification_kernel(rng, builder,
    // new RectangleSplitMergeTransform(400), p_split_merge, p_split,
    // 1, 2, "SplitMerge"));
    Sampler<GraphConfiguration<Primitive>, BirthDeathModification<Primitive>> s = new GreenSampler<>(rng, ds,
        new MetropolisAcceptance<SimpleTemperature>(), kernels);
    return s;
  }

  public static void main(String[] args) throws IOException {
    /*
     * < Retrieve the singleton instance of the parameters object... initialize the parameters object with the default values provided... parse the command line
     * to eventually change the values >
     */
    Parameters p = initialize_parameters();
    /*
     * < Input data is an image. We first retrieve from the parameters the region to process... clip the image to fit this region... and then compute the
     * gradient and build the attached view>
     */
    RandomGenerator rng = Random.random();
    /*
     * < Before launching the optimization process, we create all the required stuffs: a configuration, a sampler, a schedule scheme and an end test >
     */
    GraphConfiguration<Primitive> conf = create_configuration(p);
    Sampler<GraphConfiguration<Primitive>, BirthDeathModification<Primitive>> samp = create_sampler(p, rng);
    Schedule<SimpleTemperature> sch = create_schedule(p);
    EndTest end = create_end_test(p);
    /*
     * < Build and initialize simple visitor which prints some data on the standard output >
     */
    Visitor visitor = new OutputStreamVisitor(System.out);
    Visitor shpVisitor = new ShapefileVisitor("./target/rectanglecircle_result", new GeometryFilter() {
      CoordinateFilter coordFilter = new CoordinateFilter() {
        @Override
        public void filter(Coordinate coord) {
          coord.y *= -1;
        }
      };

      @Override
      public void filter(Geometry geom) {
        geom.apply(coordFilter);
      }
    });
    List<Visitor> list = new ArrayList<Visitor>();
    list.add(visitor);
    list.add(shpVisitor);
    CompositeVisitor mVisitor = new CompositeVisitor(list);
    init_visitor(p, mVisitor);
    /*
     * < This is the way to launch the optimization process. Here, the magic happen... >
     */
    SimulatedAnnealing.optimize(Random.random(), conf, samp, sch, end, mVisitor);
    return;
  }

  private static EndTest create_end_test(Parameters p) {
    return new MaxIterationEndTest(p.getInteger("nbiter"));
  }

  private static Schedule<SimpleTemperature> create_schedule(Parameters p) {
    return new GeometricSchedule<SimpleTemperature>(new SimpleTemperature(p.getDouble("temp")), p.getDouble("deccoef"));
  }

  private static Parameters initialize_parameters() {
    try {
      return Parameters.unmarshall(new File("./src/main/resources/rectanglecirclepacking_parameters.xml"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
