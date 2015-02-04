package fr.ign.samples.buildingfootprintrectangle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well44497b;
import org.junit.Assert;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFilter;

import fr.ign.geometry.IsoRectangle2D;
import fr.ign.geometry.Rectangle2D;
import fr.ign.geometry.Vector2D;
import fr.ign.geometry.transform.RectangleCornerTranslationTransform;
import fr.ign.geometry.transform.RectangleEdgeTranslationTransform;
import fr.ign.geometry.transform.RectangleSplitMergeTransform;
import fr.ign.image.OrientedPlanarImageWrapper;
import fr.ign.image.OrientedPlanarImageWrapperImageIO;
import fr.ign.image.OrientedView;
import fr.ign.mpp.DirectSampler;
import fr.ign.mpp.configuration.BirthDeathModification;
import fr.ign.mpp.configuration.GraphConfiguration;
import fr.ign.mpp.energy.ImageGradientUnaryEnergy;
import fr.ign.mpp.energy.IntersectionAreaBinaryEnergy;
import fr.ign.mpp.kernel.ObjectBuilder;
import fr.ign.mpp.kernel.UniformBirth;
import fr.ign.parameters.Parameters;
import fr.ign.random.Random;
import fr.ign.rjmcmc.acceptance.MetropolisAcceptance;
import fr.ign.rjmcmc.distribution.PoissonDistribution;
import fr.ign.rjmcmc.energy.BinaryEnergy;
import fr.ign.rjmcmc.energy.ConstantEnergy;
import fr.ign.rjmcmc.energy.MinusUnaryEnergy;
import fr.ign.rjmcmc.energy.MultipliesBinaryEnergy;
import fr.ign.rjmcmc.energy.MultipliesUnaryEnergy;
import fr.ign.rjmcmc.energy.UnaryEnergy;
import fr.ign.rjmcmc.kernel.DiagonalAffineTransform;
import fr.ign.rjmcmc.kernel.Kernel;
import fr.ign.rjmcmc.kernel.Transform;
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

public class BuildingFootprintRectangle {

	// [building_footprint_rectangle_init_visitor
	static void init_visitor(Parameters p, Visitor v) {
		v.init(p.getInteger("nbdump"), p.getInteger("nbsave"));
	}

	// ]

