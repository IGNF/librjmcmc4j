package fr.ign.circlepacking;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.math3.random.RandomGenerator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFilter;

import fr.ign.geometry.Circle2D;
import fr.ign.mpp.DirectSampler;
import fr.ign.mpp.configuration.BirthDeathModification;
import fr.ign.mpp.configuration.GraphConfiguration;
import fr.ign.mpp.energy.IntersectionAreaBinaryEnergy;
import fr.ign.mpp.kernel.KernelFactory;
import fr.ign.mpp.kernel.ObjectBuilder;
import fr.ign.mpp.kernel.UniformBirth;
import fr.ign.parameters.Parameters;
import fr.ign.random.Random;
import fr.ign.rjmcmc.acceptance.MetropolisAcceptance;
import fr.ign.rjmcmc.distribution.PoissonDistribution;
import fr.ign.rjmcmc.energy.BinaryEnergy;
import fr.ign.rjmcmc.energy.ConstantEnergy;
import fr.ign.rjmcmc.energy.MultipliesBinaryEnergy;
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
  static void init_visitor(Parameters p, Visitor<?, ?> v) {
    v.init(p.getInteger("nbdump"), p.getInteger("nbsave"));
  }

  public static GraphConfiguration<Circle2D> create_configuration(Parameters p) {
    ConstantEnergy<Circle2D, Circle2D> c1 = new ConstantEnergy<Circle2D, Circle2D>(p.getDouble("energy"));
    ConstantEnergy<Circle2D, Circle2D> c2 = new ConstantEnergy<Circle2D, Circle2D>(p.getDouble("surface"));
    BinaryEnergy<Circle2D, Circle2D> b1 = new IntersectionAreaBinaryEnergy<Circle2D>();
    BinaryEnergy<Circle2D, Circle2D> b2 = new MultipliesBinaryEnergy<Circle2D, Circle2D>(c2, b1);
    // empty initial configuration
    GraphConfiguration<Circle2D> conf = new GraphConfiguration<Circle2D>(c1, b2);
    conf.setSpecs("the_geom:Polygon");
    return conf;
  }

  static Sampler<GraphConfiguration<Circle2D>, BirthDeathModification<Circle2D>> create_sampler(Parameters p,
      RandomGenerator rng) {
    ObjectBuilder<Circle2D> builder = new ObjectBuilder<Circle2D>() {
      @Override
      public Circle2D build(Vector<Double> coordinates) {
        return new Circle2D(coordinates.get(0), coordinates.get(1), coordinates.get(2));
      }

      @Override
      public int size() {
        return 3;
      }

      @Override
      public void setCoordinates(Circle2D t, List<Double> coordinates) {
        coordinates.set(0, t.center_x);
        coordinates.set(1, t.center_y);
        coordinates.set(2, t.radius);
      }
    };
    double minx = p.getDouble("minx");
    double miny = p.getDouble("miny");
    double maxx = p.getDouble("maxx");
    double maxy = p.getDouble("maxy");
    double minradius = p.getDouble("minradius");
    double maxradius = p.getDouble("maxradius");
    UniformBirth<Circle2D> birth = new UniformBirth<Circle2D>(rng, new Circle2D(minx, miny, minradius), new Circle2D(
        maxx, maxy, maxradius), builder);
    double p_birthdeath = p.getDouble("pbirthdeath");
    double p_birth = p.getDouble("pbirth");
    // double p_edge = 0.25 * p.getDouble("pedge");
    // double p_corner = 0.25 * p.getDouble("pcorner");
    // double p_split_merge = p.getDouble("psplitmerge");
    // double p_split = p.getDouble("psplit");
    PoissonDistribution distribution = new PoissonDistribution(rng, p.getDouble("poisson"));
    DirectSampler<Circle2D, GraphConfiguration<Circle2D>, BirthDeathModification<Circle2D>> ds = new DirectSampler<>(
        distribution, birth);
    List<Kernel<GraphConfiguration<Circle2D>, BirthDeathModification<Circle2D>>> kernels = new ArrayList<>(3);
    KernelFactory<Circle2D, GraphConfiguration<Circle2D>, BirthDeathModification<Circle2D>> factory = new KernelFactory<>();
    kernels.add(factory.make_uniform_birth_death_kernel(rng, builder, birth, p_birthdeath, p_birth));
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
    Sampler<GraphConfiguration<Circle2D>, BirthDeathModification<Circle2D>> s = new GreenSampler<>(rng, ds,
        new MetropolisAcceptance<SimpleTemperature>(), kernels);
    return s;
  }

  public static void main(String[] args) throws IOException {
    /*
     * < Retrieve the singleton instance of the parameters object... initialize
     * the parameters object with the default values provided... parse the
     * command line to eventually change the values >
     */
    Parameters p = initialize_parameters();
    /*
     * < Input data is an image. We first retrieve from the parameters the
     * region to process... clip the image to fit this region... and then
     * compute the gradient and build the attached view>
     */
    RandomGenerator rng = Random.random();
    /*
     * < Before launching the optimization process, we create all the required
     * stuffs: a configuration, a sampler, a schedule scheme and an end test >
     */
    GraphConfiguration<Circle2D> conf = create_configuration(p);
    Sampler<GraphConfiguration<Circle2D>, BirthDeathModification<Circle2D>> samp = create_sampler(p, rng);
    Schedule<SimpleTemperature> sch = create_schedule(p);
    EndTest end = create_end_test(p);
    /*
     * < Build and initialize simple visitor which prints some data on the
     * standard output >
     */
    Visitor visitor = new OutputStreamVisitor(System.out);
    Visitor shpVisitor = new ShapefileVisitor("./target/circle_result", new GeometryFilter() {
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
     * < This is the way to launch the optimization process. Here, the magic
     * happen... >
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
      return Parameters.unmarshall(new File("./src/main/resources/circlepacking_parameters.xml"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
  // // ]
  // UnaryEnergy<Circle2D> unaryEnergy = null;
  // BinaryEnergy<Circle2D, Circle2D> binaryEnergy = null;
  // DiagonalAffineTransform transform = null;
  // RandomGenerator e;
  // Transform[] transforms;
  //
  // public CirclePacking(File dsmFile, float sigmaD, double energy, double
  // ponderationGrad, double ponderationSurface, double maxsize,
  // double maxratio) throws IOException {
  // OrientedView grad_view = new OrientedPlanarImageWrapperImageIO(dsmFile,
  // sigmaD);
  // ConstantEnergy<Circle2D, Circle2D> c1 = new ConstantEnergy<Circle2D,
  // Circle2D>(energy);
  // ConstantEnergy<Circle2D, Circle2D> c2 = new ConstantEnergy<Circle2D,
  // Circle2D>(ponderationGrad);
  // UnaryEnergy<Circle2D> u1 = new
  // ImageGradientUnaryEnergy<Circle2D>(grad_view);
  // UnaryEnergy<Circle2D> u2 = new MultipliesUnaryEnergy<Circle2D>(c2, u1);
  // UnaryEnergy<Circle2D> u3 = new MinusUnaryEnergy<Circle2D>(c1, u2);
  // ConstantEnergy<Circle2D, Circle2D> c3 = new ConstantEnergy<Circle2D,
  // Circle2D>(ponderationSurface);
  // BinaryEnergy<Circle2D, Circle2D> b1 = new
  // IntersectionAreaBinaryEnergy<Circle2D>();
  // BinaryEnergy<Circle2D, Circle2D> b2 = new MultipliesBinaryEnergy<Circle2D,
  // Circle2D>(c3, b1);
  // // c1 - c2*u1
  // // u3 = c1 - u2 = energy - c2 * u1 = energy - ponderation_grad *
  // // ImageGradientUnaryEnergy
  // // b2 = c3*b1 = ponderation_surface * IntersectionAreaBinaryEnergy
  // // empty initial configuration
  // this.unaryEnergy = u3;
  // this.binaryEnergy = b2;
  // double minx = grad_view.x0();
  // double miny = grad_view.y0();
  // double maxx = grad_view.x0() + grad_view.width();
  // double maxy = grad_view.y0() + grad_view.height();
  // double minratio = 1 / maxratio;
  // Vector<Double> coordinates = new Vector<>(5);
  // coordinates.setSize(5);
  // coordinates.set(0, minx);
  // coordinates.set(1, miny);
  // coordinates.set(2, -maxsize);
  // coordinates.set(3, -maxsize);
  // coordinates.set(4, minratio);
  // Vector<Double> d = new Vector<>(5);
  // d.setSize(5);
  // d.set(0, maxx - minx);
  // d.set(1, maxy - miny);
  // d.set(2, 2 * maxsize);
  // d.set(3, 2 * maxsize);
  // d.set(4, maxratio - minratio);
  // this.transform = new DiagonalAffineTransform(d, coordinates);
  // this.e = Random.random();
  // this.transforms = new Transform[8];
  // this.transforms[0] = new RectangleEdgeTranslationTransform(0, minratio,
  // maxratio);
  // this.transforms[1] = new RectangleEdgeTranslationTransform(1, minratio,
  // maxratio);
  // this.transforms[2] = new RectangleEdgeTranslationTransform(2, minratio,
  // maxratio);
  // this.transforms[3] = new RectangleEdgeTranslationTransform(3, minratio,
  // maxratio);
  // this.transforms[4] = new RectangleCornerTranslationTransform(0);
  // this.transforms[5] = new RectangleCornerTranslationTransform(1);
  // this.transforms[6] = new RectangleCornerTranslationTransform(2);
  // this.transforms[7] = new RectangleCornerTranslationTransform(3);
  // }
  // public double getEnergy(double[] parameterArray) {
  // Assert.assertTrue("Number of values in the parameter array is not a multiple of 5",
  // parameterArray.length % 5 == 0);
  // GraphConfiguration<Circle2D> conf = new
  // GraphConfiguration<Circle2D>(this.unaryEnergy, this.binaryEnergy);
  // for (int index = 0; index < parameterArray.length;) {
  // BirthDeathModification<Circle2D> modif = new BirthDeathModification<>();
  // Circle2D rectangle = new Circle2D(parameterArray[index++] *
  // this.transform.getMat()[0] + this.transform.getDelta()[0],
  // parameterArray[index++] * this.transform.getMat()[1] +
  // this.transform.getDelta()[1], parameterArray[index++]
  // * this.transform.getMat()[2] + this.transform.getDelta()[2],
  // parameterArray[index++]
  // * this.transform.getMat()[3] + this.transform.getDelta()[3],
  // parameterArray[index++]
  // * this.transform.getMat()[4] + this.transform.getDelta()[4]);
  // modif.insertBirth(rectangle);
  // /* double delta = */conf.deltaEnergy(modif);
  // // conf.apply(modif);
  // modif.apply(conf);
  // }
  // // System.out.println("unary = " + conf.getUnaryEnergy());
  // // System.out.println("binary = " + conf.getBinaryEnergy());
  // return conf.getEnergy();
  // }
  //
  // public void writeToShapefile(double[] parameterArray, File outputFile) {
  // Assert.assertTrue("Number of values in the parameter array is not a multiple of 5",
  // parameterArray.length % 5 == 0);
  // GraphConfiguration<Circle2D> conf = new
  // GraphConfiguration<Circle2D>(this.unaryEnergy, this.binaryEnergy);
  // BirthDeathModification<Circle2D> modif = new BirthDeathModification<>();
  // for (int index = 0; index < parameterArray.length;) {
  // Circle2D rectangle = new Circle2D(parameterArray[index++] *
  // this.transform.getMat()[0] + this.transform.getDelta()[0],
  // parameterArray[index++] * this.transform.getMat()[1] +
  // this.transform.getDelta()[1], parameterArray[index++]
  // * this.transform.getMat()[2] + this.transform.getDelta()[2],
  // parameterArray[index++]
  // * this.transform.getMat()[3] + this.transform.getDelta()[3],
  // parameterArray[index++]
  // * this.transform.getMat()[4] + this.transform.getDelta()[4]);
  // modif.insertBirth(rectangle);
  // }
  // /* double delta = */conf.deltaEnergy(modif);
  // // conf.apply(modif);
  // modif.apply(conf);
  // ShapefileWriter shpWriter = new ShapefileWriter(new GeometryFilter() {
  // CoordinateFilter coordFilter = new CoordinateFilter() {
  // @Override
  // public void filter(Coordinate coord) {
  // coord.y *= -1;
  // }
  // };
  //
  // @Override
  // public void filter(Geometry geom) {
  // geom.apply(coordFilter);
  // }
  // });
  // shpWriter.writeShapefile(outputFile, conf);
  // }
  //
  // public double[] readFromShapefile(File inputFile) {
  // ShapefileReader shpReader = new ShapefileReader(new GeometryFilter() {
  // CoordinateFilter coordFilter = new CoordinateFilter() {
  // @Override
  // public void filter(Coordinate coord) {
  // coord.y *= -1;
  // }
  // };
  //
  // @Override
  // public void filter(Geometry geom) {
  // geom.apply(coordFilter);
  // }
  // });
  // Circle2D[] rectangleArray = shpReader.readShapefile(inputFile);
  // double[] result = new double[rectangleArray.length * 5];
  // for (int index = 0; index < rectangleArray.length; index++) {
  // Circle2D r = rectangleArray[index];
  // result[index * 5] = r.centerx * this.transform.getMatInv()[0] +
  // this.transform.getDeltaInv()[0];
  // result[index * 5 + 1] = r.centery * this.transform.getMatInv()[1] +
  // this.transform.getDeltaInv()[1];
  // result[index * 5 + 2] = r.normalx * this.transform.getMatInv()[2] +
  // this.transform.getDeltaInv()[2];
  // result[index * 5 + 3] = r.normaly * this.transform.getMatInv()[3] +
  // this.transform.getDeltaInv()[3];
  // result[index * 5 + 4] = r.ratio * this.transform.getMatInv()[4] +
  // this.transform.getDeltaInv()[4];
  // }
  // // for (int index = 0; index < result.length; index++) {
  // // Assert.assertTrue("Result is not in [0,1] : " + result[index],
  // // result[index] >= 0
  // // && result[index] <= 1.0);
  // // }
  // return result;
  // }
  //
  // /**
  // * @param parameterArray
  // * @return
  // */
  // public double[] transform(double[] parameterArray) {
  // Assert.assertTrue("Number of values in the parameter array is not a multiple of 5",
  // parameterArray.length % 5 == 0);
  // Vector<Double> var0 = new Vector<>();
  // Vector<Double> var1 = new Vector<>();
  // Vector<Double> val0 = new Vector<>();
  // Vector<Double> val1 = new Vector<>();
  // int n0 = 5;
  // int n1 = 5;
  // int index = (int) Math.floor((this.transforms.length * e.nextDouble()));
  // int ntotal = transforms[index].dimension(n0, n1);
  // val0.setSize(n0);
  // var0.setSize(ntotal - n0);
  // val1.setSize(n1);
  // var1.setSize(ntotal - n1);
  // for (int i = 0; i < 5; i++) {
  // val0.set(i, parameterArray[i] * this.transform.getMat()[i] +
  // this.transform.getDelta()[i]);
  // }
  // for (int i = 0; i < ntotal - n0; i++) {
  // var0.set(i, e.nextDouble());
  // }
  // transforms[index].apply(true, val0, var0, val1, var1); // computes val1
  // // from val0
  // double[] result = new double[5];
  // for (int i = 0; i < 5; i++) {
  // result[i] = val1.get(i).doubleValue() * this.transform.getMatInv()[i] +
  // this.transform.getDeltaInv()[i];
  // }
  // return result;
  // }
  //
  // OrientedView grad_view;
  // GraphConfiguration<Circle2D> conf;
  // Sampler<GraphConfiguration<Circle2D>, BirthDeathModification<Circle2D>>
  // samp;
  // Schedule<SimpleTemperature> sch;
  // EndTest end;
  // CompositeVisitor mVisitor;
  //
  // public CirclePacking(long seed, File dsmFile, float sigmaD, double energy,
  // double ponderationGrad, double ponderationSurface,
  // double maxsize, double maxratio, double pbirthdeath, double pedge, double
  // pcorner, double psplitmerge, double temp,
  // double deccoef, int nbiter, double poisson) throws IOException {
  // Parameters p = new Parameters();
  //
  // this.grad_view = new OrientedPlanarImageWrapperImageIO(dsmFile, sigmaD);
  //
  // p.set("energy", energy);
  // p.set("ponderation_grad", ponderationGrad);
  // p.set("ponderation_surface", ponderationSurface);
  // this.conf = (GraphConfiguration<Circle2D>) create_configuration(p,
  // grad_view);
  //
  // p.set("maxsize", maxsize);
  // p.set("maxratio", maxratio);
  // p.set("pbirthdeath", pbirthdeath);
  // p.set("pedge", pedge);
  // p.set("pcorner", pcorner);
  // p.set("psplitmerge", psplitmerge);
  //
  // p.set("pbirth", 0.5);
  // p.set("psplit", 0.5);
  //
  // p.set("poisson", poisson);
  //
  // p.set("xmin", 0);
  // p.set("ymin", 0);
  //
  // p.set("xmax", 1000000);
  // p.set("ymax", 1000000);
  //
  // RandomGenerator rng = new Well44497b(seed);
  // this.samp = create_sampler(p, rng, bbox);
  //
  // p.set("temp", temp);
  // p.set("deccoef", deccoef);
  //
  // this.sch = create_schedule(p);
  //
  // p.set("nbiter", nbiter);
  // this.end = create_end_test(p);
  //
  // List<Visitor> list = new ArrayList<Visitor>();
  // this.mVisitor = new CompositeVisitor(list);
  // }
  //
  // public double optimize(File output) {
  // SimulatedAnnealing.optimize(Random.random(), conf, samp, sch, end,
  // mVisitor);
  // ShapefileWriter shpWriter = new ShapefileWriter(new GeometryFilter() {
  // CoordinateFilter coordFilter = new CoordinateFilter() {
  // @Override
  // public void filter(Coordinate coord) {
  // coord.y *= -1;
  // }
  // };
  //
  // @Override
  // public void filter(Geometry geom) {
  // geom.apply(coordFilter);
  // }
  // });
  // shpWriter.writeShapefile(output, conf);
  // return conf.getEnergy();
  // }
}
