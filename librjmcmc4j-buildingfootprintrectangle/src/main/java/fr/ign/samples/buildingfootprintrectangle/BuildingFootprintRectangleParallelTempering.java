package fr.ign.samples.buildingfootprintrectangle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.random.RandomGenerator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFilter;

import fr.ign.geometry.IsoRectangle2D;
import fr.ign.geometry.Rectangle2D;
import fr.ign.geometry.Vector2D;
import fr.ign.geometry.transform.RectangleCornerTranslationTransform;
import fr.ign.geometry.transform.RectangleEdgeTranslationTransform;
import fr.ign.image.OrientedPlanarImageWrapper;
import fr.ign.image.OrientedView;
import fr.ign.mpp.DirectSampler;
import fr.ign.mpp.configuration.BirthDeathModification;
import fr.ign.mpp.configuration.GraphConfiguration;
import fr.ign.mpp.energy.ImageGradientUnaryEnergy;
import fr.ign.mpp.energy.IntersectionAreaBinaryEnergy;
import fr.ign.mpp.kernel.KernelFactory;
import fr.ign.mpp.kernel.ObjectBuilder;
import fr.ign.mpp.kernel.UniformBirth;
import fr.ign.parameters.Parameters;
import fr.ign.random.Random;
import fr.ign.rjmcmc.acceptance.Acceptance;
import fr.ign.rjmcmc.acceptance.MetropolisAcceptance;
import fr.ign.rjmcmc.distribution.PoissonDistribution;
import fr.ign.rjmcmc.energy.BinaryEnergy;
import fr.ign.rjmcmc.energy.ConstantEnergy;
import fr.ign.rjmcmc.energy.MinusUnaryEnergy;
import fr.ign.rjmcmc.energy.MultipliesBinaryEnergy;
import fr.ign.rjmcmc.energy.MultipliesUnaryEnergy;
import fr.ign.rjmcmc.energy.UnaryEnergy;
import fr.ign.rjmcmc.kernel.Kernel;
import fr.ign.rjmcmc.kernel.SimpleObject;
import fr.ign.rjmcmc.sampler.GreenSampler;
import fr.ign.rjmcmc.sampler.Sampler;
import fr.ign.simulatedannealing.ParallelTempering;
import fr.ign.simulatedannealing.endtest.EndTest;
import fr.ign.simulatedannealing.endtest.MaxIterationEndTest;
import fr.ign.simulatedannealing.schedule.GeometricSchedule;
import fr.ign.simulatedannealing.schedule.Schedule;
import fr.ign.simulatedannealing.temperature.SimpleTemperature;
import fr.ign.simulatedannealing.visitor.CompositeVisitor;
import fr.ign.simulatedannealing.visitor.OutputStreamVisitor;
import fr.ign.simulatedannealing.visitor.ShapefileVisitor;
import fr.ign.simulatedannealing.visitor.Visitor;

public class BuildingFootprintRectangleParallelTempering<O extends SimpleObject> {

  static void init_visitor(Parameters p, Visitor<?, ?> v) {
    v.init(p.getInteger("nbdump"), p.getInteger("nbsave"));
  }

  public static GraphConfiguration<Rectangle2D> create_configuration(Parameters p, OrientedView grad) {
    String mask_file = p.getString("mask");
    if (!mask_file.isEmpty()) {
    }
    ConstantEnergy<Rectangle2D, Rectangle2D> c1 = new ConstantEnergy<Rectangle2D, Rectangle2D>(p.getDouble("energy"));
    ConstantEnergy<Rectangle2D, Rectangle2D> c2 = new ConstantEnergy<Rectangle2D, Rectangle2D>(p.getDouble("ponderation_grad"));
    UnaryEnergy<Rectangle2D> u1 = new ImageGradientUnaryEnergy<Rectangle2D>(grad);
    UnaryEnergy<Rectangle2D> u2 = new MultipliesUnaryEnergy<Rectangle2D>(c2, u1);
    UnaryEnergy<Rectangle2D> u3 = new MinusUnaryEnergy<Rectangle2D>(c1, u2);
    ConstantEnergy<Rectangle2D, Rectangle2D> c3 = new ConstantEnergy<Rectangle2D, Rectangle2D>(p.getDouble("ponderation_surface"));
    BinaryEnergy<Rectangle2D, Rectangle2D> b1 = new IntersectionAreaBinaryEnergy<Rectangle2D>();
    BinaryEnergy<Rectangle2D, Rectangle2D> b2 = new MultipliesBinaryEnergy<Rectangle2D, Rectangle2D>(c3, b1);
    // c1 - c2*u1
    // u3 = c1 - u2 = energy - c2 * u1 = energy - ponderation_grad *
    // ImageGradientUnaryEnergy
    // b2 = c3*b1 = ponderation_surface * IntersectionAreaBinaryEnergy
    // empty initial configuration
    GraphConfiguration<Rectangle2D> conf = new GraphConfiguration<>(u3, b2);
    conf.setSpecs("the_geom:Polygon");
    return conf;
  }