	public static GraphConfiguration<Rectangle2D> create_configuration(
			Parameters p, OrientedView grad) {
		String mask_file = p.getString("mask");
		if (!mask_file.isEmpty()) {
			// IsoRectangle2D bbox = get_bbox(p);
			// clip_bbox(bbox,mask_file);
			// mask_type mask(mask_file , bbox, conversion_functor() );
			// boost::gil::write_view( mask_file+"_x0.tif" ,
			// boost::gil::nth_channel_view(grad.view(),0),
			// boost::gil::tiff_tag() );
			// boost::gil::write_view( mask_file+"_y0.tif" ,
			// boost::gil::nth_channel_view(grad.view(),1),
			// boost::gil::tiff_tag() );
			// for(int j=0; j<grad.view().height();++j)
			// {
			// for(int i=0; i<grad.view().width();++i)
			// {
			// std::cout << mask.view()(i,j)<< " ";
			// if (i < mask.view().width() && j < mask.view().height() &&
			// mask.view()(i,j)<=0)
			// {
			// boost::gil::at_c<0>(grad.view()(i,j)) =
			// boost::gil::at_c<1>(grad.view()(i,j)) = 0;
			// }
			// }
			// }
			// boost::gil::write_view( mask_file+"_x.tif" ,
			// boost::gil::nth_channel_view(grad.view(),0),
			// boost::gil::tiff_tag() );
			// boost::gil::write_view( mask_file+"_y.tif" ,
			// boost::gil::nth_channel_view(grad.view(),1),
			// boost::gil::tiff_tag() );
		}

		// minus_energy<constant_energy<>,multiplies_energy<constant_energy<>,unary_energy>
		// >,
		// multiplies_energy<constant_energy<>,binary_energy>
		ConstantEnergy<Rectangle2D, Rectangle2D> c1 = new ConstantEnergy<Rectangle2D, Rectangle2D>(
				p.getDouble("energy"));
		ConstantEnergy<Rectangle2D, Rectangle2D> c2 = new ConstantEnergy<Rectangle2D, Rectangle2D>(
				p.getDouble("ponderation_grad"));
		UnaryEnergy<Rectangle2D> u1 = new ImageGradientUnaryEnergy<Rectangle2D>(
				grad);
		UnaryEnergy<Rectangle2D> u2 = new MultipliesUnaryEnergy<Rectangle2D>(
				c2, u1);
		UnaryEnergy<Rectangle2D> u3 = new MinusUnaryEnergy<Rectangle2D>(c1, u2);
		ConstantEnergy<Rectangle2D, Rectangle2D> c3 = new ConstantEnergy<Rectangle2D, Rectangle2D>(
				p.getDouble("ponderation_surface"));
		BinaryEnergy<Rectangle2D, Rectangle2D> b1 = new IntersectionAreaBinaryEnergy<Rectangle2D>();
		BinaryEnergy<Rectangle2D, Rectangle2D> b2 = new MultipliesBinaryEnergy<Rectangle2D, Rectangle2D>(
				c3, b1);
		// c1 - c2*u1
		// u3 = c1 - u2 = energy - c2 * u1 = energy - ponderation_grad *
		// ImageGradientUnaryEnergy
		// b2 = c3*b1 = ponderation_surface * IntersectionAreaBinaryEnergy
		// empty initial configuration
		GraphConfiguration<Rectangle2D> conf = new GraphConfiguration<Rectangle2D>(
				u3, b2);
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
	static Sampler<GraphConfiguration<Rectangle2D>, BirthDeathModification<Rectangle2D>> create_sampler(
			Parameters p, RandomGenerator rng, final IsoRectangle2D r) {
		Vector2D v = new Vector2D(p.getDouble("maxsize"),
				p.getDouble("maxsize"));
		double maxratio = p.getDouble("maxratio");
		double minratio = 1 / maxratio;
		ObjectBuilder<Rectangle2D> builder = new ObjectBuilder<Rectangle2D>() {
			@Override
			public Rectangle2D build(Vector<Double> coordinates) {
				return new Rectangle2D(coordinates.get(0), coordinates.get(1),
						coordinates.get(2), coordinates.get(3),
						coordinates.get(4));
			}

			@Override
			public int size() {
				return 5;
			}

			@Override
			public void setCoordinates(Rectangle2D t, List<Double> coordinates) {
				coordinates.set(0, t.centerx);
				coordinates.set(1, t.centery);
				coordinates.set(2, t.normalx);
				coordinates.set(3, t.normaly);
				coordinates.set(4, t.ratio);
			}
		};
		Vector2D n = v.negate();

		// Predicate pred = new Predicate() {
		// @Override
		// public boolean check(double[] val) {
		// // return val[0] < r.getMinX() + (r.getMaxX() - r.getMinX() / 2);
		// if (val[0] > 1) {
		// System.out.println(val[0]);
		// System.exit(0);
		// }
		// return val[0] < 0.5;
		// }
		// };

		UniformBirth<Rectangle2D> birth = new UniformBirth<Rectangle2D>(rng,
				new Rectangle2D(r.min().x(), r.min().y(), n.x(), n.y(),
						minratio), new Rectangle2D(r.max().x(), r.max().y(),
						v.x(), v.y(), maxratio), builder);

		double p_birthdeath = p.getDouble("pbirthdeath");
		double p_birth = p.getDouble("pbirth");
		double p_edge = 0.25 * p.getDouble("pedge");
		double p_corner = 0.25 * p.getDouble("pcorner");
		double p_split_merge = p.getDouble("psplitmerge");
		double p_split = p.getDouble("psplit");

		PoissonDistribution distribution = new PoissonDistribution(rng,
				p.getDouble("poisson"));

		DirectSampler<Rectangle2D> ds = new DirectSampler<>(distribution, birth);

		List<Kernel<GraphConfiguration<Rectangle2D>, BirthDeathModification<Rectangle2D>>> kernels = new ArrayList<Kernel<GraphConfiguration<Rectangle2D>, BirthDeathModification<Rectangle2D>>>(
				3);
		kernels.add(Kernel.make_uniform_birth_death_kernel(rng, builder, birth,
				p_birthdeath, p_birth));
		kernels.add(Kernel.make_uniform_modification_kernel(rng, builder,
				new RectangleEdgeTranslationTransform(0, minratio, maxratio),
				p_edge, "EdgeTrans0"));
		kernels.add(Kernel.make_uniform_modification_kernel(rng, builder,
				new RectangleEdgeTranslationTransform(1, minratio, maxratio),
				p_edge, "EdgeTrans1"));
		kernels.add(Kernel.make_uniform_modification_kernel(rng, builder,
				new RectangleEdgeTranslationTransform(2, minratio, maxratio),
				p_edge, "EdgeTrans2"));
		kernels.add(Kernel.make_uniform_modification_kernel(rng, builder,
				new RectangleEdgeTranslationTransform(3, minratio, maxratio),
				p_edge, "EdgeTrans3"));
		kernels.add(Kernel.make_uniform_modification_kernel(rng, builder,
				new RectangleCornerTranslationTransform(0), p_corner,
				"CornTrans0"));
		kernels.add(Kernel.make_uniform_modification_kernel(rng, builder,
				new RectangleCornerTranslationTransform(1), p_corner,
				"CornTrans1"));
		kernels.add(Kernel.make_uniform_modification_kernel(rng, builder,
				new RectangleCornerTranslationTransform(2), p_corner,
				"CornTrans2"));
		kernels.add(Kernel.make_uniform_modification_kernel(rng, builder,
				new RectangleCornerTranslationTransform(3), p_corner,
				"CornTrans3"));
		kernels.add(Kernel.make_uniform_modification_kernel(rng, builder,
				new RectangleSplitMergeTransform(400), p_split_merge, p_split,
				1, 2, "SplitMerge"));
		Sampler<GraphConfiguration<Rectangle2D>, BirthDeathModification<Rectangle2D>> s = new GreenSampler<>(
				rng, ds, new MetropolisAcceptance<SimpleTemperature>(), kernels);
		return s;
	}

	// ]

	void go() {
		// load dsm
		// Iso_rectangle_2 bbox = get_bbox(m_param);
		// std::string dsm_file =
		// m_param->get<boost::filesystem::path>("dsm").string();
		// clip_bbox(bbox,dsm_file );
		//
		// gradient_functor gf(m_param->get<double>("sigmaD"));
		// oriented_gradient_view grad_view(dsm_file, bbox, gf);
		// m_grad = grad_view.img();
		// std::string mask_file =
		// m_param->get<boost::filesystem::path>("mask").string();

		// load_image(dsm_file );
		// load_image(mask_file);
		//
		// set_bbox(m_param,bbox);
		// wxPoint p0(wxCoord(bbox.min().x()),wxCoord(bbox.min().y()));
		// wxPoint p1(wxCoord(bbox.max().x()),wxCoord(bbox.max().y()));
		// m_confg_visitor->set_bbox(wxRect(p0,p1));
		Parameters m_param = initialize_parameters();
		List<Visitor> list = new ArrayList<Visitor>();
		// simulated_annealing::wx::log_visitor*,
		// simulated_annealing::wx::configuration_visitor*,
		// simulated_annealing::wx::parameters_visitor*,
		// simulated_annealing::wx::chart_visitor*,
		// simulated_annealing::wx::controler_visitor*
		CompositeVisitor mVisitor = new CompositeVisitor(list);
		init_visitor(m_param, mVisitor);
		// create_configuration(m_param,grad_view,m_config);
		// <-
		// estimate_initial_temperature(p,100,*m_config);
		// ->
		// create_sampler (m_param,m_sampler);
		// create_schedule (m_param,m_schedule);
		// create_end_test (m_param,m_end_test);

		// std::cout << "Salamon initial schedule : " <<
		// salamon_initial_schedule(m_sampler->density(),*m_config,1000) <<
		// std::endl;
		// m_config->clear();
		//
		// typedef rjmcmc::any_sampler<configuration> any_sampler;
		// typedef simulated_annealing::any_visitor<configuration,any_sampler>
		// any_visitor;
		//
		// any_sampler *sampler = new any_sampler(*m_sampler);
		// any_visitor *visitor = new any_visitor(*m_visitor);
		//
		// m_thread = new boost::thread(
		// simulated_annealing::optimize<configuration,any_sampler,schedule,end_test,any_visitor>,
		// boost::ref(*m_config), boost::ref(*sampler),
		// boost::ref(*m_schedule), boost::ref(*m_end_test),
		// boost::ref(*visitor) );

	}

	// [building_footprint_rectangle_cli_main
	public static void main(String[] args) throws IOException {
		/*
		 * < Retrieve the singleton instance of the parameters object...
		 * initialize the parameters object with the default values provided...
		 * parse the command line to eventually change the values >
		 */
		Parameters p = initialize_parameters();

		/*
		 * < Input data is an image. We first retrieve from the parameters the
		 * region to process... clip the image to fit this region... and then
		 * compute the gradient and build the attached view>
		 */
		IsoRectangle2D bbox = get_bbox(p);
		String dsm_file = p.getString("dsm");

		// GradientFunctor gf = new
		// GradientFunctor(Double.parseDouble(p.get("sigmaD")));
		OrientedView grad_view = new OrientedPlanarImageWrapper(dsm_file,
				p.getFloat("sigmaD"));

		clip_bbox(bbox, grad_view);
		// p.put("bbox", bbox);

		// set_bbox(p,bbox);

		RandomGenerator rng = Random.random();
		/*
		 * < Before launching the optimization process, we create all the
		 * required stuffs: a configuration, a sampler, a schedule scheme and an
		 * end test >
		 */
		GraphConfiguration<Rectangle2D> conf = create_configuration(p, grad_view);
		Sampler<GraphConfiguration<Rectangle2D>, BirthDeathModification<Rectangle2D>> samp = create_sampler(p, rng, bbox);
		Schedule<SimpleTemperature> sch = create_schedule(p);
		EndTest end = create_end_test(p);
		/*
		 * < Build and initialize simple visitor which prints some data on the
		 * standard output >
		 */
		Visitor visitor = new OutputStreamVisitor(System.out);
		Visitor shpVisitor = new ShapefileVisitor("target\\building_result",
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
		List<Visitor> list = new ArrayList<Visitor>();
		list.add(visitor);
		list.add(shpVisitor);
		CompositeVisitor mVisitor = new CompositeVisitor(list);
		init_visitor(p, mVisitor);
		/*
		 * < This is the way to launch the optimization process. Here, the magic
		 * happen... >
		 */
		SimulatedAnnealing.optimize(Random.random(), conf, samp, sch, end,
				mVisitor);
		return;
	}

	private static void clip_bbox(IsoRectangle2D bbox, int x0, int y0, int x1,
			int y1) {
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

	private static Schedule<SimpleTemperature> create_schedule(Parameters p) {
		return new GeometricSchedule<SimpleTemperature>(new SimpleTemperature(
				p.getDouble("temp")), p.getDouble("deccoef"));
	}

	private static Parameters initialize_parameters() {
		try {
			return Parameters.unmarshall(new File(
					"./src/main/resources/building_parameters.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// ]
	UnaryEnergy<Rectangle2D> unaryEnergy = null;
	BinaryEnergy<Rectangle2D, Rectangle2D> binaryEnergy = null;
	DiagonalAffineTransform transform = null;
	RandomGenerator e;
	Transform[] transforms;

	public BuildingFootprintRectangle(File dsmFile, float sigmaD,
			double energy, double ponderationGrad, double ponderationSurface,
			double maxsize, double maxratio) throws IOException {
		OrientedView grad_view = new OrientedPlanarImageWrapperImageIO(dsmFile,
				sigmaD);
		ConstantEnergy<Rectangle2D, Rectangle2D> c1 = new ConstantEnergy<Rectangle2D, Rectangle2D>(
				energy);
		ConstantEnergy<Rectangle2D, Rectangle2D> c2 = new ConstantEnergy<Rectangle2D, Rectangle2D>(
				ponderationGrad);
		UnaryEnergy<Rectangle2D> u1 = new ImageGradientUnaryEnergy<Rectangle2D>(
				grad_view);
		UnaryEnergy<Rectangle2D> u2 = new MultipliesUnaryEnergy<Rectangle2D>(
				c2, u1);
		UnaryEnergy<Rectangle2D> u3 = new MinusUnaryEnergy<Rectangle2D>(c1, u2);
		ConstantEnergy<Rectangle2D, Rectangle2D> c3 = new ConstantEnergy<Rectangle2D, Rectangle2D>(
				ponderationSurface);
		BinaryEnergy<Rectangle2D, Rectangle2D> b1 = new IntersectionAreaBinaryEnergy<Rectangle2D>();
		BinaryEnergy<Rectangle2D, Rectangle2D> b2 = new MultipliesBinaryEnergy<Rectangle2D, Rectangle2D>(
				c3, b1);
		// c1 - c2*u1
		// u3 = c1 - u2 = energy - c2 * u1 = energy - ponderation_grad *
		// ImageGradientUnaryEnergy
		// b2 = c3*b1 = ponderation_surface * IntersectionAreaBinaryEnergy
		// empty initial configuration
		this.unaryEnergy = u3;
		this.binaryEnergy = b2;
		double minx = grad_view.x0();
		double miny = grad_view.y0();
		double maxx = grad_view.x0() + grad_view.width();
		double maxy = grad_view.y0() + grad_view.height();
		double minratio = 1 / maxratio;
		Vector<Double> coordinates = new Vector<>(5);
		coordinates.setSize(5);
		coordinates.set(0, minx);
		coordinates.set(1, miny);
		coordinates.set(2, -maxsize);
		coordinates.set(3, -maxsize);
		coordinates.set(4, minratio);
		Vector<Double> d = new Vector<>(5);
		d.setSize(5);
		d.set(0, maxx - minx);
		d.set(1, maxy - miny);
		d.set(2, 2 * maxsize);
		d.set(3, 2 * maxsize);
		d.set(4, maxratio - minratio);
		this.transform = new DiagonalAffineTransform(d, coordinates);
		this.e = Random.random();
		this.transforms = new Transform[8];
		this.transforms[0] = new RectangleEdgeTranslationTransform(0, minratio,
				maxratio);
		this.transforms[1] = new RectangleEdgeTranslationTransform(1, minratio,
				maxratio);
		this.transforms[2] = new RectangleEdgeTranslationTransform(2, minratio,
				maxratio);
		this.transforms[3] = new RectangleEdgeTranslationTransform(3, minratio,
				maxratio);
		this.transforms[4] = new RectangleCornerTranslationTransform(0);
		this.transforms[5] = new RectangleCornerTranslationTransform(1);
		this.transforms[6] = new RectangleCornerTranslationTransform(2);
		this.transforms[7] = new RectangleCornerTranslationTransform(3);
	}

	// public double getEnergy(double[] parameterArray) {
	// Assert.assertTrue("Number of values in the parameter array is not a multiple of 5",
	// parameterArray.length % 5 == 0);
	// GraphConfiguration<Rectangle2D> conf = new
	// GraphConfiguration<Rectangle2D>(this.unaryEnergy,
	// this.binaryEnergy);
	// Modification<Rectangle2D, GraphConfiguration<Rectangle2D>> modif = new
	// Modification<>();
	// for (int index = 0; index < parameterArray.length;) {
	// Rectangle2D rectangle = new Rectangle2D(parameterArray[index++] *
	// this.transform.getMat()[0]
	// + this.transform.getDelta()[0], parameterArray[index++] *
	// this.transform.getMat()[1]
	// + this.transform.getDelta()[1], parameterArray[index++] *
	// this.transform.getMat()[2]
	// + this.transform.getDelta()[2], parameterArray[index++] *
	// this.transform.getMat()[3]
	// + this.transform.getDelta()[3], parameterArray[index++] *
	// this.transform.getMat()[4]
	// + this.transform.getDelta()[4]);
	// modif.insertBirth(rectangle);
	// }
	// /* double delta = */conf.deltaEnergy(modif);
	// conf.apply(modif);
	// System.out.println("unary = " + conf.getUnaryEnergy());
	// System.out.println("binary = " + conf.getBinaryEnergy());
	// return conf.getEnergy();
	// }

	public double getEnergy(double[] parameterArray) {
		Assert.assertTrue(
				"Number of values in the parameter array is not a multiple of 5",
				parameterArray.length % 5 == 0);
		GraphConfiguration<Rectangle2D> conf = new GraphConfiguration<Rectangle2D>(
				this.unaryEnergy, this.binaryEnergy);
		for (int index = 0; index < parameterArray.length;) {
			BirthDeathModification<Rectangle2D> modif = new BirthDeathModification<>();
			Rectangle2D rectangle = new Rectangle2D(
					parameterArray[index++] * this.transform.getMat()[0]
							+ this.transform.getDelta()[0],
					parameterArray[index++] * this.transform.getMat()[1]
							+ this.transform.getDelta()[1],
					parameterArray[index++] * this.transform.getMat()[2]
							+ this.transform.getDelta()[2],
					parameterArray[index++] * this.transform.getMat()[3]
							+ this.transform.getDelta()[3],
					parameterArray[index++] * this.transform.getMat()[4]
							+ this.transform.getDelta()[4]);
			modif.insertBirth(rectangle);
			/* double delta = */conf.deltaEnergy(modif);
			// conf.apply(modif);
			modif.apply(conf);
		}
		// System.out.println("unary = " + conf.getUnaryEnergy());
		// System.out.println("binary = " + conf.getBinaryEnergy());
		return conf.getEnergy();
	}

	public void writeToShapefile(double[] parameterArray, File outputFile) {
		Assert.assertTrue(
				"Number of values in the parameter array is not a multiple of 5",
				parameterArray.length % 5 == 0);
		GraphConfiguration<Rectangle2D> conf = new GraphConfiguration<Rectangle2D>(
				this.unaryEnergy, this.binaryEnergy);
		BirthDeathModification<Rectangle2D> modif = new BirthDeathModification<>();
		for (int index = 0; index < parameterArray.length;) {
			Rectangle2D rectangle = new Rectangle2D(
					parameterArray[index++] * this.transform.getMat()[0]
							+ this.transform.getDelta()[0],
					parameterArray[index++] * this.transform.getMat()[1]
							+ this.transform.getDelta()[1],
					parameterArray[index++] * this.transform.getMat()[2]
							+ this.transform.getDelta()[2],
					parameterArray[index++] * this.transform.getMat()[3]
							+ this.transform.getDelta()[3],
					parameterArray[index++] * this.transform.getMat()[4]
							+ this.transform.getDelta()[4]);
			modif.insertBirth(rectangle);
		}
		/* double delta = */conf.deltaEnergy(modif);
		// conf.apply(modif);
		modif.apply(conf);
		ShapefileWriter shpWriter = new ShapefileWriter(new GeometryFilter() {
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
		shpWriter.writeShapefile(outputFile, conf);
	}

	public double[] readFromShapefile(File inputFile) {
		ShapefileReader shpReader = new ShapefileReader(new GeometryFilter() {
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
		Rectangle2D[] rectangleArray = shpReader.readShapefile(inputFile);
		double[] result = new double[rectangleArray.length * 5];
		for (int index = 0; index < rectangleArray.length; index++) {
			Rectangle2D r = rectangleArray[index];
			result[index * 5] = r.centerx * this.transform.getMatInv()[0]
					+ this.transform.getDeltaInv()[0];
			result[index * 5 + 1] = r.centery * this.transform.getMatInv()[1]
					+ this.transform.getDeltaInv()[1];
			result[index * 5 + 2] = r.normalx * this.transform.getMatInv()[2]
					+ this.transform.getDeltaInv()[2];
			result[index * 5 + 3] = r.normaly * this.transform.getMatInv()[3]
					+ this.transform.getDeltaInv()[3];
			result[index * 5 + 4] = r.ratio * this.transform.getMatInv()[4]
					+ this.transform.getDeltaInv()[4];
		}
		// for (int index = 0; index < result.length; index++) {
		// Assert.assertTrue("Result is not in [0,1] : " + result[index],
		// result[index] >= 0
		// && result[index] <= 1.0);
		// }
		return result;
	}

	/**
	 * @param parameterArray
	 * @return
	 */
	public double[] transform(double[] parameterArray) {
		Assert.assertTrue(
				"Number of values in the parameter array is not a multiple of 5",
				parameterArray.length % 5 == 0);
		Vector<Double> var0 = new Vector<>();
		Vector<Double> var1 = new Vector<>();
		Vector<Double> val0 = new Vector<>();
		Vector<Double> val1 = new Vector<>();
		int n0 = 5;
		int n1 = 5;
		int index = (int) Math.floor((this.transforms.length * e.nextDouble()));
		int ntotal = transforms[index].dimension(n0, n1);
		val0.setSize(n0);
		var0.setSize(ntotal - n0);
		val1.setSize(n1);
		var1.setSize(ntotal - n1);
		for (int i = 0; i < 5; i++) {
			val0.set(i, parameterArray[i] * this.transform.getMat()[i]
					+ this.transform.getDelta()[i]);
		}
		for (int i = 0; i < ntotal - n0; i++) {
			var0.set(i, e.nextDouble());
		}
		transforms[index].apply(true, val0, var0, val1, var1); // computes val1
																// from val0
		double[] result = new double[5];
		for (int i = 0; i < 5; i++) {
			result[i] = val1.get(i).doubleValue()
					* this.transform.getMatInv()[i]
					+ this.transform.getDeltaInv()[i];
		}
		return result;
	}

	OrientedView grad_view;
	GraphConfiguration<Rectangle2D> conf;
	Sampler<GraphConfiguration<Rectangle2D>, BirthDeathModification<Rectangle2D>> samp;
	Schedule<SimpleTemperature> sch;
	EndTest end;
	CompositeVisitor mVisitor;

	public BuildingFootprintRectangle(long seed, File dsmFile, float sigmaD,
			double energy, double ponderationGrad, double ponderationSurface,
			double maxsize, double maxratio, double pbirthdeath, double pedge,
			double pcorner, double psplitmerge, double temp, double deccoef,
			int nbiter, double poisson) throws IOException {
		Parameters p = new Parameters();

		this.grad_view = new OrientedPlanarImageWrapperImageIO(dsmFile, sigmaD);

		p.set("energy", energy);
		p.set("ponderation_grad", ponderationGrad);
		p.set("ponderation_surface", ponderationSurface);
		this.conf = (GraphConfiguration<Rectangle2D>) create_configuration(p,
				grad_view);

		p.set("maxsize", maxsize);
		p.set("maxratio", maxratio);
		p.set("pbirthdeath", pbirthdeath);
		p.set("pedge", pedge);
		p.set("pcorner", pcorner);
		p.set("psplitmerge", psplitmerge);

		p.set("pbirth", 0.5);
		p.set("psplit", 0.5);

		p.set("poisson", poisson);

		p.set("xmin", 0);
		p.set("ymin", 0);

		p.set("xmax", 1000000);
		p.set("ymax", 1000000);

		IsoRectangle2D bbox = get_bbox(p);

		clip_bbox(bbox, grad_view);

		RandomGenerator rng = new Well44497b(seed);
		this.samp = create_sampler(p, rng, bbox);

		p.set("temp", temp);
		p.set("deccoef", deccoef);

		this.sch = create_schedule(p);

		p.set("nbiter", nbiter);
		this.end = create_end_test(p);

		List<Visitor> list = new ArrayList<Visitor>();
		this.mVisitor = new CompositeVisitor(list);
	}

	public double optimize(File output) {
		SimulatedAnnealing.optimize(Random.random(), conf, samp, sch, end,
				mVisitor);
		ShapefileWriter shpWriter = new ShapefileWriter(new GeometryFilter() {
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
		shpWriter.writeShapefile(output, conf);
		return conf.getEnergy();
	}
}
