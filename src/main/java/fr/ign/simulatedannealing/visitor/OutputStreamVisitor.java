package fr.ign.simulatedannealing.visitor;

import java.io.PrintStream;
import java.util.Calendar;
import java.util.Formatter;

import fr.ign.mpp.configuration.ListConfiguration;
import fr.ign.rjmcmc.configuration.Configuration;
import fr.ign.rjmcmc.configuration.Modification;
import fr.ign.rjmcmc.sampler.Sampler;
import fr.ign.simulatedannealing.temperature.Temperature;

public class OutputStreamVisitor<C extends Configuration<C, M>, M extends Modification<C, M>>
		implements Visitor<C, M> {
	private PrintStream stream;
	private int dump;
	private int iter;
	private int[] accepted;
	private int[] proposed;
	private long time;
	private long timeRandomApply;
	private long timeGreenRatio;
	private long timeDelta;
	private long timeAcceptance;
	private long timeApply;
	// private long timeDeltaBirth;
	// private long timeDeltaDeath;
	private long clock_begin;
	private long clock;
	Formatter formatter;
	String formatString = "| %1$-12s ";
	String formatDouble = "| %1$-12f ";
	String formatInt = "| %1$-12d ";

	public OutputStreamVisitor(PrintStream os) {
		this.stream = os;
	}

	@Override
	public void init(int d, int save) {
		this.iter = 0;
		this.dump = d;
	}

	@Override
	public void begin(C config, Sampler<C, M> sampler, Temperature t) {
		int kernel_size = sampler.kernelSize();
		accepted = new int[kernel_size];
		proposed = new int[kernel_size];
		for (int i = 0; i < kernel_size; ++i) {
			accepted[i] = proposed[i] = 0;
		}
		this.stream.format("Starting at %1$tH:%1$tM:%1$tS%n",
				Calendar.getInstance());
		this.formatter = new Formatter(this.stream);
		this.formatter.format(this.formatString, "Iteration");
		if (config instanceof ListConfiguration) {
			this.formatter.format(this.formatString, "Objects");
		}
		for (int i = 0; i < kernel_size; ++i) {
			String s = sampler.kernelName(i);
			this.formatter.format(this.formatString, "P-" + s);
			this.formatter.format(this.formatString, "A-" + s);
		}
		for (int i = 0; i < t.size(); ++i) {
			this.formatter.format(this.formatString, "Temp_" + i);
		}
		this.formatter.format(this.formatString, "Accept");
		this.formatter.format(this.formatString, "Time(ms)");
		if (config instanceof ListConfiguration) {
			this.formatter.format(this.formatString, "U_1");
			this.formatter.format(this.formatString, "U_2");
		}
		this.formatter.format(this.formatString, "U");
		// this.formatter.format(this.formatString, "time");
		this.formatter.format(this.formatString, "RandomApply");
		this.formatter.format(this.formatString, "GreenRatio");
		this.formatter.format(this.formatString, "Delta");
		this.formatter.format(this.formatString, "Acceptance");
		this.formatter.format(this.formatString, "Apply");

		stream.println();
		stream.flush();
		clock_begin = clock = System.currentTimeMillis();
	}

	@Override
	public void end(C config, Sampler<C, M> sampler, Temperature t) {
		long clock_end = System.currentTimeMillis();
		this.stream.format("Finished at %1$tH:%1$tM:%1$tS%n",
				Calendar.getInstance());
		this.stream.println("Total elapsed time (s) :  "
				+ (clock_end - clock_begin) / 1000);
		// stream.println("Graph Data energy integrity : " +
		// (config.audit_unary_energy() -
		// config.unary_energy()));
		// stream.println("Graph Prior energy integrity: " +
		// (config.audit_binary_energy() -
		// config.binary_energy()));
		// stream.println("Graph Structure integrity : " +
		// config.audit_structure());
		// this.stream.println(config);
		// this.stream.println();
		this.stream.flush();
	}

	@Override
	public void visit(C config, Sampler<C, M> sampler, Temperature t) {
		if (sampler == null)
			return;
		int kernel_size = sampler.kernelSize();
		proposed[sampler.kernelId()]++;
		if (sampler.accepted()) {
			accepted[sampler.kernelId()]++;
		}
		time += sampler.getTimeAcceptance() + sampler.getTimeApply()
				+ sampler.getTimeDelta() + sampler.getTimeGreenRatio()
				+ sampler.getTimeRandomApply();
		timeRandomApply += sampler.getTimeRandomApply();
		timeGreenRatio += sampler.getTimeGreenRatio();
		timeDelta += sampler.getTimeDelta();
		timeAcceptance += sampler.getTimeAcceptance();
		timeApply += sampler.getTimeApply();
		// timeDeltaBirth += ((GraphConfiguration)config).getTimeDeltaBirth();
		// timeDeltaDeath += ((GraphConfiguration)config).getTimeDeltaDeath();

		++iter;
		if ((dump > 0) && (iter % dump == 0)) {
			this.formatter.format(this.formatInt, new Integer(iter));
			if (config instanceof ListConfiguration) {
				ListConfiguration<?, ?, ?> c = (ListConfiguration<?, ?, ?>) config;
				this.formatter.format(this.formatInt, new Integer(c.size()));
			}
			int total_accepted = 0;
			for (int k = 0; k < kernel_size; ++k) {
				this.formatter.format(this.formatDouble, new Double(100.
						* proposed[k] / dump));
				this.formatter.format(this.formatDouble, new Double(
						(proposed[k] > 0) ? (100. * accepted[k]) / proposed[k]
								: 100.));
				total_accepted += accepted[k];
				accepted[k] = proposed[k] = 0;
			}
			for (int i = 0; i < t.size(); i++) {
				this.formatter.format(this.formatDouble,
						new Double(t.getTemperature(i)));
			}
			this.formatter.format(this.formatDouble, new Double(
					(100. * total_accepted) / dump));
			long clock_temp = System.currentTimeMillis();
			this.formatter.format(this.formatDouble, new Double(
					(double) (clock_temp - clock) / (double) dump));
			this.clock = clock_temp;
			if (config instanceof ListConfiguration) {
				ListConfiguration<?, ?, ?> c = (ListConfiguration<?, ?, ?>) config;
				this.formatter.format(this.formatDouble,
						new Double(c.getUnaryEnergy()));
				this.formatter.format(this.formatDouble,
						new Double(c.getBinaryEnergy()));
			}
			this.formatter.format(this.formatDouble,
					new Double(config.getEnergy()));

			// this.formatter.format(this.formatDouble, new Double(time * 1000.
			// / dump));
			this.formatter.format(this.formatDouble, new Double(
					(double) timeRandomApply / (double) time));
			this.formatter.format(this.formatDouble, new Double(
					(double) timeGreenRatio / (double) time));
			this.formatter.format(this.formatDouble, new Double(
					(double) timeDelta / (double) time));
			this.formatter.format(this.formatDouble, new Double(
					(double) timeAcceptance / (double) time));
			this.formatter.format(this.formatDouble, new Double(
					(double) timeApply / (double) time));

			// this.formatter.format(this.formatDouble, new
			// Double((double)timeDeltaBirth/(double)(timeDeltaBirth+timeDeltaDeath)));
			// this.formatter.format(this.formatDouble, new
			// Double((double)timeDeltaDeath/(double)(timeDeltaBirth+timeDeltaDeath)));

			// this.formatter.format(this.formatDouble, new
			// Double(sampler.acceptanceProbability()));
			// this.formatter.format(this.formatDouble, new
			// Double(sampler.delta()));
			// this.formatter.format(this.formatDouble, new
			// Double(sampler.greenRatio()));
			// this.formatter.format(this.formatString,
			// Boolean.toString(sampler.accepted()));
			// this.formatter.format(this.formatDouble, new
			// Double(sampler.kernelRatio()));
			// this.formatter.format(this.formatDouble, new
			// Double(sampler.refPdfRatio()));
			stream.println();
			stream.flush();
		}
	}
}