  // ]
  // [building_footprint_rectangle_bbox_accessors
  static IsoRectangle2D get_bbox(Parameters p) {
    int x0 = p.getInteger("xmin");
    int x1 = p.getInteger("xmax");
    int y0 = p.getInteger("ymin");
    int y1 = p.getInteger("ymax");
    if (x0 > x1) {
      // swap(x0,x1);
      int tmp = x0;
      x0 = x1;
      x1 = tmp;
    }
    if (y0 > y1) {
      // swap(y0,y1);
      int tmp = y0;
      y0 = y1;
      y1 = tmp;
    }
    return new IsoRectangle2D(x0, y0, x1, y1);
  }

  // [building_footprint_rectangle_create_sampler
  static Sampler<GraphConfiguration<Rectangle2D>, BirthDeathModification<Rectangle2D>> create_sampler(Parameters p, RandomGenerator rng, final IsoRectangle2D r) {
    Vector2D v = new Vector2D(p.getDouble("maxsize"), p.getDouble("maxsize"));
    double maxratio = p.getDouble("maxratio");
    double minratio = 1 / maxratio;
    ObjectBuilder<Rectangle2D> builder = new ObjectBuilder<Rectangle2D>() {
      @Override
      public Rectangle2D build(double[] coordinates) {
        return new Rectangle2D(coordinates[0], coordinates[1], coordinates[2], coordinates[3], coordinates[4]);
      }

      @Override
      public int size() {
        return 5;
      }

      @Override
      public void setCoordinates(Rectangle2D t, double[] coordinates) {
        coordinates[0] = t.centerx;
        coordinates[1] = t.centery;
        coordinates[2] = t.normalx;
        coordinates[3] = t.normaly;
        coordinates[4] = t.ratio;
      }
    };
    Vector2D n = v.negate();
    UniformBirth<Rectangle2D> birth = new UniformBirth<Rectangle2D>(rng,
        new Rectangle2D(r.min().x(), r.min().y(), n.x(), n.y(), minratio),
        new Rectangle2D(r.max().x(), r.max().y(), v.x(), v.y(), maxratio), builder);

    double p_birthdeath = p.getDouble("pbirthdeath");
    double p_birth = p.getDouble("pbirth");
    double p_edge = 0.25 * p.getDouble("pedge");
    double p_corner = 0.25 * p.getDouble("pcorner");
    // double p_split_merge = p.getDouble("psplitmerge");
    // double p_split = p.getDouble("psplit");

    PoissonDistribution distribution = new PoissonDistribution(rng, p.getDouble("poisson"));

    DirectSampler<Rectangle2D, GraphConfiguration<Rectangle2D>, BirthDeathModification<Rectangle2D>> ds = new DirectSampler<>(distribution, birth);

    List<Kernel<GraphConfiguration<Rectangle2D>, BirthDeathModification<Rectangle2D>>> kernels = new ArrayList<>(3);
    KernelFactory<Rectangle2D, GraphConfiguration<Rectangle2D>, BirthDeathModification<Rectangle2D>> factory = new KernelFactory<>();
    kernels.add(factory.make_uniform_birth_death_kernel(rng, builder, birth, p_birthdeath, p_birth, "BirthDeath"));
    kernels.add(factory.make_uniform_modification_kernel(rng, builder, new RectangleEdgeTranslationTransform(0, minratio, maxratio), p_edge, "EdgeTrans0"));
    kernels.add(factory.make_uniform_modification_kernel(rng, builder, new RectangleEdgeTranslationTransform(1, minratio, maxratio), p_edge, "EdgeTrans1"));
    kernels.add(factory.make_uniform_modification_kernel(rng, builder, new RectangleEdgeTranslationTransform(2, minratio, maxratio), p_edge, "EdgeTrans2"));
    kernels.add(factory.make_uniform_modification_kernel(rng, builder, new RectangleEdgeTranslationTransform(3, minratio, maxratio), p_edge, "EdgeTrans3"));
    kernels.add(factory.make_uniform_modification_kernel(rng, builder, new RectangleCornerTranslationTransform(0), p_corner, "CornTrans0"));
    kernels.add(factory.make_uniform_modification_kernel(rng, builder, new RectangleCornerTranslationTransform(1), p_corner, "CornTrans1"));
    kernels.add(factory.make_uniform_modification_kernel(rng, builder, new RectangleCornerTranslationTransform(2), p_corner, "CornTrans2"));
    kernels.add(factory.make_uniform_modification_kernel(rng, builder, new RectangleCornerTranslationTransform(3), p_corner, "CornTrans3"));
    // kernels.add(Kernel.make_uniform_modification_kernel(builder, new RectangleSplitMergeTransform(), p_split_merge, p_split, 1, 2, "SplitMerge"));
    Acceptance<SimpleTemperature> acceptance = new MetropolisAcceptance<>();
    Sampler<GraphConfiguration<Rectangle2D>, BirthDeathModification<Rectangle2D>> s = new GreenSampler<>(rng, ds, acceptance, kernels);
    return s;
  }

  public static void main(String[] args) {
    /*
     * < Retrieve the singleton instance of the parameters object... initialize the parameters object with the default values provided... parse the command line
     * to eventually change the values >
     */
    Parameters p = initialize_parameters();
    RandomGenerator rng = Random.random();
    int nReplicas = p.getInteger("replicas");
    /*
     * < Input data is an image. We first retrieve from the parameters the region to process... clip the image to fit this region... and then compute the
     * gradient and build the attached view>
     */
    IsoRectangle2D bbox = get_bbox(p);
    String dsm_file = p.getString("dsm");
    OrientedView grad_view = new OrientedPlanarImageWrapper(dsm_file, p.getFloat("sigmaD"));
    clip_bbox(bbox, grad_view);
    /*
     * < Before launching the optimization process, we create all the required stuffs: a configuration, a sampler, a schedule scheme and an end test >
     */
    @SuppressWarnings("unchecked")
    GraphConfiguration<Rectangle2D>[] conf = new GraphConfiguration[nReplicas];
    for (int i = 0; i < nReplicas; i++) {
      conf[i] = create_configuration(p, grad_view);
    }
    Sampler<GraphConfiguration<Rectangle2D>, BirthDeathModification<Rectangle2D>> samp = create_sampler(p, rng, bbox);
    @SuppressWarnings("unchecked")
    Schedule<SimpleTemperature>[] sch = new Schedule[nReplicas];
    for (int i = 0; i < nReplicas; i++) {
      sch[i] = create_schedule(i, p);
    }
    EndTest end = create_end_test(p);
    /*
     * < Build and initialize simple visitor which prints some data on the standard output >
     */
    @SuppressWarnings("unchecked")
    Visitor<GraphConfiguration<Rectangle2D>, BirthDeathModification<Rectangle2D>>[] visitors = new Visitor[nReplicas];
    Visitor<GraphConfiguration<Rectangle2D>, BirthDeathModification<Rectangle2D>> visitor = new OutputStreamVisitor<>(System.out);
    Visitor<GraphConfiguration<Rectangle2D>, BirthDeathModification<Rectangle2D>> shpVisitor = new ShapefileVisitor<Rectangle2D, GraphConfiguration<Rectangle2D>, BirthDeathModification<Rectangle2D>>("building_parallel_result", conf[0].getSpecs(),

        new GeometryFilter() {
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
    List<Visitor<GraphConfiguration<Rectangle2D>, BirthDeathModification<Rectangle2D>>> list = new ArrayList<Visitor<GraphConfiguration<Rectangle2D>, BirthDeathModification<Rectangle2D>>>();
    list.add(visitor);
    list.add(shpVisitor);
    CompositeVisitor<GraphConfiguration<Rectangle2D>, BirthDeathModification<Rectangle2D>> mVisitor = new CompositeVisitor<>(list);
    init_visitor(p, mVisitor);
    visitors[0] = mVisitor;
    Visitor<GraphConfiguration<Rectangle2D>, BirthDeathModification<Rectangle2D>> shpVisitorMiddle = new ShapefileVisitor<Rectangle2D, GraphConfiguration<Rectangle2D>, BirthDeathModification<Rectangle2D>>("building_parallel_result_"+ (nReplicas / 2)+"_", conf[nReplicas / 2].getSpecs(),
        new GeometryFilter() {
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
    init_visitor(p, shpVisitorMiddle);

    visitors[nReplicas / 2] = shpVisitorMiddle;

    Visitor<GraphConfiguration<Rectangle2D>, BirthDeathModification<Rectangle2D>> shpVisitorLast = new ShapefileVisitor<Rectangle2D, GraphConfiguration<Rectangle2D>, BirthDeathModification<Rectangle2D>>("building_parallel_result_"+ (nReplicas - 1)+"_", conf[nReplicas - 1].getSpecs(),
        new GeometryFilter() {
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
    init_visitor(p, shpVisitorLast);
    visitors[nReplicas - 1] = shpVisitorLast;
    /*
     * < This is the way to launch the optimization process. Here, the magic happen... >
     */
    ParallelTempering.optimize(rng, conf, samp, sch, end, visitors);
    return;
  }

  private static void clip_bbox(IsoRectangle2D bbox, int x0, int y0, int x1, int y1) {
    bbox.setMinX((int) Math.max(bbox.min().x(), x0));
    bbox.setMinY((int) Math.max(bbox.min().y(), y0));
    bbox.setMaxX((int) Math.min(bbox.max().x(), x1));
    bbox.setMaxY((int) Math.min(bbox.max().y(), y1));
  }

  private static void clip_bbox(IsoRectangle2D bbox, OrientedView v) {
    clip_bbox(bbox, v.x0(), v.y0(), v.x0() + v.width(), v.y0() + v.height());
  }

  private static EndTest create_end_test(Parameters p) {
    return new MaxIterationEndTest(p.getInteger("nbiter"));
  }

  private static Schedule<SimpleTemperature> create_schedule(int i, Parameters p) {
    return new GeometricSchedule<SimpleTemperature>(new SimpleTemperature(Math.pow(p.getDouble("temp_exponent"), i)), p.getDouble("deccoef"));
  }

  private static Parameters initialize_parameters() {
    try {
      return Parameters.unmarshall(new File("./src/main/resources/building_parameters.xml"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
